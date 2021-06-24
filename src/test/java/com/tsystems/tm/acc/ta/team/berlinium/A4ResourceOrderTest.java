package com.tsystems.tm.acc.ta.team.berlinium;
/*
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
*/

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.Characteristic;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.testng.annotations.*;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndsz;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.*;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;

public class A4ResourceOrderTest {

    // test send a request (resource order) from simulated Merlin to Berlinium and get a callback

    private final String wiremockScenarioName = "A4ResourceOrderTest";

    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceOrderRobot a4ResourceOrderRobot = new A4ResourceOrderRobot();
    private A4NetworkElementGroup negData;
    private A4NetworkElement neData1;
    private A4NetworkElement neData2;
    private A4NetworkElementPort nepData1;
    private A4NetworkElementPort nepData2;
    private A4NetworkElementLink nelData;
    private A4NetworkServiceProfileA10Nsp nspA10Data1;
    private A4NetworkServiceProfileA10Nsp nspA10Data2;
    private A4TerminationPoint tpData1;
    private A4TerminationPoint tpData2;

    private ResourceOrder ro;
    private String corId;
    private final String reqUrl = "https://wiremock-acc-app-berlinium-03.priv.cl01.gigadev.telekom.de/test_url";

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), wiremockScenarioName)).build();

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.NetworkElementGroupL2Bsa);
        neData1 = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementA10NspSwitch01);
        nepData1 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        neData2 = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData2 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_002);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.networkElementLinkLcsInstalling);
        nspA10Data1 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        nspA10Data2 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        tpData1 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);
        tpData2 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        // what do we need in db?
        // NEG (lcs planning, ops not_working) with NE - ok
        // NE with endsz: see next line, category: A10NSP_SWITCH - ok
        // NEL with lbz: example: 4N1/10001-49/30/125/7KCB-49/30/125/7KCA
        // NEP at NEs, up to 16
        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neData1, negData);
        a4ResourceInventory.createNetworkElement(neData2, negData);
        a4ResourceInventory.createNetworkElementPort(nepData1, neData1);
        a4ResourceInventory.createNetworkElementPort(nepData2, neData2);
        a4ResourceInventory.createNetworkElementLink(nelData, nepData1, nepData2, neData1, neData2);
        a4ResourceInventory.createTerminationPoint(tpData1, nepData1);
        // a4ResourceInventory.createTerminationPoint(tpData2, nepData2);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data1, tpData1);
        //a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data2, tpData2);

        ro = buildResourceOrder();

        corId = UUID.randomUUID().toString();

        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), wiremockScenarioName))
                .addMerlinMock()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @AfterMethod
    public void cleanup() {
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        a4ResourceInventory.deleteA4TestDataRecursively(negData);
    }


    // TODO: Move test data generation in resource order mapper class

    public ResourceOrder buildResourceOrder() {
        return new ResourceOrder()
                .externalId("merlin_id_0815")
                .description("resource order of osr-tests")
                .name("resource order name")
                .orderItem(buildOrderItem());
    }

    public List<Characteristic> buildResourceCharacteristicList() {
        List<Characteristic> cList = new ArrayList<>();

        addCharacteristic("RahmenvertragsNr", "1122334455", "valueTypeRv", cList);
        addCharacteristic("Subscription.keyA", "f26bd5de-2150-47c7-8235-a688438973a4", "valueTypeCbr", cList);
        addCharacteristic("VUEP_Public_Referenz-Nr.", "A1000851", "valueTypeVuep", cList);
        addCharacteristic("MTU-Size", "1590", "valueTypeMtu", cList);
        addCharacteristic("LACP_aktiv", "true", "valueTypeLacp", cList);
        addCharacteristic("VLAN_Range", buildVlanRange(), "Object", cList);
        addCharacteristic("QoS_List", buildQosList(), "Object", cList);

        return cList;
    }

    private VlanRange buildVlanRange() {
        return new VlanRange()
                .vlanRangeLower("2")
                .vlanRangeUpper("3999");
    }

    private QosList buildQosList() {
        List<QosClass> qosClasses = new ArrayList<>();

        addQosClass("1", "0", "110", qosClasses);
        addQosClass("2", "1", "220", qosClasses);

        return new QosList().qosClasses(qosClasses);
    }

    private List<ResourceOrderItem> buildOrderItem() {
        List<ResourceOrderItem> orderItemList = new ArrayList<>();

        ResourceRefOrValue resource = new ResourceRefOrValue()
                .name(nelData.getLbz())
                .resourceCharacteristic(buildResourceCharacteristicList());

        ResourceOrderItem orderItem = new ResourceOrderItem()
                .action(OrderItemActionType.ADD)
                .resource(resource)
                .id("orderItemId");

        orderItemList.add(orderItem);

        return orderItemList;
    }

    private void addCharacteristic(String name, Object value, String valueType, List<Characteristic> cList) {
        cList.add(new Characteristic()
                .name(name)
                .value(value)
                .valueType(valueType)
        );
    }

    private void addQosClass(String className, String pBit, String bwDown, List<QosClass> qosClassList) {
        qosClassList.add(new QosClass()
                .qosClass(className)
                .qospBit(pBit)
                .qosBandwidthDown(bwDown)
        );
    }




    // TODO: Move below utility methods into resource order robot class

    public void setResourceName(String name, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemOrderItemId(ro);
        Objects.requireNonNull(roi.getResource()).setName(name);
    }

    public void setOrderItemAction(OrderItemActionType action, String orderItemId, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemOrderItemId(ro);  // bisher nur ein Item genutzt
        roi.setAction(action);
    }

    public void setCharacteristicValue(String name, String value, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemOrderItemId(ro);
        Characteristic c = getCharacteristic(name, roi);
        c.setValue(value);
    }

    public Characteristic getCharacteristic(String name, ResourceOrderItem roi) {
        List<Characteristic> rcList = Objects.requireNonNull(roi.getResource()).getResourceCharacteristic();

        if (rcList != null) {
            for (Characteristic characteristic : rcList) {
                if (characteristic.getName().equals(name)) {
                    return characteristic;
                }
            }
        }

        return null;
    }

    public ResourceOrderItem getResourceOrderItemOrderItemId(ResourceOrder ro) {
        List<ResourceOrderItem> roiList = ro.getOrderItem();

        if (roiList != null) {
            for (ResourceOrderItem resourceOrderItem : roiList) {
                if (resourceOrderItem.getId().equals("orderItemId"))
                    return resourceOrderItem;
            }
        }

        return null;
    }


// tests

    @Ignore
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("test ro")
    public void testRo() {

        ResourceOrder ro_0 = buildResourceOrder();

        System.out.println("+++ ro: " + ro_0);
        System.out.println("+++ nelData/LBZ in DB: " + nelData.getLbz());  // nelData.getLbz()
        //sleepForSeconds(60);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("a10-switch in resource order from Merlin is unknown")
    public void testUnknownSwitch() {

        ResourceOrder ro_1 = buildResourceOrder();

        setResourceName("4N4/1004-49/30/11/7KH0-49/30/12/7KE1", ro_1); // unknown Switch

        // setResourceName(nelData.getLbz(), ro_1); HINT HINT :)

        // String passenderWert = "123/456-" + getEndsz(neData1) + "-" + getEndsz(neData2);


        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_1);

        // receive callback with Mock
        sleepForSeconds(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo("/test_url")));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: " + ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean noSwitchTrue = ergList.toString().contains("no A10nsp switch found");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: " + mercuryPostFalse);
        System.out.println("+++ kein Add enthalten: " + notAddCaseTrue);
        System.out.println("+++ keinen Link gefunden: " + noNELTrue);
        System.out.println("+++ keinen A10-Switch gefunden: " + noSwitchTrue);
        System.out.println("+++ completed: " + completeTrue);
        System.out.println("+++ rejected: " + rejectTrue);
        System.out.println("+++  ");

        assertTrue(noSwitchTrue);
    }


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Post to Mercury is impossible")
    public void testNoPostToMercury() throws InterruptedException {


        ResourceOrder ro_2 = buildResourceOrder();

        setCharacteristicValue("Subscription.keyA", "f26bd5de/2150/47c7/8235/a688438973a4", ro_2); // erzeugt Mercury-Fehler 409

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_2);

        // receive callback with Mock
        TimeUnit.SECONDS.sleep(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo("/test_url")));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: " + ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean noMercuryPostTrue = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete


        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: " + noMercuryPostTrue);
        System.out.println("+++ kein Add enthalten: " + notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: " + noNELTrue);
        System.out.println("+++ completed: " + completeTrue);
        System.out.println("+++ rejected: " + rejectTrue);
        System.out.println("+++  ");

        //sleepForSeconds(5);    // Auswertung der DB

        assertTrue(noMercuryPostTrue);
    }


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("rebell-link for resource order from Merlin is unknown")
    public void testUnknownNel() {

        ResourceOrder ro_3 = buildResourceOrder();

        setResourceName("4N1/10001-49/30/124/7KCB-49/30/125/7KCA", ro_3); // Link is unknown

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_3);

        // receive callback with Mock
        sleepForSeconds(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo("/test_url")));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: " + ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete


        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: " + mercuryPostFalse);
        System.out.println("+++ kein Add enthalten: " + notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: " + noNELTrue);
        System.out.println("+++ completed: " + completeTrue);
        System.out.println("+++ rejected: " + rejectTrue);
        System.out.println("+++  ");

        //sleepForSeconds(5);    // Auswertung der DB

        assertTrue(noNELTrue);
    }


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with -add- and get Callback with -completed-")
    public void testAddItem() {

        ResourceOrder ro_4 = buildResourceOrder();

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_4);

        // receive callback with Mock
        sleepForSeconds(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo("/test_url")));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: " + ergList);

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: " + mercuryPostFalse);
        System.out.println("+++ Modify, Delete oder Prozessfehler enthalten: " + notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: " + noNELTrue);
        System.out.println("+++ completed: " + completeTrue);
        System.out.println("+++ rejected: " + rejectTrue);
        System.out.println("+++  ");

        //sleepForSeconds(60);   // Auswertung der DB

        assertTrue(completeTrue);
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

        ResourceOrder ro_8 = buildResourceOrder();
        setOrderItemAction(OrderItemActionType.DELETE, "orderItemId", ro_8); // delete is not implemented

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_8);

        // receive callback with Mock
        sleepForSeconds(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo("/test_url")));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: " + ergList);

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: " + mercuryPostFalse);
        System.out.println("+++ Delete enthalten: " + notAddCaseTrue);
        System.out.println("+++ completed: " + completeTrue);
        System.out.println("+++ rejected: " + rejectTrue);
        System.out.println("+++  ");

        assertTrue(notAddCaseTrue);
    }


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Modify is not implemented")
    public void testModifyNotImplemented() {

        ResourceOrder ro_9 = buildResourceOrder();
        setOrderItemAction(OrderItemActionType.MODIFY, "orderItemId", ro_9);// not implemented


        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_9);

        // receive callback with Mock
        sleepForSeconds(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo("/test_url")));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: " + ergList);

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: " + mercuryPostFalse);
        System.out.println("+++ Modify enthalten: " + notAddCaseTrue);
        System.out.println("+++ completed: " + completeTrue);
        System.out.println("+++ rejected: " + rejectTrue);
        System.out.println("+++  ");

        assertTrue(notAddCaseTrue);
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

}
