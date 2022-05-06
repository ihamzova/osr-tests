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
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.internal.collections.Pair;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_NEMO_UPDATER_MS;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndsz;
import static org.testng.Assert.*;

public class A4ResourceInventoryRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_NEMO_UPDATER_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_NEMO_UPDATER_MS));

    private final ApiClient a4ResourceInventory = new A4ResourceInventoryClient(authTokenProvider).getClient();

    @Step("Create new Network Element Group in A4 resource inventory")
    public Response createNetworkElementGroup(A4NetworkElementGroup negData) {
        NetworkElementGroupDto negDto = new A4ResourceInventoryMapper()
                .getNetworkElementGroupDto(negData);

        return createNetworkElementGroup(negDto);
    }

    @Step("Create new Network Element Group in A4 resource inventory based on NEG DTO")
    public Response createNetworkElementGroup(NetworkElementGroupDto negDto) {
        return a4ResourceInventory
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

    @Step("Create new Network Element in A4 resource inventory based on NE DTO")
    public void createNetworkElement(NetworkElementDto neDto) {
        a4ResourceInventory
                .networkElements()
                .createOrUpdateNetworkElement()
                .body(neDto)
                .uuidPath(neDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create new Network Element in A4 resource inventory")
    public void createNetworkElement(A4NetworkElement neData, A4NetworkElementGroup negData) {
        NetworkElementDto neDto = new A4ResourceInventoryMapper()
                .getNetworkElementDto(neData, negData);

        createNetworkElement(neDto);
    }

    @Step("Create new Network Element in A4 resource inventory")
    public void createNetworkElement(A4NetworkElement neData, NetworkElementGroupDto neg) {
        NetworkElementDto neDto = new A4ResourceInventoryMapper()
                .getNetworkElementDto(neData, neg);

        createNetworkElement(neDto);
    }

    @Step("Delete Network Element from A4 resource inventory")
    public void deleteNetworkElementNoChecks(String uuid) {
        a4ResourceInventory
                .networkElements()
                .deleteNetworkElement()
                .uuidPath(uuid)
                .execute(voidCheck()); // unfortunately deletion of NEs is not idempotent, therefore we deactivate the HTTP status check
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

    @Step("Create new Network Element Port in A4 resource inventory")
    public void createNetworkElementPort(NetworkElementPortDto nepDto) {
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
    public NetworkServiceProfileFtthAccessDto checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(String uuidTp, int numberOfExpectedNsp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesFtthAccessByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), numberOfExpectedNsp);

        if (!nspList.isEmpty())
            return nspList.get(0);
        else
            return null;
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

    @Step("Check if Network Element is reset to PLANNING")
    public void checkNetworkElementIsResetToPlanning(String uuidNe) {
        assertEquals(getExistingNetworkElement(uuidNe).getLifecycleState(), "PLANNING");
        assertEquals(getExistingNetworkElement(uuidNe).getOperationalState(), "NOT_WORKING");
        assertNull(getExistingNetworkElement(uuidNe).getPlannedMatNumber());
        assertNull(getExistingNetworkElement(uuidNe).getKlsId());
        assertNull(getExistingNetworkElement(uuidNe).getAddress());
        assertNull(getExistingNetworkElement(uuidNe).getPlannedRackId());
        assertNull(getExistingNetworkElement(uuidNe).getPlannedRackPosition());
        assertNull(getExistingNetworkElement(uuidNe).getZtpIdent());
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

    @Step("Delete existing Network Service Profile (L2BSA) from A4 resource inventory without check")
    public void deleteNetworkServiceProfileL2BsaWithoutCheck(String uuid) {
        a4ResourceInventory
                .networkServiceProfilesL2Bsa()
                .deleteNetworkServiceProfileL2Bsa()
                .uuidPath(uuid)
                .execute(voidCheck());
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

    @Step("Get list of NSP Ftth-Access by Termination Point reference")
    public NetworkServiceProfileFtthAccessDto getNetworkServiceProfileFtthAccessByTerminationPoint(String uuidTp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = this.getNetworkServiceProfilesFtthAccessByTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1, "No NetworkServiceProfileFtthAccessDto found with TP-Uuid : " + uuidTp);
        return nspList.get(0);
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


    @Step("Get a list of Network Elements by by ztpIdent")
    // As VPSZ & FSZ are together a unique constraint, the list will have either 0 or 1 entries
    public List<NetworkElementDto> getNetworkElementsByZtpIdent(String ztpIdent) {
        return a4ResourceInventory
                .networkElements()
                .listNetworkElements()
                .ztpIdentQuery(ztpIdent)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
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

    @Step("Check if Termination Point exists")
    public void checkTerminationPointExists(String uuid) {
        a4ResourceInventory
                .terminationPoints()
                .findTerminationPoint()
                .uuidPath(uuid)
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

    @Step("Check lifecycle state of NEL")
    public void checkLifecycleState(A4NetworkElementLink nelData, String lcs) {
        assertEquals(getExistingNetworkElementLink(nelData.getUuid()).getLifecycleState(), lcs);
    }

    @Step("Check default value correctness of NSP A10NSP")
    public void checkDefaultValuesNsp(A4NetworkServiceProfileA10Nsp nspA10Nsp) {
        final String UNDEFINED = "undefined";
        final NetworkServiceProfileA10NspDto nsp = getExistingNetworkServiceProfileA10Nsp(nspA10Nsp.getUuid());

        assertEquals(nsp.getLifecycleState(), "PLANNING");
        assertEquals(nsp.getOperationalState(), "NOT_WORKING");
        assertEquals(nsp.getAdministrativeMode(), "ENABLED");

        String crtNew = Objects.requireNonNull(nsp.getCreationTime()).toString();
        String lutNew = Objects.requireNonNull(nsp.getLastUpdateTime()).toString();
        assertNotEquals(crtNew, lutNew);

        assertEquals(nsp.getMtuSize(), "1590");
        assertEquals(nsp.getEtherType(), "0x88a8");
        assertEquals(nsp.getVirtualServiceProvider(), "DTAG");
        assertEquals(nsp.getSpecificationVersion(), "7");
        assertNull(nsp.getNumberOfAssociatedNsps());
        assertNull(nsp.getNetworkElementLinkUuid());
        assertTrue(Objects.requireNonNull(nsp.getLacpActive()));
        assertEquals(nsp.getLacpMode(), UNDEFINED);
        assertEquals(nsp.getMinActiveLagLinks(), "1");
        assertEquals(nsp.getCarrierBsaReference(), UNDEFINED);
        assertEquals(nsp.getItAccountingKey(), UNDEFINED);
        assertEquals(nsp.getDataRate(), UNDEFINED);
        assertEquals(nsp.getQosMode(), "TOLERANT");

        A10NspQosDto qosClass = Objects.requireNonNull(nsp.getQosClasses()).get(0);
        assertEquals(qosClass.getQosBandwidthDown(), UNDEFINED);
        assertEquals(qosClass.getQosBandwidthUp(), UNDEFINED);
        assertEquals(qosClass.getQosPriority(), UNDEFINED);

        VlanRangeDto vlanRange = Objects.requireNonNull(nsp.getsVlanRange()).get(0);
        assertEquals(vlanRange.getVlanRangeUpper(), UNDEFINED);
        assertEquals(vlanRange.getVlanRangeLower(), UNDEFINED);
    }

    @Step("Check that Network Element Link doesn't exists in Inventory")
    public void checkNetworkElementLinkIsDeleted(String uuid) {
        a4ResourceInventory
                .networkElementLinks()
                .findNetworkElementLink()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
    }

    @Step("Check that Termination Point doesn't exists in Inventory")
    public void checkTerminationPointIsDeleted(String uuid) {
        a4ResourceInventory
                .terminationPoints()
                .findTerminationPoint()
                .uuidPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
    }

    @Step("Check that Network Service Profile FTTH Access doesn't exists in Inventory")
    public void checkNetworkServiceProfileFtthAccessIsDeleted(String uuid) {
        a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfileFtthAccess()
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

    @Step("Check that lastSuccessfulSyncTime has been set for network element group")
    public void checkNetworkElementGroupIsUpdatedWithLastSuccessfulSyncTime(A4NetworkElementGroup negData, OffsetDateTime timeBeforeSync) {
        NetworkElementGroupDto networkElementGroupDto = getExistingNetworkElementGroup(negData.getUuid());

        assertTrue(Objects.requireNonNull(networkElementGroupDto.getLastSuccessfulSyncTime()).isAfter(timeBeforeSync));
    }

    @Step("Check that lastSuccessfulSyncTime has been set for network element group")
    public void checkNetworkElementGroupIsUpdatedWithLastSuccessfulSyncTime(String uuid, OffsetDateTime timeBeforeSync) {
        NetworkElementGroupDto networkElementGroupDto = getExistingNetworkElementGroup(uuid);

        assertTrue(Objects.requireNonNull(networkElementGroupDto.getLastSuccessfulSyncTime()).isAfter(timeBeforeSync));
    }

    @Step("Check that lifecycle state and operational state have been updated for network element")
    public void checkNetworkElementIsUpdatedWithNewStates(A4NetworkElement neData, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkElementDto networkElementDto = getExistingNetworkElement(neData.getUuid());

        assertEquals(networkElementDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkElementDto.getOperationalState(), expectedNewOperationalState);
        assertEquals(Objects.requireNonNull(networkElementDto.getLastSuccessfulSyncTime()).toString().substring(0, 12),
                OffsetDateTime.now().toString().substring(0, 12));
    }

    @Step("Check that lastSuccessfulSyncTime has been set for network element")
    public void checkNetworkElementIsUpdatedWithLastSuccessfulSyncTime(A4NetworkElement neData, OffsetDateTime timeBeforeSync) {
        checkNetworkElementIsUpdatedWithLastSuccessfulSyncTime(neData.getUuid(), timeBeforeSync);
    }

    @Step("Check that lastSuccessfulSyncTime has been set for network element")
    public void checkNetworkElementIsUpdatedWithLastSuccessfulSyncTime(String neUuid, OffsetDateTime timeBeforeSync) {
        NetworkElementDto networkElementDto = getExistingNetworkElement(neUuid);

        assertTrue(Objects.requireNonNull(networkElementDto.getLastSuccessfulSyncTime()).isAfter(timeBeforeSync));
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
            //assertEquals(networkElementDtoUnderTest.get().getDescription(), a4ImportCsvLine.getNeDescription());  // empty at plural-import
            assertEquals(networkElementDtoUnderTest.get().getVpsz(), a4ImportCsvLine.getNeVpsz());
        });
    }

    @Step("Check that Ports are created after CSV Import")
    public void checkNetworkElementPortsByImportCsvData(A4ImportCsvData a4ImportCsvData) {
        AtomicReference<NetworkElementDto> networkElementDtoUnderTest = new AtomicReference<>(new NetworkElementDto());
        AtomicReference<List<NetworkElementPortDto>> networkElementPortDtoUnderTest = new AtomicReference<>(new ArrayList<>());

        a4ImportCsvData.getCsvLines().forEach(a4ImportCsvLine -> {
            networkElementDtoUnderTest.set(getExistingNetworkElementByVpszFsz
                    (a4ImportCsvLine.getNeVpsz(), a4ImportCsvLine.getNeFsz()));

            assertNotNull(networkElementDtoUnderTest);

            networkElementPortDtoUnderTest.set(getNetworkElementPortsByNetworkElement
                    (networkElementDtoUnderTest.get().getUuid()));

            if (Objects.equals(networkElementDtoUnderTest.get().getType(), "A4-OLT-v1")) {
                assertEquals(networkElementPortDtoUnderTest.get().size(), 20);
            } else if (Objects.equals(networkElementDtoUnderTest.get().getType(), "A4-LEAF-Switch-v1")) {
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

    @Step("Check that lastSuccessfulSyncTime has been set for network element port")
    public void checkNetworkElementPortIsUpdatedWithLastSuccessfulSyncTime(A4NetworkElementPort nepData, OffsetDateTime timeBeforeSync) {
        NetworkElementPortDto networkElementPortDto = getExistingNetworkElementPort(nepData.getUuid());

        assertTrue(Objects.requireNonNull(networkElementPortDto.getLastSuccessfulSyncTime()).isAfter(timeBeforeSync));
    }

    @Step("Check that lifecycle state and operational state have been updated for network service profile (FTTH Access)")
    public void checkNetworkServiceProfileFtthAccessIsUpdatedWithNewStates(A4NetworkServiceProfileFtthAccess nspFtthData, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkServiceProfileFtthAccessDto networkServiceProfileFtthAccessDto = getExistingNetworkServiceProfileFtthAccess(nspFtthData.getUuid());

        assertEquals(networkServiceProfileFtthAccessDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkServiceProfileFtthAccessDto.getOperationalState(), expectedNewOperationalState);

    }

    @Step("Check that lastSuccessfulSyncTime has been set for network service profile FTTH Access")
    public void checkNetworkServiceProfileFtthAccessIsUpdatedWithLastSuccessfulSyncTime(A4NetworkServiceProfileFtthAccess nspFtthData, OffsetDateTime timeBeforeSync) {
        NetworkServiceProfileFtthAccessDto networkServiceProfileFtthAccessDto = getExistingNetworkServiceProfileFtthAccess(nspFtthData.getUuid());

        assertTrue(Objects.requireNonNull(networkServiceProfileFtthAccessDto.getLastSuccessfulSyncTime()).isAfter(timeBeforeSync));
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

    @Step("Check that lastSuccessfulSyncTime has been set for network service profile A10Nsp")
    public void checkNetworkServiceProfileA10NspIsUpdatedWithLastSuccessfulSyncTime(A4NetworkServiceProfileA10Nsp nspA10Data, OffsetDateTime timeBeforeSync) {
        NetworkServiceProfileA10NspDto networkServiceProfileA10NspDto = getExistingNetworkServiceProfileA10Nsp(nspA10Data.getUuid());

        assertTrue(Objects.requireNonNull(networkServiceProfileA10NspDto.getLastSuccessfulSyncTime()).isAfter(timeBeforeSync));
    }

    @Step("Check that lifecycle state and operational state have been updated for network service profile (L2BSA)")
    public void checkNetworkServiceProfileL2BsaIsUpdatedWithNewStates(A4NetworkServiceProfileL2Bsa nspL2Data, String expectedNewOperationalState, String expectedNewLifecycleState) {
        NetworkServiceProfileL2BsaDto networkServiceProfileL2BsaDto = getExistingNetworkServiceProfileL2Bsa(nspL2Data.getUuid());

        assertEquals(networkServiceProfileL2BsaDto.getLifecycleState(), expectedNewLifecycleState);
        assertEquals(networkServiceProfileL2BsaDto.getOperationalState(), expectedNewOperationalState);
    }

    @Step("Check that lastSuccessfulSyncTime has been set for network service profile L2BSA")
    public void checkNetworkServiceProfileL2BsaIsUpdatedWithLastSuccessfulSyncTime(A4NetworkServiceProfileL2Bsa nspL2BsaData, OffsetDateTime timeBeforeSync) {
        NetworkServiceProfileL2BsaDto networkServiceProfileL2BsaDto = getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());

        assertTrue(Objects.requireNonNull(networkServiceProfileL2BsaDto.getLastSuccessfulSyncTime()).isAfter(timeBeforeSync));
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

    @Step("Check that lastSuccessfulSyncTime has been set for network element link")
    public void checkNetworkElementLinkIsUpdatedWithLastSuccessfulSyncTime(A4NetworkElementLink nelData, OffsetDateTime timeBeforeSync) {
        NetworkElementLinkDto networkElementLinkDto = getExistingNetworkElementLink(nelData.getUuid());

        assertTrue(Objects.requireNonNull(networkElementLinkDto.getLastSuccessfulSyncTime()).isAfter(timeBeforeSync));
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
    public void deleteA4NetworkElementGroupsRecursively(String negName) {
        // NEG name has to be unique, so let's delete by that, to avoid constraint violations for future tests
        final List<NetworkElementGroupDto> negList = getNetworkElementGroupsByName(negName);
        negList.forEach(neg -> {
            final List<NetworkElementDto> neList = getNetworkElementsByNegUuid(neg.getUuid());

            neList.forEach(this::deleteA4NetworkElementsRecursively);
            deleteTerminationPointsRecursively(neg.getUuid());
            deleteNetworkElementGroup(neg.getUuid());
        });
    }

    private void deleteA4NetworkElementsRecursively(NetworkElementDto ne) {
        final List<NetworkElementPortDto> nepList = getNetworkElementPortsByNetworkElement(ne.getUuid());

        nepList.forEach(this::deleteNetworkElementPortsRecursively);

        deleteNetworkElementNoChecks(ne.getUuid());
    }

    public void deleteA4NetworkElementsRecursivelyByUuid(String neUuid) {
        final List<NetworkElementPortDto> nepList = getNetworkElementPortsByNetworkElement(neUuid);

        nepList.forEach(this::deleteNetworkElementPortsRecursively);

        deleteNetworkElementNoChecks(neUuid);
    }

    @Step("Delete A4 test data recursively by provided NetworkElementGroupDto")
    public void deleteA4NetworkElementGroupsRecursively(NetworkElementGroupDto negData) {
        assertNotNull(negData);
        deleteA4NetworkElementGroupsRecursively(negData.getName());
    }

    @Step("Delete A4 test data recursively by provided A4NetworkElementGroup")
    public void deleteA4NetworkElementGroupsRecursively(A4NetworkElementGroup negData) {
        assertNotNull(negData);
        deleteA4NetworkElementGroupsRecursively(negData.getName());
    }

    private void deleteNetworkElementPortsRecursively(NetworkElementPortDto nep) {
        deleteNetworkElementLinksConnectedToNePort(nep.getUuid());
        deleteTerminationPointsRecursively(nep.getUuid());
        deleteNetworkElementPort(nep.getUuid());
    }

    @Step("Delete all Termination Points, including all connected NSPs (FtthAccess, A10Nsp, L2Bsa)")
    public void deleteTerminationPointsRecursively(String uuid) {
        final List<TerminationPointDto> tpList = getTerminationPointsByNePort(uuid);

        tpList.forEach(tp -> {
            deleteNetworkServiceProfilesL2BsaConnectedToTerminationPoint(tp.getUuid());
            deleteNetworkServiceProfilesFtthAccessConnectedToTerminationPoint(tp.getUuid());
            deleteNetworkServiceProfilesA10NspConnectedToTerminationPoint(tp.getUuid());
            deleteTerminationPoint(tp.getUuid());
        });
    }

    @Step("Delete all Network Elements and Network Element Groups listed in the CSV")
    public void deleteA4TestDataRecursively(A4ImportCsvData csvData) {
        final List<String> negNameList = getDistinctListOfNegNamesFromCsvData(csvData);
        negNameList.forEach(
                this::deleteA4NetworkElementGroupsRecursively
        );

        // Normally the above lines delete all NEs under the NEGs as well. However, there could be old NE test data
        // with same VPSZ / FSZ in inventory, connected to other NEGs. To catch that situation as well, we also delete
        // the NEs explicitly.
        final List<Pair<String, String>> neEndszList = getDistinctListOfNeEndszsFromCsvData(csvData);
        neEndszList.forEach(endsz ->
                deleteA4NetworkElementsRecursively(endsz.first(), endsz.second())
        );
    }

    @Step("Delete NE by all unique constraints ztpIdent and endsz, also recursively deletes as children")
    public void deleteA4NetworkElementsRecursively(A4NetworkElement ne) {
        // NE VPSZ & FSZ has to be unique, so let's delete by that, to avoid constraint violations for future tests
        deleteA4NetworkElementsRecursively(ne.getZtpIdent());
        deleteA4NetworkElementsRecursively(ne.getVpsz(), ne.getFsz());
    }

    @Step("Delete NE by all unique constraints ztpIdent and endsz, also recursively deletes as children")
    public void deleteA4NetworkElementsRecursivelyDto(NetworkElementDto ne) {
        // NE VPSZ & FSZ has to be unique, so let's delete by that, to avoid constraint violations for future tests
        deleteA4NetworkElementsRecursively(ne.getZtpIdent());
        deleteA4NetworkElementsRecursively(ne.getVpsz(), ne.getFsz());
    }

    public void deleteA4NetworkElementsRecursively(String vpsz, String fsz) {
        final List<NetworkElementDto> neList = getNetworkElementsByVpszFsz(vpsz, fsz);
        neList.forEach(this::deleteA4NetworkElementsRecursively);
    }

    private void deleteA4NetworkElementsRecursively(String ztpIdent) {
        final List<NetworkElementDto> neList = getNetworkElementsByZtpIdent(ztpIdent);
        neList.forEach(this::deleteA4NetworkElementsRecursively);
    }

    @Step("Delete NEP by functional label, also recursively deletes as children")
    public void deleteA4NetworkElementPortsRecursively(A4NetworkElementPort nep, A4NetworkElement ne) {
        // NEP functional label & NE endsz has to be unique, so let's delete by that, to avoid constraint violations for future tests
        deleteA4NetworkElementPortsRecursively(nep.getFunctionalPortLabel(), ne.getVpsz(), ne.getFsz());
    }

    @Step("Delete NEP by functional label, also recursively deletes as children")
    public void deleteA4NetworkElementPortsRecursively(String nepFunctionalLabel, String neVpsz, String neFsz) {
        // NEP functional label & NE endsz has to be unique, so let's delete by that, to avoid constraint violations for future tests
        final List<NetworkElementPortDto> nepList = getNetworkElementPortsByFunctionalLabel(nepFunctionalLabel, getEndsz(neVpsz, neFsz));
        nepList.forEach(this::deleteNetworkElementPortsRecursively);
    }

    @Step("Get list of NEPs by functional label")
    public List<NetworkElementPortDto> getNetworkElementPortsByFunctionalLabel(String functionalLabel, String endsz) {
        return a4ResourceInventory
                .networkElementPorts()
                .findNetworkElementPorts()
                .logicalLabelQuery(functionalLabel)
                .endSzQuery(endsz)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete NSP FTTH-Access by line id and by ont serial number")
    public void deleteNspFtthAccess(A4NetworkServiceProfileFtthAccess nspFtthAccess) {
        deleteNspFtthAccess(nspFtthAccess.getLineId(), nspFtthAccess.getOntSerialNumber());
    }

    public void deleteNspFtthAccess(NetworkServiceProfileFtthAccessDto nspFtthAccess) {
        deleteNspFtthAccess(nspFtthAccess.getLineId(), nspFtthAccess.getOntSerialNumber());
    }

    public void deleteNspFtthAccess(String lineId, String ontSerialNo) {
        List<NetworkServiceProfileFtthAccessDto> nspFtthList;

        // NSP lineId (& lifecycle state) has to be unique, so let's delete by that, to avoid constraint violations for future tests
        nspFtthList = getNetworkServiceProfilesFtthAccessByLineId(lineId);
        nspFtthList.forEach(nspFtth ->
                deleteNetworkServiceProfileFtthAccess(nspFtth.getUuid())
        );

        // NSP ont serial number (& lifecycle state) has to be unique, so let's delete by that, to avoid constraint violations for future tests
        nspFtthList = getNetworkServiceProfilesFtthAccessByOntSerialNumber(ontSerialNo);
        nspFtthList.forEach(nspFtth ->
                deleteNetworkServiceProfileFtthAccess(nspFtth.getUuid())
        );
    }

    public void deleteNspsL2Bsa(A4NetworkServiceProfileL2Bsa nspL2Bsa) {
        // NSP lineId has to be unique, so let's delete by that, to avoid constraint violations for future tests

        final String lineId = nspL2Bsa.getLineId();
        final List<NetworkServiceProfileL2BsaDto> nspL2List = getNetworkServiceProfilesL2BsaByLineId(lineId);

        nspL2List.forEach(nspL2 ->
                deleteNetworkServiceProfileL2Bsa(nspL2.getUuid())
        );
    }

    @Step("Delete NSP L2BSA by line id")
    public void deleteNspsL2Bsa(String lineId) {
        // NSP lineId has to be unique, so let's delete by that, to avoid constraint violations for future tests

        final List<NetworkServiceProfileL2BsaDto> nspL2List = getNetworkServiceProfilesL2BsaByLineId(lineId);

        nspL2List.forEach(nspL2 ->
                deleteNetworkServiceProfileL2Bsa(nspL2.getUuid())
        );
    }

    @Step("Get list of NSP FTTH-Access by line id")
    public List<NetworkServiceProfileFtthAccessDto> getNetworkServiceProfilesFtthAccessByLineId(String lineId) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfilesFtthAccess()
                .lineIdQuery(lineId)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get list of NSP FTTH-Access by ont serial number")
    public List<NetworkServiceProfileFtthAccessDto> getNetworkServiceProfilesFtthAccessByOntSerialNumber(String ontSerialNo) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfilesFtthAccess()
                .ontSerialNumberQuery(ontSerialNo)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Get list of NSP L2BSA by line id")
    public List<NetworkServiceProfileL2BsaDto> getNetworkServiceProfilesL2BsaByLineId(String lineId) {
        return a4ResourceInventory
                .networkServiceProfilesL2Bsa()
                .findNetworkServiceProfilesL2Bsa()
                .lineIdQuery(lineId)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    // NEGs (by name) can appear in CSV multiple times. We only need to run through cleanup once per NEG
    private List<String> getDistinctListOfNegNamesFromCsvData(A4ImportCsvData csvData) {
        return csvData.getCsvLines()
                .stream()
                .map(A4ImportCsvLine::getNegName)
                .distinct()
                .collect(Collectors.toList());
    }

    // NEs (by endsz) can appear in CSV multiple times. We only need to run through cleanup once per NE
    private List<Pair<String, String>> getDistinctListOfNeEndszsFromCsvData(A4ImportCsvData csvData) {
        List<Pair<String, String>> endszList = new ArrayList<>();
        csvData.getCsvLines().forEach(csvLine ->
                endszList.add(new Pair<>(csvLine.getNeVpsz(), csvLine.getNeFsz()))
        );

        return endszList.stream().distinct().collect(Collectors.toList());
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

    public void createTerminationPoint(TerminationPointDto tpData) {
        a4ResourceInventory
                .terminationPoints()
                .createOrUpdateTerminationPoint()
                .body(tpData)
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

    public void createNetworkElementLink(NetworkElementLinkDto nel) {
        a4ResourceInventory
                .networkElementLinks()
                .createOrUpdateNetworkElementLink()
                .body(nel)
                .uuidPath(nel.getUuid())
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

    public void createNetworkServiceProfileFtthAccess(NetworkServiceProfileFtthAccessDto nspData) {
        a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .createOrUpdateNetworkServiceProfileFtthAccess()
                .body(nspData)
                .uuidPath(nspData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create new NetworkServiceProfileFtthAccess in A4 resource inventory")
    public void createNetworkServiceProfileFtthAccessWithPortReference(A4NetworkServiceProfileFtthAccess nspData,
                                                                       A4TerminationPoint tpData,
                                                                       A4NetworkElementPort nepData) {
        NetworkServiceProfileFtthAccessDto nspDto = new A4ResourceInventoryMapper()
                .getNetworkServiceProfileFtthAccessDto(nspData, tpData, nepData);

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

    public void createNetworkServiceProfileA10Nsp(NetworkServiceProfileA10NspDto nspData) {
        a4ResourceInventory
                .networkServiceProfilesA10Nsp()
                .createOrUpdateNetworkServiceProfileA10Nsp()
                .body(nspData)
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

    public void createNetworkServiceProfileL2Bsa(NetworkServiceProfileL2BsaDto nspData) {
        a4ResourceInventory
                .networkServiceProfilesL2Bsa()
                .createOrUpdateNetworkServiceProfileL2Bsa()
                .body(nspData)
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
