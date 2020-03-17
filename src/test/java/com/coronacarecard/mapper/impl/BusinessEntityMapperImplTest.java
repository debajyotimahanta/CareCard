package com.coronacarecard.mapper.impl;

import com.coronacarecard.dao.entity.Contact;
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
                .id("ABCD1234")
                .name("Winter Wonders Inc.")
                .address("123 Main St, Chicago")
                .contact(Contact.builder()
                        .id(Long.parseLong("1234567890"))
                        .formattedPhoneNumber("123-456-7890")
                        .internationalPhoneNumber("+1-123-456-7890")
                        .Website("www.carecard.org").build())
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
                () -> assertTrue(result.getName().equals(model.getName())),
                () -> assertTrue(result.getAddress().equals(model.getAddress())),
                () -> assertTrue(result.getLatitude().equals(model.getLatitude())),
                () -> assertTrue(result.getLongitude().equals(model.getLongitude())),
                () -> assertTrue(result.getContact().getId().equals(model.getContact().getId())),
                () -> assertTrue(result.getContact().getWebsite().equals(model.getContact().getWebsite())),
                () -> assertTrue(result.getContact().getFormattedPhoneNumber().equals(model.getContact().getFormattedPhoneNumber())),
                () -> assertTrue(result.getContact().getInternationalPhoneNumber().equals(model.getContact().getInternationalPhoneNumber()))
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
                .contact(Contact.builder()
                        .id(null)
                        .formattedPhoneNumber(null)
                        .internationalPhoneNumber(null)
                        .Website(null).build())
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
                () -> assertNotNull(result.getContact()),
                () -> assertNull(result.getContact().getId()),
                () -> assertNull(result.getContact().getWebsite()),
                () -> assertNull(result.getContact().getFormattedPhoneNumber()),
                () -> assertNull(result.getContact().getInternationalPhoneNumber())
        );
    }


    @Test
    @DisplayName("Test values of Business DAO mapped to model")
    public  void checkMappingBusinessDAOToModel(){
        // Arrange
        com.coronacarecard.dao.entity.Business dao = com.coronacarecard.dao.entity.Business.builder()
                .id("ABCD1234")
                .name("Winter Wonders Inc.")
                .address("123 Main St, Chicago")
                .contact(Contact.builder()
                        .id(Long.parseLong("1234567890"))
                        .formattedPhoneNumber("123-456-7890")
                        .internationalPhoneNumber("+1-123-456-7890")
                        .Website("www.carecard.org").build())
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
                () -> assertTrue(result.getName().equals(dao.getName())),
                () -> assertTrue(result.getAddress().equals(dao.getAddress())),
                () -> assertTrue(result.getLatitude().equals(dao.getLatitude())),
                () -> assertTrue(result.getLongitude().equals(dao.getLongitude())),
                () -> assertTrue(result.getContact().getId().equals(dao.getContact().getId())),
                () -> assertTrue(result.getContact().getWebsite().equals(dao.getContact().getWebsite())),
                () -> assertTrue(result.getContact().getFormattedPhoneNumber().equals(dao.getContact().getFormattedPhoneNumber())),
                () -> assertTrue(result.getContact().getInternationalPhoneNumber().equals(dao.getContact().getInternationalPhoneNumber()))
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
                .contact(Contact.builder()
                        .id(null)
                        .formattedPhoneNumber(null)
                        .internationalPhoneNumber(null)
                        .Website(null).build())
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
                () -> assertNotNull(result.getContact()),
                () -> assertNull(result.getContact().getId()),
                () -> assertNull(result.getContact().getWebsite()),
                () -> assertNull(result.getContact().getFormattedPhoneNumber()),
                () -> assertNull(result.getContact().getInternationalPhoneNumber())
        );
    }
}