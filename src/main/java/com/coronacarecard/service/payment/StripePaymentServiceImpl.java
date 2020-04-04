package com.coronacarecard.service.payment;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessAccountDetailRepository;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.OrderDetailRepository;
import com.coronacarecard.dao.entity.BusinessAccountDetail;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.*;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.mapper.PaymentEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.orders.OrderDetail;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service("StripePaymentService")
public class StripePaymentServiceImpl implements PaymentService {
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

    @Override
    public CheckoutResponse successPayment(String urlParams) {
        return null;
    }

    @Override
    public CheckoutResponse failedPayment(String urlParams) {
        return null;
    }


    @Override
    public OrderStatus confirmTransaction(String transactionId) throws InternalException {

        try {
            Session session = stripeCalls.retrieveSession(transactionId);
            Optional<com.coronacarecard.dao.entity.OrderDetail> maybeOrder = orderRepository.findById(Long.parseLong(session.getClientReferenceId()));
            if (!maybeOrder.isPresent()) {
                throw new InternalException("Order cannot be located");
            }

            PaymentIntent paymentIntent = stripeCalls.retrievePaymentIntent(session.getPaymentIntent());
            com.coronacarecard.dao.entity.OrderDetail order = maybeOrder.get();
            OrderDetail orderModel = OrderDetail.builder()
                    .id(order.getId())
                    .customerEmail(order.getCustomerEmail())
                    .customerMobile(order.getCustomerMobile())
                    .total(order.getTotal())
                    .build();
            if ("succeeded".equals(paymentIntent.getStatus().toLowerCase())) {
                orderRepository.save(order.toBuilder().status(OrderStatus.PAID).build());
                notificationSender.sendNotification(NotificationType.PAYMENT_COMPLETED, orderModel);
                return OrderStatus.PAID;
            }
            return order.getStatus();
        } catch (StripeException ex) {
            throw new InternalException(ex.getMessage());
        }
    }

    @Override
    public String generateOnBoardingUrl(Business business) {

        return String.format(stripeConfiguration.getConnectUrl(),
                stripeConfiguration.getClientId(),
                business.getId().toString());
    }

    @Override
    public CheckoutResponse generateCheckoutSession(OrderDetail savedOrder) throws BusinessNotFoundException, PaymentAccountNotSetupException, InternalException {
        try {
            Session session = stripeCalls.generateSession((SessionCreateParams) paymentMapper.toSessionCreateParams(savedOrder, businessService));
            return paymentMapper.toCheckoutResponse(session, savedOrder, businessService);
        } catch (StripeException ex) {
            throw new InternalException(ex.getMessage());
        }
    }

    @Override
    public void validate(OrderDetail order) {

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

        } catch ( StripeException ex) {
            log.error("Unable to import business", ex);
            throw new PaymentServiceException("Unable to import business. Error retrieving details from Payment Service");
        }

    }


}
