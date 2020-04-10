package com.coronacarecard.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.model.ClaimResult;
import com.coronacarecard.notifications.NotificationSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static com.coronacarecard.util.TestHelper.getBusinessRegistrationRequestJson;
import static com.coronacarecard.util.TestHelper.parseResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class OwnerControllerTest {

    private static final String EXTERNALPLACEID = "ChIJXbraKagPkFQRR5OlYjIcCXI";
    private static final String EMAIL           = "t@t.com";
    private static final String PHONE           = "7737322612";

    @MockBean
    private NotificationSender<com.coronacarecard.model.Business> notificationSender;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerController ownerController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @After
    public void cleanup() {
        businessRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    public void claim_valid_business() throws Exception {
        MvcResult response = mockMvc.perform(post("/owner/claim")
                .content(getBusinessRegistrationRequestJson(EXTERNALPLACEID, EMAIL, PHONE))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Business    businessDAO = businessRepository.findByExternalId(EXTERNALPLACEID).get();
        ClaimResult result      = parseResponse(response, ClaimResult.class);
        assertEquals(businessDAO.getId(), result.getBusiness().getId());
        assertEquals(businessDAO.getOwner().getEmail(), EMAIL);
        assertEquals(businessDAO.getOwner().getPhoneNumber(), PHONE);

    }

    @Test
    public void re_claim_same_business() throws Exception {
        MvcResult response1 = mockMvc.perform(post("/owner/claim")
                .content(getBusinessRegistrationRequestJson(EXTERNALPLACEID, EMAIL, PHONE))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Optional<Business> businessDAO = businessRepository.findByExternalId(EXTERNALPLACEID);
        assertTrue(businessDAO.isPresent());
        MvcResult response2 = mockMvc.perform(post("/owner/claim")
                .content(getBusinessRegistrationRequestJson(EXTERNALPLACEID, EMAIL, PHONE))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
//        assertTrue(response1.getResponse().getContentAsString().contains("\"id\":2"));
//        assertTrue(response2.getResponse().getContentAsString().contains("\"id\":2"));
        businessDAO = businessRepository.findByExternalId(EXTERNALPLACEID);
        assertTrue(businessDAO.isPresent());
    }


    @Test
    public void validate_businessId_should_not_be_null_or_empty() throws Exception {
        BusinessRegistrationRequest registrationRequest = BusinessRegistrationRequest.builder()
                .businessId("")
                .build();

        String content = objectMapper.writeValueAsString(registrationRequest);

        MvcResult result = mockMvc.perform(post("/owner/claim")
                .contentType("application/json").content(content))
        .andExpect(status().isBadRequest())
        .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));

        registrationRequest = BusinessRegistrationRequest.builder()
                .businessId("")
                .build();

        content = objectMapper.writeValueAsString(registrationRequest);

        result = mockMvc.perform(post("/owner/claim")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty")
                || result.getResponse().getContentAsString().contains("must not be null"));
    }

    @Test
    public void validate_email_should_not_be_null_or_empty() throws Exception {
        BusinessRegistrationRequest registrationRequest = BusinessRegistrationRequest.builder()
                .businessId("dljkfbaljkbd")
                .email("")
                .build();

        String content = objectMapper.writeValueAsString(registrationRequest);

        MvcResult result = mockMvc.perform(post("/owner/claim")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));

        registrationRequest = BusinessRegistrationRequest.builder()
                .businessId("dljkfbaljkbd")
                .email(null)
                .build();

        content = objectMapper.writeValueAsString(registrationRequest);

        result = mockMvc.perform(post("/owner/claim")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty")
        || result.getResponse().getContentAsString().contains("must not be null"));

    }

    @Test
    public void validate_email_should_not_be_wellformed() throws Exception {
        // Arrange
        BusinessRegistrationRequest registrationRequest = BusinessRegistrationRequest.builder()
                .businessId("djkbnlajkdf")
                .email("bad_email")
                .build();

        String content = objectMapper.writeValueAsString(registrationRequest);

        MvcResult result = mockMvc.perform(post("/owner/claim")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must be a well-formed email address"));
    }
}