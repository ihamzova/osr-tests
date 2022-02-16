package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderDetailPageRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderSearchPageRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderMainDataDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrderItemStateType;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.VUEP_PUBLIC_REFERENZ_NR;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@ServiceLog({A4_RESOURCE_INVENTORY_MS, A4_RESOURCE_INVENTORY_UI_MS, A4_RESOURCE_INVENTORY_BFF_PROXY_MS, A4_RESOURCE_ORDER_ORCHESTRATOR_MS})
@Epic("OS&R")
public class A4ResourceOrderSearchPageTest extends GigabitTest {

    private final A4ResourceOrderSearchPageRobot a4ResourceOrderSearchPageRobot = new A4ResourceOrderSearchPageRobot();
    private final A4ResourceOrderDetailPageRobot a4ResourceOrderDetailPageRobot = new A4ResourceOrderDetailPageRobot();
    private final A4ResourceOrderRobot a4ResourceOrderRobot = new A4ResourceOrderRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final String DEFAULT_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String vuep = "A1000858-" + UUID.randomUUID();
    private final int SleeperInSec = 20; // workaround while performance problems

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData1;
    private A4NetworkElement neData2;
    private A4NetworkElement neData3;
    private A4NetworkElementPort nepData1;
    private A4NetworkElementPort nepData2;
    private A4NetworkElementPort nepData3;
    private ResourceOrder ro;

    @BeforeClass()
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.NetworkElementGroupL2Bsa);

        neData1 = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementA10NspSwitch01);
        nepData1 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_100G_001);

        neData2 = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData2 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_002);

        neData3 = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementB);
        nepData3 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);

        A4NetworkElementLink nelData1 = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.networkElementLinkLcsInstalling);
        A4NetworkElementLink nelData2 = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

        A4NetworkServiceProfileA10Nsp nspA10Data1 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        A4TerminationPoint tpData1 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);

        UewegData uewegData1 = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.uewegA);
        UewegData uewegData2 = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.uewegB);

        cleanUp();

        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neData1, negData);
        a4ResourceInventory.createNetworkElement(neData2, negData);
        a4ResourceInventory.createNetworkElement(neData3, negData);
        a4ResourceInventory.createNetworkElementPort(nepData1, neData1);
        a4ResourceInventory.createNetworkElementPort(nepData2, neData2);
        a4ResourceInventory.createNetworkElementPort(nepData3, neData3);
        a4ResourceInventory.createNetworkElementLink(nelData1, nepData1, nepData2, neData1, neData2, uewegData1);
        a4ResourceInventory.createNetworkElementLink(nelData2, nepData1, nepData3, neData1, neData3, uewegData2);
        a4ResourceInventory.createTerminationPoint(tpData1, nepData1);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data1, tpData1);

        ro = initResourceOrder(nelData1);
        sendResourceOrder(ro);
    }

    @BeforeMethod
    void setup() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    private ResourceOrder initResourceOrder(A4NetworkElementLink nelData) {
        ResourceOrder resourceOrder = a4ResourceOrderRobot.buildResourceOrder();
        a4ResourceOrderRobot.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData, resourceOrder);
        a4ResourceOrderRobot.setCharacteristicValue(VUEP_PUBLIC_REFERENZ_NR, vuep, DEFAULT_ORDER_ITEM_ID, resourceOrder);
        return resourceOrder;
    }

    private void sendResourceOrder(ResourceOrder resourceOrder) {
        a4ResourceOrderRobot.sendPostResourceOrder(resourceOrder); // case-sensitive problem
        sleepForSeconds(10);
    }

    @AfterClass
    public void cleanUp() {
        // Delete all A4 data which might provoke problems because of unique constraints
        a4ResourceInventory.deleteA4NetworkElementGroupsRecursively(negData);
        a4ResourceInventory.deleteA4NetworkElementsRecursively(neData1);
        a4ResourceInventory.deleteA4NetworkElementsRecursively(neData2);
        a4ResourceInventory.deleteA4NetworkElementsRecursively(neData3);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepData1, neData1);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepData2, neData2);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepData3, neData3);

        a4ResourceOrderRobot.deleteA4TestDataRecursively(ro);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, all checkboxes without vuep")
    public void testRoSearchAllCheckboxesWithoutVuep() {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();

        a4ResourceOrderSearchPageRobot.selectCompleted();
        a4ResourceOrderSearchPageRobot.selectInProgress();
        a4ResourceOrderSearchPageRobot.selectRejected();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(SleeperInSec);// wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / 6);

        // get ROs from DB
        List<ResourceOrderMainDataDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(""); // or vuep

        // filter, also null
        List<ResourceOrderMainDataDto> filteredRoList;
        filteredRoList = allRoList
                .stream()
                .filter(group -> Objects.equals(group.getState(), "COMPLETED")
                        || Objects.equals(group.getState(), "completed")
                        || Objects.equals(group.getState(), "INPROGRESS")
                        || Objects.equals(group.getState(), "inprogress")
                        || Objects.equals(group.getState(), "REJECTED")
                        || Objects.equals(group.getState(), "rejected"))
                .collect(Collectors.toList());

        // sort
        List<ResourceOrderMainDataDto> sortedRoList;
        sortedRoList = filteredRoList
                .stream()
                .filter(x -> x.getId() != null)
                .sorted(Comparator.comparing(ResourceOrderMainDataDto::getId))
                .collect(Collectors.toList());

        System.out.println("+++ number of filtered ROs in DB : " + filteredRoList.size());

        assertEquals(roCollection.size() / 6, sortedRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();

        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(sortedRoList.get(0).getId());


        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        // assertEquals(roiCollection.size()/8, Objects.requireNonNull(sortedRoList.get(0).getOrderItem()).size());// different number of columns
        assertEquals(roiCollection.get(0).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());

    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, no checkbox without vuep")
    public void testRoSearchNoCheckboxWithoutVuep() {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(SleeperInSec);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / 6);

        // get ROs from DB
        List<ResourceOrderMainDataDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(""); // or vuep

        // filter, also null
        List<ResourceOrderMainDataDto> filteredRoList;
        filteredRoList = allRoList
                .stream()
                .filter(group -> Objects.equals(group.getState(), "COMPLETED")
                        || Objects.equals(group.getState(), "completed")
                        || Objects.equals(group.getState(), "INPROGRESS")
                        || Objects.equals(group.getState(), "inprogress")
                        || Objects.equals(group.getState(), "REJECTED")
                        || Objects.equals(group.getState(), "rejected"))
                .collect(Collectors.toList());

        // sort
        List<ResourceOrderMainDataDto> sortedRoList;
        sortedRoList = filteredRoList
                .stream()
                .filter(x -> x.getId() != null)
                .sorted(Comparator.comparing(ResourceOrderMainDataDto::getId))
                .collect(Collectors.toList());

        System.out.println("+++ number of filtered ROs in DB : " + filteredRoList.size());

        assertEquals(roCollection.size() / 6, sortedRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();


        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(sortedRoList.get(0).getId());

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        // assertEquals(roiCollection.size()/8, Objects.requireNonNull(sortedRoList.get(0).getOrderItem()).size());// different number of columns
        assertEquals(roiCollection.get(0).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());

    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, no checkbox with vuep")
    public void testRoSearchNoCheckboxWithVuep() {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.enterRoVuep(vuep);
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(8);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / 6);

        // get ROs from DB
        List<ResourceOrderMainDataDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(vuep);

        // filter, also null
        List<ResourceOrderMainDataDto> filteredRoList;
        filteredRoList = allRoList
                .stream()
                .filter(group -> Objects.equals(group.getState(), "COMPLETED")
                        || Objects.equals(group.getState(), "completed")
                        || Objects.equals(group.getState(), "INPROGRESS")
                        || Objects.equals(group.getState(), "inprogress")
                        || Objects.equals(group.getState(), "REJECTED")
                        || Objects.equals(group.getState(), "rejected"))
                .collect(Collectors.toList());

        // sort
        List<ResourceOrderMainDataDto> sortedRoList;
        sortedRoList = filteredRoList
                .stream()
                .filter(x -> x.getId() != null)
                .sorted(Comparator.comparing(ResourceOrderMainDataDto::getId))
                .collect(Collectors.toList());

        System.out.println("+++ number of filtered ROs in DB : " + filteredRoList.size());

        assertEquals(roCollection.size() / 6, sortedRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();


        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(sortedRoList.get(0).getId());

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        // assertEquals(roiCollection.size()/8, Objects.requireNonNull(sortedRoList.get(0).getOrderItem()).size());// different number of columns
        assertEquals(roiCollection.get(0).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());

    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, completed with vuep")
    public void testRoSearchCompletedWithVuep() {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.enterRoVuep(vuep);
        a4ResourceOrderSearchPageRobot.selectCompleted();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(8);// wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / 6);

        // get ROs from DB, filter completed
        List<ResourceOrderMainDataDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(vuep);
        System.out.println("+++ number of all ROs in DB with vuep: " + allRoList.size());
        // System.out.println("+++ allRoList: "+allRoList);

        // filter, also null
        List<ResourceOrderMainDataDto> filteredRoList;
        filteredRoList = allRoList
                .stream()
                .filter(group -> Objects.equals(group.getState(), "COMPLETED")
                        || Objects.equals(group.getState(), "completed"))
                .collect(Collectors.toList());

        // sort
        List<ResourceOrderMainDataDto> sortedRoList;
        sortedRoList = filteredRoList.stream()
                .filter(x -> x.getId() != null)
                .sorted(Comparator.comparing(ResourceOrderMainDataDto::getId))
                .collect(Collectors.toList());
        System.out.println("+++ number of filtered ROs in DB : " + sortedRoList.size());

        // search-page
        assertEquals(roCollection.size() / 6, filteredRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();


        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(sortedRoList.get(0).getId());


        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        //  assertEquals(roiCollection.size()/8, Objects.requireNonNull(sortedRoList.get(0).getOrderItem()).size());// different number of columns
        assertEquals(roiCollection.get(0).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, rejected and inprogress with vuep")
    public void testRoSearchRejectedInprogressWithVuep() {
        a4ResourceOrderRobot.deleteA4TestDataRecursively(ro);
        //creating a RO with wrong LBZ to provoke RO status = rejected
        assertNotNull(ro.getOrderItem());
        Objects.requireNonNull(ro.getOrderItem().get(0).getResource()).setName("x");
        sendResourceOrder(ro);

        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.enterRoVuep(vuep);
        a4ResourceOrderSearchPageRobot.selectInProgress();
        a4ResourceOrderSearchPageRobot.selectRejected();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(1);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / 6);

        // search-page
        assertEquals(roCollection.get(0).innerText(), ro.getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), ro.getExternalId()); // ext ID
        assertEquals(roCollection.get(2).innerText(), vuep); // vuep

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();

        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(ro.getId());

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), ro.getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), ro.getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), ResourceOrderItemStateType.REJECTED.toString());

        assertEquals(roiCollection.get(0).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());
    }
}
