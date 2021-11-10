package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileA10NspDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.VlanRange;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@ServiceLog({A4_RESOURCE_ORDER_ORCHESTRATOR_MS})
@Epic("OS&R")
public class A4ResourceOrderTest {

    // test send a request (resource order) from simulated Merlin to Berlinium and get a callback

    private final String DEFAULT_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String wiremockScenarioName = "A4ResourceOrderTest";
    private final int sleepTimer = 10;

    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceOrderRobot a4ResourceOrder = new A4ResourceOrderRobot();

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

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), wiremockScenarioName)).build();

    @BeforeClass
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

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neData1, negData);
        a4ResourceInventory.createNetworkElement(neData2, negData);
        a4ResourceInventory.createNetworkElement(neData3, negData);
        a4ResourceInventory.createNetworkElementPort(nepData1, neData1);
        a4ResourceInventory.createNetworkElementPort(nepData2, neData2);
        a4ResourceInventory.createNetworkElementPort(nepData3, neData3);
        a4ResourceInventory.createNetworkElementLink(nelData1, nepData1, nepData2, neData1, neData2, uewegData1);
        nelData2.setLifecycleState("INSTALLING");
        a4ResourceInventory.createNetworkElementLink(nelData2, nepData1, nepData3, neData1, neData3, uewegData2);
        a4ResourceInventory.createTerminationPoint(tpData1, nepData1);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data1, tpData1);

        ro = a4ResourceOrder.buildResourceOrder();

        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), wiremockScenarioName))
                .addMerlinMock()
                .addRebellMock(neData1, uewegData1, neData2, uewegData2, neData3)
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
        wiremock.fetchAndDeleteServeEvents();

    }

    @AfterMethod
    public void cleanup() {
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        a4ResourceInventory.deleteA4TestDataRecursively(negData);
        // a4ResourceOrder.deleteA4TestDataRecursively(ro);
        a4ResourceOrder.cleanCallbacksInWiremock();
        // https://wiremock-acc-app-berlinium-03.priv.cl01.gigadev.telekom.de/__admin/requests/remove
        // body: {
        //    "method": "POST",
        //    "url": "/test_url"
        // }
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without vlan-range values (=null)")
    public void testRoWithoutVlanRangeValues() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        VlanRange vlanRange = new VlanRange()
                .vlanRangeLower(null)
                .vlanRangeUpper(null);
        a4ResourceOrder.setCharacteristicValue(VLAN_RANGE, vlanRange, DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro with empty vlan-range values")
    public void testRoWithEmptyVlanRangeValues() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        VlanRange vlanRange = new VlanRange()
                .vlanRangeLower("")  // values empty, no change in db; ok
                .vlanRangeUpper("");
        a4ResourceOrder.setCharacteristicValue(VLAN_RANGE, vlanRange, DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro with one empty and one valid vlan-range value")
    public void testRoWithEmptyAndValidVlanRangeValues() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        VlanRange vlanRange = new VlanRange()
                .vlanRangeLower("3")
                .vlanRangeUpper("");
        a4ResourceOrder.setCharacteristicValue(VLAN_RANGE, vlanRange, DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro with one null and one valid vlan-range value")
    public void testRoWithNullAndValidVlanRangeValues() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        VlanRange vlanRange = new VlanRange()
                .vlanRangeLower(null)
                .vlanRangeUpper("4012");
        a4ResourceOrder.setCharacteristicValue(VLAN_RANGE, vlanRange, DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without vlan-range")
    public void testRoWithoutVlanRange() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.removeCharacteristic(VLAN_RANGE, DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(5);

        // THEN
        a4ResourceOrder.checkResourceOrderIsCompleted();
        a4ResourceOrder.checkOrderItemIsCompleted(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without characteristic vlan-range")
    public void testRoWithoutVlanRangeCharacteristic() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.removeCharacteristic(VLAN_RANGE, DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsCompleted();
        a4ResourceOrder.checkOrderItemIsCompleted(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Post to Mercury is impossible")
    public void testNoPostToMercury() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setCharacteristicValue(CARRIER_BSA_REFERENCE, "f26bd5de/2150/47c7/8235/a688438973a4", DEFAULT_ORDER_ITEM_ID, ro); // erzeugt Mercury-Fehler 409

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("rebell-link for resource order from Merlin is unknown")
    public void testUnknownNel() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setResourceName("4N1/10001-49/30/124/7KCB-49/30/125/7KCA", DEFAULT_ORDER_ITEM_ID, ro); // Link is unknown

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with -add- and get Callback with -completed-")
    public void testAddItem() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        NetworkServiceProfileA10NspDto networkServiceProfileA10NspDto =
                a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nspA10Data1.getUuid());
        Assert.assertEquals(networkServiceProfileA10NspDto.getNetworkElementLinkUuid(), nelData1.getUuid());

        a4ResourceOrder.checkResourceOrderIsCompleted();
        a4ResourceOrder.checkOrderItemIsCompleted(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.getResourceOrderFromDbAndCheckIfCompleted(ro.getId());
    }

/*
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with -add- 2 items and get Callback with -completed-")
    public void test2AddItems()  {

    ResourceOrder ro_5 = buildResourceOrder();


        rv2.setName("RahmenvertragsNr");
        rv2.setValue("1122334456");
        rv2.setValueType("valueTypeRv2");
        cbr2.setName("Subscription.keyA");
        cbr2.setValue("f26bd5de-2150-47c7-8235-a688438973a5");
        cbr2.setValueType("valueTypeCbr2");
        resourceCharacteristicList2.add(rv2);
        resourceCharacteristicList2.add(cbr2);

        vuep2.setName("VUEP_Public_Referenz-Nr.");
        vuep2.setValue("A1000852");
        vuep2.setValueType("valueTypeVuep2");
        mtu2.setName("MTU-Size");
        mtu2.setValue("1500");
        mtu2.setValueType("valueTypeMtu2");
        lacp2.setName("LACP_aktiv");
        lacp2.setValue("true");
        lacp2.setValueType("valueTypeLacp2");

        vlanRange2.setVlanRangeLower("0");
        vlanRange2.setVlanRangeUpper("4094");
        characteristicVLANrange2.setName("VLAN_Range");
        characteristicVLANrange2.setValue(vlanRange2);
        characteristicVLANrange2.setValueType("valueTypeVlan2");

        resourceCharacteristicList2.add(vuep2);
        resourceCharacteristicList2.add(mtu2);
        resourceCharacteristicList2.add(lacp2);
        resourceCharacteristicList2.add(characteristicVLANrange2);

        resource2.setName("4L2/100211-49/30/150/7KDC-49/30/150/7KD3");  // 2. Item
        resource2.setResourceCharacteristic(resourceCharacteristicList2);

        orderItem2.setAction(OrderItemActionType.ADD);
        orderItem2.setResource(resource2);
        orderItem2.setId("orderItemId");
        orderItemList.add(orderItem2);

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro);

        // receive callback with Mock
        sleepForSeconds(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo( "/test_url" )));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: "+ergList);

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ Modify, Delete oder Prozessfehler enthalten: "+notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: "+noNELTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        //sleepForSeconds(5);    // Auswertung der DB

        assertTrue(completeTrue);
    }
     */

    /*
@Ignore
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with -add- 2 items and get Callback with -rejected-")
    public void testAdd2LinksOneIsUnknown()  {

       ResourceOrder ro_6 = buildResourceOrder();

        rv2.setName("RahmenvertragsNr");
        rv2.setValue("1122334456");
        rv2.setValueType("valueTypeRv");
        cbr2.setName("Subscription.keyA");
        cbr2.setValue("f26bd5de-2150-47c7-8235-a688438973a5");
        cbr2.setValueType("valueTypeCbr");
        resourceCharacteristicList2.add(rv2);
        resourceCharacteristicList2.add(cbr2);

        resource2.setName("4L3/100211-49/30/150/7KDC-49/30/150/7KD3");  // 2. Item, Link falsch
        resource2.setResourceCharacteristic(resourceCharacteristicList2);

        orderItem2.setAction(OrderItemActionType.ADD);
        orderItem2.setResource(resource2);
        orderItem2.setId("orderItemId");
        orderItemList.add(orderItem2);

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro);

        // receive callback with Mock
        sleepForSeconds(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo( "/test_url" )));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: "+ergList);

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ Delete oder Modify enthalten: "+notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: "+noNELTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        //sleepForSeconds(5);    // Auswertung der DB

        assertTrue(rejectTrue);
    }
     */

    /*
@Ignore
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add/delete-case: send RO with -add- and -delete- items and get Callback with -rejected-")
    public void testAddItemAndDeleteItem() {

     ResourceOrder ro_7 = buildResourceOrder();

        rv2.setName("RahmenvertragsNr");
        rv2.setValue("1122334456");
        rv2.setValueType("valueTypeRv");
        cbr2.setName("Subscription.keyA");
        cbr2.setValue("f26bd5de-2150-47c7-8235-a688438973a5");
        cbr2.setValueType("valueTypeCbr");
        resourceCharacteristicList2.add(rv2);
        resourceCharacteristicList2.add(cbr2);

        resource2.setName("4L2/100211-49/30/150/7KDC-49/30/150/7KD3");  // 2. Item
        resource2.setResourceCharacteristic(resourceCharacteristicList2);

        orderItem2.setAction(OrderItemActionType.DELETE);
        orderItem2.setResource(resource2);
        orderItem2.setId("orderItemId");
        orderItemList.add(orderItem2);

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro);

        // receive callback with Mock
        sleepForSeconds(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo( "/test_url" )));



        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: "+ergList);

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");
        boolean AddCaseTrue = ergList.toString().contains("add");
        boolean DeleteCaseTrue = ergList.toString().contains("delete");

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ Delete oder Modify enthalten: "+notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: "+noNELTrue);
        System.out.println("+++ Add gefunden: "+AddCaseTrue);
        System.out.println("+++ Delete gefunden: "+DeleteCaseTrue);


        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        //sleepForSeconds(5);    // Auswertung der DB

        assertTrue(rejectTrue);
    }
     */

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Delete is not implemented")
    public void testDeleteNotImplemented() {
        // GIVEN
        a4ResourceOrder.addOrderItemDelete(DEFAULT_ORDER_ITEM_ID, nelData1, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Modify is not implemented")
    public void testModifyNotImplemented() {
        // GIVEN
        a4ResourceOrder.addOrderItemModify(DEFAULT_ORDER_ITEM_ID, nelData1, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }

    // functions comes later:
    /*
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("modify-case: NSP of a10nsp remains OPERATING")
    public void testModifyLink() {
        //

    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("delete-case: NSP of a10nsp changed to DEACTIVATED")
    public void testDeleteLink() {
        //

    }
    */

    @Test
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-115133 Resource order has two order items with same resource.name (LBZ)")
    public void testRoWithDuplicateLbzInRoi() {
        // GIVEN
        final String roiId2 = "roiId2";
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.addOrderItemAdd(roiId2, nelData1, ro); // uses same nelData, therefore same LBZ mapped to resource.name

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkResourceOrderIsRejected();
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.checkOrderItemIsRejected(roiId2);
    }

    @Test
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-115139 Resource order has two order items without same resource.name (LBZ)")
    public void testRoWithoutDuplicateLbzInRoi() {
        // GIVEN
        final String roiId2 = "roiId2";
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.addOrderItemAdd(roiId2, nelData2, ro); // uses other nelData, therefore other LBZ mapped to resource.name

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        //sleepForSeconds(5);

        // THEN
        a4ResourceOrder.checkOrderItemIsCompleted(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.checkOrderItemIsCompleted(roiId2);
        a4ResourceOrder.checkResourceOrderIsCompleted();
    }

    @DataProvider(name = "characteristicNamesEmptyString")
    public static Object[] characteristicNamesEmptyString() {
        return new Object[]{
                RAHMEN_VERTRAGS_NR,
                CARRIER_BSA_REFERENCE,
                VUEP_PUBLIC_REFERENZ_NR,
                LACP_AKTVUEP_PUBLIC_REFERENZ_NRIV,
                MTU_SIZE};
    }

    @Test(dataProvider = "characteristicNamesEmptyString")
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-112658 Resource order: Characteristic in order item has value empty string \"\"")
    public void testRoWithCharacteristicWithEmtpyString(String cName) {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setCharacteristicValue(cName, "", DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.checkResourceOrderIsRejected();
    }

    @DataProvider(name = "characteristicNamesEmptyList")
    public static Object[] characteristicNamesEmptyList() {
        return new Object[]{
                VLAN_RANGE};
    }

    @Test(dataProvider = "characteristicNamesEmptyList")
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-112658 Resource order: Characteristic in order item has value empty list []")
    public void testRoWithCharacteristicWithEmtpyList(String cName) {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setCharacteristicValue(cName, new ArrayList<>(), DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.checkResourceOrderIsRejected();
    }

}
