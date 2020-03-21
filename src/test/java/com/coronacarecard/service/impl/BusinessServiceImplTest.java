package com.coronacarecard.service.impl;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.service.BusinessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest
public class BusinessServiceImplTest {

    @Autowired
    BusinessService businessService;

    @Test
    public void create() throws BusinessNotFoundException, InternalException {
        Business result = businessService.create("ChIJKV8LiAcPkFQRgaK8WZdjnuY");
        assertEquals("What the Pho!", result.getName());
    }
}