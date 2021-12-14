package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4NemoUpdaterClient;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.nemo.updater.client.model.UpdateNemoTask;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_INVENTORY_IMPORTER_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.NemoStub.NEMO_URL;

@Slf4j
public class A4NemoUpdaterRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_INVENTORY_SERVICE_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_INVENTORY_SERVICE_MS));

    private static final AuthTokenProvider authTokenProviderImporter =
            new RhssoClientFlowAuthTokenProvider(A4_INVENTORY_IMPORTER_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_INVENTORY_IMPORTER_MS));

    private final ApiClient a4NemoUpdater = new A4NemoUpdaterClient(authTokenProvider).getClient();
    private final ApiClient a4NemoUpdaterImporter = new A4NemoUpdaterClient(authTokenProviderImporter).getClient();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();

    @Step("Trigger NEMO Update")
    public void triggerNemoUpdate(String uuid) {
        UpdateNemoTask updateNemoTask = new UpdateNemoTask();
        updateNemoTask.setEntityUuid(uuid);
        a4NemoUpdaterImporter
                .nemoUpdateService()
                .updateNemoTask()
                .body(updateNemoTask)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Trigger NEMO Update")
    public void triggerAsyncNemoUpdate(List<String> uuids) {
        ArrayList updateNemoTaskAsync = new ArrayList();
        updateNemoTaskAsync.addAll(uuids);
        a4NemoUpdater
                .nemoUpdateServiceAsync()
                .updateNemoTaskAsync()
                .body(updateNemoTaskAsync)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step("Check if PUT request to NEMO wiremock with logical resource has happened")
    public void checkLogicalResourcePutRequestToNemoWiremock(String uuid) {
        System.out.println("+++ Start checkLogicalResourceRequestToNemoWiremock auf einen Treffer");
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
        checkNetworkServiceProfileA10NspPutRequestToNemoWiremock (uuidTp, 1);
    }

    @Step("Check if PUT request to NEMO wiremock with network service profile A10NSP has happened")
    public void checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(String uuidTp, int count) {
        List<NetworkServiceProfileA10NspDto> nspList = a4Inventory
                .getNetworkServiceProfilesA10NspByTerminationPoint(uuidTp);
        System.out.println("+++ Mock nsp-list: "+nspList);
        Assert.assertEquals(nspList.size(), 1);
        System.out.println("+++ checkLogicalResourcePutRequestToNemoWiremock ");
        checkLogicalResourceRequestToNemoWiremock(nspList.get(0).getUuid(), "PUT", count);
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

    @Step("Check if PUT request to NEMO wiremock with network element has happened")
    public void checkNetworkElementPutRequestToNemoWiremock(String vpsz, String fsz) {
        List<NetworkElementDto> neList = a4Inventory
                .getNetworkElementsByVpszFsz(vpsz, fsz);

        Assert.assertEquals(neList.size(), 1);
        //System.out.println("+++ ne-list: "+neList);
        checkLogicalResourcePutRequestToNemoWiremock(neList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network element link has happened")
    public void checkNetworkElementLinkPutRequestToNemoWiremock(String uuidNep) {
        List<NetworkElementLinkDto> nelList = a4Inventory
                .getNetworkElementLinksByNePort(uuidNep);
        Assert.assertEquals(nelList.size(), 1);
        checkLogicalResourcePutRequestToNemoWiremock(nelList.get(0).getUuid());
    }

    @Step("Check if PUT request to NEMO wiremock with network element link has happened")
    public void checkNetworkElementLinkPutRequestToNemoWiremockByNel(String uuidNel) {
        checkLogicalResourcePutRequestToNemoWiremock(uuidNel);
    }

    @Step("Check if PUT request to NEMO wiremock with two network element links at one port has happened")
    public void checkTwoNetworkElementLinksPutRequestToNemoWiremock(String uuidNep) {
        List<NetworkElementLinkDto> nelList = a4Inventory
                .getNetworkElementLinksByNePort(uuidNep);
        Assert.assertEquals(nelList.size(), 2);
        System.out.println("+++ checkLogicalResourceNelPutRequestToNemoWiremock for one nel in case of two nel's: "+nelList);
        System.out.println("+++ get 0: ");
        checkLogicalResourcePutRequestToNemoWiremock(nelList.get(0).getUuid());
        System.out.println("+++ get 1: ");
        checkLogicalResourcePutRequestToNemoWiremock(nelList.get(1).getUuid());
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
