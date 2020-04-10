package com.coronacarecard.controller;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.coronacarecard.service.CloudStorageService;
import com.coronacarecard.util.TestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static com.coronacarecard.util.TestHelper.parseResponse;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class BusinessControllerTest {
    private static int seed = 0;

    @Autowired
    private BusinessRepository businessRepository;

    @MockBean
    private CloudStorageService cloudStorageService;

    @Autowired
    private BusinessController businessController;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void init() {
        if (seed == 0) {
            String idPrefix = "78255b5db1ca027c669ca49e9576d7a26b40f7a";
            for (int i = 0; i < 10; i++) {
                TestHelper.createEntry(businessRepository, "773773773",
                        idPrefix + i, "RandomName" + i);

            }
            seed++;
        }
    }

    @Test
    public void search_for_existing() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/business/search?count=5&page=1&searchtext=RandomName")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        PagedBusinessSearchResult result = parseResponse(response, PagedBusinessSearchResult.class);
        assertEquals(2, result.getTotalPages().intValue());
    }

    @Test
    public void search_for_non_existing() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/business/search?count=5&page=1&searchtext=what the pho")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        PagedBusinessSearchResult result = parseResponse(response, PagedBusinessSearchResult.class);
        assertEquals(0, result.getTotalPages().intValue());
        assertEquals(0, result.getItems().size());

    }

    @Test
    public void search_for_external_system() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/business/searchexternal?searchtext=What the Pho! 98021")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        BusinessSearchResult[] result = parseResponse(response, BusinessSearchResult[].class);
        assertEquals("What the Pho!", result[0].getName());
        assertEquals("ChIJKV8LiAcPkFQRgaK8WZdjnuY", result[0].getExternalRefId());

    }

    @Test
    public void import_for_external_system() throws Exception {
        final String whatThePhoId = "ChIJicMwN4lskFQR9brCQh07Xyo";
        Optional<Business> beforeImport = businessRepository.findByExternalId(whatThePhoId);
        assertFalse(beforeImport.isPresent());
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/business/import?googleplaceid=" + whatThePhoId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Optional<Business> afterImport = businessRepository.findByExternalId(whatThePhoId);
        assertTrue(afterImport.isPresent());
        assertEquals("10680 NE 8th St, Bellevue, WA 98004, USA", afterImport.get().getAddress());
    }

    @Test
    public void import_existing_business() throws Exception {
        final String bambooVillageId = "ChIJYeSlblAUkFQRTxEWGp0HG-k";
        mockMvc.perform(MockMvcRequestBuilders.get("/business/import?googleplaceid=" + bambooVillageId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Optional<Business> initialImport = businessRepository.findByExternalId(bambooVillageId);
        assertTrue(initialImport.isPresent());
        Business importedBusiness = initialImport.get();
        assertEquals("4900 Stone Way N, Seattle, WA 98103, USA", importedBusiness.getAddress());
        importedBusiness = importedBusiness.toBuilder().address("DUMMY").build();
        businessRepository.save(importedBusiness);
        assertEquals("DUMMY", importedBusiness.getAddress());
        mockMvc.perform(MockMvcRequestBuilders.get("/business/import?googleplaceid=" + bambooVillageId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Optional<Business> afterImport = businessRepository.findByExternalId(bambooVillageId);
        assertTrue(afterImport.isPresent());
        assertEquals("DUMMY", afterImport.get().getAddress());
    }

    @Test
    public void update_existing_business() throws Exception {
        final String meowtropolitanId = "ChIJr1VNXVEUkFQRx8VSwHmQayg";
        mockMvc.perform(MockMvcRequestBuilders.get("/business/import?googleplaceid=" + meowtropolitanId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Optional<Business> initialImport = businessRepository.findByExternalId(meowtropolitanId);
        assertTrue(initialImport.isPresent());
        Business importedBusiness = businessRepository.findById(initialImport.get().getId()).get();
        assertEquals("1225 N 45th St, Seattle, WA 98103, USA", importedBusiness.getAddress());
        importedBusiness = importedBusiness.toBuilder().address("DUMMY").build();
        businessRepository.save(importedBusiness);
        assertEquals("DUMMY", importedBusiness.getAddress());
        mockMvc.perform(MockMvcRequestBuilders.get("/business/update?googleplaceid=" + meowtropolitanId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Optional<Business> afterImport = businessRepository.findByExternalId(meowtropolitanId);
        assertTrue(afterImport.isPresent());
        assertEquals("1225 N 45th St, Seattle, WA 98103, USA", afterImport.get().getAddress());
    }

    @Test
    public void get_business_with_external_id() throws Exception {
        Business existingBusiness = businessRepository.findAll(PageRequest.of(1, 5)).get().findFirst().get();
        MvcResult response = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/business/" + existingBusiness.getExternalRefId())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Business result = parseResponse(response, Business.class);

        assertEquals(existingBusiness.getId(), result.getId());
        assertEquals(existingBusiness.getExternalRefId(), result.getExternalRefId());

    }

    @Test
    public void get_business_with_non_existent_id() throws Exception {
        final String mongolian_grid_id="ChIJeSVts2QSkFQRyse0d8pWNa0";
        MvcResult response= mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/business/"+mongolian_grid_id )
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        com.coronacarecard.model.Business result=parseResponse(response, com.coronacarecard.model.Business.class);
        assertTrue(result!=null);
        assertEquals(result.getExternalRefId(),mongolian_grid_id);
        assertTrue(!result.isActive());

    }

    @Test
    public void validate_bad_request_if_search_text_isNullOrEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/business/search")
        .param("searchtext", "")
        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));
    }


    @Test
    public void validate_bad_request_if_searchexternal_text_isNullOrEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/business/searchexternal")
                .param("searchtext", "")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));
    }

    @Test
    public void validate_bad_request_if_update_param_isNullOrEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/business/update")
                .param("googleplaceid", "")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));
    }

    @Test
    public void validate_bad_request_if_import_param_isNullOrEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/business/import")
                .param("googleplaceid", "")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));
    }
}