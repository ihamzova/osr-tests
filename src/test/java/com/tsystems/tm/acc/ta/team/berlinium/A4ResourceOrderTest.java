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
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
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
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static org.testng.Assert.assertEquals;

@ServiceLog({A4_RESOURCE_ORDER_ORCHESTRATOR_MS})
@Epic("OS&R")
public class A4ResourceOrderTest {

    // test send a request (resource order) from simulated Merlin to Berlinium and get a callback

    private final String DEFAULT_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String SECOND_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String wiremockScenarioName = "A4ResourceOrderTest";
    private final int sleepTimer = 10;

    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceOrderRobot a4ResourceOrder = new A4ResourceOrderRobot();

    private final Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();
    private final Map<String, A4NetworkElement> a4NetworkElementLinks = new HashMap<>();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData1;
    private A4NetworkElement neData2;
    private A4NetworkElement neData3;
    private A4NetworkElementPort nepData1;
    private A4NetworkElementPort nepData2;
    private A4NetworkElementPort nepData3;
    private A4NetworkElementPort nepData4;
    private A4NetworkElementLink nelData1;
    private A4NetworkElementLink nelData2;
    private A4NetworkServiceProfileA10Nsp nspA10Data1;
    private A4NetworkServiceProfileA10Nsp nspA10Data2;
    private A4NetworkServiceProfileA10Nsp nspA10Data3;
    private A4NetworkServiceProfileA10Nsp nspA10Data4;
    private A4TerminationPoint tpData1;
    private A4TerminationPoint tpData2;
    private A4TerminationPoint tpData3;
    private A4TerminationPoint tpData4;

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
                .get(A4NetworkElementCase.networkElementA10NspSwitch02);
        nepData3 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        nepData4 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_1G_002);
        nelData1 = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.networkElementLinkLcsInstalling);
        nelData2 = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        nspA10Data1 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        nspA10Data2 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.networkServiceProfileA10NspPrePro);
        nspA10Data3 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.networkServiceProfileA10NspPrePro2);
        nspA10Data4 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.networkServiceProfileA10NspPrePro3);
        tpData1 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);
        tpData2 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.terminationPointA10NspPrePro);
        tpData3 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.terminationPointA10NspPrePro2);
        tpData4 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.terminationPointA10NspPrePro3);
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
        a4ResourceInventory.createNetworkElementPort(nepData4, neData2);
        // all nel's need same ne1 (type a10-switch) ! important for lbz in ro-items
        a4ResourceInventory.createNetworkElementLink(nelData1, nepData1, nepData2, neData1, neData2, uewegData1);
        a4ResourceInventory.createNetworkElementLink(nelData2, nepData1, nepData3, neData1, neData3, uewegData2);
        System.out.println("+++ nel1: "+a4ResourceInventory.getExistingNetworkElementLink(nelData1.getUuid()));
        System.out.println("+++ nel2: "+a4ResourceInventory.getExistingNetworkElementLink(nelData2.getUuid()));
        a4ResourceInventory.createTerminationPoint(tpData1, nepData1);
        a4ResourceInventory.createTerminationPoint(tpData2, nepData2);
        a4ResourceInventory.createTerminationPoint(tpData3, nepData3);
        a4ResourceInventory.createTerminationPoint(tpData4, nepData4);
        System.out.println("+++ NSP1: "+nspA10Data1);
        System.out.println("+++ NSP2: "+nspA10Data2);
        System.out.println("+++ NSP3: "+nspA10Data3);
        System.out.println("+++ NSP4: "+nspA10Data4);
        System.out.println("+++ TP1: "+tpData1);
        System.out.println("+++ TP2: "+tpData2);
        System.out.println("+++ TP3: "+tpData3);
        System.out.println("+++ TP4: "+tpData4);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data1, tpData1);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data2, tpData2);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data3, tpData3);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data4, tpData4);
        System.out.println("+++ NSP1 in DB: "+a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nspA10Data1.getUuid()));
        System.out.println("+++ NSP2 in DB: "+a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nspA10Data2.getUuid()));
        System.out.println("+++ NSP3 in DB: "+a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nspA10Data3.getUuid()));
        System.out.println("+++ NSP4 in DB: "+a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nspA10Data4.getUuid()));


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
        a4ResourceOrder.cleanCallbacksInWiremock();
        // https://wiremock-acc-app-berlinium-03.priv.cl01.gigadev.telekom.de/__admin/requests/remove
        // body: {
        //    "method": "POST",
        //    "url": "/test_url"
        // }

        wiremock.getWireMock().resetRequests();  // löscht die Counter nach jedem einzelnen Test ! wichtig !
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
    public void testRoNoPostToMercury() {
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
    public void testRoUnknownNel() {
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
    public void testRoAddItem() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData2, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        //NetworkServiceProfileA10NspDto networkServiceProfileA10NspDto = a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nspA10Data3.getUuid());
        //Assert.assertEquals(networkServiceProfileA10NspDto.getNetworkElementLinkUuid(), nelData2.getUuid()); // wozu dient der Vergleich?

        a4ResourceOrder.checkResourceOrderIsCompleted();
        a4ResourceOrder.checkOrderItemIsCompleted(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.getResourceOrderFromDbAndCheckIfCompleted(ro.getId());
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with two -add- and get Callback with -completed-")
    public void testRo2AddItems() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.addOrderItemAdd(SECOND_ORDER_ITEM_ID, nelData2, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        //NetworkServiceProfileA10NspDto networkServiceProfileA10NspDto = a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nspA10Data1.getUuid());
       // NetworkServiceProfileA10NspDto networkServiceProfileA10NspDto2 = a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nspA10Data2.getUuid());

       // Assert.assertEquals(networkServiceProfileA10NspDto.getNetworkElementLinkUuid(), nelData1.getUuid());
       // Assert.assertEquals(networkServiceProfileA10NspDto2.getNetworkElementLinkUuid(), nelData2.getUuid());

        a4ResourceOrder.checkResourceOrderIsCompleted();
        a4ResourceOrder.checkOrderItemIsCompleted(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.checkOrderItemIsCompleted(SECOND_ORDER_ITEM_ID);
        a4ResourceOrder.getResourceOrderFromDbAndCheckIfCompleted(ro.getId());
    }

    @Test(description = "DIGIHUB-76370 a10-ro delete")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130475")
    @Description("delete-case: send RO with -delete- and get Callback with -completed-")
    public void testRoDeleteItem() {
        // GIVEN
        a4ResourceOrder.addOrderItemDelete(DEFAULT_ORDER_ITEM_ID, nelData1, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkDefaultValuesNsp(nspA10Data1);
        assertEquals(a4ResourceInventory.getExistingNetworkElementLink(nelData1.getUuid()).getLifecycleState(), "DEACTIVATED");

        a4NemoUpdater.checkNetworkElementLinkPutRequestToNemoWiremockByNel(nelData1.getUuid());
        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(tpData1.getUuid());

        a4ResourceOrder.checkResourceOrderIsCompleted();
        a4ResourceOrder.checkOrderItemIsCompleted(DEFAULT_ORDER_ITEM_ID);
    }

    @Test(description = "DIGIHUB-76370 a10-ro delete")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130477")
    @Description("delete-case: send RO with two -delete- and get Callback with -completed-")
    public void testRo2DeleteItems() {
        // GIVEN
        a4ResourceOrder.addOrderItemDelete(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.addOrderItemDelete(SECOND_ORDER_ITEM_ID, nelData2, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkDefaultValuesNsp(nspA10Data1);
        assertEquals(a4ResourceInventory.getExistingNetworkElementLink(nelData1.getUuid()).getLifecycleState(), "DEACTIVATED");
        assertEquals(a4ResourceInventory.getExistingNetworkElementLink(nelData2.getUuid()).getLifecycleState(), "DEACTIVATED");

        a4NemoUpdater.checkTwoNetworkElementLinksPutRequestToNemoWiremock(nepData1.getUuid());
        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(tpData1.getUuid(),2);

        a4ResourceOrder.checkResourceOrderIsCompleted();
        a4ResourceOrder.checkOrderItemIsCompleted(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.checkOrderItemIsCompleted(SECOND_ORDER_ITEM_ID);
    }

    @Test(description = "DIGIHUB-119735 a10-ro delete, prevalidation, action check")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130474")
    @Description("mixed-case: send RO with -add- and -delete- and get Callback with -rejected-")
    public void testRoDeleteItemAndAddItem() {
        // GIVEN
        a4ResourceOrder.addOrderItemDelete(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.addOrderItemAdd(SECOND_ORDER_ITEM_ID, nelData2, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
       a4ResourceOrder.checkResourceOrderIsRejected();
       a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
    }


    @DataProvider(name = "characteristicNamesDelete")
    public static Object[] characteristicNamesDeleteString() {
        return new Object[]{
                RAHMEN_VERTRAGS_NR,
                CARRIER_BSA_REFERENCE,
                VUEP_PUBLIC_REFERENZ_NR,
                LACP_AKTVUEP_PUBLIC_REFERENZ_NRIV,
                MTU_SIZE,
                VLAN_RANGE,
                QOS_LIST};
    }

    @Test(dataProvider = "characteristicNamesDelete")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130481")
    @Description("DIGIHUB-xxx Resource order: Characteristic in delete-order item has wrong value \"\"")
    public void testRoDeleteWithEmptyValues(String cName) {
        // GIVEN
        a4ResourceOrder.addOrderItemDelete(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setCharacteristicValue(cName, "", DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrder(ro);
        sleepForSeconds(sleepTimer);

        // THEN
        a4ResourceOrder.checkOrderItemIsRejected(DEFAULT_ORDER_ITEM_ID);
        a4ResourceOrder.checkResourceOrderIsRejected();
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
