package com.coronacarecard.service;

import com.coronacarecard.config.GoogleConfiguration;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.config.LocalSecretsDataStore;
import com.coronacarecard.mapper.impl.BusinessEntityMapperImpl;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.service.impl.GooglePlaceServiceImpl;
import com.google.maps.ImageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BusinessEntityMapperImpl.class, GooglePlaceServiceImpl.class,
        GoogleConfiguration.class, LocalSecretsDataStore.class})
public class GooglePlaceServiceTest {
    public static final String ID = "ChIJicMwN4lskFQR9brCQh07Xyo";
    @Autowired
    GooglePlaceService service;

    @Test
    public void getBusiness() throws BusinessNotFoundException, InternalException {
        Business business = service.getBusiness(ID);
        assertNotNull(business);
        assertEquals(ID, business.getExternalRefId());
        assertEquals("10680 NE 8th St, Bellevue, WA 98004, USA", business.getAddress());
        assertEquals("What The Pho", business.getName());
        assertEquals("(425) 462-5600", business.getFormattedPhoneNumber());
        assertEquals("+1 425-462-5600", business.getInternationalPhoneNumber());
        assertNull(business.getPhoto().getPhotoUrl());
    }

    @Test
    public void search() throws InternalException {
        List<BusinessSearchResult> result = service.search("What the pho", Optional.empty(), Optional.empty());
        // Commented because search results from different location results in different result depending on machine
        // location
        //        assertEquals(ID, result.get(0).getExternalRefId());

        assertNotNull(result);
    }

    @Test
    public void searchMultiple() throws InternalException {
        List<BusinessSearchResult> result = service.search("pho", Optional.empty(), Optional.empty());
        assertTrue(result.size()> 1);
    }

    @Test
    public void getPhoto() throws InternalException {
        // Arrange
        String photoReference = "CnRvAAAAwMpdHeWlXl-lH0vp7lez4znKPIWSWvgvZFISdKx45AwJVP1Qp37YOrH7sqHMJ8C-vBDC546decipPHchJhHZL94RcTUfPa1jWzo-rSHaTlbNtjh-N68RkcToUCuY9v2HNpo5mziqkir37WU8FJEqVBIQ4k938TI3e7bf8xq-uwDZcxoUbO_ZJzPxremiQurAYzCTwRhE_V0";

        // Act
        ImageResult result = service.getPhoto(photoReference);

        // Assert
        assertNotNull(result);
    }
}