package com.coronacarecard.mapper.impl;

import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.BusinessState;
import com.coronacarecard.model.Photo;
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

import static java.util.UUID.randomUUID;
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
                .id(randomUUID())
                .externalRefId("ABCD1234")
                .name("Winter Wonders Inc.")
                .address("123 Main St, Chicago")
                .formattedPhoneNumber("123-456-7890")
                .internationalPhoneNumber("+1-123-456-7890")
                .Website("www.carecard.org")
                .latitude(Double.parseDouble("12345.6789"))
                .description("desc")
                .longitude(Double.parseDouble("23456.789"))
                .photo(Photo.builder()
                        .photoReference("CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU")
                        .photoUrl("https://lh3.googleusercontent.com/-qXSKhZUMPUs/T_AwIsUMSQI/AAAAAAAAB6o/AFnN5wOBZCg/s1600-h400/googlePhotowalk7.jpg")
                        .build())
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
                () -> assertTrue(result.getDescription().equals(model.getDescription())),
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
                .photo(null)
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
                .id(randomUUID())
                .externalRefId("ABCD1234")
                .name("Winter Wonders Inc.")
                .address("123 Main St, Chicago")
                .formattedPhoneNumber("123-456-7890")
                .internationalPhoneNumber("+1-123-456-7890")
                .description("desc")
                .Website("www.carecard.org")
                .latitude(Double.parseDouble("12345.6789"))
                .longitude(Double.parseDouble("23456.789"))
                .photoReference("CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU")
                .state(BusinessState.Active)
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
                () -> assertTrue(result.getDescription().equals(dao.getDescription())),
                () -> assertTrue(result.getWebsite().equals(dao.getWebsite())),
                () -> assertTrue(result.getFormattedPhoneNumber().equals(dao.getFormattedPhoneNumber())),
                () -> assertTrue(result.getInternationalPhoneNumber().equals(dao.getInternationalPhoneNumber())),
                ()-> assertTrue(result.isActive())
        );
    }

    @Test
    @DisplayName("Test values of PlaceDetails mapped to model")
    public  void checkMappingPlaceDetailsToModel(){
        // Arrange
        LatLng mockLocation = new LatLng(0.0, 0.0);

        Geometry mockGeometry = new Geometry();
        mockGeometry.location = mockLocation;

        com.google.maps.model.Photo mockPhoto = new com.google.maps.model.Photo();
        mockPhoto.width = 12;
        mockPhoto.height = 45;
        mockPhoto.photoReference = "jkdnvkljanfdl";
        mockPhoto.htmlAttributions = new String[] {
                "ljkvandjklfvnadkvfdpjfkm"
        };

        PlaceDetails mockPlace = new PlaceDetails();
        mockPlace.photos = new com.google.maps.model.Photo[] {
                mockPhoto
        };
        mockPlace.geometry = mockGeometry;
        mockPlace.placeId = "17291872918720189";
        mockPlace.name = "Mock Place";
        mockPlace.formattedAddress = "123 Main St.";
        mockPlace.internationalPhoneNumber = "+1 (234) 567-8910";
        mockPlace.formattedPhoneNumber = "+1 (234) 567-8910";
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        Business result = target.toModel(mockPlace);

        // Assert
        assertAll("Value matches",
                () -> assertTrue(result.getPhoto().getHeight() == mockPhoto.height),
                () -> assertTrue(result.getPhoto().getWidth() == mockPhoto.width),
                () -> assertTrue(result.getPhoto().getPhotoReference() == mockPhoto.photoReference),
                () -> assertTrue(result.getPhoto().getPhotoAttributions()[0].equals(mockPhoto.htmlAttributions[0])),
                () -> assertTrue(result.getExternalRefId().equals(mockPlace.placeId)),
                () -> assertTrue(result.getName().equals(mockPlace.name)),
                () -> assertTrue(result.getAddress().equals(mockPlace.formattedAddress)),
                () -> assertTrue(result.getInternationalPhoneNumber().equals(mockPlace.internationalPhoneNumber)),
                () -> assertTrue(result.getFormattedPhoneNumber().equals(mockPlace.formattedPhoneNumber)),
                () -> assertTrue(result.getLatitude() == mockPlace.geometry.location.lat),
                () -> assertTrue(result.getLongitude() == mockPlace.geometry.location.lng)
        );
    }

    @Test
    @DisplayName("Test values of PlaceDetails mapped to model when no photos are available")
    public  void checkMappingPlaceDetailsToModelForNullPhoto(){
        // Arrange
        LatLng mockLocation = new LatLng(0.0, 0.0);

        Geometry mockGeometry = new Geometry();
        mockGeometry.location = mockLocation;

        PlaceDetails mockPlace = new PlaceDetails();
        mockPlace.photos = null;
        mockPlace.geometry = mockGeometry;
        mockPlace.placeId = "17291872918720189";
        mockPlace.name = "Mock Place";
        mockPlace.formattedAddress = "123 Main St.";
        mockPlace.internationalPhoneNumber = "+1 (234) 567-8910";
        mockPlace.formattedPhoneNumber = "+1 (234) 567-8910";
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        Business result = target.toModel(mockPlace);

        // Assert
        assertAll("Value matches",
                () -> assertNull(result.getPhoto())
        );
    }

    @Test
    @DisplayName("Test values of Business DAO mapped to model inactive business")
    public  void checkMappingBusinessDAOToModelInactiveBusiness(){
        // Arrange
        com.coronacarecard.dao.entity.Business dao = com.coronacarecard.dao.entity.Business.builder()
                .id(randomUUID())
                .externalRefId("ABCD1234")
                .name("Winter Wonders Inc.")
                .address("123 Main St, Chicago")
                .formattedPhoneNumber("123-456-7890")
                .internationalPhoneNumber("+1-123-456-7890")
                .description("desc")
                .Website("www.carecard.org")
                .latitude(Double.parseDouble("12345.6789"))
                .longitude(Double.parseDouble("23456.789"))
                .photoReference("CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU")
                .state(BusinessState.Draft)
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
                () -> assertTrue(result.getDescription().equals(dao.getDescription())),
                () -> assertTrue(result.getWebsite().equals(dao.getWebsite())),
                () -> assertTrue(result.getFormattedPhoneNumber().equals(dao.getFormattedPhoneNumber())),
                () -> assertTrue(result.getInternationalPhoneNumber().equals(dao.getInternationalPhoneNumber())),
                ()-> assertTrue(!result.isActive())
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

    @Test
    @DisplayName("Test values of Places Search result mapped to Business Search Result")
    public  void checkMappingPlacesSearchResultToModel(){
        // Arrange
        Geometry geometry = new Geometry();
        geometry.location = new LatLng();
        geometry.location.lat = 12347.212;
        geometry.location.lng = 4892.212;

        PlacesSearchResult dao = new PlacesSearchResult();
        dao.placeId = "PlaceId12345";
        dao.name = "Winter Wonder Inc";
        dao.formattedAddress = "123 Main St, Chicago";
        dao.geometry = geometry;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        BusinessSearchResult result = target.toSearchResult(dao);

        // Assert
        assertAll("Value matches",
                () -> assertTrue(result.getExternalRefId().equals(dao.placeId)),
                () -> assertTrue(result.getName().equals(dao.name)),
                () -> assertTrue(result.getAddress().equals(dao.formattedAddress))
        );
    }

    @Test
    @DisplayName("Test Null value propagation from Place Search Result to Model")
    public  void checkNullValuePropagationFromPlaceSearchResultTModel(){
        // Arrange
        Geometry geometry = new Geometry();
        geometry.location = new LatLng();
        geometry.location.lat = 12347.212;
        geometry.location.lng = 4892.212;

        PlacesSearchResult dao = new PlacesSearchResult();
        dao.placeId = dao.name = dao.formattedAddress = null;
        dao.geometry = geometry;
        BusinessEntityMapperImpl target = new BusinessEntityMapperImpl();

        // Act
        BusinessSearchResult result = target.toSearchResult(dao);

        // Assert
        assertAll("Value matches",
                () -> assertNull(result.getExternalRefId()),
                () -> assertNull(result.getName()),
                () -> assertNull(result.getAddress())
        );
    }
 }