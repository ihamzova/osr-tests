package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.api.osr.A4NemoUpdaterClient;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.internal.client.model.UpdateNemoTask;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.internal.thread.ThreadExecutionException;
import org.testng.internal.thread.ThreadTimeoutException;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.NemoStub.NEMO_URL;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

@Slf4j
public class A4NemoUpdaterRobot {

    private final ApiClient a4NemoUpdater = new A4NemoUpdaterClient().getClient();

    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();

    @Step("Trigger NEMO Update")
    public void triggerNemoUpdate(String uuid) {
        UpdateNemoTask updateNemoTask = new UpdateNemoTask();
        updateNemoTask.setEntityUuid(uuid);
        a4NemoUpdater
                .nemoUpdateService()
                .updateNemoTask()
                .body(updateNemoTask)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Check if PUT request to NEMO wiremock with logical resource has happened")
    public void checkLogicalResourcePutRequestToNemoWiremock(String uuid) {
        checkLogicalResourceRequestToNemoWiremock(uuid, "PUT", 1);
    }

    @Step("Check if PUT request to NEMO wiremock with logical resource has NOT happened")
    public void checkLogicalResourcePutRequestToNemoWiremockDidntHappen(String uuid) {
        checkLogicalResourceRequestToNemoWiremock(uuid, "PUT", 0);
    }

    @Step("Check if DELETE request to NEMO wiremock with logical resource has happened")
    public void checkLogicalResourceDeleteRequestToNemoWiremock(String uuid) {
        checkLogicalResourceRequestToNemoWiremock(uuid, "DELETE", 1);
    }

    @Step("Check if request to NEMO wiremock with logical resource has happened")
    /*
      Checks whether an HTTP-request has reached the Nemo-Wiremock
     * @param uuid uuid of the LogicalResource to be checked
     * @param method name of the HTTP-method to be used
     */
    public void checkLogicalResourceRequestToNemoWiremock(String uuid, String method, int count) {
        WireMockFactory.get()
                .retrieve(
                        exactly(count),
                        newRequestPattern(
                                RequestMethod.fromString(method),
                                urlPathEqualTo(NEMO_URL + "/" + uuid)));
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile FTTH Access has happened")
    public void checkNetworkServiceProfileFtthAccessPutRequestToNemoWiremock(String uuidTp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = a4Inventory
                .getNetworkServiceProfilesFtthAccessByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);

        checkLogicalResourcePutRequestToNemoWiremock(nspList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile A10NSP has happened")
    public void checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(String uuidTp) {
        List<NetworkServiceProfileA10NspDto> nspList = a4Inventory
                .getNetworkServiceProfilesA10NspByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);

        checkLogicalResourcePutRequestToNemoWiremock(nspList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile L2BSA has happened")
    public void checkNetworkServiceProfileL2BsaPutRequestToNemoWiremock(String uuidTp) {
        List<NetworkServiceProfileL2BsaDto> nspList = a4Inventory
                .getNetworkServiceProfilesL2BsaByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);

        checkLogicalResourcePutRequestToNemoWiremock(nspList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile A10NSP has NOT happened")
    public void checkNetworkServiceProfileA10NspPutRequestToNemoWiremockDidntHappen(String uuidTp) {
        List<NetworkServiceProfileA10NspDto> nspList = a4Inventory
                .getNetworkServiceProfilesA10NspByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);

        checkLogicalResourcePutRequestToNemoWiremockDidntHappen(nspList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile L2BSA has NOT happened")
    public void checkNetworkServiceProfileL2BsaPutRequestToNemoWiremockDidntHappen(String uuidTp) {
        List<NetworkServiceProfileL2BsaDto> nspList = a4Inventory
                .getNetworkServiceProfilesL2BsaByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);

        checkLogicalResourcePutRequestToNemoWiremockDidntHappen(nspList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network element link has happened")
    public void checkNetworkElementLinkPutRequestToNemoWiremock(String uuidNep) {
        List<NetworkElementLinkDto> nelList = a4Inventory
                .getNetworkElementLinksByNePort(uuidNep);
        Assert.assertEquals(nelList.size(), 1);

        checkLogicalResourcePutRequestToNemoWiremock(nelList.get(0).getUuid());
    }

    @Step("Check if NEMO update PUT requests have been sent (asynchronous)")
    public void checkAsyncNemoUpdatePutRequests(A4ImportCsvData csvData) {
        //gather all UUIDs in a list
        //UUIDs of NEP, NE and NEG
        //they are needed to check if nemo received all of them
        List<String> uuidList = new ArrayList<>();

        csvData.getCsvLines().forEach((csvLine) ->
                uuidList.add(a4Inventory.getExistingNetworkElementByVpszFsz(csvLine.getNeVpsz(), csvLine.getNeFsz()).getUuid())
        );

        //get the ports of each NE
        List<NetworkElementPortDto> networkElementPortDtoList = new ArrayList<>();

        uuidList.forEach((neUuid) ->
                networkElementPortDtoList.addAll(a4Inventory.getNetworkElementPortsByNetworkElement(neUuid))
        );

        //add uuids of each port to uuidList
        networkElementPortDtoList.forEach(networkElementPortDto ->
                uuidList.add(networkElementPortDto.getUuid())
        );

        //get UUIDs of the group
        //assumption is that NEG name is unique so that first element can be taken
        uuidList.add(
                a4Inventory
                        .getNetworkElementGroupsByName(csvData
                                .getCsvLines().stream()
                                .findFirst().get().getNegName()).get(0).getUuid()
        );

         //uuidList.add("kram");
        //check if requests reached Wiremock
        //if so delivery by AMQ-consumer was successful
//            uuidList.forEach(uuid -> {
//                new Thread(() -> {
//                checkLogicalResourcePutRequestToNemoWiremock(uuid);
//                log.debug(uuid + "+++finish+++");
//                });
//            });
        uuidList.forEach(this::checkLogicalResourcePutRequestToNemoWiremock);
    }

}
