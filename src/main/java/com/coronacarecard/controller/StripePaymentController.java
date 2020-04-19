package com.coronacarecard.controller;

import com.coronacarecard.config.StripeConfiguration;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.*;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessState;
import com.coronacarecard.model.SuccessPaymentNotification;
import com.coronacarecard.queue.QueuePublisher;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("payment/stripe")
@Validated
public class StripePaymentController {
    private static Log log = LogFactory.getLog(StripePaymentController.class);

    @Autowired
    @Qualifier("StripePaymentService")
    private PaymentService paymentService;

    @Autowired
    private QueuePublisher<SuccessPaymentNotification> queuePublisher;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private StripeConfiguration stripeConfiguration;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessService businessService;

    @Value("${spring.app.forntEndBaseUrl}")
    private String forntEndBaseUrl;


    @ResponseBody
    @RequestMapping(consumes = "application/json",
            produces = "application/json",
            method = RequestMethod.POST,
            value = "webhook")
    public String stripeWebhookEndpoint(@RequestBody String payload,
                                        @RequestHeader("Stripe-Signature") String sigHeader)
            throws StripeWebHookError {

        Event event;
        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, stripeConfiguration.getWebHookSecret()
            );
        } catch (JsonSyntaxException e) {
            // Invalid payload
            log.error("Unable to parse stripe json", e);
            throw new StripeWebHookError();
        } catch (SignatureVerificationException e) {
            // Invalid signature
            log.error("Cannot validate stripe payload", e);
            throw new StripeWebHookError();
        }
        if ("checkout.session.completed".equals(event.getType())) {
            // Deserialize the nested object inside the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            if (dataObjectDeserializer.getObject().isPresent()) {
                Session checkoutSession = (Session) dataObjectDeserializer.getObject().get();
                try {
                    queuePublisher.publishEvent(getPayLoad(checkoutSession));
                } catch (JsonProcessingException e) {
                    log.error("Queue cannot send event", e);
                    throw new StripeWebHookError();
                }
            }
        } else {
            log.info(String.format("Ignoring event %s of type %s", event.getId(), event.getType()));
        }
        return null;
    }

    private SuccessPaymentNotification getPayLoad(Session checkoutSession) {
        return SuccessPaymentNotification.builder()
                .orderId(UUID.fromString(checkoutSession.getClientReferenceId()))
                .paymentId(checkoutSession.getId())
                .build();
    }


    @GetMapping("/business/onboard/{id}")
    public String onboard(@PathVariable UUID id) throws BusinessNotFoundException, InternalException {
        try {
            Business business = businessService.getBusiness(id);
            return paymentService.generateOnBoardingUrl(business);
        } catch (NumberFormatException ex) {
            log.error(String.format("The id %s is not in the proper format", id));
            //TODO: This is a BadRequest exception
            throw new InternalException("Id is not in the proper format");
        }
    }

    @GetMapping("/business/confirm")
    @Transactional
    public void confirm(@NotEmpty @NotNull @RequestParam(value = "code") String code,
                        @NotEmpty @NotNull @RequestParam(value = "state") String state,
                        HttpServletResponse httpServletResponse)
            throws BusinessClaimException, BusinessNotFoundException, IOException, InternalException,
            PaymentServiceException, BusinessAlreadyClaimedException {
        UUID id = UUID.fromString(state);
        Business business = paymentService.importBusiness(code, state);
        if (id.compareTo(business.getId()) != 0) {
            log.error(String.format("The business id %s and state's id %s don't match something is wrong",
                    business.getId().toString(), id.toString()));
            throw new BusinessClaimException("The business id dont match");
        }
        Optional<com.coronacarecard.dao.entity.Business> businessDAO = businessRepository.findById(business.getId());

        if (!businessDAO.isPresent()) {
            log.error(String.format("The business %s does not exists in the system", id));
            throw new BusinessNotFoundException("Business not registered with us. Please contact the administrator.");
        }
        businessDAO.get().setState(BusinessState.Active);
        businessDAO.get().setExternalRefId(business.getExternalRefId());
        businessRepository.save(businessDAO.get());
        httpServletResponse.sendRedirect(forntEndBaseUrl + "/businesses/" + businessDAO.get().getExternalRefId() + "?confirm=true");

    }
}
