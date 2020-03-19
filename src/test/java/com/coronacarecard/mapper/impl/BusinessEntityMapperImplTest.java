package com.coronacarecard.mapper.impl;

import com.coronacarecard.model.Business;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BusinessEntityMapperImpl.class})
public class BusinessEntityMapperImplTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Test values of Business model mapped to DAO")
    public  void checkMappingBusinessModelToDAO(){
        // Arrange
        Business model = Business.builder()
                .id(1234L)
                .externalRefId("ABCD1234")
                .name("Winter Wonders Inc.")
                .address("123 Main St, Chicago")
                .formattedPhoneNumber("123-456-7890")
                .internationalPhoneNumber("+1-123-456-7890")
                .Website("www.carecard.org")
                .latitude(Double.parseDouble("12345.6789"))
                .longitude(Double.parseDouble("23456.789"))
                .photoUrl("http://profile.photo.io/sam")
                .build();
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        com.coronacarecard.dao.entity.Business result = target.toDAO(model);

        // Assert
        assertAll("Value matches",
                () -> assertTrue(result.getId().equals(model.getId())),
                () -> assertTrue(result.getExternalRefId().equals(model.getExternalRefId())),
                () -> assertTrue(result.getName().equals(model.getName())),
                () -> assertTrue(result.getAddress().equals(model.getAddress())),
                () -> assertTrue(result.getLatitude().equals(model.getLatitude())),
                () -> assertTrue(result.getLongitude().equals(model.getLongitude())),
                () -> assertTrue(result.getWebsite().equals(model.getWebsite())),
                () -> assertTrue(result.getFormattedPhoneNumber().equals(model.getFormattedPhoneNumber())),
                () -> assertTrue(result.getInternationalPhoneNumber().equals(model.getInternationalPhoneNumber()))
        );
    }

    @Test
    @DisplayName("Test Null value propagation from Model to DAO")
    public  void checkNullValuePropagationFromBusinessModelToDAO(){
        // Arrange
        Business model = Business.builder()
                .id(null)
                .name(null)
                .address(null)
                .formattedPhoneNumber(null)
                .internationalPhoneNumber(null)
                .Website(null)
                .latitude(null)
                .longitude(null)
                .photoUrl(null)
                .build();
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        com.coronacarecard.dao.entity.Business result = target.toDAO(model);

        // Assert
        assertAll("Value matches",
                () -> assertNull(result.getId()),
                () -> assertNull(result.getId()),
                () -> assertNull(result.getName()),
                () -> assertNull(result.getAddress()),
                () -> assertNull(result.getLatitude()),
                () -> assertNull(result.getLongitude()),
                () -> assertNull(result.getWebsite()),
                () -> assertNull(result.getFormattedPhoneNumber()),
                () -> assertNull(result.getInternationalPhoneNumber())
        );
    }


    @Test
    @DisplayName("Test values of Business DAO mapped to model")
    public  void checkMappingBusinessDAOToModel(){
        // Arrange
        com.coronacarecard.dao.entity.Business dao = com.coronacarecard.dao.entity.Business.builder()
                .id(1234L)
                .externalRefId("ABCD1234")
                .name("Winter Wonders Inc.")
                .address("123 Main St, Chicago")
                .formattedPhoneNumber("123-456-7890")
                .internationalPhoneNumber("+1-123-456-7890")
                .Website("www.carecard.org")
                .latitude(Double.parseDouble("12345.6789"))
                .longitude(Double.parseDouble("23456.789"))
                .photoUrl("http://profile.photo.io/sam")
                .build();
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        Business result = target.toModel(dao);

        // Assert
        assertAll("Value matches",
                () -> assertTrue(result.getId().equals(dao.getId())),
                () -> assertTrue(result.getExternalRefId().equals(dao.getExternalRefId())),
                () -> assertTrue(result.getName().equals(dao.getName())),
                () -> assertTrue(result.getAddress().equals(dao.getAddress())),
                () -> assertTrue(result.getLatitude().equals(dao.getLatitude())),
                () -> assertTrue(result.getLongitude().equals(dao.getLongitude())),
                () -> assertTrue(result.getWebsite().equals(dao.getWebsite())),
                () -> assertTrue(result.getFormattedPhoneNumber().equals(dao.getFormattedPhoneNumber())),
                () -> assertTrue(result.getInternationalPhoneNumber().equals(dao.getInternationalPhoneNumber()))
        );
    }

    @Test
    @DisplayName("Test Null value propagation from Dao to Model")
    public  void checkNullValuePropagationFromBusinessDAOToModel(){
        // Arrange
        com.coronacarecard.dao.entity.Business dao = com.coronacarecard.dao.entity.Business.builder()
                .id(null)
                .name(null)
                .address(null)
                .formattedPhoneNumber(null)
                .internationalPhoneNumber(null)
                .Website(null)
                .latitude(null)
                .longitude(null)
                .photoUrl(null)
                .build();
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        Business result = target.toModel(dao);

        // Assert
        assertAll("Value matches",
                () -> assertNull(result.getId()),
                () -> assertNull(result.getId()),
                () -> assertNull(result.getName()),
                () -> assertNull(result.getAddress()),
                () -> assertNull(result.getLatitude()),
                () -> assertNull(result.getLongitude()),
                () -> assertNull(result.getWebsite()),
                () -> assertNull(result.getFormattedPhoneNumber()),
                () -> assertNull(result.getInternationalPhoneNumber())
        );
    }
}