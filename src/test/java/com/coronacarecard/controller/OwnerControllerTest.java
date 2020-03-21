package com.coronacarecard.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.model.BusinessRegistrationRequest;
import com.coronacarecard.model.ClaimResult;
import com.coronacarecard.notifications.NotificationSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static com.coronacarecard.util.TestHelper.parseResponse;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class OwnerControllerTest {

    public static final String PLACEID = "ChIJXbraKagPkFQRR5OlYjIcCXI";
    public static final String EMAIL = "t@t.com";
    public static final String PHONE = "7737322612";

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

    @After
    public void cleanup() {
        businessRepository.deleteAll();
        userRepository.deleteAll();

    }
    @Test
    public void claim_valid_business() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/owner/register")
                .content(getRequest(PLACEID, EMAIL, PHONE))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Business businessDAO = businessRepository.findByExternalId(PLACEID).get();
        ClaimResult result = parseResponse(response, ClaimResult.class);
        assertEquals(businessDAO.getId(), result.getBusiness().getId());
        assertEquals(businessDAO.getOwner().getEmail(), EMAIL);
        assertEquals(businessDAO.getOwner().getPhoneNumber(), PHONE);

    }

    private String getRequest(String businessId, String email, String phone) throws JsonProcessingException {
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

    @Test
    public void re_claim_same_business() throws Exception {
        MvcResult response1 = mockMvc.perform(MockMvcRequestBuilders.post("/owner/register")
                .content(getRequest(PLACEID, EMAIL, PHONE))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Optional<Business> businessDAO = businessRepository.findByExternalId(PLACEID);
        assertTrue(businessDAO.isPresent());
        MvcResult response2 = mockMvc.perform(MockMvcRequestBuilders.post("/owner/register")
                .content(getRequest(PLACEID, EMAIL, PHONE))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(response1.getResponse().getContentAsString(),
                response2.getResponse().getContentAsString());
        businessDAO = businessRepository.findByExternalId(PLACEID);
        assertTrue(businessDAO.isPresent());


    }

}