package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementLinkDto;
import io.qameta.allure.Step;

import java.util.List;

import static org.testng.Assert.*;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4FrontEndInventoryImporterRobot {

    private static final int HTTP_CODE_OK_200 = 200;
    private static final int HTTP_CODE_NO_CONTENT_204 = 204;

    A4ResourceInventoryClient a4ResourceInventoryClient = new A4ResourceInventoryClient();

    public void checkUpdateNetworkElementPsl(String networkElementUuid, EquipmentData equipmentData) {

        //TODO: implement dynamic wiremock and its answers here

        NetworkElementDto networkElementDto =
                a4ResourceInventoryClient
                        .getClient()
                        .networkElements()
                        .findNetworkElement()
                        .uuidPath(networkElementUuid)
                        .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertEquals(networkElementDto.getUuid(), networkElementUuid);
        assertEquals(networkElementDto.getPlannedMatNumber(), equipmentData.getSubmt());
        assertEquals(networkElementDto.getKlsId(), "17056514");
        assertEquals(networkElementDto.getPlannedRackId(), "000031-000000-001-004-002-021");
        assertEquals(networkElementDto.getPlannedRackPosition(), "1 / 2 / 3 / 4");
    }


    @Step("Check created NEL")
    public void checkNetworkElementLinkExists(UewegData uewegData,
                                              String uuidNetworkElementPortA,
                                              String uuidNetworkElementPortB) {

        List<NetworkElementLinkDto> networkElementLinkDtoList = a4ResourceInventoryClient
                .getClient()
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(uuidNetworkElementPortA)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertEquals(networkElementLinkDtoList.size(), 1);

//        assertEquals(networkElementLinkDtoList.get(0).getDescription(), uewegData.getVersionId());
        assertEquals(networkElementLinkDtoList.get(0).getLbz(), uewegData.getLbz());
//        assertEquals(networkElementLinkDtoList.get(0).getLsz(), uewegData.getLsz());
        assertEquals(networkElementLinkDtoList.get(0).getUeWegId(), uewegData.getUewegId());
//        assertEquals(networkElementLinkDtoList.get(0).getPluralId(), uewegData.getPluralId());
        assertEquals(networkElementLinkDtoList.get(0).getNetworkElementPortAUuid(), uuidNetworkElementPortA);
        assertEquals(networkElementLinkDtoList.get(0).getNetworkElementPortAUuid(), uuidNetworkElementPortB);
    }

    @Step("Delete created NELs")
    public void cleanUpNetworkElementLinks(String uuidNetworkElementPort) {
        List<NetworkElementLinkDto> networkElementLinkDtoList = a4ResourceInventoryClient
                .getClient()
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(uuidNetworkElementPort)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        networkElementLinkDtoList.forEach(networkElementLinkDto -> a4ResourceInventoryClient
                .getClient()
                .networkElementLinks()
                .deleteNetworkElementLink()
                .uuidPath(networkElementLinkDto.getUuid())
//                    .execute(response -> response);
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204))));
    }

}

