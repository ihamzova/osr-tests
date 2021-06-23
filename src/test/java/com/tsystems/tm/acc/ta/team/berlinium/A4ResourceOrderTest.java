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
    private String reqUrl = "https://wiremock-acc-app-berlinium-03.priv.cl01.gigadev.telekom.de/test_url";

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

// before, test data

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
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

        nspA10Data1 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        nspA10Data2 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        tpData1 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);
        tpData2 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);

        System.out.println("+++ neData1 : "+neData1);
        System.out.println("+++ neData2 : "+neData2);

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
        a4ResourceInventory.createNetworkElementLink(nelData, nepData1, nepData2, neData1, neData2); // LBZ etwa: 4N4/1004-49/2986/0/7KCA-49/2986/0/7KCA
        a4ResourceInventory.createTerminationPoint(tpData1, nepData1);
        a4ResourceInventory.createTerminationPoint(tpData2, nepData2);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data1, tpData1);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data2, tpData2);


        corId = UUID.randomUUID().toString();

        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory
                //.get(), "NewTpFromNemoWithPreprovisioningTest"))
                .get(), "A4ResourceOrderTest"))
                .addMerlinMock()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

// after, clean
    @AfterMethod
    public void cleanup() {
        a4ResourceInventory.deleteA4TestDataRecursively(negData);



        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
}

// common
    public List<Characteristic> buildResourceCharacteristicList (){

        Characteristic rv = new Characteristic();
        Characteristic cbr = new Characteristic();
        Characteristic vuep = new Characteristic();
        Characteristic lacp = new Characteristic();
        Characteristic mtu = new Characteristic();
        Characteristic characteristicVLANrange = new Characteristic();
        VlanRange vlanRange = new VlanRange();
        List<VlanRange> vlanRanges = new ArrayList<>();
        Characteristic characteristicQos = new Characteristic();
        QosList qosList = new QosList();
        List<QosClass> qosClasses = new ArrayList<>();
        QosClass qosClass1 = new QosClass();

        ArrayList<Characteristic> resourceCharacteristicList_local = new ArrayList<>();

        rv.setName("RahmenvertragsNr");
        rv.setValue("1122334455");
        rv.setValueType("valueTypeRv");
        cbr.setName("Subscription.keyA");
        cbr.setValue("f26bd5de-2150-47c7-8235-a688438973a4");
        cbr.setValueType("valueTypeCbr");
        vuep.setName("VUEP_Public_Referenz-Nr.");
        vuep.setValue("A1000851");
        vuep.setValueType("valueTypeVuep");
        mtu.setName("MTU-Size");
        mtu.setValue("1590");
        mtu.setValueType("valueTypeMtu");
        lacp.setName("LACP_aktiv");
        lacp.setValue("true");
        lacp.setValueType("valueTypeLacp");

        vlanRange.setVlanRangeLower("2");
        vlanRange.setVlanRangeUpper("3999");
        vlanRanges.add(vlanRange);
        //vlanRangeList.setVlanRanges(vlanRanges);
        characteristicVLANrange.setName("VLAN_Range");
        characteristicVLANrange.setValue(vlanRange);
        characteristicVLANrange.setValueType("Object");

        qosClass1.setQosClass("1");
        qosClass1.setQospBit("0");
        qosClass1.setQosBandwidthDown("110");
        qosClasses.add(qosClass1);
        QosClass qosClass2 = new QosClass();
        qosClass2.setQosClass("2");
        qosClass2.setQospBit("1");
        qosClass2.setQosBandwidthDown("220");
        qosClasses.add(qosClass2);
        qosList.setQosClasses(qosClasses);
        characteristicQos.setName("QoS_List");  // alt: "QosList"
        characteristicQos.setValue(qosList);
        characteristicQos.setValueType("Object");

        resourceCharacteristicList_local.add(characteristicQos);
        resourceCharacteristicList_local.add(vuep);
        resourceCharacteristicList_local.add(rv);
        resourceCharacteristicList_local.add(mtu);
        resourceCharacteristicList_local.add(lacp);
        resourceCharacteristicList_local.add(characteristicVLANrange);
        resourceCharacteristicList_local.add(cbr);
        return resourceCharacteristicList_local;
    }
    public ResourceOrder buildResourceOrder (){


        List<ResourceOrderItem> orderItemList = new ArrayList<>();
        ResourceOrderItem orderItem1 = new ResourceOrderItem();
        ResourceRefOrValue resource = new ResourceRefOrValue();

        ResourceOrder ro_local = new ResourceOrder();

        List<Characteristic> resourceCharacteristicList = buildResourceCharacteristicList();

       //resource.setName("4N1/10001-49/30/125/7KCB-49/30/125/7KCA");
       // resource.setName("935/100211-49/30/150/7KCA-49/30/150/7KCB");
        System.out.println("+++ nelData.getLbz(): "+nelData.getLbz());
        resource.setName(nelData.getLbz());
        resource.setResourceCharacteristic(resourceCharacteristicList);

        orderItem1.setAction(OrderItemActionType.ADD);
        orderItem1.setResource(resource);
        orderItem1.setId("orderItemId");
        orderItemList.add(orderItem1);

        ro_local.setExternalId("merlin_id_0815");
        ro_local.setDescription("resource order of osr-tests");
        ro_local.setName("resource order name");
        ro_local.setOrderItem(orderItemList);

        return ro_local;
    }

// tests


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("test ro")
    public void testRo() {
       // neData2.setFsz("7KCB"); tut nicht
        ResourceOrder ro_0 = buildResourceOrder();


        System.out.println("+++ ro: "+ro_0);
        System.out.println("+++ nelData/LBZ: "+nelData.getLbz());  // nelData.getLbz()
        sleepForSeconds(15);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("a10-switch in resource order from Merlin is unknown")
    public void testUnknownSwitch() throws InterruptedException {

        ResourceOrder ro_1 = buildResourceOrder();

        setResourceName("4N4/1004-49/30/11/7KH0-49/30/12/7KE1", ro_1); // unknown Switch

//        setResourceName(nelData.getLbz(), ro_1); HINT HINT :)

   //     String passenderWert = "123/456-" + getEndsz(neData1) + "-" + getEndsz(neData2);


        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_1);

        // receive callback with Mock
        TimeUnit.SECONDS.sleep(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo( "/test_url" )));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: "+ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean noSwitchTrue = ergList.toString().contains("no A10nsp switch found");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ kein Add enthalten: "+notAddCaseTrue);
        System.out.println("+++ keinen Link gefunden: "+noNELTrue);
        System.out.println("+++ keinen A10-Switch gefunden: "+noSwitchTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        assertTrue(noSwitchTrue);
    }




    public void setResourceName(String name, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemOrderItemId(ro);
        roi.getResource().setName(name);
    }

    public void setCharacteristicValue(String name, String value, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemOrderItemId(ro);
        Characteristic c = getCharacteristic(name, roi);
        c.setValue(value);
    }

    public Characteristic getCharacteristic(String name, ResourceOrderItem roi) {
        List<Characteristic> rcList = roi.getResource().getResourceCharacteristic();

        for(Characteristic characteristic : rcList) {
            if(characteristic.getName().equals(name)) {
                return characteristic;
            }
        }

        return null;
    }

    public ResourceOrderItem getResourceOrderItemOrderItemId(ResourceOrder ro) {
        List<ResourceOrderItem> roiList = ro.getOrderItem();

        for (ResourceOrderItem resourceOrderItem : roiList) {
            if (resourceOrderItem.getId().equals("orderItemId"))
                return resourceOrderItem;
        }

        return null;
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
                                urlPathEqualTo( "/test_url" )));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: "+ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean noMercuryPostTrue = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete


        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+noMercuryPostTrue);
        System.out.println("+++ kein Add enthalten: "+notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: "+noNELTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        //TimeUnit.SECONDS.sleep(10);   // Auswertung der DB

        assertTrue(noMercuryPostTrue);
    }



    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("rebell-link for resource order from Merlin is unknown")
    public void testUnknownNel() throws InterruptedException {

        ResourceOrder ro_3 = buildResourceOrder();

        setResourceName("4N1/10001-49/30/124/7KCB-49/30/125/7KCA", ro_3); // Link is unknown

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_3);

        // receive callback with Mock
        TimeUnit.SECONDS.sleep(5);

        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo( "/test_url" )));

        System.out.println(" ");
        System.out.println("+++ ");
        System.out.println("+++ empfangener Callback: "+ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete


        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ kein Add enthalten: "+notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: "+noNELTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        //TimeUnit.SECONDS.sleep(25);   // Auswertung der DB

        assertTrue(noNELTrue);
    }





    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with -add- and get Callback with -completed-")
    public void testAddItem() throws InterruptedException {

         ResourceOrder ro_4 = buildResourceOrder();

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro_4);

        // receive callback with Mock
        TimeUnit.SECONDS.sleep(5);

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

        //TimeUnit.SECONDS.sleep(25);   // Auswertung der DB

        assertTrue(completeTrue);
    }



    /*
@Ignore
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with -add- 2 items and get Callback with -completed-")
    public void test2AddItems() throws InterruptedException {

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
        TimeUnit.SECONDS.sleep(5);

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

        //TimeUnit.SECONDS.sleep(25);   // Auswertung der DB

        assertTrue(completeTrue);
    }
     */

    /*
@Ignore
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: send RO with -add- 2 items and get Callback with -rejected-")
    public void testAdd2LinksOneIsUnknown() throws InterruptedException {

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
        TimeUnit.SECONDS.sleep(5);

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

        //TimeUnit.SECONDS.sleep(25);   // Auswertung der DB

        assertTrue(rejectTrue);
    }

     */

    /*
@Ignore
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add/delete-case: send RO with -add- and -delete- items and get Callback with -rejected-")
    public void testAddItemAndDeleteItem() throws InterruptedException {

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
        TimeUnit.SECONDS.sleep(5);

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

        //TimeUnit.SECONDS.sleep(25);   // Auswertung der DB

        assertTrue(rejectTrue);
    }

     */

    /*
@Ignore
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Delete is not implemented")
    public void testDeleteNotImplemented() throws InterruptedException {

      ResourceOrder ro_8 = buildResourceOrder();
        orderItem1.setAction(OrderItemActionType.DELETE);   // not implemented

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro);

        // receive callback with Mock
        TimeUnit.SECONDS.sleep(5);

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
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ Delete enthalten: "+notAddCaseTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        assertTrue(notAddCaseTrue);
    }

     */

    /*
@Ignore
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Modify is not implemented")
    public void testModifyNotImplemented() throws InterruptedException {

       ResourceOrder ro_9 = buildResourceOrder();
        orderItem1.setAction(OrderItemActionType.MODIFY);    // not implemented

        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(reqUrl, corId, ro);

        // receive callback with Mock
        TimeUnit.SECONDS.sleep(5);

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
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ Modify enthalten: "+notAddCaseTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
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
