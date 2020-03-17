package com.coronacarecard.util;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.Contact;

public class RepoUtil {
    public static void createEntry(BusinessRepository businessRepository,
                                   String phoneNumber, String id, String name) {
        businessRepository.save(Business.builder()
                .address("17-18 Prince Albert St, Brighton, United Kingdom")
                .name(name)
                .latitude(50.821282)
                .longitude(-0.140887)
                .id(id)
                .contact(Contact.builder()
                        .formattedPhoneNumber("773732223")
                        .internationalPhoneNumber(phoneNumber)
                        .build())
                .build());
    }
}
