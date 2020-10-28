package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.*;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.internal.collections.Pair;

import java.util.List;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static org.testng.Assert.assertEquals;

public class A4ResourceInventoryRobot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_NO_CONTENT_204 = 204;

    private final ApiClient a4ResourceInventory = new A4ResourceInventoryClient().getClient();

    @Step("Create new Network Element Group in A4 resource inventory")
    public void createNetworkElementGroup(A4NetworkElementGroup negData) {
        NetworkElementGroupDto negDto = new A4ResourceInventoryMapper()
                .getNetworkElementGroupDto(negData);

        a4ResourceInventory
                .networkElementGroups()
                .createOrUpdateNetworkElementGroup()
                .body(negDto)
                .uuidPath(negDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete existing Network Element Group from A4 resource inventory")
    public void deleteNetworkElementGroup(String uuid) {
        a4ResourceInventory
                .networkElementGroups()
                .deleteNetworkElementGroup()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create new Network Element in A4 resource inventory")
    public void createNetworkElement(A4NetworkElement neData, A4NetworkElementGroup negData) {
        NetworkElementDto neDto = new A4ResourceInventoryMapper()
                .getNetworkElementDto(neData, negData);

        a4ResourceInventory
                .networkElements()
                .createOrUpdateNetworkElement()
                .body(neDto)
                .uuidPath(neDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete existing Network Element from A4 resource inventory")
    public void deleteNetworkElement(String uuid) {
        a4ResourceInventory
                .networkElements()
                .deleteNetworkElement()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create new Network Element Port in A4 resource inventory")
    public void createNetworkElementPort(A4NetworkElementPort nepData, A4NetworkElement neData) {
        NetworkElementPortDto nepDto = new A4ResourceInventoryMapper()
                .getNetworkElementPortDto(nepData, neData);

        a4ResourceInventory
                .networkElementPorts()
                .createOrUpdateNetworkElementPort()
                .body(nepDto)
                .uuidPath(nepDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete existing Network Element Port from A4 resource inventory")
    public void deleteNetworkElementPort(String uuid) {
        a4ResourceInventory
                .networkElementPorts()
                .deleteNetworkElementPort()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete existing Network ElementLink from A4 resource inventory")
    public void deleteNetworkElementLink(String uuid) {
        a4ResourceInventory
                .networkElementLinks()
                .deleteNetworkElementLink()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete all Network Element Links connected to given Network Element Port")
    public void deleteNetworkElementLinksConnectedToNePort(String portUuid) {
        List<NetworkElementLinkDto> networkElementLinkDtoList = getNetworkElementLinksByNePort(portUuid);

        networkElementLinkDtoList.forEach(nel ->
                deleteNetworkElementLink(nel.getUuid())
        );
    }

    @Step("Delete existing Termination Point from A4 resource inventory")
    public void deleteTerminationPoint(String uuid) {
        a4ResourceInventory
                .terminationPoints()
                .deleteTerminationPoint()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Check if one Network Service Profile connected to Termination Point exists")
    public void checkNetworkServiceProfileConnectedToTerminationPointExists(String uuidTp, int numberOfExpectedNsp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), numberOfExpectedNsp);
    }

    @Step("Delete existing Network Service Profile from A4 resource inventory")
    public void deleteNetworkServiceProfile(String uuid) {
        a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .deleteNetworkServiceProfileFtthAccess()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete all Network Service Profiles connected to given Termination Point")
    public void deleteNetworkServiceProfilesConnectedToTerminationPoint(String uuidTp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesByTerminationPoint(uuidTp);

        nspList.forEach(nsp ->
                deleteNetworkServiceProfile(nsp.getUuid())
        );
    }


    @Step("Get Network Service Profiles by UUID")
    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileByUuid(String uuid) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfileFtthAccess()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get a list of Network Service Profiles by Termination Point UUID")
    public List<NetworkServiceProfileFtthAccessDto> getNetworkServiceProfilesByTerminationPoint(String uuidTp) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfilesFtthAccess()
                .terminationPointUuidQuery(uuidTp)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("Get a list of Network Service Profiles by LineId")
    public List<NetworkServiceProfileFtthAccessDto> getNetworkServiceProfilesByLineId(String lineId) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfilesFtthAccess()
                .lineIdQuery(lineId)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get a list of Network Element Links by Network Element Port UUID")
    public List<NetworkElementLinkDto> getNetworkElementLinksByNePort(String uuidNep) {
        return a4ResourceInventory
                .networkElementLinks()
                .listNetworkElementLinks()
                .networkElementPortUuidQuery(uuidNep)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get a list of Network Elements by by VPSZ/FSZ")
    // As VPSZ & FSZ are together a unique constraint, the list will have either 0 or 1 entries
    public List<NetworkElementDto> getNetworkElementsByVpszFsz(String vpsz, String fsz) {
        return a4ResourceInventory
                .networkElements()
                .listNetworkElements()
                .vpszQuery(vpsz)
                .fszQuery(fsz)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get an existing Network Elements by VPSZ/FSZ")
    public NetworkElementDto getExistingNetworkElementByVpszFsz(String vpsz, String fsz) {
        List<NetworkElementDto> networkElementDtoList = getNetworkElementsByVpszFsz(vpsz, fsz);

        // List has only size 1, return uuid of element
        // If list has not size 1 an error occurred
        assertEquals(networkElementDtoList.size(), 1);

        return networkElementDtoList.get(0);
    }

    @Step("Get a list of Network Element Ports by Network Element")
    public List<NetworkElementPortDto> getNetworkElementPortsByNetworkElement(String networkElementUuid) {
        return a4ResourceInventory
                .networkElementPorts()
                .findNetworkElementPorts()
                .networkElementUuidQuery(networkElementUuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get a list of Termination Points by Network Element Port")
    public List<TerminationPointDto> getTerminationPointsByNePort(String tpUuid) {
        return a4ResourceInventory
                .terminationPoints()
                .findTerminationPoints()
                .parentUuidQuery(tpUuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Check that NEL was created and is connected to Network Element Ports")
    public void checkNetworkElementLinkConnectedToNePortExists(UewegData uewegData,
                                                               String uuidNetworkElementPortA,
                                                               String uuidNetworkElementPortB) {

        List<NetworkElementLinkDto> networkElementLinkDtoList = getNetworkElementLinksByNePort(uuidNetworkElementPortA);

        assertEquals(networkElementLinkDtoList.size(), 1);

        assertEquals(networkElementLinkDtoList.get(0).getUeWegId(), uewegData.getUewegId());
        assertEquals(networkElementLinkDtoList.get(0).getNetworkElementPortAUuid(), uuidNetworkElementPortA);
        assertEquals(networkElementLinkDtoList.get(0).getNetworkElementPortBUuid(), uuidNetworkElementPortB);
    }

    @Step("Get existing Network Element Group by UUID")
    public NetworkElementGroupDto getExistingNetworkElementGroup(String uuid) {
        return a4ResourceInventory
                .networkElementGroups()
                .findNetworkElementGroup()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get existing Network Element by UUID")
    public NetworkElementDto getExistingNetworkElement(String uuid) {
        return a4ResourceInventory
                .networkElements()
                .findNetworkElement()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get existing Network Element Port by UUID")
    public NetworkElementPortDto getExistingNetworkElementPort(String uuid) {
        return a4ResourceInventory
                .networkElementPorts()
                .findNetworkElementPort()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get existing Network Service Profile (FTTH Access) by UUID")
    public NetworkServiceProfileFtthAccessDto getExistingNetworkServiceProfileFtthAccess(String uuid) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfileFtthAccess()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get existing Network Element Link by UUID")
    public NetworkElementLinkDto getExistingNetworkElementLink(String uuid) {
        return a4ResourceInventory
                .networkElementLinks()
                .findNetworkElementLink()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Check that existing Network Element has been enriched with data from PSL")
    public void checkNetworkElementIsUpdatedWithPslData(String networkElementUuid, EquipmentData equipmentData) {
        NetworkElementDto networkElementDto = getExistingNetworkElement(networkElementUuid);

        assertEquals(networkElementDto.getUuid(), networkElementUuid);
        assertEquals(networkElementDto.getPlannedMatNumber(), equipmentData.getSubmt());
        assertEquals(networkElementDto.getKlsId(), equipmentData.getKlsId());
    }

    @Step("Check that lifecycle state and operational state have been updated for network element group")
    public void checkNetworkElementGroupIsUpdatedWithNewStates(A4NetworkElementGroup negData, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkElementGroupDto networkElementGroupDto = getExistingNetworkElementGroup(negData.getUuid());

        assertEquals(networkElementGroupDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkElementGroupDto.getOperationalState(), expectedNewOperationalState);
    }

    @Step("Check that lifecycle state and operational state have been updated for network element")
    public void checkNetworkElementIsUpdatedWithNewStates(A4NetworkElement neData, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkElementDto networkElementDto = getExistingNetworkElement(neData.getUuid());

        assertEquals(networkElementDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkElementDto.getOperationalState(), expectedNewOperationalState);
    }

    @Step("Check that operational state has been updated for network element port")
    public void checkNetworkElementPortIsUpdatedWithNewState(A4NetworkElementPort nepData, String expectedNewOperationalState) {
        NetworkElementPortDto networkElementPortDto = getExistingNetworkElementPort(nepData.getUuid());

        // NEPs do not have a lifecycle state
        assertEquals(networkElementPortDto.getOperationalState(), expectedNewOperationalState);
    }

    @Step("Check that lifecycle state and operational state have been updated for network service profile (FTTH Access)")
    public void checkNetworkServiceProfileFtthAccessIsUpdatedWithNewStates(A4NetworkServiceProfileFtthAccess nspFtthData, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkServiceProfileFtthAccessDto networkServiceProfileFtthAccessDto = getExistingNetworkServiceProfileFtthAccess(nspFtthData.getUuid());

        assertEquals(networkServiceProfileFtthAccessDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkServiceProfileFtthAccessDto.getOperationalState(), expectedNewOperationalState);
    }

    @Step("Check that lifecycle state and operational state have been updated for network element link")
    public void checkNetworkElementLinkIsUpdatedWithNewStates(A4NetworkElementLink nelData, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkElementLinkDto networkElementLinkDto = getExistingNetworkElementLink(nelData.getUuid());

        assertEquals(networkElementLinkDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkElementLinkDto.getOperationalState(), expectedNewOperationalState);
    }

    @Step("Delete all Network Element Groups with a given name")
    /*
    Unfortunately this cannot be combined with deleteA4NetworkElementsIncludingChildren method, because not possible to
    get all NEs connected to a NEG. This means that there's still some danger that some old test data collides with the
    newly-to-be-created test data. To be solved with DIGIHUB-68288
     */
    public void deleteNetworkElementGroups(A4NetworkElementGroup negData) {
        deleteNetworkElementGroups(negData.getName());
    }

    @Step("Get a list of Network Element Groups by name")
    // As name is unique constraint, the list will have either 0 or 1 entries
    public List<NetworkElementGroupDto> getNetworkElementGroupsByName(String name) {
        return a4ResourceInventory
                .networkElementGroups()
                .listNetworkElementGroups()
                .nameQuery(name)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete all Network Element Groups with a given name")
    public void deleteNetworkElementGroups(String negName) {
        List<NetworkElementGroupDto> negList = getNetworkElementGroupsByName(negName);

        negList.forEach(neg ->
                deleteNetworkElementGroup(neg.getUuid())
        );
    }

    @Step("Delete all Network Elements with given VPSZ/FSZ, including any connected NEPs, TPs, NELs and NSPs")
    public void deleteA4NetworkElementsIncludingChildren(A4NetworkElement neData) {
        deleteA4NetworkElementsIncludingChildren(neData.getVpsz(), neData.getFsz());
    }

    @Step("Delete all Network Elements with given VPSZ/FSZ, including any connected NEPs, TPs, NELs and NSPs")
    public void deleteA4NetworkElementsIncludingChildren(String vpsz, String fsz) {
        List<NetworkElementDto> neList = getNetworkElementsByVpszFsz(vpsz, fsz);

        neList.forEach(ne -> {
            List<NetworkElementPortDto> nepList = getNetworkElementPortsByNetworkElement(ne.getUuid());

            nepList.forEach(nep -> {
                deleteNetworkElementLinksConnectedToNePort(nep.getUuid());
                List<TerminationPointDto> tpList = getTerminationPointsByNePort(nep.getUuid());

                tpList.forEach(tp -> {
                    deleteNetworkServiceProfilesConnectedToTerminationPoint(tp.getUuid());
                    deleteTerminationPoint(tp.getUuid());
                });

                deleteNetworkElementPort(nep.getUuid());
            });

            deleteNetworkElement(ne.getUuid());
        });
    }

    @Step("Delete all Network Elements and Network Element Groups listed in the CSV, including any connected NEPs, TPs, NELs and NSPs")
    public void deleteA4EntriesIncludingChildren(A4ImportCsvData csvData) {
        List<String> negNameList = getDistinctListOfNegNamesFromCsvData(csvData);
        List<Pair<String, String>> vpszAndFszList = getDistinctListOfVpszAndFszFromCsvData(csvData);

        /*
        Delete all NEs (and any connected children) first. Don't include deletion of NEGs in this loop to make sure no
        NE is connected to the NEGs anymore.
        Note that the double-loop is not necessary anymore when DIGIHUB-68288 is implemented
         */
        vpszAndFszList.forEach(vpszAndFsz ->
                deleteA4NetworkElementsIncludingChildren(vpszAndFsz.first(), vpszAndFsz.second())
        );

        // Now delete all NEGs in extra loop
        negNameList.forEach(
                this::deleteNetworkElementGroups
        );
    }

    @Step("Delete all Network Elements and Network Element Groups listed in the CSV, including any connected NEPs")
    // Note that this step does not delete any connected NELs, TPs or NSPs, as after CSV import no such entities exist
    public void deleteA4EntriesIncludingNeps(A4ImportCsvData csvData) {
        List<String> negNameList = getDistinctListOfNegNamesFromCsvData(csvData);
        List<Pair<String, String>> vpszAndFszList = getDistinctListOfVpszAndFszFromCsvData(csvData);

        /*
        Delete all NEs (and any connected NEPs) first. Don't include deletion of NEGs in this loop to make sure no
        NE is connected to the NEGs anymore.
        Note that the double-loop is not necessary anymore when DIGIHUB-68288 is implemented
         */
        vpszAndFszList.forEach(vpszAndFsz -> {
            List<NetworkElementDto> neList = getNetworkElementsByVpszFsz(vpszAndFsz.first(), vpszAndFsz.second());

            neList.forEach(ne -> {
                List<NetworkElementPortDto> nepList = getNetworkElementPortsByNetworkElement(ne.getUuid());

                nepList.forEach(nep ->
                        deleteNetworkElementPort(nep.getUuid())
                );

                deleteNetworkElement(ne.getUuid());
            });

        });

        // Now delete all NEGs in extra loop
        negNameList.forEach(
                this::deleteNetworkElementGroups
        );
    }

    // NEGs (by name) can appear in CSV multiple times. We only need to run through cleanup once per NEG
    private List<String> getDistinctListOfNegNamesFromCsvData(A4ImportCsvData csvData) {
        return csvData.getCsvLines()
                .stream()
                .map(A4ImportCsvLine::getNegName)
                .distinct()
                .collect(Collectors.toList());
    }

    // NEs (by VPSZ & FSZ) can appear in CSV multiple times. We only need to run through cleanup once per NE
    private List<Pair<String, String>> getDistinctListOfVpszAndFszFromCsvData(A4ImportCsvData csvData) {
        return csvData.getCsvLines()
                .stream()
                .map(
                        ne -> Pair.create(ne.getNeVpsz(), ne.getNeFsz())
                )
                .distinct()
                .collect(Collectors.toList());
    }

    @Step("Create new TerminationPoint in A4 resource inventory")
    public void createTerminationPoint(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        TerminationPointDto tpDto = new A4ResourceInventoryMapper()
                .getTerminationPointDto(tpData, nepData);

        a4ResourceInventory
                .terminationPoints()
                .createOrUpdateTerminationPoint()
                .body(tpDto)
                .uuidPath(tpData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create new NetworkElementLink in A4 resource inventory")
    public void createNetworkElementLink(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB) {
        NetworkElementLinkDto nelDto = new A4ResourceInventoryMapper()
                .getNetworkElementLinkDto(nelData, nepDataA, nepDataB);

        a4ResourceInventory
                .networkElementLinks()
                .createOrUpdateNetworkElementLink()
                .body(nelDto)
                .uuidPath(nelData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("Create new NetworkServiceProfileFtthAccess in A4 resource inventory")
    public void createNetworkServiceProfileFtthAccess(A4NetworkServiceProfileFtthAccess nspData, A4TerminationPoint tpData) {
        NetworkServiceProfileFtthAccessDto nspDto = new A4ResourceInventoryMapper()
                .getNetworkServiceProfileFtthAccessDto(nspData, tpData);

        a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .createOrUpdateNetworkServiceProfileFtthAccess()
                .body(nspDto)
                .uuidPath(nspData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create test data for all A4 element types")
    public void createTestDataForAllA4ElementTypes(A4NetworkElementGroup negData, A4NetworkElement neData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, A4TerminationPoint tpData, A4NetworkServiceProfileFtthAccess nspFtthData, A4NetworkElementLink nelData) {
        createNetworkElementGroup(negData);
        createNetworkElement(neData, negData);
        createNetworkElementPort(nepDataA, neData);
        createNetworkElementPort(nepDataB, neData);
        createTerminationPoint(tpData, nepDataA);
        createNetworkServiceProfileFtthAccess(nspFtthData, tpData);
        createNetworkElementLink(nelData, nepDataA, nepDataB);
    }

}
