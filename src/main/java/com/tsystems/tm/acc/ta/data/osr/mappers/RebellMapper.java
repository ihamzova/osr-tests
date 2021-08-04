package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.EndSz;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Endpoint;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Ueweg;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper.nelLsz;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper.nelOrderNumber;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndsz;

public class RebellMapper {

    public List<Ueweg> getUewegList(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
        List<Ueweg> ueWegeList = new ArrayList<>();
        ueWegeList.add(getUeWeg(uewegData, neA, neB));

        return ueWegeList;
    }

    public List<Ueweg> getUewegListMultiple(A4NetworkElement neA, List<UewegData> uewegData, List<A4NetworkElement> neB) {
        List<Ueweg> ueWegeList = new ArrayList<>();
        for(int i = 0; i < uewegData.size(); i++){
            ueWegeList.add(getUeWeg(uewegData.get(i), neA, neB.get(i)));
        }
        return ueWegeList;
    }

    public List<Ueweg> getUewegListEmpty() {
        return new ArrayList<>();
    }

    private Ueweg getUeWeg(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
        String endSzA = getEndsz(neA).replace("/", "_");
        String endSzB = getEndsz(neB).replace("/", "_");

        return new Ueweg()
                .id(1)
                .lsz(nelLsz)
                .lszErg("LszErg")
                .ordNr(nelOrderNumber)
                .pluralId("Plural ID")
                .status("ignored")
                .uewegId(uewegData.getUewegId())
                .validFrom("2020-10-26T18:03:37.598Z")
                .validUntil("2020-10-26T18:03:37.598Z")
                .version("ignored")
                .versionId("Description NEL")
                .endPointA(new Endpoint()
                        .deviceHostName("ignored")
                        .portName(uewegData.getVendorPortNameA())
                        .portPosition("ignored")
                        .vendorPortName("ignored")
                        .endSz(endSzA)
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored")))
                .endPointB(new Endpoint()
                        .deviceHostName("ignored")
                        .portName(uewegData.getVendorPortNameB())
                        .portPosition("ignored")
                        .vendorPortName("ignored")
                        .endSz(endSzB)
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored")));
    }

}
