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
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest
public class BusinessServiceImplTest {

    @Autowired
    BusinessService businessService;

    @Test
    public void create() throws BusinessNotFoundException, InternalException {
        Business result = businessService.getOrCreate("ChIJKV8LiAcPkFQRgaK8WZdjnuY");
        assertEquals("What the Pho!", result.getName());
        assertNotNull(result.getPhoto());
        assertNotNull(result.getPhoto().getPhotoReference());
        assertNotNull(result.getPhoto().getPhotoAttributions());
        assertNotNull(result.getPhoto().getHeight());
        assertNotNull(result.getPhoto().getWidth());
        assertNotNull(result.getPhoto().getPhotoUrl());
    }

    @Test
    public void update() throws BusinessNotFoundException, InternalException {
        Business result = businessService.createOrUpdate("ChIJr1VNXVEUkFQRx8VSwHmQayg");
        assertEquals("Seattle Meowtropolitan", result.getName());
    }
}