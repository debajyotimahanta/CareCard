package com.coronacarecard.model;

import com.coronacarecard.model.orders.OrderStatus;

import java.io.Serializable;
import java.util.UUID;

@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode
public class Business implements Serializable {
    private UUID   id;
    private String externalRefId;
    private Double latitude;
    private Double longitude;
    private String name;
    private String address;
    private Photo  photo;
    private String description;
    private String formattedPhoneNumber;
    private String internationalPhoneNumber;
    private String Website;
    private boolean isActive;
    private OrderStatus status;
}