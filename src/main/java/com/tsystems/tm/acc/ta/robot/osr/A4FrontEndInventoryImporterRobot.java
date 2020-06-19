package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementLinkDto;

import java.util.List;
import static org.testng.Assert.*;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4FrontEndInventoryImporterRobot {

    private static final int HTTP_CODE_OK_200 = 200;

    A4ResourceInventoryClient a4ResourceInventoryClient = new A4ResourceInventoryClient();

    public void checkNetworkElementLinksExist(String uuidNetworkElementPort){

        List<NetworkElementLinkDto> networkElementLinkDtoList =
        a4ResourceInventoryClient
                .getClient()
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(uuidNetworkElementPort)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertEquals(networkElementLinkDtoList.size(),1);

        assertEquals(networkElementLinkDtoList.get(0).getUeWegId(),"Oma123, Opa123" );

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

