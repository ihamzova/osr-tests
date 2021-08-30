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
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrder;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.VUEP_PUBLIC_REFERENZ_NR;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class A4ResourceOrderSearchPageTest extends GigabitTest {

    private final A4ResourceOrderSearchPageRobot a4ResourceOrderSearchPageRobot = new A4ResourceOrderSearchPageRobot();
    private final A4ResourceOrderDetailPageRobot a4ResourceOrderDetailPageRobot = new A4ResourceOrderDetailPageRobot();
    private final A4ResourceOrderRobot a4ResourceOrderRobot = new A4ResourceOrderRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final String DEFAULT_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String vuep = "A1000858";

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData1;
    private A4NetworkElement neData2;
    private A4NetworkElement neData3;
    private A4NetworkElementPort nepData1;
    private A4NetworkElementPort nepData2;
    private A4NetworkElementPort nepData3;
    private A4NetworkElementLink nelData1;
    private A4NetworkElementLink nelData2;
    private A4NetworkServiceProfileA10Nsp nspA10Data1;
    private A4TerminationPoint tpData1;
    private UewegData uewegData1;
    private UewegData uewegData2;

    private ResourceOrder ro;


    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

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

        nelData1 = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.networkElementLinkLcsInstalling);
        nelData2 = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

        nspA10Data1 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        tpData1 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);

        uewegData1 = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.uewegA);
        uewegData2 = osrTestContext.getData().getUewegDataDataProvider()
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

        ro = a4ResourceOrderRobot.buildResourceOrder();

        a4ResourceOrderRobot.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrderRobot.setCharacteristicValue(VUEP_PUBLIC_REFERENZ_NR, vuep, DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        //a4ResourceOrderRobot.sendPostResourceOrder(ro); // case-sensitive problem
        sleepForSeconds(10);
    }


    @AfterClass
    public void cleanUp() {
        a4ResourceInventory.deleteA4TestDataRecursively(negData);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, all checkboxes without vuep")
    public void testRoSearchAllCheckboxesWithoutVuep() throws InterruptedException {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();

        a4ResourceOrderSearchPageRobot.selectCompleted();
        a4ResourceOrderSearchPageRobot.selectInProgress();
        a4ResourceOrderSearchPageRobot.selectRejected();

        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        TimeUnit.SECONDS.sleep(15);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : "+roCollection.size()/6);

        // get ROs from DB
        List<ResourceOrderDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(""); // or vuep
        System.out.println("+++ number of ROs in DB : "+allRoList.size());

        // sort
        List<ResourceOrderDto> sortedRoList;
        sortedRoList = allRoList
                .stream().sorted(Comparator.comparing(ResourceOrderDto::getId))
                .collect(Collectors.toList());

        //assertEquals(roCollection.size()/6, sortedRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
        //  assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date is null in db

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();

        TimeUnit.SECONDS.sleep(2);  // wait for looking

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        assertEquals(roiCollection.size()/8, sortedRoList.get(0).getOrderItem().size());
        assertEquals(roiCollection.get(0).innerText(), sortedRoList.get(0).getOrderItem().get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), sortedRoList.get(0).getOrderItem().get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), sortedRoList.get(0).getOrderItem().get(0).getResource().getName()); // lbz
        assertEquals(roiCollection.get(3).innerText(), sortedRoList.get(0).getOrderItem().get(0).getState());

    }


    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, no checkbox without vuep")
    public void testRoSearchNoCheckboxWithoutVuep() throws InterruptedException {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        TimeUnit.SECONDS.sleep(15);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : "+roCollection.size()/6);

        // get ROs from DB
        List<ResourceOrderDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(""); // or vuep
        System.out.println("+++ number of ROs in DB : "+allRoList.size());

        // sort
        List<ResourceOrderDto> sortedRoList;
        sortedRoList = allRoList
                .stream().sorted(Comparator.comparing(ResourceOrderDto::getId))
                .collect(Collectors.toList());

      //assertEquals(roCollection.size()/6, sortedRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
      //  assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date is null in db

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();

        TimeUnit.SECONDS.sleep(2);  // wait for looking

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        assertEquals(roiCollection.size()/8, sortedRoList.get(0).getOrderItem().size());
        assertEquals(roiCollection.get(0).innerText(), sortedRoList.get(0).getOrderItem().get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), sortedRoList.get(0).getOrderItem().get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), sortedRoList.get(0).getOrderItem().get(0).getResource().getName()); // lbz
        assertEquals(roiCollection.get(3).innerText(), sortedRoList.get(0).getOrderItem().get(0).getState());

    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, no checkbox with vuep")
    public void testRoSearchNoCheckboxWithVuep() throws InterruptedException {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.enterRoVuep(vuep);
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        TimeUnit.SECONDS.sleep(15);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : "+roCollection.size()/6);

        // get ROs from DB
        List<ResourceOrderDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(vuep);
        System.out.println("+++ number of ROs in DB : "+allRoList.size());

        // sort
        List<ResourceOrderDto> sortedRoList;
        sortedRoList = allRoList
                .stream().sorted(Comparator.comparing(ResourceOrderDto::getId))
                .collect(Collectors.toList());
        System.out.println("+++ Größe sortierte Dto-Liste: "+sortedRoList.size());

       // assertEquals(roCollection.size()/6, sortedRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();
        TimeUnit.SECONDS.sleep(2);  // wait for looking

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        assertEquals(roiCollection.size()/8, sortedRoList.get(0).getOrderItem().size());
        assertEquals(roiCollection.get(0).innerText(), sortedRoList.get(0).getOrderItem().get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), sortedRoList.get(0).getOrderItem().get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), sortedRoList.get(0).getOrderItem().get(0).getResource().getName()); // lbz
        assertEquals(roiCollection.get(3).innerText(), sortedRoList.get(0).getOrderItem().get(0).getState());

    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, completed with vuep")
    public void testRoSearchCompletedWithVuep() throws InterruptedException {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.enterRoVuep(vuep);
        a4ResourceOrderSearchPageRobot.selectCompleted();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        TimeUnit.SECONDS.sleep(15);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();

        // get ROs from DB, filter completed
        List<ResourceOrderDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(vuep);

        List<ResourceOrderDto> filteredRoList;
        filteredRoList = allRoList
                .stream()
                .filter(group -> group.getState().equals("COMPLETED") )
                .collect(Collectors.toList());

        // sort
        List<ResourceOrderDto> sortedRoList;
                sortedRoList = filteredRoList
                .stream().sorted(Comparator.comparing(ResourceOrderDto::getId))
                .collect(Collectors.toList());

        // search-page
        assertEquals(roCollection.size()/6, filteredRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();
        TimeUnit.SECONDS.sleep(2);  // wait for looking

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        assertEquals(roiCollection.size()/8, sortedRoList.get(0).getOrderItem().size());
        assertEquals(roiCollection.get(0).innerText(), sortedRoList.get(0).getOrderItem().get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), sortedRoList.get(0).getOrderItem().get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), sortedRoList.get(0).getOrderItem().get(0).getResource().getName()); // lbz
        assertEquals(roiCollection.get(3).innerText(), sortedRoList.get(0).getOrderItem().get(0).getState());

    }


    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser, rejected and inprogress with vuep")
    public void testRoSearchRejectedInprogressWithVuep() throws InterruptedException {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.enterRoVuep(vuep);
        a4ResourceOrderSearchPageRobot.selectInProgress();
        a4ResourceOrderSearchPageRobot.selectRejected();
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();
        TimeUnit.SECONDS.sleep(15);  // wait for result

        // read ui
        ElementsCollection roCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : "+roCollection.size()/6);

        // get ROs from DB
        List<ResourceOrderDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(vuep);

        List<ResourceOrderDto> filteredRoList;
        filteredRoList = allRoList
                .stream()
                .filter(group -> group.getState().equals("INPROGRESS") || group.getState().equals("REJECTED"))
                .collect(Collectors.toList());

        // sort
        List<ResourceOrderDto> sortedRoList;
        sortedRoList = filteredRoList
                .stream().sorted(Comparator.comparing(ResourceOrderDto::getId))
                .collect(Collectors.toList());

        System.out.println("+++ number of ROs in sortierter Liste : "+sortedRoList.size());

        // search-page
        assertEquals(roCollection.size()/6, sortedRoList.size());
        assertEquals(roCollection.get(0).innerText(), sortedRoList.get(0).getId()); // RO-ID
        assertEquals(roCollection.get(1).innerText(), sortedRoList.get(0).getExternalId()); // ext ID
        assertEquals(roCollection.get(4).innerText(), sortedRoList.get(0).getOrderDate()); // Order Date

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        ElementsCollection roiCollection = a4ResourceOrderDetailPageRobot.getRoiElementsCollection();
        TimeUnit.SECONDS.sleep(2);  // wait for looking

        // detail-page head
        assertEquals(a4ResourceOrderDetailPageRobot.readRoId(), sortedRoList.get(0).getId()); // ro-id
        assertEquals(a4ResourceOrderDetailPageRobot.readExternalOrderId(), sortedRoList.get(0).getExternalId());
        assertEquals(a4ResourceOrderDetailPageRobot.readStatus(), sortedRoList.get(0).getState());

        // detail-page table
        assertEquals(roiCollection.size()/8, sortedRoList.get(0).getOrderItem().size());
        assertEquals(roiCollection.get(0).innerText(), sortedRoList.get(0).getOrderItem().get(0).getId()); //roi-id
        assertEquals(roiCollection.get(1).innerText(), sortedRoList.get(0).getOrderItem().get(0).getAction());
        assertEquals(roiCollection.get(2).innerText(), sortedRoList.get(0).getOrderItem().get(0).getResource().getName()); // lbz
        assertEquals(roiCollection.get(3).innerText(), sortedRoList.get(0).getOrderItem().get(0).getState());


    }


}
