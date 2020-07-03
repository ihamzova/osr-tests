package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {
    private String type;
    private String externalId;
    private String terminationExternalId;
    private String subscriberId;
    private String subscriptionId;
    private String kundennummer;
    private String cplId;
    private String homeId;
    private String toNr;
    private String id;
    private String notificationSubscriberId;
    private Customer customer;
    private Property property;
    private String tanLink;
}
