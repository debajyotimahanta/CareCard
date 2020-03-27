package com.coronacarecard.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.BusinessClaimException;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessState;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.CryptoService;
import com.coronacarecard.service.PaymentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("payment/strip")
public class StripPaymentController {
    private static Log log = LogFactory.getLog(StripPaymentController.class);
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private BusinessRepository businessRepository;

    @Value("${spring.app.forntEndBaseUrl}")
    private String forntEndBaseUrl;

    @GetMapping("/success")
    public CheckoutResponse checkout(String urlParams) {
        return paymentService.successPayment(PaymentSystem.STRIPE, urlParams);
    }


    @GetMapping("/failure")
    public CheckoutResponse fail(String urlParams) {
        return paymentService.failedPayment(PaymentSystem.STRIPE, urlParams);
    }

    @GetMapping("/business/confirm")
    @Transactional
    public void confirm(@RequestParam(value = "code") String code,
                        @RequestParam(value = "state") String state,
                        HttpServletResponse httpServletResponse)
            throws BusinessClaimException, BusinessNotFoundException, IOException, InternalException {
        String decryptedPlaceId = cryptoService.decrypt(state);
        Long id = Long.parseLong(decryptedPlaceId);
        Business business = paymentService.getBusinessDetails(PaymentSystem.STRIPE, code);
        if (id != business.getId()) {
            log.error(String.format("The business id %s and state's id %s don't match something is wrong",
                    business.getId(), id));
            throw new BusinessClaimException("The business id dont match");
        }
        Optional<com.coronacarecard.dao.entity.Business> businessDAO = businessRepository.findById(business.getId());

        if (!businessDAO.isPresent()) {
            log.error(String.format("The business %s does not exists in the system", id));
            throw new BusinessNotFoundException();
        }
        com.coronacarecard.dao.entity.Business.BusinessBuilder businessBuilder = businessDAO.get().toBuilder();
        businessRepository.save(
                businessBuilder
                        .state(BusinessState.Active)
                        .externalRefId(business.getExternalRefId())
                        .build()
        );
        httpServletResponse.sendRedirect(forntEndBaseUrl + "/" + businessDAO.get().getExternalRefId() + "?confirm=true");

    }
}
