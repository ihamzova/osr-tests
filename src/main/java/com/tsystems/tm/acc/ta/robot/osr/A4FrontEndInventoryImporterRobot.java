package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementLinkDto;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.model.Equipment;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.model.ReadEquipmentResponseHolder;

import java.util.List;
import static org.testng.Assert.*;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4FrontEndInventoryImporterRobot {

    private static final int HTTP_CODE_OK_200 = 200;

    A4ResourceInventoryClient a4ResourceInventoryClient = new A4ResourceInventoryClient();

    public void checkUpdateNetworkElementPsl(String networkElementUuid, ReadEquipmentResponseHolder pslCallback) {

        //TODO: implement dynamic wiremock and its answers here:
        Equipment equipment = pslCallback.getResponse().getResponseData().getEquipment().get(0);

        NetworkElementDto networkElementDto =
                a4ResourceInventoryClient
                        .getClient()
                        .networkElements()
                        .findNetworkElement()
                        .uuidPath(networkElementUuid)
                        .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertEquals(networkElementDto.getUuid(), networkElementUuid);
        assertEquals(networkElementDto.getPlannedMatNumber(), "42999900");
        assertEquals(networkElementDto.getKlsId(), "17056514");
        assertEquals(networkElementDto.getPlannedRackId(), "000031-000000-001-004-002-021");
        assertEquals(networkElementDto.getPlannedRackPosition(), "1 / 2 / 3 / 4");
    }


    public void checkNetworkElementLinksExist(String uuidNetworkElementPort, String uewegId){

        List<NetworkElementLinkDto> networkElementLinkDtoList =
        a4ResourceInventoryClient
                .getClient()
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(uuidNetworkElementPort)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertEquals(networkElementLinkDtoList.size(),1);

        assertEquals(networkElementLinkDtoList.get(0).getUeWegId(), uewegId);
//        networkElementLinkDtoList.forEach(networkElementLinkDto -> {
//            networkElementLinkDto.getUeWegId().matches("I1234567891, R1234567891");
//        });
    }

    public void cleanUpNetworkElementLinks(String uuidNetworkElementPort){

        List<NetworkElementLinkDto> networkElementLinkDtoList =
                a4ResourceInventoryClient
                        .getClient()
                        .networkElementLinks()
                        .listNetworkElementLinks()
                        .networkElementPortUuidQuery(uuidNetworkElementPort)
                        .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        networkElementLinkDtoList.forEach(networkElementLinkDto -> {
            a4ResourceInventoryClient
                    .getClient()
                    .networkElementLinks()
                    .deleteNetworkElementLink()
                    .uuidPath(networkElementLinkDto.getUuid())
                    .execute(response -> response);
        });
    }

}

