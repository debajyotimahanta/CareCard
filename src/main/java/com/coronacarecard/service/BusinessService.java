package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.model.Business;

public interface BusinessService {

    Business create(String id) throws BusinessNotFoundException;
}
