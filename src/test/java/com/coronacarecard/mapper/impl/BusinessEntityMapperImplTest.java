package com.coronacarecard.mapper.impl;

import com.coronacarecard.dao.entity.Contact;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public void checkMappingBusinessModelToDAO() {
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
    public void checkNullValuePropagationFromBusinessModelToDAO() {
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
    public void checkMappingBusinessDAOToModel() {
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
    public void checkNullValuePropagationFromBusinessDAOToModel() {
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
    @DisplayName("Test values of Places Search result mapped to Business Search Result")
    public void checkMappingPlacesSearchResultToModel() {
        // Arrange
        PlacesSearchResult dao = new PlacesSearchResult();
        dao.placeId = "PlaceId12345";
        dao.name = "Winter Wonder Inc";
        dao.formattedAddress = "123 Main St, Chicago";
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        BusinessSearchResult result = target.toSearchResult(dao);

        // Assert
        assertAll("Value matches",
                () -> assertTrue(result.getId().equals(dao.placeId)),
                () -> assertTrue(result.getName().equals(dao.name)),
                () -> assertTrue(result.getAddress().equals(dao.formattedAddress))
        );
    }

    @Test
    @DisplayName("Test Null value propagation from Place Search Result to Model")
    public void checkNullValuePropagationFromPlaceSearchResultTModel() {
        // Arrange
        PlacesSearchResult dao = new PlacesSearchResult();
        dao.placeId = dao.name = dao.formattedAddress = null;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        BusinessSearchResult result = target.toSearchResult(dao);

        // Assert
        assertAll("Value matches",
                () -> assertNull(result.getId()),
                () -> assertNull(result.getName()),
                () -> assertNull(result.getAddress())
        );
    }

    @Test
    @DisplayName("Test values of Places Details mapped to Business")
    public void checkMappingPlacesDetailsToModel() {
        // Arrange
        LatLng location = new LatLng();
        location.lat = 12345.678;
        location.lng = 345.987;
        Geometry geo = new Geometry();
        geo.location = location;

        PlaceDetails dao = new PlaceDetails();
        dao.placeId = "PlaceId12345";
        dao.name = "Winter Wonder Inc.";
        dao.formattedAddress = "123 Main St, Chicago";
        dao.geometry = geo;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        Business result = target.toModel(dao);

        // Assert
        assertAll("Value matches",
                () -> assertTrue(result.getId().equals(dao.placeId)),
                () -> assertTrue(result.getName().equals(dao.name)),
                () -> assertTrue(result.getAddress().equals(dao.formattedAddress)),
                () -> assertTrue(result.getLatitude().equals(dao.geometry.location.lat)),
                () -> assertTrue(result.getLongitude().equals(dao.geometry.location.lng))
        );
    }

    @Test
    @DisplayName("Test Null value propagation from Places Details mapped to Business")
    public void checkNullValuePropagationFromPlaceDetailsToModel() {
        // Arrange
        LatLng location = new LatLng();
        location.lat = location.lng = Double.NaN;
        Geometry geo = new Geometry();
        geo.location = location;

        PlaceDetails dao = new PlaceDetails();
        dao.placeId = dao.name = dao.formattedAddress = null;
        dao.geometry = geo;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        Business result = target.toModel(dao);

        // Assert
        assertAll("Value matches",
                () -> assertNull(result.getId()),
                () -> assertNull(result.getName()),
                () -> assertNull(result.getAddress()),
                () -> assertTrue(Double.isNaN(result.getLatitude())),
                () -> assertTrue(Double.isNaN(result.getLongitude()))
        );
    }

    @Test
    @DisplayName("Test values of Places Details mapped to Business")
    public void checkMappingToPagedSearchResult() {
        // Arrange
        List<BusinessSearchResult> businessSearchResults = Stream.generate(BusinessSearchResult::new)
                .limit(7)
                .collect(Collectors.toList());
        int pageNumber = 3;
        int pageSize = 40;
        int totalPages = 34;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        PagedBusinessSearchResult result = target.toPagedSearchResult(businessSearchResults, pageNumber, pageSize, totalPages);

        // Assert
        assertAll("Value matches",
                () -> assertTrue(result.getItems().size() == businessSearchResults.size()),
                () -> assertTrue(result.getPageNumber() == pageNumber),
                () -> assertTrue(result.getPageSize() == pageSize),
                () -> assertTrue(result.getTotalPages() == totalPages)
        );
    }

    @Test
    @DisplayName("Test Null value propagation from Places Details mapped to Business")
    public void checkNullValuePropagationFromBusinessResultsToPagedSearchDetails() {
        // Arrange
        List<BusinessSearchResult> businessSearchResults = null;
        int pageNumber = 3;
        int pageSize = 40;
        int totalPages = 34;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        PagedBusinessSearchResult result = target.toPagedSearchResult(businessSearchResults, pageNumber, pageSize, totalPages);

        // Assert
        assertAll("Value matches",
                () -> assertNull(result.getItems()),
                () -> assertTrue(result.getPageNumber() == pageNumber),
                () -> assertTrue(result.getPageSize() == pageSize),
                () -> assertTrue(result.getTotalPages() == totalPages)
        );
    }

    @Test()
    @DisplayName("Test Paged Business Search Result doesn't accept pageNumber less than 0")
    public void checkPagedResultDoesNotSupportPageNumberValueLessThanZero() {
        // Arrange
        List<BusinessSearchResult> businessSearchResults = null;
        int pageNumber = -1;
        int pageSize = 40;
        int totalPages = 34;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act and Assert
        assertThrows(InternalException.class, () -> target.toPagedSearchResult(businessSearchResults, pageNumber, pageSize, totalPages));
    }

    @Test()
    @DisplayName("Test Paged Business Search Result pageNumber value not greater than totalPages")
    public void checkPagedResultDoesNotSupportPageNumberValueLessThanTotalPages() {
        // Arrange
        List<BusinessSearchResult> businessSearchResults = null;
        int pageNumber = 10;
        int pageSize = 40;
        int totalPages = 5;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act and Assert
        assertThrows(InternalException.class, () -> target.toPagedSearchResult(businessSearchResults, pageNumber, pageSize, totalPages));
    }

    @Test
    @DisplayName("Test values of Business Dao mapped to Business Search Result")
    public void checkMappingBusinessItemToSearchResult() {
        // Arrange
        com.coronacarecard.dao.entity.Business dao = com.coronacarecard.dao.entity.Business.builder()
                .id("ITEM12345")
                .name("Winter Wonder Inc.")
                .address("123 Main St, Chicago")
                .build();


        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        BusinessSearchResult result = target.toSearchResult(dao);

        // Assert
        assertAll("Value matches",
                () -> assertTrue(result.getId().equals(dao.getId())),
                () -> assertTrue(result.getName().equals(dao.getName())),
                () -> assertTrue(result.getAddress().equals(dao.getAddress()))
        );
    }

    @Test
    @DisplayName("Test Null value propagation from Business Dao mapped to Business Search Result")
    public void checkNullValuePropagationFromBusinessItemToSearchResult() {
        // Arrange
        com.coronacarecard.dao.entity.Business dao = com.coronacarecard.dao.entity.Business.builder()
                .id(null)
                .name(null)
                .address(null)
                .build();


        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        BusinessSearchResult result = target.toSearchResult(dao);

        // Assert
        assertAll("Value matches",
                () -> assertNull(result.getId()),
                () -> assertNull(result.getName()),
                () -> assertNull(result.getAddress())
        );
    }
}