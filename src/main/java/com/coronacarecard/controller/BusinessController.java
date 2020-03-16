package com.coronacarecard.controller;

import com.coronacarecard.model.Business;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusinessController {

    @GetMapping("/create")
    public Business create(@RequestParam(value = "name", defaultValue = "World") String name) {
        return Business.builder().address("Hello world " + name).build();
    }

}
