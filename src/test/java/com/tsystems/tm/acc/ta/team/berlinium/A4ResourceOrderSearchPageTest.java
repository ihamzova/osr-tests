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
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderDetailPageRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderSearchPageRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderMainDataDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrderItemStateType;
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

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.PUBLIC_REFERENCE_ID;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@ServiceLog({A4_RESOURCE_INVENTORY_MS, A4_RESOURCE_INVENTORY_UI_MS, A4_RESOURCE_INVENTORY_BFF_PROXY_MS, A4_RESOURCE_ORDER_ORCHESTRATOR_MS})
@Epic("OS&R")
public class A4ResourceOrderSearchPageTest extends GigabitTest {

    private final int numberOfROColumns = 7;
    private final A4ResourceOrderSearchPageRobot a4ResourceOrderSearchPageRobot = new A4ResourceOrderSearchPageRobot();
    private final A4ResourceOrderDetailPageRobot a4ResourceOrderDetailPageRobot = new A4ResourceOrderDetailPageRobot();
    private final A4ResourceOrderRobot a4ResourceOrderRobot = new A4ResourceOrderRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final String DEFAULT_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String publicReferenceId = "A1000858-" + UUID.randomUUID();
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
        String roId = sendResourceOrder(ro);
        ro.setId(roId);

    }

    @BeforeMethod
    void setup() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    private ResourceOrder initResourceOrder(A4NetworkElementLink nelData) {
        ResourceOrder resourceOrder = a4ResourceOrderRobot.buildResourceOrder();
        a4ResourceOrderRobot.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData, resourceOrder);
        a4ResourceOrderRobot.setCharacteristicValue(PUBLIC_REFERENCE_ID, publicReferenceId, DEFAULT_ORDER_ITEM_ID, resourceOrder);
        return resourceOrder;
    }

    private String sendResourceOrder(ResourceOrder resourceOrder) {
        String roId = a4ResourceOrderRobot.sendPostResourceOrder(resourceOrder); // case-sensitive problem
        sleepForSeconds(10);
        return roId;
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
    @Description("test RO search page of A4 browser, all checkboxes without publicReferenceId")
    public void testRoSearchAllCheckboxesWithoutPublicReferenceId() {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.clickA10NSPResourceOrder();
        a4ResourceOrderSearchPageRobot.selectCompleted();
        a4ResourceOrderSearchPageRobot.selectInProgress();
        a4ResourceOrderSearchPageRobot.selectRejected();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(SleeperInSec);// wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / numberOfROColumns); // 7 is the number of columns

        // get ROs from DB
        List<ResourceOrderMainDataDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByPublicReferenceIdFromDb(""); // or publicReferenceId

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

        assertEquals(roCollection.size() / numberOfROColumns, sortedRoList.size());
        // assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText().trim(), sortedRoList.get(0).getId().trim()); // RO-ID
        assertEquals(roCollection.get(2).innerText().trim(), sortedRoList.get(0).getExternalId().trim()); // ext ID
        assertEquals(roCollection.get(5).innerText().trim(), sortedRoList.get(0).getOrderDate().trim()); // Order Date

        a4ResourceOrderSearchPageRobot.clickDetailLinkForFirstROInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();

        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(sortedRoList.get(0).getId());


        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        // assertEquals(roiCollection.size()/8, Objects.requireNonNull(sortedRoList.get(0).getOrderItem()).size());// different number of columns
        assertEquals(roiCollection.get(0).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText().trim(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());

    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, no checkbox without public reference id")
    public void testRoSearchNoCheckboxWithoutPublicReferenceId() {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.clickA10NSPResourceOrder();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(SleeperInSec);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / numberOfROColumns);

        // get ROs from DB
        List<ResourceOrderMainDataDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByPublicReferenceIdFromDb(""); // or publicReferenceId

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

        assertEquals(roCollection.size() / numberOfROColumns, sortedRoList.size());
        assertEquals(roCollection.get(1).innerText().trim(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(2).innerText().trim(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(5).innerText().trim(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickDetailLinkForFirstROInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();


        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(sortedRoList.get(0).getId());

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        // assertEquals(roiCollection.size()/8, Objects.requireNonNull(sortedRoList.get(0).getOrderItem()).size());// different number of columns
        assertEquals(roiCollection.get(0).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText().trim(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());

    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, no checkbox with public reference id")
    public void testRoSearchNoCheckboxWithPublicReferenceId() {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.clickA10NSPResourceOrder();
        a4ResourceOrderSearchPageRobot.enterRoPublicReferenceId(publicReferenceId);
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(8);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / numberOfROColumns);

        // get ROs from DB
        List<ResourceOrderMainDataDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByPublicReferenceIdFromDb(publicReferenceId);

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

        assertEquals(roCollection.size() / numberOfROColumns, sortedRoList.size());
        assertEquals(roCollection.get(1).innerText().trim(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(2).innerText().trim(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(5).innerText().trim(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickDetailLinkForFirstROInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();

        System.out.println("sortedRoList.get(0).getId(): " +  sortedRoList.get(0).getId());

        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(sortedRoList.get(0).getId());

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        // assertEquals(roiCollection.size()/8, Objects.requireNonNull(sortedRoList.get(0).getOrderItem()).size());// different number of columns
        assertEquals(roiCollection.get(0).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText().trim(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());

    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, completed with publicReferenceId")
    public void testRoSearchCompletedWithPublicReferenceId() {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();

        a4ResourceOrderSearchPageRobot.clickA10NSPResourceOrder();
        a4ResourceOrderSearchPageRobot.enterRoPublicReferenceId(publicReferenceId);
        a4ResourceOrderSearchPageRobot.selectCompleted();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(18);// wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / numberOfROColumns);

        // get ROs from DB, filter completed
        List<ResourceOrderMainDataDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByPublicReferenceIdFromDb(publicReferenceId);
        System.out.println("+++ number of all ROs in DB with publicReferenceId: " + allRoList.size());
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
        assertEquals(roCollection.size() / numberOfROColumns, filteredRoList.size());
        assertEquals(roCollection.get(1).innerText().trim(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(2).innerText().trim(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(5).innerText().trim(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickDetailLinkForFirstROInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();


        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(sortedRoList.get(0).getId());


        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        //  assertEquals(roiCollection.size()/8, Objects.requireNonNull(sortedRoList.get(0).getOrderItem()).size());// different number of columns
        assertEquals(roiCollection.get(0).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText().trim(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, rejected and inprogress with publicReferenceId")
    public void testRoSearchRejectedInprogressWithPublicReferenceId() {
        a4ResourceOrderRobot.deleteA4TestDataRecursively(ro);
        //creating a RO with wrong LBZ to provoke RO status = rejected
        assertNotNull(ro.getOrderItem());
        Objects.requireNonNull(ro.getOrderItem().get(0).getResource()).setName("x");

        ro.setId(null);
        String roId = sendResourceOrder(ro);

        ro.setId(roId);
        a4ResourceOrderSearchPageRobot.openRoSearchPage();

        a4ResourceOrderSearchPageRobot.clickA10NSPResourceOrder();
        a4ResourceOrderSearchPageRobot.enterRoPublicReferenceId(publicReferenceId);
        a4ResourceOrderSearchPageRobot.selectInProgress();
        a4ResourceOrderSearchPageRobot.selectRejected();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        sleepForSeconds(1);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : " + roCollection.size() / numberOfROColumns);

        // search-page
        assertEquals(roCollection.get(1).innerText().trim(), ro.getId()); // RO-ID
        assertEquals(roCollection.get(2).innerText().trim(), ro.getExternalId()); // ext ID
        assertEquals(roCollection.get(3).innerText().trim(), publicReferenceId); // publicReferenceId

        a4ResourceOrderSearchPageRobot.clickDetailLinkForFirstROInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();

        // the list consists of MainDto without items, so we need to load the ro itself again with full data
        ResourceOrderDto resourceOrderDto = a4ResourceOrderRobot.getResourceOrderFromDb(ro.getId());

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), ro.getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), ro.getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), ResourceOrderItemStateType.REJECTED.toString());

        assertEquals(roiCollection.get(0).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getAction());
        assertEquals(roiCollection.get(2).innerText().trim(), Objects.requireNonNull(Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getResourceRefOrValueName())); // lbz
        assertEquals(roiCollection.get(3).innerText().trim(), Objects.requireNonNull(resourceOrderDto.getOrderItem()).get(0).getState());
    }
}
