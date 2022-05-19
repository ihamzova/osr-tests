package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementLinkDto;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.EndSz;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Endpoint;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Ueweg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper.NEL_LSZ;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper.NEL_ORDER_NUMBER;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndsz;

public class RebellMapper {

    private static final String IGNORED = "ignored";

    public List<Ueweg> getUewegList(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
        List<Ueweg> ueWegeList = new ArrayList<>();
        ueWegeList.add(getUeWeg(uewegData, neA, neB));

        return ueWegeList;
    }

    public List<Ueweg> getUewegListMultiple(A4NetworkElement neA, List<UewegData> uewegData, List<A4NetworkElement> neB) {
        List<Ueweg> ueWegeList = new ArrayList<>();
        for (int i = 0; i < uewegData.size(); i++) {
            ueWegeList.add(getUeWeg(uewegData.get(i), neA, neB.get(i)));
        }
        return ueWegeList;
    }

    public List<Ueweg> getUewegListEmpty() {
        return new ArrayList<>();
    }

    private Ueweg getUeWeg(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
        final String endSzA = getEndsz(neA).replace("/", "_");
        final String endSzB = getEndsz(neB).replace("/", "_");

        Ueweg ueweg = getDefaultUeweg();
        ueweg.setUewegId(uewegData.getUewegId());
        ueweg.getEndPointA().setEndSz(endSzA);
        ueweg.getEndPointA().setVendorPortName(uewegData.getVendorPortNameA());
        ueweg.getEndPointB().setEndSz(endSzB);
        ueweg.getEndPointB().setVendorPortName(uewegData.getVendorPortNameB());

        return ueweg;
    }

    public Ueweg getUewegByNel(String endszA, String endszB, NetworkElementLinkDto nel, String vendorPortNameA, String vendorPortNameB) {
        Ueweg ueweg = getDefaultUeweg();
        ueweg.setLsz(nel.getLsz());
        ueweg.setOrdNr(nel.getOrderNumber());
        ueweg.getEndPointA().setEndSz(endszA.replace("/", "_"));
        ueweg.getEndPointA().setVendorPortName(vendorPortNameA);
        ueweg.getEndPointB().setEndSz(endszB.replace("/", "_"));
        ueweg.getEndPointB().setVendorPortName(vendorPortNameB);

        return ueweg;
    }

    private Ueweg getDefaultUeweg() {
        return new Ueweg()
                .id(1)
                .lsz(NEL_LSZ)
                .lszErg("LszErg")
                .ordNr(NEL_ORDER_NUMBER)
                .pluralId("Plural ID")
                .status(IGNORED)
                .uewegId(UUID.randomUUID().toString())
                .validFrom("2020-10-26T18:03:37.598Z")
                .validUntil("2020-10-26T18:03:37.598Z")
                .version(IGNORED)
                .versionId("Description NEL")
                .endPointA(new Endpoint()
                        .deviceHostName(IGNORED)
                        .portName(null) // fill with VendorPortNameA
                        .portPosition(IGNORED)
                        .vendorPortName(IGNORED)
                        .endSz(null) // fill with endszA (_ instead of /)
                        .endSzParts(new EndSz()
                                .akz(IGNORED)
                                .fsz(IGNORED)
                                .nkz(IGNORED)
                                .vkz(IGNORED)))
                .endPointB(new Endpoint()
                        .deviceHostName(IGNORED)
                        .portName(null) // fill with VendorPortNameB
                        .portPosition(IGNORED)
                        .vendorPortName(IGNORED)
                        .endSz(null) // fill with endszB (_ instead of /)
                        .endSzParts(new EndSz()
                                .akz(IGNORED)
                                .fsz(IGNORED)
                                .nkz(IGNORED)
                                .vkz(IGNORED)));
    }

}
