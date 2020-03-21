package com.coronacarecard.dao.entity;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.util.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTest {

    public static final String INTERNATIONAL_PHONE_NUMBER = "+44 737327272";
    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createBusiness() {
        String id = "78255b5db1ca027c669ca49e9576d7a26b40f7f9";
        TestHelper.createEntry(businessRepository, INTERNATIONAL_PHONE_NUMBER, id, "Food for Friends");
        Optional<Business> createdBusiness = businessRepository.findByExternalId(id);
        assertTrue(createdBusiness.isPresent());
        assertNotNull(createdBusiness.get().getId());
        assertEquals(id, createdBusiness.get().getExternalRefId());
        assertEquals(INTERNATIONAL_PHONE_NUMBER, createdBusiness.get().getInternationalPhoneNumber());

        String email = "g@g.com";
        userRepository.save(User.builder()
                .business(createdBusiness.get())
                .email(email)
                .phoneNumber("77777777")
                .build());

        User result = userRepository.findByEmail(email);
        assertNotNull(result);
        assertNotNull(result.getBusiness());
        assertEquals(createdBusiness.get().getExternalRefId(), result.getBusiness().getExternalRefId());

        Optional<Business> afterOwner = businessRepository.findByExternalId(id);
        assertNotNull(afterOwner.get());
    }

    private <T> Specification<T> getEqualSpecification(String key, String value) {

        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(key).as(String.class), value);
            }
        };
    }


    @Test
    public void getPagedResults() {
        String idPreix = "78255b5db1ca027c669ca49e9576d7a26b40f7a";
        for (int i = 0; i < 10; i++) {
            TestHelper.createEntry(businessRepository, INTERNATIONAL_PHONE_NUMBER,
                    idPreix + i, "RandomName" + i);

        }

        for (int i = 10; i < 30; i++) {
            TestHelper.createEntry(businessRepository, INTERNATIONAL_PHONE_NUMBER,
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
