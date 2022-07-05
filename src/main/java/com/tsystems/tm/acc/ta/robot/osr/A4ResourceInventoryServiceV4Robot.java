package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceV4Client;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.model.*;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NOT_FOUND_404;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getPortNumberByFunctionalPortLabel;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static org.testng.Assert.*;

public class A4ResourceInventoryServiceV4Robot {

    private final A4ResourceInventoryServiceV4Client a4ResourceInventoryService = new A4ResourceInventoryServiceV4Client();

    @Step("Read all Network Elements as list from v4 API")
    public List<NetworkElement> getNetworkElementsV4ByEndsz(String endsz) {
        return a4ResourceInventoryService.getClient()
                .networkElement()
                .listNetworkElement()
                .endszQuery(endsz)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("List all Network Service Profile Ftth Access from v4 API")
    public List<NspFtthAccess> getAllNetworkServiceProfilesFtthAccessV4() {
        return a4ResourceInventoryService.getClient()
                .nspFtthAccess()
                .listNspFtthAccess()
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read one Network Service Profile Ftth Access from v4 API")
    public NspFtthAccess getNetworkServiceProfileFtthAccessV4ByUuid(String uuid) {
        return a4ResourceInventoryService.getClient()
                .nspFtthAccess()
                .retrieveNspFtthAccess()
                .idPath(uuid)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read Network Service Profile Ftth Access via OntSerialNumber from v4 API")
    public List<NspFtthAccess> getNetworkServiceProfilesFtthAccessV4ByOntSerialNumber(String ontSerialNumber) {
        return a4ResourceInventoryService.getClient()
                .nspFtthAccess()
                .listNspFtthAccess()
                .ontSerialNumberQuery(ontSerialNumber)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read Network Service Profile Ftth Access via Line Id from v4 API")
    public List<NspFtthAccess> getNetworkServiceProfilesFtthAccessV4ByLineId(String lineId) {
        return a4ResourceInventoryService.getClient()
                .nspFtthAccess()
                .listNspFtthAccess()
                .lineIdQuery(lineId)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    public void checkIfNetworkElementExistsByUuid(A4NetworkElement neData) {
        String endsz = neData.getVpsz() + "/" + neData.getFsz();
        List<NetworkElement> neList = getNetworkElementsV4ByEndsz(endsz);

        assertEquals(neList.size(), 1);
        assertEquals(neList.get(0).getVpsz(), neData.getVpsz());
        assertEquals(neList.get(0).getFsz(), neData.getFsz());
        assertEquals(neList.get(0).getId(), neData.getUuid());
        assertEquals(neList.get(0).getCategory(), neData.getCategory());
        assertEquals(neList.get(0).getKlsId(), neData.getKlsId());
    }

    @Step("Search all Network Element Groups by name as list from v4 API")
    public List<NetworkElementGroup> getNetworkElementGroupsV4ByName(String name) {
        return a4ResourceInventoryService.getClient().networkElementGroup()
                .listNetworkElementGroup()
                .nameQuery(name)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read one NetworkElement from v4 API")
    public NetworkElementGroup getNetworkElementGroupV4(String uuid) {
        return a4ResourceInventoryService.getClient()
                .networkElementGroup()
                .retrieveNetworkElementGroup()
                .idPath(uuid)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    public void checkIfNetworkElementGroupExistsByName(A4NetworkElementGroup negData) {
        String name = negData.getName();
        List<NetworkElementGroup> negList = getNetworkElementGroupsV4ByName(name);
        assertEquals(negList.size(), 1);
        assertEquals(negList.get(0).getId(), negData.getUuid());
        assertEquals(Objects.requireNonNull(negList.get(0).getLifecycleState()).toString(), negData.getLifecycleState());
    }

    public void checkIfNetworkElementGroupExistsByUuid(A4NetworkElementGroup negData) {
        String uuid = negData.getUuid();
        NetworkElementGroup neg = getNetworkElementGroupV4(uuid);
        assertEquals(neg.getId(), negData.getUuid());
        assertEquals(Objects.requireNonNull(neg.getLifecycleState()).toString(), negData.getLifecycleState());
        assertEquals(Objects.requireNonNull(neg.getOperationalState()).toString(), negData.getOperationalState());
    }

    public void checkNotFoundErrorForNonExistingNeg() {
        a4ResourceInventoryService.getClient()
                .networkElementGroup()
                .retrieveNetworkElementGroup()
                .idPath(UUID.randomUUID().toString())
                .execute(checkStatus(HTTP_CODE_NOT_FOUND_404));
    }

    @Step("Read all TerminationPoints as list from v4 API")
    public List<TerminationPoint> getAllTerminationPointsV4() {
        return a4ResourceInventoryService.getClient().terminationPoint()
                .listTerminationPoint()
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read all TerminationPoints as list from v4 API")
    public List<TerminationPoint> getTerminationPointsV4ByPort(String nepUuid) {
        return a4ResourceInventoryService.getClient().terminationPoint()
                .listTerminationPoint()
                .parentUuidQuery(nepUuid)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Check if list of all existing Termination Points (v4 API) contains at least one entry")
    public void checkIfAnyTerminationPointsExist(int minimalExpectedCount) {
        List<TerminationPoint> tpList = getAllTerminationPointsV4();
        assertTrue(tpList.size() >= minimalExpectedCount);
    }

    public void checkIfTerminationPointExistsByPort(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        List<String> tpV4UuidList = getTerminationPointsV4ByPort(nepData.getUuid())
                .stream()
                .map(TerminationPoint::getId)
                .collect(Collectors.toList());

        assertEquals(tpV4UuidList.size(), 1);
        assertEquals(tpV4UuidList.get(0), tpData.getUuid());
    }

    @Step("Check if list of all existing Network Service Profiles FTTH Access (v4 API) contains at least one entry")
    public void checkIfAnyNetworkServiceProfileFtthAccessesExist(int minimalExpectedCount) {
        List<NspFtthAccess> nspList = getAllNetworkServiceProfilesFtthAccessV4();
        assertTrue(nspList.size() >= minimalExpectedCount);
    }

    public void checkResourceRelationshipsByNetworkServiceProfileFtthAccess(A4NetworkServiceProfileFtthAccess nspData) {
        NspFtthAccess nspFtthAccess = getNetworkServiceProfileFtthAccessV4ByUuid(nspData.getUuid());
        List<ResourceRelationship> rrl = nspFtthAccess.getResourceRelationship();

        assertNotNull(rrl);
        assertEquals(rrl.size(), 2);

        ResourceRelationship resourceRelationshipTp = rrl.get(0);
        assertEquals(resourceRelationshipTp.getResource().getId(), nspFtthAccess.getTerminationPointUuid());

        ResourceRelationship resourceRelationshipNep = nspFtthAccess.getResourceRelationship().get(1);
        assertEquals(resourceRelationshipNep.getResource().getId(), nspData.getOltPortOntLastRegisteredOn());
    }

    public void checkIfNetworkServiceProfileFtthAccessExistsByOntSerialNumber(A4NetworkServiceProfileFtthAccess nspData) {
        List<String> nspV4UuidList = getNetworkServiceProfilesFtthAccessV4ByOntSerialNumber(nspData.getOntSerialNumber())
                .stream()
                .map(NspFtthAccess::getId)
                .collect(Collectors.toList());

        assertEquals(nspV4UuidList.size(), 1);
        assertEquals(nspV4UuidList.get(0), nspData.getUuid());
    }


    public void checkIfNetworkServiceProfileFtthAccessExistsByLineId(A4NetworkServiceProfileFtthAccess nspData) {
        List<String> nspV4UuidList = getNetworkServiceProfilesFtthAccessV4ByLineId(nspData.getLineId())
                .stream()
                .map(NspFtthAccess::getId)
                .collect(Collectors.toList());

        assertEquals(nspV4UuidList.size(), 1);
        assertEquals(nspV4UuidList.get(0), nspData.getUuid());
    }

    @Step("Read all NetworkElementLink as list from v4 API")
    public List<NetworkElementLink> getAllNetworkElementLinksV4() {
        return a4ResourceInventoryService.getClient().networkElementLink()
                .listNetworkElementLink()
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read all NetworkElementLink as list from v4 API")
    public List<NetworkElementLink> getNetworkElementLinksV4ByLbz(String lbz) {
        return a4ResourceInventoryService.getClient().networkElementLink()
                .listNetworkElementLink()
                .lbzQuery(lbz)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Check if list of all existing NetworkElementLink (v4 API) contains at least one entry")
    public void checkIfAnyNetworkElementLinksExist(int minimalExpectedCount) {
        List<NetworkElementLink> nelList = getAllNetworkElementLinksV4();
        assertTrue(nelList.size() >= minimalExpectedCount);
    }

    public void checkIfNetworkElementLinkExistsByLbz(A4NetworkElementLink nelData) {
        List<String> nelV4UuidList = getNetworkElementLinksV4ByLbz(nelData.getLbz())
                .stream()
                .map(NetworkElementLink::getId)
                .collect(Collectors.toList());

        assertEquals(nelV4UuidList.size(), 1);
        assertEquals(nelV4UuidList.get(0), nelData.getUuid());
    }

    @Step("Read  Network Element Ports as list by NE Uuid from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByNetworkElementUuidV4(String uuid) {
        return a4ResourceInventoryService.getClient().networkElementPort()
                .listNetworkElementPort()
                .networkElementUuidQuery(uuid)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read  Network Element Ports as list by NE Endsz from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByEndszV4(String endsz) {
        return a4ResourceInventoryService.getClient().networkElementPort()
                .listNetworkElementPort()
                .networkElementEndszQuery(endsz)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read  Network Element Ports as list by NE Endsz and Port Type from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByEndszAndTypeV4(String endsz, String type) {
        return a4ResourceInventoryService.getClient().networkElementPort()
                .listNetworkElementPort()
                .networkElementEndszQuery(endsz)
                .portTypeQuery(type)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read  Network Element Ports as list by NE Endsz and Port Type from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByEndszAndTypeAndPortnumberV4(String endsz, String type, String portNumber) {
        return a4ResourceInventoryService.getClient().networkElementPort()
                .listNetworkElementPort()
                .networkElementEndszQuery(endsz)
                .portTypeQuery(type)
                .portNumberQuery(portNumber)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Read  Network Element Ports as list by NE Endsz and functionalPortLabel from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByEndszAndFunctionalPortLabelV4(String endsz, String functionalPortLabel) {
        return a4ResourceInventoryService.getClient().networkElementPort()
                .listNetworkElementPort()
                .networkElementEndszQuery(endsz)
                .functionalPortLabelQuery(functionalPortLabel)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    public void checkIfNetworkElementPortExistsByEndszAndFunctionPortLabel(A4NetworkElement neData, A4NetworkElementPort nepData) {
        List<String> nepV4UuidList = getNetworkElementPortsByEndszAndFunctionalPortLabelV4(
                neData.getVpsz() + "/" + neData.getFsz(),
                nepData.getFunctionalPortLabel())
                .stream()
                .map(NetworkElementPort::getId)
                .collect(Collectors.toList());

        assertEquals(nepV4UuidList.size(), 1);
        assertEquals(nepV4UuidList.get(0), nepData.getUuid());
    }

    public void checkIfNetworkElementPortExistsByEndszAndType(A4NetworkElement neData, A4NetworkElementPort nepData) {
        List<String> nepV4UuidList = getNetworkElementPortsByEndszAndTypeV4(
                neData.getVpsz() + "/" + neData.getFsz(),
                nepData.getType())
                .stream()
                .map(NetworkElementPort::getId)
                .collect(Collectors.toList());

        assertEquals(nepV4UuidList.size(), 1);
        assertEquals(nepV4UuidList.get(0), nepData.getUuid());
    }

    public void checkIfNetworkElementPortsExistByEndszAndTypeAndPortnumber(A4NetworkElement neData, A4NetworkElementPort nepData) {
        List<String> nepV4UuidList = getNetworkElementPortsByEndszAndTypeAndPortnumberV4(
                neData.getVpsz() + "/" + neData.getFsz(),
                nepData.getType(),
                getPortNumberByFunctionalPortLabel(nepData.getFunctionalPortLabel()))
                .stream()
                .map(NetworkElementPort::getId)
                .collect(Collectors.toList());

        assertEquals(nepV4UuidList.size(), 1);
        assertEquals(nepV4UuidList.get(0), nepData.getUuid());
    }

    public void checkIfNetworkElementPortExistsByEndsz(A4NetworkElement neData, A4NetworkElementPort nepData) {
        List<String> nepV4UuidList = getNetworkElementPortsByEndszV4(neData.getVpsz() + "/" + neData.getFsz())
                .stream()
                .map(NetworkElementPort::getId)
                .collect(Collectors.toList());

        assertEquals(nepV4UuidList.size(), 1);
        assertEquals(nepV4UuidList.get(0), nepData.getUuid());
    }

    public void checkIfNetworkElementPortExistsByNetworkUuid(A4NetworkElement neData, A4NetworkElementPort nepData) {
        List<String> nepV4UuidList = getNetworkElementPortsByNetworkElementUuidV4(neData.getUuid())
                .stream()
                .map(NetworkElementPort::getId)
                .collect(Collectors.toList());

        assertEquals(nepV4UuidList.size(), 1);
        assertEquals(nepV4UuidList.get(0), nepData.getUuid());
    }

}
