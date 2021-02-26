package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_SERVICE_MS;

/*@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_SERVICE_MS)*/
@Epic("OS&R domain")
@Feature("Accessing entries in a4-resource-inventory via the a4-resource-inventory-service as logical resource objects")
@TmsLink("DIGIHUB-57771")
public class A4ResourceInventoryServiceTest extends BaseTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepDataA;
    private A4TerminationPoint tpL2BsaData;
    private A4NetworkServiceProfileL2Bsa nspL2Data;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepDataA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nspL2Data = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.defaultNetworkServiceProfileL2Bsa);
        tpL2BsaData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointL2Bsa);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepDataA, neData);
        a4Inventory.createTerminationPoint(tpL2BsaData, nepDataA);
        a4Inventory.createNetworkServiceProfileL2Bsa(nspL2Data, tpL2BsaData);
    }

    @AfterMethod
    public void cleanup() {
        a4Inventory.deleteA4TestData(negData, neData);
    }

    @Test(description = "DIGIHUB-57774 Create new network element in inventory and read it as logical resource")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-57774")
    @Description("Create new network element in inventory and read it as logical resource")
    public void testCreateNeg_checkLogicalResource_deleteNeg() {
        // THEN / Assert
        a4Nemo.checkLogicalResourceIsNetworkElementGroup(negData);
    }

    @Test(description = "DIGIHUB-xxxx LineID should be included in logicalResource represenation of NSP L2BSA")
    @Owner("e.balla@t-systems.com, bela.kovac@t-systems.com")
    @Description("NEMO sends a status patch for A4 Network Service Profile (L2BSA)")
    public void testNemoStatusPatchForNspL2BSA_noChanges() {
        // THEN
        a4Nemo.checkLogicalResourceHasCharacteristic(nspL2Data.getUuid(), "lineId", nspL2Data.getLineId());
    }

}
