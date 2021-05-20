package com.tsystems.tm.acc.ta.team.berlinium;
/*
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
*/
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4CarrierManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
//import com.tsystems.tm.a4.queuedispatcher;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import org.springframework.jms.annotation.EnableJms;
//import org.springframework.jms.core.JmsTemplate;

//import com.tsystems.tm.a4.resourceorderorchestrator.queue;
//import javax.jms.JMSException;
//import org.springframework.jms.core.MessageCreator;
//import com.tsystems.tm.a4.queuedispatcher.queue.QueueConfiguration;
import io.qameta.allure.*;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_UI_MS;


public class A4ResourceOrderTest {

    // test simulate Merlin
    // test send a request (resource order) from Merlin to Berlinium and get a callback

    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4CarrierManagementRobot a4CarrierManagement = new A4CarrierManagementRobot();

    private final A4ResourceOrderRobot a4ResourceOrderRobot = new A4ResourceOrderRobot();
    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;

    private ResourceOrderCreate ro;
    private String corId;



// before, test data

    @BeforeClass
    public void init() {
/*
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.NetworkElementGroupL2Bsa);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);

        // Ensure that no old test data is in the way
        cleanup();

 */
    }
    @BeforeMethod
    public void setup() {
/*
        // what do we need in db?
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);
        //a4Inventory.createTerminationPoint(tpPonData, nepData);
        //a4Inventory.createTerminationPoint(tpL2BsaData,negData);


 */
        ro = new ResourceOrderCreate();
       // ro.setAtBaseType("test");           // wofür?
       // corId = UUID.randomUUID().toString();

    }

// after, clean
    @AfterMethod
    public void cleanup() {
        //    a4Inventory.deleteA4TestDataRecursively(negData);
}



// tests

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("a10-switch in resource order from Merlin is unknown")
    public void testUnknownSwitch() {

        // create a ro with link with NSP of unknown a10nsp

        //System.out.println("+++ RO noch leer: "+ro);

        List<ResourceOrderItem> orderItemList = new ArrayList();
        ResourceOrderItem orderItem = new ResourceOrderItem();

        orderItem.setAction(OrderItemActionType.ADD);
        orderItemList.add(orderItem);
        ro.setOrderItem(orderItemList);
       // System.out.println("+++ RO mit add-Item: " + ro);

        // send to queue, with QueueProducer? (qp.sendToResourceOrderOrchestrator(ro, corId, "http://localhost");)   eher nicht
        // Eingangsrequest über: <Umgebung>/reqcb/resource-order-resource-inventory/v1/resourceOrder

      //  public static final String ENDPOINT = Umgebung + "/reqcb/resource-order-resource-inventory/v1/resourceOrder";

        corId = "0815";

      a4ResourceOrderRobot.sendPostResourceOrder(corId, ro);   // ResourceOrder oder A4ResourceOrder?










        // receive a callback
    }














    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("rebell-link for resource order from Merlin is unknown")
    public void testUnknownNel() {
        //

    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("add-case: NSP of a10nsp changed from PLANNING to INSTALLING")
    public void testAddLink() {
        // send a request with link with NSP of a10nsp lcs-state 'planning'
        // receive a callback



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
