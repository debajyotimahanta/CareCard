package com.coronacarecard.util;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.model.Currency;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.model.orders.OrderLine;
import com.coronacarecard.model.orders.OrderStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TestHelper {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    @Transactional
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
        return ow.writeValueAsString(req);
    }

    public static String getPlainTextPlaceId() {
        return "2";
    }

    public static OrderDetail getOrder(List<String> businessIds) {
        List<OrderLine> line = new ArrayList<>();
        for (String id : businessIds) {

            line.add(OrderLine.builder()
                    .businessId(id.toString())
                    .tip(10.0)
                    .items(getItems())
                    .build());
        }

        return OrderDetail.builder()
                .contribution(100.0)
                .customerEmail("cust@email.com")
                .customerMobile("773")
                .status(OrderStatus.PENDING)
                .processingFee(48.17)
                .contribution(2.5)
                .total(1602.5)
                .orderLine(line)
                .currency(Currency.USD)
                .build();

    }

    private static List<com.coronacarecard.model.orders.Item> getItems() {
        List<com.coronacarecard.model.orders.Item> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(com.coronacarecard.model.orders.Item.builder()
                    .unitPrice(10.0)
                    .quantity(i + 1)
                    .build()
            );
        }
        return items;
    }

}
