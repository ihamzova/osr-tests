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
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    private ResourceOrder ro;
    private String corId;
    private String reqUrl = "https://wiremock-acc-app-berlinium-03.priv.cl01.gigadev.telekom.de/test_url";

    // Initialize with dummy wiremock so that cleanUp() call within init() doesn't run into nullpointer
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    List<ResourceOrderItem> orderItemList = new ArrayList<>();
    List<Characteristic> resourceCharacteristicList = new ArrayList<>();
    ResourceOrderItem orderItem1 = new ResourceOrderItem();
    ResourceRefOrValue resource = new ResourceRefOrValue();
    Characteristic rv = new Characteristic();
    Characteristic cbr = new Characteristic();

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
                .get(A4NetworkElementCase.networkElementA10NspSwitch01);
        nepData2 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

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
        a4ResourceInventory.createNetworkElementLink(nelData, nepData1, nepData2);

        ro = new ResourceOrder();
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

// tests

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("a10-switch in resource order from Merlin is unknown")
    public void testUnknownSwitch() throws InterruptedException {

        // create a ro with link with NSP of unknown a10nsp; --> not yet realized

        rv.setName("RahmenvertragsNr");
        rv.setValue("1122334455");
        cbr.setName("Subscription.keyA");
        cbr.setValue("f26bd5de-2150-47c7-8235-a688438973a4");
        resourceCharacteristicList.add(rv);
        resourceCharacteristicList.add(cbr);

        resource.setName("4N4/1004-49/30/11/7KH0-49/30/12/7KE1");
        resource.setResourceCharacteristic(resourceCharacteristicList);

        orderItem1.setAction(OrderItemActionType.ADD);
        orderItem1.setResource(resource);
        orderItemList.add(orderItem1);

        ro.setExternalId("merlin_id_0815");
        ro.setDescription("resource order of osr-tests");
        ro.setName("resource order name");
        ro.setOrderItem(orderItemList);

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
        System.out.println("+++ empfangener Callback: "+ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean noNELTrue = ergList.toString().contains("Links are not present");
        boolean noSwitchTrue = ergList.toString().contains("no A10nsp switch found");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete


        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ kein Add enthalten: "+notAddCaseTrue);
        System.out.println("+++ kein Link gefunden: "+noNELTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");


        assertTrue(noSwitchTrue);

    }


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Post to Mercury is impossible")
    public void testNoPostToMercury() throws InterruptedException {

        rv.setName("RahmenvertragsNr");
        rv.setValue("1122334455");
        cbr.setName("Subscription.keyA");
        cbr.setValue("f26bd5de/2150/47c7/8235/a688438973a4");  // erzeugt Mercury-Fehlermeldung 409
        resourceCharacteristicList.add(rv);
        resourceCharacteristicList.add(cbr);

        resource.setName("4N1/10001-49/30/125/7KCB-49/30/125/7KCA");
        resource.setResourceCharacteristic(resourceCharacteristicList);

        orderItem1.setAction(OrderItemActionType.ADD);
        orderItem1.setResource(resource);
        orderItemList.add(orderItem1);

        ro.setExternalId("merlin_id_0815");
        ro.setDescription("resource order of osr-tests");
        ro.setName("resource order name");
        ro.setOrderItem(orderItemList);

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

        //TimeUnit.SECONDS.sleep(25);   // Auswertung der DB

        assertTrue(noMercuryPostTrue);
        System.out.println("+++ fertig! ");
    }



    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("rebell-link for resource order from Merlin is unknown")
    public void testUnknownNel() throws InterruptedException {


        rv.setName("RahmenvertragsNr");
        rv.setValue("1122334455");
        cbr.setName("Subscription.keyA");
        cbr.setValue("f26bd5de-2150-47c7-8235-a688438973a4");
        resourceCharacteristicList.add(rv);
        resourceCharacteristicList.add(cbr);

        resource.setName("4N1/10001-49/30/124/7KCB-49/30/125/7KCA");
        resource.setResourceCharacteristic(resourceCharacteristicList);

        orderItem1.setAction(OrderItemActionType.ADD);
        orderItem1.setResource(resource);
        orderItemList.add(orderItem1);

        ro.setExternalId("merlin_id_0815");
        ro.setDescription("resource order of osr-tests");
        ro.setName("resource order name");
        ro.setOrderItem(orderItemList);

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
    @Description("add-case: Callback of Add-RO is completed")
    public void testAddLink() throws InterruptedException {
        // send a add request
        // receive a callback

        rv.setName("RahmenvertragsNr");
        rv.setValue("1122334455");
        cbr.setName("Subscription.keyA");
        cbr.setValue("f26bd5de-2150-47c7-8235-a688438973a4");
        resourceCharacteristicList.add(rv);
        resourceCharacteristicList.add(cbr);

        resource.setName("4N1/10001-49/30/125/7KCB-49/30/125/7KCA");
        resource.setResourceCharacteristic(resourceCharacteristicList);

        orderItem1.setAction(OrderItemActionType.ADD);
        orderItem1.setResource(resource);
        orderItemList.add(orderItem1);

        ro.setExternalId("merlin_id_0815");
        ro.setDescription("resource order of osr-tests");
        ro.setName("resource order name");
        ro.setOrderItem(orderItemList);

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

        assertTrue(completeTrue);


    }



    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Delete is not implemented")
    public void testDeleteNotImplemented() throws InterruptedException {

        rv.setName("RahmenvertragsNr");
        rv.setValue("1122334455");
        cbr.setName("Subscription.keyA");
        cbr.setValue("f26bd5de-2150-47c7-8235-a688438973a4");
        resourceCharacteristicList.add(rv);
        resourceCharacteristicList.add(cbr);

        resource.setName("4N1/10001-49/30/125/7KCB-49/30/125/7KCA");
        resource.setResourceCharacteristic(resourceCharacteristicList);

        orderItem1.setAction(OrderItemActionType.DELETE);
        orderItem1.setResource(resource);
        orderItemList.add(orderItem1);

        ro.setExternalId("merlin_id_0815");
        ro.setDescription("resource order of osr-tests");
        ro.setName("resource order name");
        ro.setOrderItem(orderItemList);

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
        System.out.println("+++ empfangener Callback: "+ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ kein Add enthalten: "+notAddCaseTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        assertTrue(notAddCaseTrue);

        System.out.println("+++ fertig! ");
    }


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("Delete is not implemented")
    public void testModifyNotImplemented() throws InterruptedException {

        rv.setName("RahmenvertragsNr");
        rv.setValue("1122334455");
        cbr.setName("Subscription.keyA");
        cbr.setValue("f26bd5de-2150-47c7-8235-a688438973a4");
        resourceCharacteristicList.add(rv);
        resourceCharacteristicList.add(cbr);

        resource.setName("4N1/10001-49/30/125/7KCB-49/30/125/7KCA");
        resource.setResourceCharacteristic(resourceCharacteristicList);

        orderItem1.setAction(OrderItemActionType.MODIFY);
        orderItem1.setResource(resource);
        orderItemList.add(orderItem1);

        ro.setExternalId("merlin_id_0815");
        ro.setDescription("resource order of osr-tests");
        ro.setName("resource order name");
        ro.setOrderItem(orderItemList);

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
        System.out.println("+++ empfangener Callback: "+ergList);  // liefert den gesamten Callback-Request

        boolean rejectTrue = ergList.toString().contains("rejected");
        boolean completeTrue = ergList.toString().contains("completed");
        boolean mercuryPostFalse = ergList.toString().contains("409 Conflict");
        boolean notAddCaseTrue = ergList.toString().contains("not be processed");  // modify or delete

        System.out.println("+++  ");
        System.out.println("+++ POST an Mercury fehlerhaft: "+mercuryPostFalse);
        System.out.println("+++ kein Add enthalten: "+notAddCaseTrue);
        System.out.println("+++ completed: "+completeTrue);
        System.out.println("+++ rejected: "+rejectTrue);
        System.out.println("+++  ");

        assertTrue(notAddCaseTrue);

        System.out.println("+++ fertig! ");
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
