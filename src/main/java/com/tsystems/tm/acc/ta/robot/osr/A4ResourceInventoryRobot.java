package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.generators.A4NetworkElementGenerator;
import com.tsystems.tm.acc.ta.data.osr.generators.A4NetworkElementGroupGenerator;
import com.tsystems.tm.acc.ta.data.osr.generators.A4NetworkElementPortGenerator;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.*;
import io.qameta.allure.Step;
import io.restassured.response.ResponseOptions;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static org.testng.Assert.assertEquals;

public class A4ResourceInventoryRobot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_NO_CONTENT_204 = 204;

    private final ApiClient a4ResourceInventory = new A4ResourceInventoryClient().getClient();

    @Step("Create new network element group in A4 inventory")
    public void createNetworkElementGroup(A4NetworkElementGroup negData) {
        A4NetworkElementGroupGenerator a4NetworkElementGroupGenerator = new A4NetworkElementGroupGenerator();
        NetworkElementGroupDto negDto = a4NetworkElementGroupGenerator.generateAsDto(negData);

        a4ResourceInventory
                .networkElementGroups()
                .createOrUpdateNetworkElementGroup()
                .body(negDto)
                .uuidPath(negDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element group from A4 inventory")
    public void deleteNetworkElementGroup(String uuid) {
        a4ResourceInventory
                .networkElementGroups()
                .deleteNetworkElementGroup()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create network element in A4 inventory")
    public void createNetworkElement(A4NetworkElement neData, A4NetworkElementGroup negData) {
        A4NetworkElementGenerator a4NetworkElementGenerator = new A4NetworkElementGenerator();
        NetworkElementDto neDto = a4NetworkElementGenerator.generateAsDto(neData, negData);

        a4ResourceInventory
                .networkElements()
                .createOrUpdateNetworkElement()
                .body(neDto)
                .uuidPath(neDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element from A4 inventory")
    public void deleteNetworkElement(String uuid) {
        a4ResourceInventory
                .networkElements()
                .deleteNetworkElement()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create network element port in A4 inventory")
    public void createNetworkElementPort(A4NetworkElementPort nepData, A4NetworkElement neData) {
        A4NetworkElementPortGenerator a4NetworkElementPortGenerator = new A4NetworkElementPortGenerator();
        NetworkElementPortDto nepDto = a4NetworkElementPortGenerator.generateAsDto(nepData, neData);

        a4ResourceInventory
                .networkElementPorts()
                .createOrUpdateNetworkElementPort()
                .body(nepDto)
                .uuidPath(nepDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element port from A4 inventory")
    public void deleteNetworkElementPort(String uuid) {
        a4ResourceInventory
                .networkElementPorts()
                .deleteNetworkElementPort()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete network element link from A4 inventory")
    public void deleteNetworkElementLinksByNePortUuid(String portUuid) {
        List<NetworkElementLinkDto> networkElementLinkDtoList = a4ResourceInventory
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(portUuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        networkElementLinkDtoList.forEach((nel) ->
                a4ResourceInventory
                        .networkElementLinks()
                        .deleteNetworkElementLink()
                        .uuidPath(nel.getUuid())
                        .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)))
        );
    }

    @Step("Delete termination point from A4 inventory")
    public void deleteTerminationPoint(String uuid) {
        a4ResourceInventory
                .terminationPoints()
                .deleteTerminationPoint()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete network element link from A4 inventory")
    public void deleteNetworkElementLink(String uuid) {
        a4ResourceInventory
                .networkElementLinks()
                .deleteNetworkElementLink()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create NEG, NE & NEP in A4 inventory")
    public void setUpPrerequisiteElements(A4NetworkElementGroup negData, A4NetworkElement neData, A4NetworkElementPort nepData) {
        createNetworkElementGroup(negData);
        createNetworkElement(neData, negData);
        createNetworkElementPort(nepData, neData);
    }

    @Step("Delete NEG, NE & NEP from A4 inventory")
    public void deletePrerequisiteElements(String negUuid, String neUuid, String nepUuid) {
        deleteNetworkElementPort(nepUuid);
        deleteNetworkElement(neUuid);
        deleteNetworkElementGroup(negUuid);
    }

    @Step("Check if one network service profile connected to termination point exists")
    public void checkNetworkServiceProfileConnectedToTerminationPointExists(String uuidTp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);
    }

    @Step("Delete network service profiles connected to termination point")
    public void deleteNetworkServiceProfilesConnectedToTerminationPoint(String uuidTp) {
        // First: Find all NSPs connected to given TP (expected: only 1 NSP connected)
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesByTerminationPoint(uuidTp);

        // Second: Delete found NSPs
        a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .deleteNetworkServiceProfileFtthAccess()
                .uuidPath(nspList.get(0).getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    public void deletePortsOfNetworkElement(String neUuid) {
        List<NetworkElementPortDto> ports = a4ResourceInventory
                .networkElementPorts()
                .findNetworkElementPorts()
                .networkElementUuidQuery(neUuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        ports.forEach(networkElementPortDto ->
                deleteNetworkElementPort(networkElementPortDto.getUuid())
        );
    }

    @Step("Get a list of Network Service Profiles based on Termination Point UUID")
    public List<NetworkServiceProfileFtthAccessDto> getNetworkServiceProfilesByTerminationPoint(String uuidTp) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfilesFtthAccess()
                .terminationPointUuidQuery(uuidTp)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get a list of Network Element Links based on Network Element Port UUID")
    public List<NetworkElementLinkDto> getNetworkElementLinksByNePortUuid(String uuidNep) {
        return a4ResourceInventory
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(uuidNep)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete list of network Elements by vpsz/fsz")
    public void deleteNetworkElements(A4ImportCsvData csvData) {
        List<A4ImportCsvLine> list = csvData.getCsvLines();

        list.forEach(a4ResourceInventoryEntry -> {
            List<NetworkElementDto> actualNeList = a4ResourceInventory
                    .networkElements()
                    .listNetworkElements()
                    .fszQuery(a4ResourceInventoryEntry.getNeFsz())
                    .vpszQuery(a4ResourceInventoryEntry.getNeVpsz())
                    .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

            actualNeList.stream().findFirst().ifPresent(networkElementDto -> {
                deletePortsOfNetworkElement(networkElementDto.getUuid());
                deleteNetworkElement(networkElementDto.getUuid());
            });
        });
    }

    @Step("GET network element uuid by VPSZ/FSZ")
    public NetworkElementDto getNetworkElementByVpszFsz(String vpsz, String fsz) {
        List<NetworkElementDto> networkElementDtoList = a4ResourceInventory
                .networkElements()
                .listNetworkElements()
                .vpszQuery(vpsz)
                .fszQuery(fsz)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        // List has only size 1, return uuid of element
        // If list has not size 1 an error occurred
        assertEquals(networkElementDtoList.size(), 1);

        return networkElementDtoList.get(0);
    }

    @Step("GET network element port uuid list by networkElementUuid")
    public List<NetworkElementPortDto> getNetworkElementPorts(String networkElementUuid) {
        return a4ResourceInventory
                .networkElementPorts()
                .findNetworkElementPorts()
                .networkElementUuidQuery(networkElementUuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network group by name")
    public void deleteGroupByName(A4ImportCsvData csvData) {
        String groupName = csvData.getCsvLines().get(0).getNegName();

        List<NetworkElementGroupDto> actualNegList = a4ResourceInventory
                .networkElementGroups()
                .listNetworkElementGroups()
                .nameQuery(groupName)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        actualNegList.stream().findFirst().ifPresent(networkElementGroupDto -> a4ResourceInventory
                .networkElementGroups()
                .deleteNetworkElementGroup()
                .uuidPath(networkElementGroupDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204))));
    }

    @Step("Check created NEL")
    public void checkNetworkElementLinkExists(UewegData uewegData,
                                              String uuidNetworkElementPortA,
                                              String uuidNetworkElementPortB) {

        List<NetworkElementLinkDto> networkElementLinkDtoList = a4ResourceInventory
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(uuidNetworkElementPortA)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertEquals(networkElementLinkDtoList.size(), 1);

        assertEquals(networkElementLinkDtoList.get(0).getUeWegId(), uewegData.getUewegId());
        assertEquals(networkElementLinkDtoList.get(0).getNetworkElementPortAUuid(), uuidNetworkElementPortA);
        assertEquals(networkElementLinkDtoList.get(0).getNetworkElementPortBUuid(), uuidNetworkElementPortB);
    }

    @Step("Delete created NELs")
    public void cleanUpNetworkElementLinks(String uuidNetworkElementPort) {
        List<NetworkElementLinkDto> networkElementLinkDtoList = a4ResourceInventory
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(uuidNetworkElementPort)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        networkElementLinkDtoList.forEach((nel) ->
                deleteNetworkElementLink(nel.getUuid())
        );
    }

    public void checkNetworkElementUpdateWithPslData(String networkElementUuid, EquipmentData equipmentData) {
        NetworkElementDto networkElementDto = a4ResourceInventory
                .networkElements()
                .findNetworkElement()
                .uuidPath(networkElementUuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertEquals(networkElementDto.getUuid(), networkElementUuid);
        assertEquals(networkElementDto.getPlannedMatNumber(), equipmentData.getSubmt());
        assertEquals(networkElementDto.getKlsId(), equipmentData.getKlsId());
    }

    /*
    Before a Network Element Port (NEP) can be deleted from A4 Resource Inventory, all belonging/connected "child"
    entities have to be deleted first. Candidates are Network Element Links (NEL), Termination Points (TP), and their
    "children" Network Service Profiles (NSP). This robot takes care of all that.
     */
    @Step("Wipe NEP (existing or not) and any connected TPs, NELs and NSPs")
    public void wipeA4NetworkElementPortsIncludingChildren(A4NetworkElementPort nepData, A4NetworkElement neData) {
        // Find NEP with LogicalLabel
        List<NetworkElementPortDto> nepList = a4ResourceInventory
                .networkElementPorts()
                .findNetworkElementPorts()
                .logicalLabelQuery(nepData.getLogicalLabel())
                .networkElementUuidQuery(neData.getUuid())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        nepList.forEach((nep) -> {

            // Find and delete any NELs
            deleteNetworkElementLinksByNePortUuid(nep.getUuid());

            // Find any TPs
            List<TerminationPointDto> tpList = a4ResourceInventory
                    .terminationPoints()
                    .findTerminationPoints()
                    .parentUuidQuery(nep.getUuid())
                    .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

            tpList.forEach((tp) -> {
                // Find and delete any NSPs
                deleteNetworkServiceProfilesConnectedToTerminationPoint(tp.getUuid());

                // Delete any found TPs
                deleteTerminationPoint(tp.getUuid());
            });

            // Delete NEP (existing or not)
            a4ResourceInventory
                    .networkElementPorts()
                    .deleteNetworkElementPort()
                    .uuidPath(nep.getUuid())
                    .execute(ResponseOptions::thenReturn); // do not care about 204 or 404
        });
    }

}
