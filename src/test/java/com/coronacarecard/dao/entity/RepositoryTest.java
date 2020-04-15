package com.coronacarecard.dao.entity;

import com.coronacarecard.dao.BusinessAccountDetailRepository;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.OrderDetailRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.service.ShoppingCartService;
import com.coronacarecard.util.TestHelper;
import org.junit.Ignore;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTest {

    public static final String INTERNATIONAL_PHONE_NUMBER = "+44 737327272";
    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private BusinessAccountDetailRepository businessAccountDetailRepository;

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
        Business toBeUpdatedBusiness = createdBusiness.get();
        updateUser(email, toBeUpdatedBusiness);

        User result = userRepository.findByEmail(email);
        assertNotNull(result);
        //assertNotNull(result.getBusiness());
        assertNotNull(result.getAccount());
        BusinessAccountDetail accountCreated = businessAccountDetailRepository.findAll().iterator().next();
        assertNotNull(accountCreated);
        Business businessWithAllDetails = businessAccountDetailRepository.findBusiness(createdBusiness.get().getId());
        assertNotNull(businessWithAllDetails.getOwner().getAccount());

    }

    @Transactional
    private void updateUser(String email, Business toBeUpdatedBusiness) {
        businessRepository.save(toBeUpdatedBusiness.toBuilder().owner(
                User.builder()
                        .email(email)
                        .account(BusinessAccountDetail.builder()
                                .externalRefId("ext")
                                .build())
                        .phoneNumber("77777777")
                        .build()
        ).build());
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

    @Test
    public void createCart() throws Exception {
        String idPrefix = "78255b5db1ca027c669ca49e9576d7a26b40f7f";
        String email = "test@test.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .phoneNumber("12345")
                .account(BusinessAccountDetail.builder()
                        .externalRefId("acct_1GVS21ANS82v412Y")
                        .build())
                .build());
        List<UUID> businessIds = new ArrayList<>();
        List<String> businessExternalIds=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Business business = TestHelper.createEntry(businessRepository, INTERNATIONAL_PHONE_NUMBER + i,
                    idPrefix + i, "Food for Friends" + i);
            businessIds.add(business.getId());
            businessRepository.save(business.toBuilder().owner(user).externalRefId("ch"+i).build());
            businessExternalIds.add("ch"+i);
        }

        OrderDetail orders = TestHelper.getOrder(businessExternalIds);
        CheckoutResponse response= shoppingCartService.checkout(PaymentSystem.STRIPE, orders);
        com.coronacarecard.dao.entity.OrderDetail storedOrder =
                orderDetailRepository.findAll().iterator().next();
        assertNotNull(storedOrder);
        assertNotNull(response.getSessionId());
        assertEquals(response.getSessionId(),storedOrder.getSessionId());
        assertEquals(10, storedOrder.getOrderItems().size());
        assertEquals(5, storedOrder.getOrderItems().get(0).getItems().size());
    }


}
