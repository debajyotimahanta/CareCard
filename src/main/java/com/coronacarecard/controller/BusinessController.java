package com.coronacarecard.controller;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.coronacarecard.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("business")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @GetMapping("/import")
    public Business importFromGoogle(@RequestParam(value = "googleplaceid") String googlePlaceId)
            throws BusinessNotFoundException, InternalException {
        return businessService.getOrCreate(googlePlaceId);
    }

    @RequestMapping(value = "/{externalId}", method = RequestMethod.GET)
    public Business getBusiness(@PathVariable String externalId) throws
            BusinessNotFoundException, InternalException {
        return businessService.getOrCreate(externalId);
    }

    @GetMapping("/update")
    public Business updateFromGoogle(@RequestParam(value = "googleplaceid") String googlePlaceId)
            throws BusinessNotFoundException, InternalException {
        return businessService.createOrUpdate(googlePlaceId);
    }

    @GetMapping("/searchexternal")
    public List<BusinessSearchResult> searchExternal(@RequestParam(value = "searchtext") String searchText,
                                             @RequestParam(value = "latitude") Optional<Double> latitude,
                                             @RequestParam(value = "longitude") Optional<Double> longitude)
            throws InternalException {

        return businessService.externalSearch(searchText,
                latitude,
                longitude);
    }

    @GetMapping("/search")
    public PagedBusinessSearchResult search(@RequestParam(value = "searchtext") String searchText,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page ,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count) {
        return businessService.search(searchText, page, count);
    }

}
