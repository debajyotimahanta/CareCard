package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.model.Business;

public interface GooglePlaceService {

    Business getBusiness(String id) throws BusinessNotFoundException;
}
