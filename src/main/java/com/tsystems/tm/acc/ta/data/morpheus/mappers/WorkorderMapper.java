package com.tsystems.tm.acc.ta.data.morpheus.mappers;


import com.tsystems.tm.acc.tests.osr.workorder.model.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;

public class WorkorderMapper {

    public Workorder getWorkorder() {
        return new Workorder()
                .id(2L)
                .type("DPU_INSTALLATION")
                .status(Workorder.StatusEnum.CREATED)
                .source(Workorder.SourceEnum.IBT)
                .networkTopology(Workorder.NetworkTopologyEnum.NE4)
                .dueDateTimeBegin(OffsetDateTime.now())
                .dueDateTimeEnd(OffsetDateTime.now())
                .completionDateTimeEnd(OffsetDateTime.now())
                .workforceExternalId("123456ABC")
                .taskDescription("Gf-AP Installation im Hausanschluss-Raum inkl. Netzschalt-Taetigkeiten im NvT")
                .supplierProjectId(10032L)
                .supplierPartyId(new BigDecimal(10001))
                .externalOrderReferenceType(Workorder.ExternalOrderReferenceTypeEnum.CUSTOMER_INSTALLATION_ORDER)
                .externalOrderReferenceId("10122")
                .comment("Bitte beim Nachbarn klingeln falls nicht anwesend.")
                .klsId(new BigDecimal(123456789))
                .folId("123456789")
                .gigaAreaNumber("GigaArea_001")
                .contactPersons(Collections.singletonList(getContactPerson()))
                .creationDate(OffsetDateTime.now())
                .workCharacteristic(Collections.singletonList(getWorkCharacteristic()));

    }

    public ContactPerson getContactPerson(){
        return new ContactPerson()
                .salutation(ContactPerson.SalutationEnum.MR)
                .title("Dr.")
                .role("Boss")
                .givenName("Max")
                .familyName("Mustermann")
                .companyName("Monsters corporation")
                .customer(true)
                .propertyContact(true)
                .primaryContact(true)
                .contactMedium(getContactMedium());

    }

    public ContactMedium getContactMedium(){
        return new ContactMedium()
                .emailAddress("Max.Mustermann@t-online.de")
                .fixedLineNumber("+49000000000")
                .mobileNumber("+4900000000")
                .messengerNumber("+4900000000")
                .preferredContactMedium(ContactMedium.PreferredContactMediumEnum.MOBILE_PHONE);
    }

    public WorkCharacteristic getWorkCharacteristic(){
        return new WorkCharacteristic()
                .name("ConnectionType")
                .valueType("boolean")
                .value("FiberAccessHome");
    }

    public PagingInfo getPagingInfo(){
        return new PagingInfo();
    }


    public PagedWorkorderList getPagedWorkorderList() {

        return new PagedWorkorderList()
                .paging(getPagingInfo())
                .workorders(Arrays.asList(getWorkorder()));

    }

}
