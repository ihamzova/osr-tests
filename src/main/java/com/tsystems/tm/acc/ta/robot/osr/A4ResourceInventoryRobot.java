package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class A4ResourceInventoryRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_INVENTORY_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_INVENTORY_MS));

    private final ApiClient a4ResourceInventory = new A4ResourceInventoryClient(authTokenProvider).getClient();

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

    @Step("Check if one Network Service Profile FTTH Access connected to Termination Point exists")
    public void checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(String uuidTp, int numberOfExpectedNsp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesFtthAccessByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), numberOfExpectedNsp);
    }

    @Step("Check if one Network Service Profile A10NSP connected to Termination Point exists")
    public void checkNetworkServiceProfileA10NspConnectedToTerminationPointExists(String uuidTp, int numberOfExpectedNsp) {
        List<NetworkServiceProfileA10NspDto> nspList = getNetworkServiceProfilesA10NspByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), numberOfExpectedNsp);
    }

    @Step("Check if one Network Service Profile L2BSA connected to Termination Point exists")
    public void checkNetworkServiceProfileL2BsaConnectedToTerminationPointExists(String uuidTp, int numberOfExpectedNsp) {
        List<NetworkServiceProfileL2BsaDto> nspList = getNetworkServiceProfilesL2BsaByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), numberOfExpectedNsp);
    }

    @Step("Delete existing Network Service Profile (FTTH Access) from A4 resource inventory")
    public void deleteNetworkServiceProfileFtthAccess(String uuid) {
        a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .deleteNetworkServiceProfileFtthAccess()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete existing Network Service Profile (A10NSP) from A4 resource inventory")
    public void deleteNetworkServiceProfileA10Nsp(String uuid) {
        a4ResourceInventory
                .networkServiceProfilesA10Nsp()
                .deleteNetworkServiceProfileA10Nsp()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete existing Network Service Profile (L2BSA) from A4 resource inventory")
    public void deleteNetworkServiceProfileL2Bsa(String uuid) {
        a4ResourceInventory
                .networkServiceProfilesL2Bsa()
                .deleteNetworkServiceProfileL2Bsa()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete all Network Service Profiles (FTTH Access) connected to given Termination Point")
    public void deleteNetworkServiceProfilesFtthAccessConnectedToTerminationPoint(String uuidTp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesFtthAccessByTerminationPoint(uuidTp);

        nspList.forEach(nsp ->
                deleteNetworkServiceProfileFtthAccess(nsp.getUuid())
        );
    }

    @Step("Delete all Network Service Profiles (A10NSP) connected to given Termination Point")
    public void deleteNetworkServiceProfilesA10NspConnectedToTerminationPoint(String uuidTp) {
        List<NetworkServiceProfileA10NspDto> nspList = getNetworkServiceProfilesA10NspByTerminationPoint(uuidTp);

        nspList.forEach(nsp ->
                deleteNetworkServiceProfileA10Nsp(nsp.getUuid())
        );
    }

    @Step("Delete all Network Service Profiles (L2BSA) connected to given Termination Point")
    public void deleteNetworkServiceProfilesL2BsaConnectedToTerminationPoint(String uuidTp) {
        List<NetworkServiceProfileL2BsaDto> nspList = getNetworkServiceProfilesL2BsaByTerminationPoint(uuidTp);

        nspList.forEach(nsp ->
                deleteNetworkServiceProfileL2Bsa(nsp.getUuid())
        );
    }

    @Step("Get Network Service Profiles (A10NSP) by UUID")
    public NetworkServiceProfileA10NspDto getNetworkServiceProfileA10NspByUuid(String uuid) {
        return a4ResourceInventory
                .networkServiceProfilesA10Nsp()
                .findNetworkServiceProfileA10Nsp()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get a list of Network Service Profiles FTTH Access by Termination Point UUID")
    public List<NetworkServiceProfileFtthAccessDto> getNetworkServiceProfilesFtthAccessByTerminationPoint(String uuidTp) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfilesFtthAccess()
                .terminationPointUuidQuery(uuidTp)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get a list of Network Service Profiles A10NSP by Termination Point UUID")
    public List<NetworkServiceProfileA10NspDto> getNetworkServiceProfilesA10NspByTerminationPoint(String uuidTp) {
        return a4ResourceInventory
                .networkServiceProfilesA10Nsp()
                .findNetworkServiceProfilesA10Nsp()
                .terminationPointUuidQuery(uuidTp)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get a list of Network Service Profiles L2BSA by Termination Point UUID")
    public List<NetworkServiceProfileL2BsaDto> getNetworkServiceProfilesL2BsaByTerminationPoint(String uuidTp) {
        return a4ResourceInventory
                .networkServiceProfilesL2Bsa()
                .findNetworkServiceProfilesL2Bsa()
                .terminationPointUuidQuery(uuidTp)
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

    @Step("Get a list of Network Elements by NEG uuid")
    public List<NetworkElementDto> getNetworkElementsByNegUuid(String negUuid) {
        return a4ResourceInventory
                .networkElements()
                .listNetworkElements()
                .networkElementGroupUuidQuery(negUuid)
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

    @Step("Get all existing Network Element Groups")
    public List<NetworkElementGroupDto> getExistingNetworkElementGroupAll() {
        return a4ResourceInventory
                .networkElementGroups()
                .listNetworkElementGroups()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get all existing Network Elements")
    public List<NetworkElementDto> getExistingNetworkElementAll() {
        return a4ResourceInventory
                .networkElements()
                .listNetworkElements()
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

    @Step("Get existing Network Service Profile (A10NSP) by UUID")
    public NetworkServiceProfileA10NspDto getExistingNetworkServiceProfileA10Nsp(String uuid) {
        return a4ResourceInventory
                .networkServiceProfilesA10Nsp()
                .findNetworkServiceProfileA10Nsp()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get existing Network Service Profile (L2BSA) by UUID")
    public NetworkServiceProfileL2BsaDto getExistingNetworkServiceProfileL2Bsa(String uuid) {
        return a4ResourceInventory
                .networkServiceProfilesL2Bsa()
                .findNetworkServiceProfileL2Bsa()
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

    @Step("Check that Network Element Link doesn't exists in Inventory")
    public void checkNetworkElementLinkIsDeleted(String uuid) {
        a4ResourceInventory
                .networkElementLinks()
                .findNetworkElementLink()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
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

    @Step("Check that lifecycle state and operational state have been updated for network element")
    public void checkNetworkElementByCsvData(A4ImportCsvData a4ImportCsvData) {
        AtomicReference<NetworkElementDto> networkElementDtoUnderTest = new AtomicReference<>(new NetworkElementDto());

        AtomicReference<List<NetworkElementGroupDto>> networkElementGroupDtoListUnderTest = new AtomicReference<>(new ArrayList<>());

        a4ImportCsvData.getCsvLines().forEach(a4ImportCsvLine -> {
            networkElementDtoUnderTest.set(getExistingNetworkElementByVpszFsz
                    (a4ImportCsvLine.getNeVpsz(), a4ImportCsvLine.getNeFsz()));

            networkElementGroupDtoListUnderTest.set(getNetworkElementGroupsByName(a4ImportCsvLine.getNegName()));

            //check of vpsz and fsz is redundant, so proceed with the other params

            //if neg with name was found, name was correct, additional check is
            //that only and exactly this one group with this name was found
            assertEquals(networkElementGroupDtoListUnderTest.get().size(), 1);
            assertEquals(networkElementDtoUnderTest.get().getDescription(), a4ImportCsvLine.getNeDescription());
        });
    }

    @Step("Check that Ports are created after CSV Import")
    public void checkNetworkElementPortsByImportCsvData(A4ImportCsvData a4ImportCsvData) {
        AtomicReference<NetworkElementDto> networkElementDtoUnderTest = new AtomicReference<>(new NetworkElementDto());
        AtomicReference<List<NetworkElementPortDto>> networkElementPortDtoUnderTest = new AtomicReference<>(new ArrayList<>());

        //AtomicReference<List<NetworkElementGroupDto>> networkElementGroupDtoListUnderTest = new AtomicReference<>(new ArrayList<>());

        a4ImportCsvData.getCsvLines().forEach(a4ImportCsvLine -> {
            networkElementDtoUnderTest.set(getExistingNetworkElementByVpszFsz
                    (a4ImportCsvLine.getNeVpsz(), a4ImportCsvLine.getNeFsz()));

            assertNotNull(networkElementDtoUnderTest);

            networkElementPortDtoUnderTest.set(getNetworkElementPortsByNetworkElement
                    (networkElementDtoUnderTest.get().getUuid()));

            if (networkElementDtoUnderTest.get().getType().equals("A4-OLT-v1")) {
                assertEquals(networkElementPortDtoUnderTest.get().size(), 20);
            } else if (networkElementDtoUnderTest.get().getType().equals("A4-LEAF-Switch-v1")) {
                assertEquals(networkElementPortDtoUnderTest.get().size(), 56);
            } else {
                assertEquals(networkElementPortDtoUnderTest.get().size(), 0);
            }
        });
    }

    @Step("Check that operational state has been updated for network element port")
    public void checkNetworkElementPortIsUpdatedWithNewStateAndDescription(A4NetworkElementPort nepData, String expectedNewOperationalState, String expectedNewDescription) {
        NetworkElementPortDto networkElementPortDto = getExistingNetworkElementPort(nepData.getUuid());

        // NEPs do not have a lifecycle state
        assertEquals(networkElementPortDto.getOperationalState(), expectedNewOperationalState);
        assertEquals(networkElementPortDto.getDescription(), expectedNewDescription);
    }

    @Step("Check that lifecycle state and operational state have been updated for network service profile (FTTH Access)")
    public void checkNetworkServiceProfileFtthAccessIsUpdatedWithNewStates(A4NetworkServiceProfileFtthAccess nspFtthData, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkServiceProfileFtthAccessDto networkServiceProfileFtthAccessDto = getExistingNetworkServiceProfileFtthAccess(nspFtthData.getUuid());

        assertEquals(networkServiceProfileFtthAccessDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkServiceProfileFtthAccessDto.getOperationalState(), expectedNewOperationalState);

    }

    @Step("Check that lifecycle state, operational state and port reference have been updated for network service profile (FTTH Access)")
    public void checkNetworkServiceProfileFtthAccessIsUpdatedWithNewStatesAndPortRef
            (A4NetworkServiceProfileFtthAccess nspFtthData, String expectedNewOperationalState,
             String expectedNewLifecycleState, A4NetworkElementPort nepData) {
        NetworkServiceProfileFtthAccessDto networkServiceProfileFtthAccessDto =
                getExistingNetworkServiceProfileFtthAccess(nspFtthData.getUuid());

        assertEquals(networkServiceProfileFtthAccessDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkServiceProfileFtthAccessDto.getOperationalState(), expectedNewOperationalState);
        assertEquals(networkServiceProfileFtthAccessDto.getOltPortOntLastRegisteredOn(), nepData.getUuid());
    }

    @Step("Check that lifecycle state and operational state have been updated for network service profile (A10NSP)")
    public void checkNetworkServiceProfileA10NspIsUpdatedWithNewStates(A4NetworkServiceProfileA10Nsp nspA10Data, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkServiceProfileA10NspDto networkServiceProfileA10NspDto = getExistingNetworkServiceProfileA10Nsp(nspA10Data.getUuid());

        assertEquals(networkServiceProfileA10NspDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkServiceProfileA10NspDto.getOperationalState(), expectedNewOperationalState);
    }

    @Step("Check that lifecycle state and operational state have been updated for network service profile (L2BSA)")
    public void checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(A4NetworkServiceProfileL2Bsa nspL2Data, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkServiceProfileL2BsaDto networkServiceProfileL2BsaDto = getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());

        assertEquals(networkServiceProfileL2BsaDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkServiceProfileL2BsaDto.getOperationalState(), expectedNewOperationalState);
    }

    @Step("Check for Network Service Profile (L2BSA) that no properties have changed values")
    public void checkThatNoFieldsAreChanged(A4NetworkServiceProfileL2Bsa nspL2Data, NetworkServiceProfileL2BsaDto nspOld) {
        NetworkServiceProfileL2BsaDto nspNew = getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());

        // Known "feature": LastUpdateTime is updated, despite no other fields being changed. Ignore this in assertion
        nspNew.setLastUpdateTime(nspOld.getLastUpdateTime());

        assertEquals(nspNew, nspOld);
    }

    @Step("Check that lifecycle state and operational state have been updated for network element link")
    public void checkNetworkElementLinkIsUpdatedWithNewStates(A4NetworkElementLink nelData, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkElementLinkDto networkElementLinkDto = getExistingNetworkElementLink(nelData.getUuid());

        assertEquals(networkElementLinkDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkElementLinkDto.getOperationalState(), expectedNewOperationalState);
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

    @Step("Delete A4 test data recursively by provided NEG name (NEG, NEs, NEPs, NELs, TPs, NSPs (FtthAccess, A10Nsp, L2Bsa)")
    public void deleteA4TestDataRecursively(String negName) {
        List<NetworkElementGroupDto> negList = getNetworkElementGroupsByName(negName);

        negList.forEach(neg -> {
            List<NetworkElementDto> neList = getNetworkElementsByNegUuid(neg.getUuid());

            neList.forEach(ne -> {
                List<NetworkElementPortDto> nepList = getNetworkElementPortsByNetworkElement(ne.getUuid());

                nepList.forEach(nep -> {
                    deleteNetworkElementLinksConnectedToNePort(nep.getUuid());
                    deleteTerminationPointsAndNspsConnectedToNepOrNeg(nep.getUuid());
                    deleteNetworkElementPort(nep.getUuid());
                });

                deleteNetworkElement(ne.getUuid());
            });

            deleteTerminationPointsAndNspsConnectedToNepOrNeg(neg.getUuid());
            deleteNetworkElementGroup(neg.getUuid());
        });
    }

    @Step("Delete A4 test data recursively by provided NEG name (NEG, NEs, NEPs, NELs, TPs, NSPs (FtthAccess, A10Nsp, L2Bsa)")
    public void deleteA4TestDataRecursively(A4NetworkElementGroup negData) {
        deleteA4TestDataRecursively(negData.getName());
    }

    @Step("Delete all Termination Points, including all connected NSPs (FtthAccess, A10Nsp, L2Bsa)")
    public void deleteTerminationPointsAndNspChildren(List<TerminationPointDto> tpList) {
        tpList.forEach(tp -> {
            deleteNetworkServiceProfilesL2BsaConnectedToTerminationPoint(tp.getUuid());
            deleteNetworkServiceProfilesFtthAccessConnectedToTerminationPoint(tp.getUuid());
            deleteNetworkServiceProfilesA10NspConnectedToTerminationPoint(tp.getUuid());
            deleteTerminationPoint(tp.getUuid());
        });
    }

    @Step("Delete all Termination Points connected to NEP")
    public void deleteTerminationPointsAndNspsConnectedToNepOrNeg(String uuid) {
        List<TerminationPointDto> tpList = getTerminationPointsByNePort(uuid);
        deleteTerminationPointsAndNspChildren(tpList);
    }

    @Step("Delete all Network Elements and Network Element Groups listed in the CSV")
    public void deleteA4TestDataRecursively(A4ImportCsvData csvData) {
        List<String> negNameList = getDistinctListOfNegNamesFromCsvData(csvData);

        negNameList.forEach(
                this::deleteA4TestDataRecursively
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

    @Step("Create new TerminationPoint in A4 resource inventory")
    public void createTerminationPoint(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        TerminationPointDto tpDto = new A4ResourceInventoryMapper()
                .getTerminationPointDto(tpData, nepData.getUuid());

        a4ResourceInventory
                .terminationPoints()
                .createOrUpdateTerminationPoint()
                .body(tpDto)
                .uuidPath(tpData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create new TerminationPoint in A4 resource inventory")
    public void createTerminationPoint(A4TerminationPoint tpData, A4NetworkElementGroup negData) {
        TerminationPointDto tpDto = new A4ResourceInventoryMapper()
                .getTerminationPointDto(tpData, negData.getUuid());

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

    @Step("Create new NetworkElementLink in A4 resource inventory")
    public void createNetworkElementLink(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, A4NetworkElement neDataA, A4NetworkElement neDataB) {
        NetworkElementLinkDto nelDto = new A4ResourceInventoryMapper()
                .getNetworkElementLinkDto(nelData, nepDataA, nepDataB, neDataA, neDataB);

        a4ResourceInventory
                .networkElementLinks()
                .createOrUpdateNetworkElementLink()
                .body(nelDto)
                .uuidPath(nelData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create new NetworkElementLink in A4 resource inventory")
    public void createNetworkElementLink(A4NetworkElementLink nelData, A4NetworkElementPort nepDataA, A4NetworkElementPort nepDataB, A4NetworkElement neDataA, A4NetworkElement neDataB, UewegData uewegData) {
        NetworkElementLinkDto nelDto = new A4ResourceInventoryMapper()
                .getNetworkElementLinkDto(nelData, nepDataA, nepDataB, neDataA, neDataB, uewegData);

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

    @Step("Create new NetworkServiceProfileA10NSP in A4 resource inventory")
    public void createNetworkServiceProfileA10Nsp(A4NetworkServiceProfileA10Nsp nspData, A4TerminationPoint tpData) {
        NetworkServiceProfileA10NspDto nspDto = new A4ResourceInventoryMapper()
                .getNetworkServiceProfileA10NspDto(nspData, tpData);

        a4ResourceInventory
                .networkServiceProfilesA10Nsp()
                .createOrUpdateNetworkServiceProfileA10Nsp()
                .body(nspDto)
                .uuidPath(nspData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create new NetworkServiceProfileL2Bsa in A4 resource inventory")
    public void createNetworkServiceProfileL2Bsa(A4NetworkServiceProfileL2Bsa nspData, A4TerminationPoint tpData) {
        // Creation of DTO-Object with NSP and TP Data with reference
        NetworkServiceProfileL2BsaDto nspDto = new A4ResourceInventoryMapper()
                .getNetworkServiceProfileL2BsaDto(nspData, tpData);

        a4ResourceInventory
                .networkServiceProfilesL2Bsa()
                .createOrUpdateNetworkServiceProfileL2Bsa()
                .body(nspDto)
                .uuidPath(nspData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Set new LifecycleState for existing NetworkServiceProfile L2BSA")
    public void setLifecycleState(A4NetworkServiceProfileL2Bsa nspL2Data, String lifecycleState) {
        NetworkServiceProfileL2BsaDto nspL2Bsa = getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());
        nspL2Bsa.setLifecycleState(lifecycleState);

        a4ResourceInventory
                .networkServiceProfilesL2Bsa()
                .createOrUpdateNetworkServiceProfileL2Bsa()
                .body(nspL2Bsa)
                .uuidPath(nspL2Bsa.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Check if NEL is in state INSTALLING")
    public void checkNetworkElementLinkInStateInstalling(String uuidNep) {
        List<NetworkElementLinkDto> nelList = getNetworkElementLinksByNePort(uuidNep);
        Assert.assertEquals(nelList.size(), 1);

        Assert.assertEquals(nelList.get(0).getLifecycleState(), "INSTALLING");
    }

}
