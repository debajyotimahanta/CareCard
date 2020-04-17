package com.coronacarecard.service.payment;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessAccountDetailRepository;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.OrderDetailRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.BusinessAccountDetail;
import com.coronacarecard.dao.entity.OrderItem;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.*;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.mapper.PaymentEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentState;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.model.orders.OrderLine;
import com.coronacarecard.model.orders.OrderStatus;
import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.model.oauth.TokenResponse;
import com.stripe.param.checkout.SessionCreateParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service("StripePaymentService")
public class StripePaymentServiceImpl implements PaymentService {
    public static final String ORDER_ID = "ORDER_ID";
    private static Log log = LogFactory.getLog(StripePaymentServiceImpl.class);

    @Autowired
    private StripeConfiguration stripeConfiguration;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderDetailRepository orderRepository;

    @Autowired
    private BusinessAccountDetailRepository businessAccountDetailRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    @Qualifier("StripeEntityMapper")
    private PaymentEntityMapper paymentMapper;

    @Autowired
    private StripeCalls stripeCalls;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Autowired
    private NotificationSender<OrderDetail> notificationSender;

    @Value("${spring.app.apiBaseUrl}")
    private String apiUrl;

    private Double STRIPE_PROCESSING_FEE_VARIABLE_PERCENT = 0.029;
    private Double STRIPE_PROCESSING_FEE_FIXED = 0.3;
    private Double EQUALITY_CHECK_LIMIT = 0.0001;

    @Override
    public CheckoutResponse successPayment(String urlParams) {
        return null;
    }

    @Override
    public CheckoutResponse failedPayment(String urlParams) {
        return null;
    }


    @Override
    @Transactional
    public OrderStatus confirmTransaction(String paymentIntentId, UUID orderId)
            throws InternalException, PaymentServiceException {

        try {
            Optional<com.coronacarecard.dao.entity.OrderDetail> maybeOrder = orderRepository.findById(orderId);
            if (!maybeOrder.isPresent()) {
                throw new InternalException("Order cannot be located");
            }
            com.coronacarecard.dao.entity.OrderDetail order = maybeOrder.get();

            PaymentIntent paymentIntent = stripeCalls.retrievePaymentIntent(paymentIntentId);
            if ("succeeded".equals(paymentIntent.getStatus().toLowerCase())) {
                validateSuccessPayment(order, paymentIntent);
                transferFunds(order, paymentIntent.getCharges().getData().get(0).getId());
                order.setStatus(OrderStatus.PAID);
                orderRepository.save(order);
                OrderDetail orderModel = buildOrderDetail(order);
                //TODO (sandeep_hook) Dont pass orderModel here it has too much info just pass what we need to send the email
                // like the business email, customer email, gift card ids, and probably amount?
                notificationSender.sendNotification(NotificationType.PAYMENT_COMPLETED, orderModel);
                return OrderStatus.PAID;
            } else {
                log.info(String.format("Ignoring intent %s of type %s", paymentIntentId, paymentIntent.getStatus()));
            }
            return order.getStatus();
        } catch (StripeException ex) {
            throw new InternalException(ex.getMessage());
        }
    }

    private void transferFunds(com.coronacarecard.dao.entity.OrderDetail order, String chargeId)
            throws StripeException {
        for (OrderItem orderItem : order.getOrderItems()) {
            String transferId = stripeCalls.transferFund(orderItem.getBusiness().getExternalRefId(),
                    orderItem.fundsToTransfer(),
                    order.getId(),
                    order.getCurrency(),
                    chargeId
            );
            orderItem.setPaymentState(PaymentState.Processed);
            orderItem.setProcessingId(transferId);
        }
        ;
    }

    private OrderDetail buildOrderDetail(com.coronacarecard.dao.entity.OrderDetail order) {
        return OrderDetail.builder()
                .id(order.getId())
                .customerEmail(order.getCustomerEmail())
                .customerMobile(order.getCustomerMobile())
                .total(order.getTotal())
                .build();
    }

    private void validateSuccessPayment(com.coronacarecard.dao.entity.OrderDetail order, PaymentIntent paymentIntent) throws PaymentServiceException {
        UUID paymentOrderId = getOrderIdFromPaymentIntent(paymentIntent);
        if (order.getId() != paymentOrderId) {
            log.error(String.format("The order id %s is not the same as payment order id %s", order.getId(), paymentOrderId));
            throw new PaymentServiceException("The stripe  details and the order details dont match");
        }

        if (order.getTotal().longValue() != paymentIntent.getAmount()) {
            log.error(String.format("The order total %s doesnt match payment total %s", order.getTotal(), paymentIntent.getAmount()));
            throw new PaymentServiceException("Totals dont match");
        }

    }

    private UUID getOrderIdFromPaymentIntent(PaymentIntent paymentIntent) {
        return UUID.fromString(paymentIntent.getMetadata().get(ORDER_ID));
    }

    @Override
    public String generateOnBoardingUrl(Business business) {

        return String.format(stripeConfiguration.getConnectUrl(),
                stripeConfiguration.getClientId(),
                business.getId().toString(),
                apiUrl);
    }

    @Override
    public CheckoutResponse generateCheckoutSession(OrderDetail savedOrder) throws BusinessNotFoundException, PaymentAccountNotSetupException, InternalException {
        try {
            Session session = stripeCalls.generateSession((SessionCreateParams) paymentMapper.toSessionCreateParams(savedOrder, businessService));
            updateIntentWithOrderId(session.getPaymentIntent(), savedOrder.getId());
            return paymentMapper.toCheckoutResponse(session, savedOrder, businessService);
        } catch (StripeException ex) {
            log.error("Cannot generate session", ex);
            throw new InternalException(ex.getMessage());
        }
    }

    private void updateIntentWithOrderId(String paymentIntent, UUID id) throws StripeException {
        PaymentIntent intent = stripeCalls.retrievePaymentIntent(paymentIntent);
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(ORDER_ID, id.toString());
        Map<String, Object> params = new HashMap<>();
        params.put("metadata", metadata);
        intent.update(params);
    }

    @Override
    public void validate(OrderDetail order) throws PaymentServiceException {

        Double total = 0.0;
        for (OrderLine line : order.getOrderLine()) {
            total += line.getTip();
            total += line.getItems()
                    .stream()
                    .map(li -> li.getQuantity() * li.getUnitPrice())
                    .reduce(0.0, (e1, e2) -> e1 + e2);

        }
        total += order.getContribution();
        if (Math.abs(total - order.getTotal()) > EQUALITY_CHECK_LIMIT) {
            throw new PaymentServiceException(String.format("Total does not match with line items, expected:%1$s, received:%2$s"
                    , total.toString(), order.getTotal().toString()));
        }
        if (Math.abs(order.getProcessingFee() - calculateProcessingFee(order)) > EQUALITY_CHECK_LIMIT) {
            throw new PaymentServiceException(String.format("Processing fee does not match for the payment service, expected:%1$s, received:%2$s",
                    calculateProcessingFee(order).toString(), order.getProcessingFee().toString()));
        }
    }

    @Override
    public Double calculateProcessingFee(OrderDetail orderDetail) {
        Double processingFee = ((orderDetail.getTotal() + STRIPE_PROCESSING_FEE_FIXED) / (Double) (1 - STRIPE_PROCESSING_FEE_VARIABLE_PERCENT));
        Double result = (Math.round(((processingFee - orderDetail.getTotal()) * 100.0)) / 100.0);
        return result;
    }

    @Override
    @Transactional
    public Business importBusiness(String code, String state) throws BusinessNotFoundException,
            PaymentServiceException, BusinessAlreadyClaimedException {
        try {
            Optional<com.coronacarecard.dao.entity.Business> businessDAO =
                    businessRepository.findById(UUID.fromString(state));
            if (!businessDAO.isPresent()) {
                throw new BusinessNotFoundException("Business not registered with us. Please contact the administrator.");
            }
            com.coronacarecard.dao.entity.Business business = businessDAO.get();
            if (business.getOwner().getAccount() != null) {
                throw new BusinessAlreadyClaimedException();
            }

            User businessOwner = business.getOwner();

            BusinessAccountDetail.BusinessAccountDetailBuilder accountDetailBuilder = BusinessAccountDetail.builder();

            TokenResponse response = stripeCalls.token(code);
            accountDetailBuilder.externalRefId(response.getStripeUserId());
            accountDetailBuilder.refreshToken(
                    cryptoService.encrypt(
                            response.getRawJsonObject().get("refresh_token").getAsString()
                    ));
            accountDetailBuilder.accessToken(
                    cryptoService.encrypt(
                            response.getRawJsonObject().get("access_token").getAsString()
                    ));
            BusinessAccountDetail savedAccountDetails =
                    businessAccountDetailRepository.save(accountDetailBuilder.build());
            businessOwner.setAccount(savedAccountDetails);
            userRepository.save(businessOwner);
            return businessEntityMapper.toModel(businessDAO.get());

        } catch (StripeException ex) {
            log.error("Unable to import business", ex);
            throw new PaymentServiceException("Unable to import business. Error retrieving details from Payment Service");
        }

    }


}
