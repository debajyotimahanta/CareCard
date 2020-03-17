package com.coronacarecard.dao.entity;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.util.RepoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTest {

    public static final String INTERNATIONAL_PHONE_NUMBER = "+44 737327272";
    @Autowired
    private BusinessRepository businessRepository;

    @Test
    public void createBusiness() {
        String id = "78255b5db1ca027c669ca49e9576d7a26b40f7f9";
        RepoUtil.createEntry(businessRepository,  INTERNATIONAL_PHONE_NUMBER,id, "Food for Friends");
        Optional<Business> createdBusiness = businessRepository.findById(id);
        assertTrue(createdBusiness.isPresent());
        assertEquals(INTERNATIONAL_PHONE_NUMBER, createdBusiness.get().getInternationalPhoneNumber());

    }

    @Test
    public void getPagedResults() {
        String idPreix = "78255b5db1ca027c669ca49e9576d7a26b40f7a";
        for (int i = 0; i < 10; i++) {
            RepoUtil.createEntry(businessRepository,  INTERNATIONAL_PHONE_NUMBER,
                    idPreix + i, "RandomName" + i);

        }

        for (int i = 10; i < 30; i++) {
            RepoUtil.createEntry(businessRepository,  INTERNATIONAL_PHONE_NUMBER,
                    idPreix + i, "Business number " + i);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(1, 5, sort);
        Page<Business> result1 = businessRepository.findByName("RandomName", page);
        assertEquals(2, result1.getTotalPages());
        assertEquals(5, result1.getNumberOfElements());

        Page<Business> result2 = businessRepository.findByName("Business number", page);
        assertEquals(4, result2.getTotalPages());
        assertEquals(5, result2.getNumberOfElements());

    }


}
