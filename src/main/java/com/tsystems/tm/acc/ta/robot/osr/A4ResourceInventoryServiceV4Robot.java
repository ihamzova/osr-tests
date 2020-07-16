package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceV4Client;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.model.NetworkElement;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4ResourceInventoryServiceV4Robot {
    private static final Integer HTTP_CODE_OK_200 = 200;

    private ApiClient a4ResourceInventoryService = new A4ResourceInventoryServiceV4Client().getClient();

    @Step("Read all Network Elements as list from v4 API")
    public List<NetworkElement> getAllNetworkElementsV4() {
        return a4ResourceInventoryService.networkElement()
                .listNetworkElement()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Read all Network Elements as list from v4 API")
    public List<NetworkElement> getNetworkElementsV4ByEndsz(String endsz) {
        return a4ResourceInventoryService.networkElement()
                .listNetworkElement()
                .endszQuery(endsz)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Read one NetworkElement from v4 API")
    public NetworkElement getNetworkElementV4(String uuid) {
        return a4ResourceInventoryService
                .networkElement()
                .retrieveNetworkElement()
                .idPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    public void checkIfNetworkElementExists(A4NetworkElement neData) {
        String endsz = neData.getVpsz() + "/" + neData.getFsz();
        List<NetworkElement> neList = getNetworkElementsV4ByEndsz(endsz);
        Assert.assertEquals(neList.size(), 1);
        Assert.assertEquals(neList.get(0).getVpsz(), neData.getVpsz());
        Assert.assertEquals(neList.get(0).getFsz(), neData.getFsz());
        Assert.assertEquals(neList.get(0).getId(), neData.getUuid());
        Assert.assertEquals(neList.get(0).getCategory(), neData.getCategory());
        Assert.assertEquals(neList.get(0).getKlsId(), neData.getKlsId());
    }

    public void checkNumberOfNetworkElements(List<A4NetworkElement> neDataList) {
        List<NetworkElement> neList = getAllNetworkElementsV4();
        Assert.assertEquals(neList.size(), neDataList.size());
    }
}
