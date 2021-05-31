package com.tsystems.tm.acc.ta.team.berlinium;
/*
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
*/

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class A4ResourceOrderTest {

    // test send a request (resource order) from Merlin to Berlinium and get a callback

    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceOrderRobot a4ResourceOrderRobot = new A4ResourceOrderRobot();
    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
   // private A4NetworkElementPort nepData;

    private ResourceOrderCreate ro;
    private String corId;


// before, test data

    @BeforeClass
    public void init() {

        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.NetworkElementGroupL2Bsa);       // wie muss unsere NEG aussehen?
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);               // wie muss unser NE aussehen?
  /*
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);

        // Ensure that no old test data is in the way
        cleanup();

 */
    }
    @BeforeMethod
    public void setup() {

        // what do we need in db?
        a4ResourceInventory.createNetworkElementGroup(negData);
/*
        a4Inventory.createNetworkElementPort(nepData, neData);
        //a4Inventory.createTerminationPoint(tpPonData, nepData);
        //a4Inventory.createTerminationPoint(tpL2BsaData,negData);

 */
        ro = new ResourceOrderCreate();
        corId = UUID.randomUUID().toString();
    }

// after, clean
    @AfterMethod
    public void cleanup() {
        a4ResourceInventory.deleteA4TestDataRecursively(negData);
}

// tests

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @Description("a10-switch in resource order from Merlin is unknown")
    public void testUnknownSwitch() {

        // create a ro with link with NSP of unknown a10nsp; --> not yet realized

        List<ResourceOrderItem> orderItemList = new ArrayList();
        ResourceOrderItem orderItem = new ResourceOrderItem();

        ro.setAtBaseType("test");
        ro.setDescription("description of resource order");
        ro.setName("name of resource order");
        ro.setStartDate(OffsetDateTime.parse("2021-05-22T13:08:56.206+02:00"));

        orderItem.setAction(OrderItemActionType.ADD);
        orderItem.setId("itemId01");
        orderItem.setState(ResourceOrderItemStateType.valueOf("PENDING"));

        orderItemList.add(orderItem);
        ro.setOrderItem(orderItemList);
        System.out.println("+++ RO mit add-Item: " + ro);


        // send to queue
        a4ResourceOrderRobot.sendPostResourceOrder(corId, ro);


        // receive callback




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
