package com.coronacarecard.web.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.exceptions.CustomerException;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.OwnerService;
import com.coronacarecard.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("admin/web")
public class AdminWebController {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    @Qualifier("StripePaymentService")
    private PaymentService paymentService;

    @GetMapping("/business")
    public String main(@PageableDefault(size = 10) Pageable pageable,
                       Model model) {
        Page<Business> businesses = businessRepository.findAll(pageable);
        model.addAttribute("page", businesses);

        return "business"; //view
    }

    @RequestMapping(value = "/business/{id}/approve", method = RequestMethod.GET)
    public String approve(@PathVariable UUID id,
                       Model model) throws CustomerException {
        ownerService.approveClaim(PaymentSystem.STRIPE, id);
        return "redirect:/admin/web/business";

    }

    @RequestMapping(value = "/business/{id}/decline", method = RequestMethod.GET)
    public String decline(@PathVariable UUID id,
                                  Model model) throws CustomerException {
        ownerService.declineClaim(id);
        return "redirect:/admin/web/business";

    }

    @RequestMapping(value = "/business/{id}/url", method = RequestMethod.GET)
    public @ResponseBody
    String getOnboardingUrl(@PathVariable UUID id,
                            Model model) throws CustomerException {

        return paymentService.generateOnBoardingUrl(businessService.getBusiness(id));
    }

}
