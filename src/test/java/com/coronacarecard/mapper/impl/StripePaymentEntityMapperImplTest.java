package com.coronacarecard.mapper.impl;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.OrderDetailRepository;
import com.coronacarecard.dao.UserRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.BusinessAccountDetail;
import com.coronacarecard.dao.entity.User;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PaymentAccountNotSetupException;
import com.coronacarecard.mapper.OrderDetailMapper;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.model.orders.OrderLine;
import com.coronacarecard.service.impl.ShoppingCartServiceImpl;
import com.coronacarecard.util.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StripePaymentEntityMapperImplTest {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Test
    public void toOrder() throws InternalException, PaymentAccountNotSetupException, BusinessNotFoundException {
        String idPrefix = "78255b5db1ca027c669ca49e9576d7a26b40f7f";
        String email = "test@test.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .phoneNumber("12345")
                .account(BusinessAccountDetail.builder()
                        .externalRefId("acct_1GSRdxIsoQ5ULXuu")
                        .build())
                .build());
        List<UUID> businessIds = new ArrayList<>();
        List<String> businessExternalIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Business business = TestHelper.createEntry(businessRepository, "773732261" + i,
                    idPrefix + i, "Food for Friends" + i);
            businessIds.add(business.getId());
            businessRepository.save(business.toBuilder().owner(user).externalRefId("ch" + i).build());
            businessExternalIds.add("ch" + i);
        }

        OrderDetail orders = TestHelper.getOrder(businessExternalIds);
        shoppingCartService.saveOrder(orders);

        com.coronacarecard.dao.entity.OrderDetail savedOrder = orderDetailRepository.findAll().iterator().next();

        OrderDetail mappedOrder = orderDetailMapper.toOrder(savedOrder);
        orders.setId(mappedOrder.getId());
        List<OrderLine> orderLines = mappedOrder.getOrderLine();
        List<OrderLine> newOrderLines = new ArrayList<>();
        for(OrderLine line: orderLines) {
            newOrderLines.add(OrderLine.builder()
                    .tip(line.getTip())
                    .businessId(line.getBusinessId())
                    .businessName("Food for Friends"+ line.getBusinessId().replace("ch",""))
                    .items(line.getItems())
                    .build());
        }
        orders.setOrderLine(newOrderLines);
        assertEquals(orders, mappedOrder);

    }
}