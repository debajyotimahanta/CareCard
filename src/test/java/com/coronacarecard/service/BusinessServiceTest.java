package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.notifications.NotificationSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest(properties = {"MASTER_KEY_ID=arn:aws:kms:us-west-1:008731829883:key/a72c4b37-325e-4254-9a9f-38592d01e0b2",
        "spring.app.forntEndBaseUrl=http://base","spring.app.appUrl:http://appbase"})
public class BusinessServiceTest {
    @MockBean
    private CloudStorageService cloudStorageService;

    @MockBean
    private NotificationSender<Business> notificationSender;


    @Autowired
    BusinessService businessService;

    @Before
    public void init() throws InternalException {
        when(cloudStorageService.getObjectUrl(any(), any())).thenReturn("url");
    }

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