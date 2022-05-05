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
import com.tsystems.tm.acc.ta.robot.osr.*;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrderStateType;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.VlanRange;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.*;

import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_ORDER_ORCHESTRATOR_MS;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrderItemStateType.*;

@ServiceLog({A4_RESOURCE_ORDER_ORCHESTRATOR_MS})
@Epic("OS&R")
public class A4ResourceOrderTest {
    // test send a request (resource order) from simulated Sputnik to Berlinium and get a callback
    private final String DEFAULT_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String SECOND_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String WIREMOCK_SCENARIO_NAME = "A4ResourceOrderTest";
    private final int SLEEP_TIMER = 2;
    private final String URL_EVENT_PUBLISH = "/upstream-partner/tardis/resource-order-resource-inventory/a10nsp/resourceOrderingManagement/horizon/events/v1";
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceOrderRobot a4ResourceOrder = new A4ResourceOrderRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4WiremockRebellRobot a4WiremockRebellRobot = new A4WiremockRebellRobot();
    private final A4WiremockA10nspA4Robot a4WiremockA10nspA4Robot = new A4WiremockA10nspA4Robot();
    private final A4WiremockRobot a4Wiremock = new A4WiremockRobot();
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
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), WIREMOCK_SCENARIO_NAME)).build();

    @DataProvider(name = "vlanRangeCombinations")
    public static Object[][] vlanRangeCombinations() {
        VlanRange vlanRange1 = new VlanRange()
                .vlanRangeLower(null)
                .vlanRangeUpper(null);

        VlanRange vlanRange2 = new VlanRange()
                .vlanRangeLower("")  // values empty, no change in db; ok
                .vlanRangeUpper("");

        VlanRange vlanRange3 = new VlanRange()
                .vlanRangeLower("3")
                .vlanRangeUpper("");

        VlanRange vlanRange4 = new VlanRange()
                .vlanRangeLower(null)
                .vlanRangeUpper("4012");

        return new Object[][]{
                {vlanRange1},
                {vlanRange2},
                {vlanRange3},
                {vlanRange4}};
    }

    @DataProvider(name = "characteristicNamesDelete")
    public static Object[][] characteristicNamesDeleteString() {
        return new Object[][]{
                {FRAME_CONTRACT_ID},
                {CARRIER_BSA_REFERENCE},
                {PUBLIC_REFERENCE_ID},
                {LACP_ACTIVE},
                {MTU_SIZE},
                {VLAN_RANGE},
                {QOS_LIST}};
    }

    @DataProvider(name = "characteristicNamesEmptyString")
    public static Object[][] characteristicNamesEmptyString() {
        return new Object[][]{
                {FRAME_CONTRACT_ID},
                {CARRIER_BSA_REFERENCE},
                {PUBLIC_REFERENCE_ID},
                {LACP_ACTIVE},
                {MTU_SIZE}};
    }

    @DataProvider(name = "characteristicNamesEmptyList")
    public static Object[][] characteristicNamesEmptyList() {
        return new Object[][]{{VLAN_RANGE}};
    }

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
        uewegData1 = osrTestContext.getData().getUewegDataDataProvider().get(UewegDataCase.uewegA);
        uewegData2 = osrTestContext.getData().getUewegDataDataProvider().get(UewegDataCase.uewegB);
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
        a4ResourceInventory.createTerminationPoint(tpData1, nepData1);
        a4ResourceInventory.createTerminationPoint(tpData2, nepData2);
        a4ResourceInventory.createTerminationPoint(tpData3, nepData3);
        a4ResourceInventory.createTerminationPoint(tpData4, nepData4);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data1, tpData1);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data2, tpData2);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data3, tpData3);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data4, tpData4);
        ro = a4ResourceOrder.buildResourceOrder();
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), WIREMOCK_SCENARIO_NAME))
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
        wiremock.getWireMock().resetRequests();
        a4ResourceOrder.cleanCallbacksInWiremock();
        // Delete all A4 data which might provoke problems because of unique constraints
        a4ResourceInventory.deleteA4NetworkElementGroupsRecursively(negData);
        a4ResourceInventory.deleteA4NetworkElementsRecursively(neData1);
        a4ResourceInventory.deleteA4NetworkElementsRecursively(neData2);
        a4ResourceInventory.deleteA4NetworkElementsRecursively(neData3);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepData1, neData1);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepData2, neData2);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepData3, neData3);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepData4, neData2);
        a4ResourceOrder.deleteA4TestDataRecursively(ro);
    }

    @Test(dataProvider = "vlanRangeCombinations")
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without vlan-range values (=null)")
    public void testRoWithoutVlanRangeValues(VlanRange vlanRange) {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setCharacteristicValue(VLAN_RANGE, vlanRange, DEFAULT_ORDER_ITEM_ID, ro);
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without vlan-range")
    public void testRoWithoutVlanRange() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.removeCharacteristic(VLAN_RANGE, DEFAULT_ORDER_ITEM_ID, ro);
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfCompleted(ro, roUuid);
        a4WiremockRebellRobot.checkSyncRequestToRebellWiremock(getEndsz(neData1), "GET", 1);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, COMPLETED);
        A10nspA4Dto a10nspA4Dto = a4ResourceOrder.getA10NspA4Dto(ro);
        a4WiremockA10nspA4Robot.checkSyncRequestToA10nspA4Wiremock(a10nspA4Dto, "POST", 1);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without characteristic vlan-range")
    public void testRoWithoutVlanRangeCharacteristic() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.removeCharacteristic(VLAN_RANGE, DEFAULT_ORDER_ITEM_ID, ro);
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfCompleted(ro, roUuid);
        a4WiremockRebellRobot.checkSyncRequestToRebellWiremock(getEndsz(neData1), "GET", 1);
        A10nspA4Dto a10nspA4Dto = a4ResourceOrder.getA10NspA4Dto(ro);
        a4WiremockA10nspA4Robot.checkSyncRequestToA10nspA4Wiremock(a10nspA4Dto, "POST", 1);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, COMPLETED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Post to Mercury is impossible")
    public void testRoNoPostToMercury() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setCharacteristicValue(CARRIER_BSA_REFERENCE, "f26bd5de/2150/47c7/8235/a688438973a4", DEFAULT_ORDER_ITEM_ID, ro); // erzeugt Mercury-Fehler 409
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("rebell-link for resource order from Merlin is unknown")
    public void testRoUnknownNel() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setResourceName("4N1/10001-49/30/124/7KCB-49/30/125/7KCA", DEFAULT_ORDER_ITEM_ID, ro); // Link is unknown
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);

    }

    @Test
    @Owner("DL_Berlinium@telekom.de")
    @TmsLink("DIGIHUB-126401")
    @Description("add-case: send RO with -add- and get -completed-")
    public void testRoAddItem() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData2, ro);
        // post first input request that resource order processing is starting
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // process resource order checks
        a4ResourceOrder.checkResourceOrderAndFirstItemState(ro, roUuid, ResourceOrderStateType.COMPLETED);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, COMPLETED);
        a4WiremockRebellRobot.checkSyncRequestToRebellWiremock(getEndsz(neData1), HttpMethod.GET, 1);
        a4WiremockA10nspA4Robot.checkSyncRequestToA10nspA4Wiremock(a4ResourceOrder.getA10NspA4Dto(ro), HttpMethod.POST, 1);
        // all done, finally check event publishing
        // TODO: if event processing is working in gigahub
        //a4Wiremock.checkSyncRequest(URL_EVENT_PUBLISH, HttpMethod.POST, 1, 1000);
    }

    @Test(description = "DIGIHUB-142958 error-case: send RO with Id and get Response with -400- from roo")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-142958")
    @Description("error-case: send RO with Id and get Response with -400- from roo")
    public void testRoAddItemWithId() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData2, ro);
        ro.setId(UUID.randomUUID().toString());
        System.out.println("+++ RO mit uuid: " + ro);

        // WHEN
        a4ResourceOrder.sendPostResourceOrderError400(ro);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfNotInDb(ro);
        ro = null; // damit am Ende keine Daten gelöscht werden, die nicht vorhanden sind
    }

    @Test(description = "DIGIHUB-142958 error-case: send RO with invalid structure and get Response with -400- from swagger")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-142958")
    @Description("error-case: send RO with invalid structure and get Response with -400- from swagger")
    public void testRoAddItemWithInvalidStructure() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData2, ro);
        Objects.requireNonNull(ro.getOrderItem()).get(0).setId(null);
        System.out.println("+++ RO mit uuid: " + ro);
        // WHEN
        a4ResourceOrder.sendPostResourceOrderError400(ro);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfNotInDb(ro);
        ro = null; // damit am Ende keine Daten gelöscht werden, die nicht vorhanden sind
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with two -add- and get Callback with -completed-")
    public void testRo2AddItems() {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.addOrderItemAdd(SECOND_ORDER_ITEM_ID, nelData2, ro);
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfCompleted(ro, roUuid);
        a4WiremockRebellRobot.checkSyncRequestToRebellWiremock(getEndsz(neData1), "GET", 1);
        A10nspA4Dto a10nspA4Dto = a4ResourceOrder.getA10NspA4Dto(ro);
        a4WiremockA10nspA4Robot.checkSyncRequestToA10nspA4Wiremock(a10nspA4Dto, "POST", 1);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, COMPLETED);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), SECOND_ORDER_ITEM_ID, COMPLETED);
        a4ResourceOrder.checkResourceOrderAndFirstItemState(ro, null, ResourceOrderStateType.COMPLETED);
    }

    @Test(description = "DIGIHUB-76370 a10-ro delete")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130475")
    @Description("delete-case: send RO with -delete- and get Callback with -completed-")
    public void testRoDeleteItem() {
        // GIVEN
        a4ResourceOrder.addOrderItemDelete(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceInventory.checkDefaultValuesNsp(nspA10Data1);
        a4ResourceInventory.checkLifecycleState(nelData1, "DEACTIVATED");
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfCompleted(ro, roUuid);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, COMPLETED);
        a4NemoUpdater.checkNetworkElementLinkPutRequestToNemoWiremockByNel(nelData1);
        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(tpData1);
        a4WiremockRebellRobot.checkSyncRequestToRebellWiremock(getEndsz(neData1), "GET", 0);
        A10nspA4Dto a10nspA4Dto = a4ResourceOrder.getA10NspA4Dto(ro);
        a4WiremockA10nspA4Robot.checkSyncRequestToA10nspA4Wiremock(a10nspA4Dto, "POST", 0);
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
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceInventory.checkDefaultValuesNsp(nspA10Data1);
        a4ResourceInventory.checkLifecycleState(nelData1, "DEACTIVATED");
        a4ResourceInventory.checkLifecycleState(nelData2, "DEACTIVATED");
        a4NemoUpdater.checkTwoNetworkElementLinksPutRequestToNemoWiremock(nepData1);
        a4NemoUpdater.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(tpData1, 2);
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfCompleted(ro, roUuid);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, COMPLETED);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), SECOND_ORDER_ITEM_ID, COMPLETED);
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
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);

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
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Modify is not implemented")
    public void testModifyNotImplemented() {
        // GIVEN
        a4ResourceOrder.addOrderItemModify(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);
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
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), roiId2, REJECTED);
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
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(5);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfCompleted(ro, roUuid);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, COMPLETED);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), roiId2, COMPLETED);

    }

    @Test(dataProvider = "characteristicNamesEmptyString")
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-112658 Resource order: Characteristic in order item has value empty string \"\"")
    public void testRoWithCharacteristicWithEmtpyString(String cName) {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setCharacteristicValue(cName, "", DEFAULT_ORDER_ITEM_ID, ro);
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);
    }

    @Test(dataProvider = "characteristicNamesEmptyList")
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-112658 Resource order: Characteristic in order item has value empty list []")
    public void testRoWithCharacteristicWithEmtpyList(String cName) {
        // GIVEN
        a4ResourceOrder.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrder.setCharacteristicValue(cName, new ArrayList<>(), DEFAULT_ORDER_ITEM_ID, ro);
        // WHEN
        String roUuid = a4ResourceOrder.sendPostResourceOrder(ro);
        System.out.println("+++ uuid der RO: " + roUuid);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4ResourceOrder.getResourceOrdersFromDbAndCheckIfRejected(ro);
        a4ResourceOrder.checkResourceOrderItemState(ro.getId(), DEFAULT_ORDER_ITEM_ID, REJECTED);
    }
}
