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
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

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

    public static String getEncryptedPlaceId() {
        return "AYADeIXVciaTokbIm/lgZShUQUUAcQACAAZAcnR2YWwACCNwb3N0dmFyABVhd3MtY3J5cHRvLXB1YmxpYy1rZXkAREFzRnVTbTNTa09iSVdDT1ZybUlmTW1vczUrU0U0MmtQM09sSm9NQ2RZbTRSVm0wWGU3Qi9XZWxJNm4zcysyZU5ndz09AAEAB2F3cy1rbXMAS2Fybjphd3M6a21zOnVzLXdlc3QtMTowMDg3MzE4Mjk4ODM6a2V5L2E3MmM0YjM3LTMyNWUtNDI1NC05YTlmLTM4NTkyZDAxZTBiMgC4AQIBAHh0UzEv7yUk26LYZnWVPK4O+FnSzA+3LVt5Pv0y2mD5fwGS5GANwK0XsFwr1PS0yIHDAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMniXRk6Y5kKstsGINAgEQgDtkySdaHk/f+pv0AuqOZkjZYCZ65u9sumH2rJ5DlBY0EF022IdQ33SxBu8azbvPplRgnUBinnN1kClkiAIAAAAADAAAEAAAAAAAAAAAAAAAAACRTNFMT6Zuqx0M0+JBOyH1/////wAAAAEAAAAAAAAAAAAAAAEAAAABBx5NKtbH4N45qqVsv2epaQ0AZzBlAjBPHvYaqn/A4vhtloDptTYjQAKVNR79pLou9d3ocscHgAkGl0uBAQXBTaMplsFWnd0CMQCcSTvy4Ghy007RfaJvlhlPoKZ/Wox2Sec5/mlauVzY0I3jYoDP/3e3GS5YEKKKjRc=";
    }

    public static String getPlainTextPlaceId() {
        return "2";
    }

}
