package com.coronacarecard.util;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

public class TestHelper {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    public static Business createEntry(BusinessRepository businessRepository,
                                       String phoneNumber, String id, String name) {
        return businessRepository.save(Business.builder()
                .address("17-18 Prince Albert St, Brighton, United Kingdom")
                .name(name)
                .latitude(50.821282)
                .longitude(-0.140887)
                .externalRefId(id)
                .formattedPhoneNumber("773732223")
                .internationalPhoneNumber(phoneNumber)
                .build());
    }

    public static <T> T parseResponse(MvcResult result, Class<T> responseClass)
            throws UnsupportedEncodingException, JsonProcessingException {
        String contentAsString = result.getResponse().getContentAsString();
        return MAPPER.readValue(contentAsString, responseClass);
    }

    public static String getBusinessRegistrationRequestJson(String businessId, String email, String phone) throws JsonProcessingException {
        BusinessRegistrationRequest req = BusinessRegistrationRequest.builder()
                .businessId(businessId)
                .email(email)
                .phone(phone)
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(req );
    }
}
