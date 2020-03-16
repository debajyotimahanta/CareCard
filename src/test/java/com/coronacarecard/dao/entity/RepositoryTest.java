package com.coronacarecard.dao.entity;

import com.coronacarecard.dao.BusinessRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTest {

    @Autowired
    private BusinessRepository businessRepository;

    @Test
    public void createBusiness() {
        String id = "78255b5db1ca027c669ca49e9576d7a26b40f7f9";
        String formattedPhoneNumber = "773732223";
        businessRepository.save(Business.builder()
                .address("17-18 Prince Albert St, Brighton, United Kingdom")
                .name("Food for Friends")
                .latitude(50.821282)
                .longitude( -0.140887)
                .id(id)
                .contact(Contact.builder()
                        .formattedPhoneNumber(formattedPhoneNumber)
                        .internationalPhoneNumber("+44 737327272")
                        .build())
                .build());
        Optional<Business> createdBusiness = businessRepository.findById(id);
        assertTrue(createdBusiness.isPresent());
        Contact contact = createdBusiness.get().getContact();
        assertEquals(formattedPhoneNumber, contact.getFormattedPhoneNumber());

    }


}
