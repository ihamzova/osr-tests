package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.EndSz;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Endpoint;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Ueweg;

import java.util.Collections;
import java.util.List;

public class RebellMapper {
    public List<Ueweg> getUewegList(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
        String endSzA = neA.getVpsz() + "/" + neA.getFsz();
        endSzA = endSzA.replace("/", "_");

        String endSzB = neB.getVpsz() + "/" + neB.getFsz();
        endSzB = endSzB.replace("/", "_");

        String endSzQueryParam = endSzA;

        return Collections.singletonList(new Ueweg()
                .id(1)
                .lsz("LSZ")
                .lszErg("LszErg")
                .ordNr("Order Number")
                .pluralId("Plural ID")
                .status("ignored")
                .uewegId(uewegData.getUewegId())
                .validFrom("ignored")
                .validUntil("ignored")
                .version("ignored")
                .versionId("Description NEL")
                .endPointA(new Endpoint()
                        .deviceHostName("ignored")
                        .portName("ignored")
                        .portPosition("ignored")
                        .vendorPortName(uewegData.getVendorPortNameA())
                        .endSz(endSzA)
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored")))
                .endPointB(new Endpoint()
                        .deviceHostName("ignored")
                        .portName("ignored")
                        .portPosition("ignored")
                        .vendorPortName(uewegData.getVendorPortNameB())
                        .endSz(endSzB)
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored"))));
    }
}
