package com.coronacarecard.web.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.exceptions.CustomerException;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@Controller
@RequestMapping("admin/web")
public class AdminWebController {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private OwnerService ownerService;

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

}
