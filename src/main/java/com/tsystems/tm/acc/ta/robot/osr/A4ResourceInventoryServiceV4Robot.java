package com.tsystems.tm.acc.ta.robot.osr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceV4Client;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.model.NetworkElement;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.model.NetworkElementGroup;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static org.testng.Assert.*;

public class A4ResourceInventoryServiceV4Robot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_NOT_FOUND_404 = 404;

    private ApiClient a4ResourceInventoryService = new A4ResourceInventoryServiceV4Client().getClient();

    //private ObjectMapper objectMapper = new ObjectMapper();

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
        assertEquals(neList.size(), 1);
        assertEquals(neList.get(0).getVpsz(), neData.getVpsz());
        assertEquals(neList.get(0).getFsz(), neData.getFsz());
        assertEquals(neList.get(0).getId(), neData.getUuid());
        assertEquals(neList.get(0).getCategory(), neData.getCategory());
        assertEquals(neList.get(0).getKlsId(), neData.getKlsId());
    }

    public void checkNumberOfNetworkElements(List<A4NetworkElement> neDataList) {
        List<NetworkElement> neList = getAllNetworkElementsV4();
        assertEquals(neList.size(), neDataList.size());
    }

    @Step("Read all Network Element Groups as list from v4 API")
    public List<NetworkElementGroup> getAllNetworkElementGorupsV4() {
        return a4ResourceInventoryService.networkElementGroup()
                .listNetworkElementGroup()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Search all Network Element Groups by name as list from v4 API")
    public List<NetworkElementGroup> getNetworkElementGroupsV4ByName(String name) {
        return a4ResourceInventoryService.networkElementGroup()
                .listNetworkElementGroup()
                .nameQuery(name)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Read one NetworkElement from v4 API")
    public NetworkElementGroup getNetworkElementGroupV4(String uuid) {
        return a4ResourceInventoryService
                .networkElementGroup()
                .retrieveNetworkElementGroup()
                .idPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    public void checkIfNetworkElementGroupExistsByName(A4NetworkElementGroup negData) {
        String name = negData.getName();
        List<NetworkElementGroup> negList = getNetworkElementGroupsV4ByName(name);
        assertEquals(negList.size(), 1);
        assertEquals(negList.get(0).getId(), negData.getUuid());
        assertEquals(negList.get(0).getLifecycleState().toString(), negData.getLifecycleState());
    }

    public void checkNumberOfNetworkElementGroups(List<A4NetworkElementGroup> negDataList){
        List<NetworkElementGroup> negList = getAllNetworkElementGorupsV4();
        assertEquals(negList.size(), negDataList.size());
    }

    public void checkIfNetworkElementGroupExistsByUuid(A4NetworkElementGroup negData) {
        String uuid = negData.getUuid();
        NetworkElementGroup neg = getNetworkElementGroupV4(uuid);
        assertEquals(neg.getId(), negData.getUuid());
        assertEquals(neg.getLifecycleState().toString(), negData.getLifecycleState());
        assertEquals(neg.getOperationalState().toString(), negData.getOperationalState());
    }

    public void checkErrorNotFound(A4NetworkElementGroup negData) {
        String uuid = negData.getUuid() + "1";

        Response r = a4ResourceInventoryService
                .networkElementGroup()
                .retrieveNetworkElementGroup()
                .idPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));

//        Error e = new Error();
//        try {
//            e = objectMapper.readValue(r.getBody().print(), Error.class);
//        } catch (IOException ioException) {
//            fail("No Error Object is thrown!");
//        }
//
//        assertEquals(e.getCode(), "00000002");
//        assertEquals(e.getStatus(), "404 NOT_FOUND");
//        assertEquals(e.getReason(), "Element not found in database");

        assertTrue(r.getBody().print().contains("Element not found in database"));

    }
}
