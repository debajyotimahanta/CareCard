package com.coronacarecard.controller;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.coronacarecard.service.BusinessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("business")
@Validated
public class BusinessController {

    private static final Logger log = LogManager.getLogger(BusinessController.class);
    @Autowired
    private        BusinessService businessService;

    @GetMapping("/import")
    public Business importFromGoogle(@NotEmpty @NotNull @RequestParam(value = "googleplaceid") String googlePlaceId)
            throws BusinessNotFoundException, InternalException {
        return businessService.getOrCreate(googlePlaceId);
    }

    @RequestMapping(value = "/{externalId}", method = RequestMethod.GET)
    public Business getBusiness(@PathVariable String externalId) throws
            BusinessNotFoundException, InternalException {
        return businessService.getOrCreate(externalId);
    }

    @GetMapping("/update")
    public Business updateFromGoogle(@NotEmpty @NotNull @RequestParam(value = "googleplaceid") String googlePlaceId)
            throws BusinessNotFoundException, InternalException {
        return businessService.createOrUpdate(googlePlaceId);
    }

    @GetMapping("/searchexternal")
    public List<BusinessSearchResult> searchExternal(@NotNull @NotEmpty @RequestParam(value = "searchtext") String searchText,
                                                     @RequestParam(value = "latitude") Optional<Double> latitude,
                                                     @RequestParam(value = "longitude") Optional<Double> longitude)
            throws InternalException {
        log.info("Search for business with search text : " + searchText);
        return businessService.externalSearch(searchText,
                latitude,
                longitude);
    }

    @GetMapping("/search")
    public PagedBusinessSearchResult search(@NotNull @NotEmpty @RequestParam(value = "searchtext") String searchText,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count) {
        return businessService.search(searchText, page, count);
    }

}
