package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryServiceV4Client;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.v4.client.model.*;
import io.qameta.allure.Step;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static org.testng.Assert.*;

public class A4ResourceInventoryServiceV4Robot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_NOT_FOUND_404 = 404;

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


    @Step("List all Network Service Profile Ftth Access from v4 API")
    public List<NspFtthAccess> getAllNetworkServiceProfilesFtthAccessV4() {
        return a4ResourceInventoryService
                .nspFtthAccess()
                .listNspFtthAccess()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }



    @Step("Read one Network Service Profile Ftth Access from v4 API")
    public NspFtthAccess getNetworkServiceProfileFtthAccessV4ByUuid(String uuid) {
        return a4ResourceInventoryService
                .nspFtthAccess()
                .retrieveNspFtthAccess()
                .idPath(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }



    @Step("Read Network Service Profile Ftth Access via OntSerialNumber from v4 API")
    public List<NspFtthAccess> getNetworkServiceProfilesFtthAccessV4ByOntSerialNumber(String ontSerialNumber) {
        return a4ResourceInventoryService
                .nspFtthAccess()
                .listNspFtthAccess()
                .ontSerialNumberQuery(ontSerialNumber)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("Read Network Service Profile Ftth Access via Line Id from v4 API")
    public List<NspFtthAccess> getNetworkServiceProfilesFtthAccessV4ByLineId(String lineId) {
        return a4ResourceInventoryService
                .nspFtthAccess()
                .listNspFtthAccess()
                .lineIdQuery(lineId)
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
    public List<NetworkElementGroup> getAllNetworkElementGroupsV4() {
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
        List<NetworkElementGroup> negList = getAllNetworkElementGroupsV4();
        assertEquals(negList.size(), negDataList.size());
    }

    public void checkIfNetworkElementGroupExistsByUuid(A4NetworkElementGroup negData) {
        String uuid = negData.getUuid();
        NetworkElementGroup neg = getNetworkElementGroupV4(uuid);
        assertEquals(neg.getId(), negData.getUuid());
        assertEquals(neg.getLifecycleState().toString(), negData.getLifecycleState());
        assertEquals(neg.getOperationalState().toString(), negData.getOperationalState());
    }

    public void checkNotFoundErrorForNonExistendNeg() {
        String uuid = String.valueOf(UUID.randomUUID());

        a4ResourceInventoryService
            .networkElementGroup()
            .retrieveNetworkElementGroup()
            .idPath(uuid)
            .execute(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));

    }

    @Step("Read all TerminationPoints as list from v4 API")
    public List<TerminationPoint> getAllTerminationPointsV4() {
        return a4ResourceInventoryService.terminationPoint()
                .listTerminationPoint()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Read all TerminationPoints as list from v4 API")
    public List<TerminationPoint> getTerminationPointsV4ByPort(String nepUuid) {
        return a4ResourceInventoryService.terminationPoint()
                .listTerminationPoint()
                .parentUuidQuery(nepUuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    public void checkIfTerminationPointExists(A4TerminationPoint tpData) {
        List<TerminationPoint> tpList = getAllTerminationPointsV4();

        TerminationPoint tp = tpList.stream()
                .filter(i -> tpData.getUuid().equals(i.getId()))
                .findAny()
                .orElse(null);

        assertNotNull(tp);
    }

    public void checkIfTerminationPointExistsByPort(A4TerminationPoint tpData, A4NetworkElementPort nepData) {
        List<TerminationPoint> tpList = getTerminationPointsV4ByPort(nepData.getUuid());

        TerminationPoint tp = tpList.stream()
                .filter(i -> tpData.getUuid().equals(i.getId()))
                .findAny()
                .orElse(null);

        assertNotNull(tp);
        assertEquals(tp.getResourceRelationship().get(0).getResource().getId(), nepData.getUuid());
    }



    public void checkIfNetworkServiceProfileFtthAccessExists(A4NetworkServiceProfileFtthAccess nspData) {
        List<NspFtthAccess> nspList = getAllNetworkServiceProfilesFtthAccessV4();

        NspFtthAccess nsp = nspList.stream()
                .filter(i -> nspData.getUuid().equals(i.getId()))
                .findAny()
                .orElse(null);

        assertNotNull(nsp);
    }


    public void checkIfNetworkServiceProfilesFtthAccessExistsByOntSerialNumber(List<A4NetworkServiceProfileFtthAccess> nspDataList, String ontSerialNumber) {
        List<NspFtthAccess> nspList = getNetworkServiceProfilesFtthAccessV4ByOntSerialNumber(ontSerialNumber);


        List<A4NetworkServiceProfileFtthAccess> filteredList = nspDataList.stream()
                .filter(elementFromNspDataList -> nspList.stream()
                        .anyMatch(elementFromNspList ->
                                elementFromNspDataList.getOntSerialNumber().equals(ontSerialNumber) &&
                                        elementFromNspList.getOntSerialNumber().equals(elementFromNspDataList.getOntSerialNumber())))
                .collect(Collectors.toList());

        assertEquals(nspList.size(), filteredList.size());


      //  assertTrue(nspDataList.containsAll(nspList));

    }


    public void checkIfNetworkServiceProfilesFtthAccessExistsByLineId(List<A4NetworkServiceProfileFtthAccess> nspDataList, String lineId) {
        List<NspFtthAccess> nspList = getNetworkServiceProfilesFtthAccessV4ByLineId(lineId);


//       assertTrue(nspDataList.containsAll(nspList) );



        List<A4NetworkServiceProfileFtthAccess> filteredList = nspDataList.stream()
                .filter(elementFromNspDataList -> nspList.stream()
                        .anyMatch(elementFromNspList ->
                                elementFromNspDataList.getLineId().equals(lineId) &&
                                        elementFromNspList.getLineId().equals(elementFromNspDataList.getLineId())))
                .collect(Collectors.toList());


        assertEquals(nspList.size(), filteredList.size());


    }



    @Step("Read all NetworkElementLink as list from v4 API")
    public List<NetworkElementLink> getAllNetworkElementLinksV4() {
        return a4ResourceInventoryService.networkElementLink()
                .listNetworkElementLink()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Read all NetworkElementLink as list from v4 API")
    public List<NetworkElementLink> getNetworkElementLinksV4ByLbz(String lbz) {
        return a4ResourceInventoryService.networkElementLink()
                .listNetworkElementLink()
                .lbzQuery(lbz)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Read all NetworkElementLink as list from v4 API")
    public List<NetworkElementLink> getNetworkElementLinksV4ByLsz(String lsz) {
        return a4ResourceInventoryService.networkElementLink()
                .listNetworkElementLink()
                .lszQuery(lsz)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Read all NetworkElementLink as list from v4 API")
    public List<NetworkElementLink> getNetworkElementLinksV4ByUeWegId(String ueWegId) {
        return a4ResourceInventoryService.networkElementLink()
                .listNetworkElementLink()
                .ueWegIdQuery(ueWegId)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }



    public void checkIfNetworkElementLinkExists(A4NetworkElementLink nelData) {
        List<NetworkElementLink> nelList = getAllNetworkElementLinksV4();

        NetworkElementLink nel = nelList.stream()
                .filter(i -> nelData.getUuid().equals(i.getId()))
                .findAny()
                .orElse(null);

        assertNotNull(nel);
    }

    public void checkIfNetworkElementLinkExistsByLbz(A4NetworkElementLink nelData) {
        List<NetworkElementLink> nelList = getNetworkElementLinksV4ByLbz(nelData.getLbz());

        NetworkElementLink nel = nelList.stream()
                .filter(i -> nelData.getLbz().equals(i.getLbz()))
                .findAny()
                .orElse(null);

        assertNotNull(nel);
        assertEquals(nel.getLbz(), nelData.getLbz());
    }



    @Step("Read all Network Element Ports as list from v4 API")
    public List<NetworkElementPort> getAllNetworkElementPortsV4() {
        return a4ResourceInventoryService.networkElementPort()
                .listNetworkElementPort()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }



    @Step("Read  Network Element Ports as list by NE Uuid from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByNetworkElementUuidV4(String uuid) {
        return a4ResourceInventoryService.networkElementPort()
                .listNetworkElementPort()
                .networkElementUuidQuery(uuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("Read  Network Element Ports as list by NE Endsz from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByEndszV4(String endsz) {
        return a4ResourceInventoryService.networkElementPort()
                .listNetworkElementPort()
                .networkElementEndszQuery(endsz)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("Read  Network Element Ports as list by NE Endsz and Port Type from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByEndszAndTypeV4(String endsz, String type) {
        return a4ResourceInventoryService.networkElementPort()
                .listNetworkElementPort()
                .networkElementEndszQuery(endsz)
                .portTypeQuery(type)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("Read  Network Element Ports as list by NE Endsz and functionalPortLabel from v4 API")
    public List<NetworkElementPort> getNetworkElementPortsByEndszAndFunctionalPortLabelV4(String endsz, String functionalPortLabel) {
        return a4ResourceInventoryService.networkElementPort()
                .listNetworkElementPort()
                .networkElementEndszQuery(endsz)
                .functionalPortLabelQuery(functionalPortLabel)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }



    public void checkIfNetworkElementPortsByEndszAndFunctionPortLabelAreInList(List<A4NetworkElementPort> nepDataList, String endsz, String functionalPortLabel) {
        List<NetworkElementPort> nepList = getNetworkElementPortsByEndszAndFunctionalPortLabelV4(endsz, functionalPortLabel);


        List<A4NetworkElementPort> filteredList = nepDataList.stream()
                .filter(elementFromNepDataList -> nepList.stream()
                        .anyMatch(elementFromNepList ->
                            (elementFromNepDataList.getNetworkElementEndsz().equals(endsz) &&
                                        elementFromNepList.getNetworkElementEndsz().equals(elementFromNepDataList.getNetworkElementEndsz())) &&
                                    (elementFromNepDataList.getFunctionalPortLabel().equals(functionalPortLabel) &&
                                    elementFromNepList.getFunctionalPortLabel().equals(elementFromNepDataList.getFunctionalPortLabel())))
                        )
                .collect(Collectors.toList());

        assertEquals(nepList.size(), filteredList.size());
        //  assertTrue(nspDataList.containsAll(nspList));

    }



    public void checkIfNetworkElementPortsByEndszAndTypeAreInList(List<A4NetworkElementPort> nepDataList, String endsz, String type) {
        List<NetworkElementPort> nepList = getNetworkElementPortsByEndszAndTypeV4(endsz, type);


        List<A4NetworkElementPort> filteredList = nepDataList.stream()
                .filter(elementFromNepDataList -> nepList.stream()
                        .anyMatch(elementFromNepList ->
                                (elementFromNepDataList.getNetworkElementEndsz().equals(endsz) &&
                                        elementFromNepList.getNetworkElementEndsz().equals(elementFromNepDataList.getNetworkElementEndsz())) &&
                                        (elementFromNepDataList.getType().equals(type) &&
                                                elementFromNepList.getPortType().equals(elementFromNepDataList.getType())))
                )
                .collect(Collectors.toList());

        assertEquals(nepList.size(), filteredList.size());
        //  assertTrue(nspDataList.containsAll(nspList));

    }

    public void checkIfNetworkElementPortsByEndszAreInList(List<A4NetworkElementPort> nepDataList, String endsz) {
        List<NetworkElementPort> nepList = getNetworkElementPortsByEndszV4(endsz);


        List<A4NetworkElementPort> filteredList = nepDataList.stream()
                .filter(elementFromNepDataList -> nepList.stream()
                        .anyMatch(elementFromNepList ->
                                elementFromNepDataList.getNetworkElementEndsz().equals(endsz) &&
                                        elementFromNepList.getNetworkElementEndsz().equals(elementFromNepDataList.getNetworkElementEndsz())))
                .collect(Collectors.toList());

        assertEquals(nepList.size(), filteredList.size());
        //  assertTrue(nspDataList.containsAll(nspList));

    }


    public void checkIfNetworkElementPortsByNetworkUuidAreInList(List<A4NetworkElementPort> nepDataList, String networkUuid) {
        List<NetworkElementPort> nepList = getNetworkElementPortsByNetworkElementUuidV4(networkUuid);


        List<A4NetworkElementPort> filteredList = nepDataList.stream()
                .filter(elementFromNepDataList -> nepList.stream()
                        .anyMatch(elementFromNepList ->
                                elementFromNepDataList.getNetworkElementUuid().equals(networkUuid) &&
                                        elementFromNepList.getNetworkElementUuid().equals(elementFromNepDataList.getNetworkElementUuid())))
                .collect(Collectors.toList());

        assertEquals(nepList.size(), filteredList.size());
        //  assertTrue(nspDataList.containsAll(nspList));

    }
}
