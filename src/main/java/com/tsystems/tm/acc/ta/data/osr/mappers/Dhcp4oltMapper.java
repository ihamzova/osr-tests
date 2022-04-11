package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.api.client.dhcp4olt.model.BNG;
import com.tsystems.tm.api.client.dhcp4olt.model.BNGGetResponse;
import com.tsystems.tm.api.client.dhcp4olt.model.OLT;
import com.tsystems.tm.api.client.dhcp4olt.model.OLTGetResponse;

import java.math.BigDecimal;
import java.util.Collections;

public class Dhcp4oltMapper {

    public OLT getOLT(OltDevice oltDevice) {
     return new OLT()
             .id(new BigDecimal("1"))
             .ip("192.168.158.3")
             .bngId(1)
             .typeId(1)
             .endszOlt(oltDevice.getEndsz().replace("/", "_"))
             .endszBng(oltDevice.getBngEndsz().replace("/", "_"))
             .serialnumber(oltDevice.getSeriennummer())
             .type("ADTRAN.SDX.6320");
   }

   public BNG getBNG(OltDevice oltDevice) {
        return new BNG()
                .id(new BigDecimal("1"))
                .endsz(oltDevice.getBngEndsz().replace("/", "_"))
                .ipManagement("192.168.102.102")
                .ipRelay("151.130.120.1")
                .rangeStart("151.130.120.2")
                .rangeEnd("151.130.120.60")
                .subnetMask("255.255.255.192");
   }

   public OLTGetResponse getOLTGetResponse(OltDevice oltDevice) {
        return new OLTGetResponse()
                .page(1)
                .pageCount(1)
                .results(1)
                .resultsPerPage(1)
                .data(Collections.singletonList(getOLT(oltDevice)));
   }

   public BNGGetResponse getBNGGetResponse(OltDevice oltDevice) {
        return new BNGGetResponse()
                .page(1)
                .pageCount(1)
                .results(1)
                .resultsPerPage(1)
                .data(Collections.singletonList(getBNG(oltDevice)));
   }

}
