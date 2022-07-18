package com.tsystems.tm.acc.ta.team.berlinium;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.data.osr.models.DataBundle;
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
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.*;

import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.UUID;

import static com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase.*;
import static com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase.NetworkElementGroupL2Bsa;
import static com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase.*;
import static com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase.*;
import static com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase.*;
import static com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase.*;
import static com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_ORDER_ORCHESTRATOR_MS;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.ResourceOrderItemStateType.*;
import static org.testng.Assert.assertNotNull;

@ServiceLog({A4_RESOURCE_ORDER_ORCHESTRATOR_MS})
@Epic("OS&R")
@Slf4j
public class A4ResourceOrderTest {

    public static final String DEACTIVATED = "DEACTIVATED";
    private final String WIREMOCK_SCENARIO_NAME = "A4ResourceOrderTest";
    private final int SLEEP_TIMER = 5;
    private final A4ResourceInventoryRobot a4RobotRI = new A4ResourceInventoryRobot();
    private final A4ResourceOrderRobot a4RobotRO = new A4ResourceOrderRobot();
    private final A4NemoUpdaterRobot a4RobotNU = new A4NemoUpdaterRobot();
    private final A4WiremockRebellRobot a4WmRobotRebell = new A4WiremockRebellRobot();
    private final A4WiremockA10nspA4Robot a4WmRobotA10Nsp = new A4WiremockA10nspA4Robot();
    private final A4WiremockRobot a4WmRobot = new A4WiremockRobot();
    // test send a request (resource order) from simulated Sputnik to Berlinium and get a callback
    private String orderItemId;
    private String orderItemId2;
    private A4NetworkElementGroup a4NEG;
    private A4NetworkElement a4NE;
    private A4NetworkElement a4NE2;
    private A4NetworkElement a4NE3;
    private A4NetworkElementPort a4NEP;
    private A4NetworkElementPort a4NEP2;
    private A4NetworkElementPort a4NEP3;
    private A4NetworkElementPort a4NEP4;
    private A4NetworkElementLink a4NEL;
    private A4NetworkElementLink a4NEL2;
    private A4NetworkServiceProfileA10Nsp a4A10Nsp;
    private A4TerminationPoint a4TP;
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
        // Ensure that no old test data is in the way
        cleanupWiremock();
    }

    @BeforeMethod
    public void setup() {
        DataBundle data = OsrTestContext.get().getData();
        orderItemId = getPrefixWithRandom("orderItemId-", 6);
        orderItemId2 = getPrefixWithRandom("orderItemId-", 6);
        a4NEG = data.getA4NetworkElementGroupDataProvider().get(NetworkElementGroupL2Bsa);
        a4NEG.setName(replaceLast(4, a4NEG.getName(), getRandomDigits(4)));
        a4NE = data.getA4NetworkElementDataProvider().get(networkElementA10NspSwitch01);
        a4NEP = data.getA4NetworkElementPortDataProvider().get(networkElementPort_logicalLabel_100G_001);
        a4NE2 = data.getA4NetworkElementDataProvider().get(defaultNetworkElement);
        a4NEP2 = data.getA4NetworkElementPortDataProvider().get(networkElementPort_logicalLabel_10G_002);
        a4NE3 = data.getA4NetworkElementDataProvider().get(networkElementA10NspSwitch02);
        a4NEP3 = data.getA4NetworkElementPortDataProvider().get(networkElementPort_logicalLabel_10G_001);
        a4NEP4 = data.getA4NetworkElementPortDataProvider().get(networkElementPort_logicalLabel_1G_002);
        a4NEL = data.getA4NetworkElementLinkDataProvider().get(networkElementLinkLcsInstalling);
        a4NEL2 = data.getA4NetworkElementLinkDataProvider().get(defaultNetworkElementLink);
        a4A10Nsp = data.getA4NetworkServiceProfileA10NspDataProvider().get(defaultNetworkServiceProfileA10Nsp);
        A4NetworkServiceProfileA10Nsp nspA10Data2 = data.getA4NetworkServiceProfileA10NspDataProvider().get(networkServiceProfileA10NspPrePro);
        A4NetworkServiceProfileA10Nsp nspA10Data3 = data.getA4NetworkServiceProfileA10NspDataProvider().get(networkServiceProfileA10NspPrePro2);
        A4NetworkServiceProfileA10Nsp nspA10Data4 = data.getA4NetworkServiceProfileA10NspDataProvider().get(networkServiceProfileA10NspPrePro3);
        a4TP = data.getA4TerminationPointDataProvider().get(defaultTerminationPointA10Nsp);
        A4TerminationPoint tpData2 = data.getA4TerminationPointDataProvider().get(terminationPointA10NspPrePro);
        A4TerminationPoint tpData3 = data.getA4TerminationPointDataProvider().get(terminationPointA10NspPrePro2);
        A4TerminationPoint tpData4 = data.getA4TerminationPointDataProvider().get(terminationPointA10NspPrePro3);
        UewegData uewegData1 = data.getUewegDataDataProvider().get(uewegA);
        UewegData uewegData2 = data.getUewegDataDataProvider().get(uewegB);

        a4RobotRI.createNetworkElementGroup(a4NEG);
        a4RobotRI.createNetworkElement(a4NE, a4NEG);
        a4RobotRI.createNetworkElement(a4NE2, a4NEG);
        a4RobotRI.createNetworkElement(a4NE3, a4NEG);
        a4RobotRI.createNetworkElementPort(a4NEP, a4NE);
        a4RobotRI.createNetworkElementPort(a4NEP2, a4NE2);
        a4RobotRI.createNetworkElementPort(a4NEP3, a4NE3);
        a4RobotRI.createNetworkElementPort(a4NEP4, a4NE2);
        // all nel's need same ne1 (type a10-switch) ! important for lbz in ro-items
        a4RobotRI.createNetworkElementLink(a4NEL, a4NEP, a4NEP2, a4NE, a4NE2, uewegData1);
        a4RobotRI.createNetworkElementLink(a4NEL2, a4NEP, a4NEP3, a4NE, a4NE3, uewegData2);
        a4RobotRI.createTerminationPoint(a4TP, a4NEP);
        a4RobotRI.createTerminationPoint(tpData2, a4NEP2);
        a4RobotRI.createTerminationPoint(tpData3, a4NEP3);
        a4RobotRI.createTerminationPoint(tpData4, a4NEP4);
        a4RobotRI.createNetworkServiceProfileA10Nsp(a4A10Nsp, a4TP);
        a4RobotRI.createNetworkServiceProfileA10Nsp(nspA10Data2, tpData2);
        a4RobotRI.createNetworkServiceProfileA10Nsp(nspA10Data3, tpData3);
        a4RobotRI.createNetworkServiceProfileA10Nsp(nspA10Data4, tpData4);
        ro = a4RobotRO.buildResourceOrder();
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), WIREMOCK_SCENARIO_NAME))
                .addMerlinMock()
                .addRebellMock(a4NE, uewegData1, a4NE2, uewegData2, a4NE3)
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
        wiremock.fetchAndDeleteServeEvents();
    }

    @AfterMethod
    public void cleanup() {
        // clean wiremock gedöhns
        cleanupWiremock();
        // Delete all A4 data which might provoke problems because of unique constraints
        a4RobotRI.deleteA4NetworkElementGroupsRecursively(a4NEG);
        a4RobotRI.deleteA4NetworkElementsRecursively(a4NE);
        a4RobotRI.deleteA4NetworkElementsRecursively(a4NE2);
        a4RobotRI.deleteA4NetworkElementsRecursively(a4NE3);
        a4RobotRI.deleteA4NetworkElementPortsRecursively(a4NEP, a4NE);
        a4RobotRI.deleteA4NetworkElementPortsRecursively(a4NEP2, a4NE2);
        a4RobotRI.deleteA4NetworkElementPortsRecursively(a4NEP3, a4NE3);
        a4RobotRI.deleteA4NetworkElementPortsRecursively(a4NEP4, a4NE2);
        a4RobotRO.deleteA4TestDataRecursively(ro);
    }

    private void cleanupWiremock() {
        wiremock.close();
        wiremock.eventsHook(saveEventsToDefaultDir()).eventsHook(attachEventsToAllureReport());
        wiremock.getWireMock().resetRequests();
        a4RobotRO.cleanCallbacksInWiremock();
    }

    private void sendRoAndCheckState(ResourceOrderStateType state) {
        sendRoAndCheckState(state, orderItemId);
    }

    private void sendRoAndCheckState(ResourceOrderStateType state, String orderItemId) {
        String URL_EVENT_PUBLISH = "/tardis/horizon/events/v1";
        // additional security line, to avoid existing ro
        a4RobotRO.deleteA4TestDataRecursively(ro);
        // now let's create a "new" ro
        a4RobotRO.sendPostResourceOrder(ro);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4RobotRO.checkResourceOrderState(ro, ro.getId(), state);
        a4RobotRO.checkResourceOrderItemState(ro.getId(), orderItemId, a4RobotRO.getRoiType(state));
        if (state == ResourceOrderStateType.COMPLETED) {
            a4WmRobotRebell.checkSyncRequestToRebellWiremock(getEndsz(a4NE), HttpMethod.GET, 1);
            a4WmRobotA10Nsp.checkSyncRequestToA10nspA4Wiremock(a4RobotRO.getA10NspA4Dto(ro), HttpMethod.POST, 1);
            // all done, finally check event publishing
            a4WmRobot.checkSyncRequest(URL_EVENT_PUBLISH, RequestMethod.POST, 1, 1000);
        }
    }

    @Test
    @Owner("DL_Berlinium@telekom.de")
    @TmsLink("DIGIHUB-126401")
    @Description("add-case: send RO with -add- and get -completed-")
    public void testRoAddItem() {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        // THEN
        sendRoAndCheckState(ResourceOrderStateType.COMPLETED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with two ROI -add- and get Callback with -completed-")
    public void testRo2AddItems() {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        a4RobotRO.addOrderItemAdd(orderItemId2, a4NEL2, ro);
        // THEN
        sendRoAndCheckState(ResourceOrderStateType.COMPLETED);
        a4RobotRO.checkResourceOrderItemState(ro.getId(), orderItemId2, COMPLETED);
    }

    @Test(dataProvider = "vlanRangeCombinations")
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without vlan-range values (=null)")
    public void testRoWithoutVlanRangeValues(VlanRange vlanRange) {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        a4RobotRO.setCharacteristicValue(VLAN_RANGE, vlanRange, orderItemId, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without vlan-range")
    public void testRoWithoutVlanRange() {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        a4RobotRO.removeCharacteristic(VLAN_RANGE, orderItemId, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.COMPLETED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("ro without characteristic vlan-range")
    public void testRoWithoutVlanRangeCharacteristic() {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        a4RobotRO.removeCharacteristic(VLAN_RANGE, orderItemId, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.COMPLETED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Post to Mercury is impossible")
    public void testRoNoPostToMercury() {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        // erzeugt Mercury-Fehler 409
        a4RobotRO.setCharacteristicValue(CARRIER_BSA_REFERENCE, "f26bd5de/2150/47c7/8235/a688438973a4", orderItemId, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("rebell-link for resource order from Merlin is unknown")
    public void testRoUnknownNel() {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        // Link is unknown
        a4RobotRO.setResourceName("4N1/10001-49/30/124/7KCB-49/30/125/7KCA", orderItemId, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-142958")
    @Description("error-case: send RO with Id and get Response with -400- from roo")
    public void testRoAddItemWithId() {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL2, ro);
        ro.setId(UUID.randomUUID().toString());
        log.info("+++ RO mit uuid: " + ro);
        // WHEN
        a4RobotRO.sendPostResourceOrderError400(ro);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4RobotRO.getResourceOrdersFromDbAndCheckIfNotInDb(ro);
        ro = null; // damit am Ende keine Daten gelöscht werden, die nicht vorhanden sind
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-142958")
    @Description("error-case: send RO with invalid structure and get Response with -400- from swagger")
    public void testRoAddItemWithInvalidStructure() {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL2, ro);
        assertNotNull(ro.getOrderItem());
        ro.getOrderItem().get(0).setId(null);
        log.info("+++ RO mit uuid: " + ro);
        // WHEN
        a4RobotRO.sendPostResourceOrderError400(ro);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4RobotRO.getResourceOrdersFromDbAndCheckIfNotInDb(ro);
        ro = null; // damit am Ende keine Daten gelöscht werden, die nicht vorhanden sind
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130475")
    @Description("delete-case: send RO with -delete- and get Callback with -completed-")
    public void testRoDeleteItem() {
        // GIVEN
        a4RobotRO.addOrderItemDelete(orderItemId, a4NEL, ro);
        // WHEN
        a4RobotRO.sendPostResourceOrder(ro);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4RobotRI.checkDefaultValuesNsp(a4A10Nsp);
        a4RobotRI.checkLifecycleState(a4NEL, DEACTIVATED);
        a4RobotRO.checkResourceOrderItemState(ro.getId(), orderItemId, COMPLETED);
        a4RobotNU.checkLogicalResourcePutRequestToNemoWiremock(a4NEL.getUuid());
        a4RobotNU.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(a4TP);
        a4WmRobotRebell.checkSyncRequestToRebellWiremock(getEndsz(a4NE), HttpMethod.GET, 0);
        a4WmRobotA10Nsp.checkSyncRequestToA10nspA4Wiremock(a4RobotRO.getA10NspA4Dto(ro), HttpMethod.POST, 0);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130477")
    @Description("delete-case: send RO with two -delete- and get Callback with -completed-")
    public void testRo2DeleteItems() {
        // GIVEN
        a4RobotRO.addOrderItemDelete(orderItemId, a4NEL, ro);
        a4RobotRO.addOrderItemDelete(orderItemId2, a4NEL2, ro);
        // WHEN
        a4RobotRO.sendPostResourceOrder(ro);
        sleepForSeconds(SLEEP_TIMER);
        // THEN
        a4RobotRI.checkDefaultValuesNsp(a4A10Nsp);
        a4RobotRI.checkLifecycleState(a4NEL, DEACTIVATED);
        a4RobotRI.checkLifecycleState(a4NEL2, DEACTIVATED);
        a4RobotNU.checkTwoNetworkElementLinksPutRequestToNemoWiremock(a4NEP);
        a4RobotNU.checkNetworkServiceProfileA10NspPutRequestToNemoWiremock(a4TP, 2);
        a4RobotRO.getResourceOrdersFromDbAndCheckIfCompleted(ro);
        a4RobotRO.checkResourceOrderItemState(ro.getId(), orderItemId, COMPLETED);
        a4RobotRO.checkResourceOrderItemState(ro.getId(), orderItemId2, COMPLETED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130474")
    @Description("mixed-case: send RO with -add- and -delete- and get Callback with -rejected-")
    public void testRoDeleteItemAndAddItem() {
        // GIVEN
        a4RobotRO.addOrderItemDelete(orderItemId, a4NEL, ro);
        a4RobotRO.addOrderItemAdd(orderItemId2, a4NEL2, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
    }

    @Test(dataProvider = "characteristicNamesDelete")
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-130481")
    @Description("DIGIHUB-xxx Resource order: Characteristic in delete-order item has wrong value \"\"")
    public void testRoDeleteWithEmptyValues(String cName) {
        // GIVEN
        a4RobotRO.addOrderItemDelete(orderItemId, a4NEL, ro);
        a4RobotRO.setCharacteristicValue(cName, "", orderItemId, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Modify is not implemented")
    public void testModifyNotImplemented() {
        // GIVEN
        a4RobotRO.addOrderItemModify(orderItemId, a4NEL, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
    }

    @Test
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-115133 Resource order has two order items with same resource.name (LBZ)")
    public void testRoWithDuplicateLbzInRoi() {
        // GIVEN
        final String roiId2 = "roiId2";
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        a4RobotRO.addOrderItemAdd(roiId2, a4NEL, ro); // uses same nelData, therefore same LBZ mapped to resource.name
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
        a4RobotRO.checkResourceOrderItemState(ro.getId(), roiId2, REJECTED);
    }

    @Test
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-115139 Resource order has two order items without same resource.name (LBZ)")
    public void testRoWithoutDuplicateLbzInRoi() {
        // GIVEN
        final String roiId2 = "roiId2";
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        a4RobotRO.addOrderItemAdd(roiId2, a4NEL2, ro); // uses other nelData, therefore other LBZ mapped to resource.name
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.COMPLETED);
        a4RobotRO.checkResourceOrderItemState(ro.getId(), roiId2, COMPLETED);
    }

    @Test(dataProvider = "characteristicNamesEmptyString")
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-112658 Resource order: Characteristic in order item has value empty string \"\"")
    public void testRoWithCharacteristicWithEmtpyString(String cName) {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        a4RobotRO.setCharacteristicValue(cName, "", orderItemId, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
    }

    @Test(dataProvider = "characteristicNamesEmptyList")
    @Owner("bela.kovac@t-systems.com")
    @Description("DIGIHUB-112658 Resource order: Characteristic in order item has value empty list []")
    public void testRoWithCharacteristicWithEmtpyList(String cName) {
        // GIVEN
        a4RobotRO.addOrderItemAdd(orderItemId, a4NEL, ro);
        a4RobotRO.setCharacteristicValue(cName, new ArrayList<>(), orderItemId, ro);
        // WHEN
        sendRoAndCheckState(ResourceOrderStateType.REJECTED);
    }
}
