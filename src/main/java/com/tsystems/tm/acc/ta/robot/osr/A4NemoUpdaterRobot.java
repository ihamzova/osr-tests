package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.api.osr.A4NemoUpdaterClient;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.client.model.UpdateNemoTask;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.NemoStub.NEMO_URL;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
public class A4NemoUpdaterRobot {
    private final A4NemoUpdaterClient a4NemoUpdater = new A4NemoUpdaterClient();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();

    @Step("Trigger NEMO Update")
    public void triggerNemoUpdate(String uuid) {
        UpdateNemoTask updateNemoTask = new UpdateNemoTask();
        updateNemoTask.setEntityUuid(uuid);
        a4NemoUpdater.getClient()
                .nemoUpdateService()
                .updateNemoTask()
                .body(updateNemoTask)
                .execute(checkStatus(HTTP_CODE_CREATED_201));
    }

    @Step("Trigger NEMO Update")
    public void triggerAsyncNemoUpdate(List<String> uuids) {
        a4NemoUpdater.getClient()
                .nemoUpdateServiceAsync()
                .updateNemoTaskAsync()
                .body(uuids)
                .execute(checkStatus(HTTP_CODE_CREATED_201));
    }

    @Step("Check if PUT request to NEMO wiremock with logical resource has happened")
    public void checkLogicalResourcePutRequestToNemoWiremock(String uuid) {
        checkLogicalResourceRequestToNemoWiremock(uuid, "PUT", 1);
    }

    @Step("Check if DELETE request to NEMO wiremock with logical resource has happened")
    public void checkLogicalResourceDeleteRequestToNemoWiremock(String uuid) {
        checkLogicalResourceRequestToNemoWiremock(uuid, "DELETE", 1);
    }

    @Step("Check if request to NEMO wiremock with logical resource has happened")
    /* Checks whether an HTTP-request has reached the Nemo-Wiremock
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
        checkLogicalResourcePutRequestToNemoWiremock(a4Inventory.getNetworkServiceProfileFtthAccessByTerminationPoint(uuidTp).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile A10NSP has happened")
    public void checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(A4TerminationPoint tpData) {
        checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(tpData, 1);
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile A10NSP has happened")
    public void checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(A4TerminationPoint tpData, int count) {
        List<NetworkServiceProfileA10NspDto> nspList = a4Inventory.getNetworkServiceProfilesA10NspByTerminationPoint(tpData.getUuid());
        Assert.assertEquals(nspList.size(), 1);
        checkLogicalResourceRequestToNemoWiremock(nspList.get(0).getUuid(), "PUT", count);
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile L2BSA has happened")
    public void checkNetworkServiceProfileL2BsaPutRequestToNemoWiremock(String uuidTp) {
        List<NetworkServiceProfileL2BsaDto> nspList = a4Inventory
                .getNetworkServiceProfilesL2BsaByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);

        checkLogicalResourcePutRequestToNemoWiremock(nspList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network element has happened")
    public void checkNetworkElementPutRequestToNemoWiremock(String vpsz, String fsz) {
        List<NetworkElementDto> neList = a4Inventory
                .getNetworkElementsByVpszFsz(vpsz, fsz);

        Assert.assertEquals(neList.size(), 1);
        checkLogicalResourcePutRequestToNemoWiremock(neList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network element link has happened")
    public void checkNetworkElementLinkPutRequestToNemoWiremock(String uuidNep) {
        List<NetworkElementLinkDto> nelList = a4Inventory
                .getNetworkElementLinksByNePort(uuidNep);
        Assert.assertEquals(nelList.size(), 1);
        checkLogicalResourcePutRequestToNemoWiremock(nelList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with two network element links at one port has happened")
    public void checkTwoNetworkElementLinksPutRequestToNemoWiremock(A4NetworkElementPort nepData) {
        List<NetworkElementLinkDto> nelList = a4Inventory.getNetworkElementLinksByNePort(nepData.getUuid());
        Assert.assertEquals(nelList.size(), 2);
        checkLogicalResourcePutRequestToNemoWiremock(nelList.get(0).getUuid());
        checkLogicalResourcePutRequestToNemoWiremock(nelList.get(1).getUuid());
    }

    @Step("Check if NEMO update PUT requests have been sent (asynchronous)")
    public void checkAsyncNemoUpdatePutRequests(A4ImportCsvData csvData) {
        //gather all UUIDs in a list
        //UUIDs of NEP, NE and NEG
        //they are needed to check if nemo received all of them
        List<String> uuidList = new ArrayList<>();

        csvData.getCsvLines().forEach(csvLine ->
                uuidList.add(a4Inventory.getExistingNetworkElementByVpszFsz(csvLine.getNeVpsz(), csvLine.getNeFsz()).getUuid())
        );

        //get the ports of each NE
        List<NetworkElementPortDto> networkElementPortDtoList = new ArrayList<>();

        uuidList.forEach(neUuid ->
                networkElementPortDtoList.addAll(a4Inventory.getNetworkElementPortsByNetworkElement(neUuid))
        );

        //add uuids of each port to uuidList
        networkElementPortDtoList.forEach(networkElementPortDto ->
                uuidList.add(networkElementPortDto.getUuid())
        );

        //get UUIDs of the group
        //assumption is that NEG name is unique so that first element can be taken
        Optional<A4ImportCsvLine> firstCsvLine = csvData.getCsvLines().stream().findFirst();
        if (firstCsvLine.isPresent()) {
            String negName = firstCsvLine.get().getNegName();
            uuidList.add(
                    a4Inventory.getNetworkElementGroupsByName(negName).get(0).getUuid()
            );
            uuidList.forEach(this::checkLogicalResourcePutRequestToNemoWiremock);
        }
    }

}
