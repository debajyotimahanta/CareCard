package com.coronacarecard.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.*;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessState;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.PaymentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("payment/stripe")
public class StripePaymentController {
    private static Log log = LogFactory.getLog(StripePaymentController.class);

    @Autowired
    @Qualifier("StripePaymentService")
    private PaymentService paymentService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessService businessService;

    @Value("${spring.app.forntEndBaseUrl}")
    private String forntEndBaseUrl;

    @GetMapping("/success")
    public CheckoutResponse checkout(String urlParams) {
        return paymentService.successPayment(urlParams);
    }


    @GetMapping("/failure")
    public CheckoutResponse fail(String urlParams) {
        return paymentService.failedPayment(urlParams);
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
    public void confirm(@RequestParam(value = "code") String code,
                        @RequestParam(value = "state") String state,
                        HttpServletResponse httpServletResponse)
            throws BusinessClaimException, BusinessNotFoundException, IOException, InternalException,
            PayementServiceException, BusinessAlreadyClaimedException {
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
            throw new BusinessNotFoundException();
        }
        businessDAO.get().setState(BusinessState.Active);
        businessDAO.get().setExternalRefId(business.getExternalRefId());
        businessRepository.save(businessDAO.get());
        httpServletResponse.sendRedirect(forntEndBaseUrl + "/" + businessDAO.get().getExternalRefId() + "?confirm=true");

    }
}
