package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.*;

public class NewTpFromNemoWithPreprovisioningTest extends ApiTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpData;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        tpData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPoint);
    }

    @BeforeMethod
    public void setUp() {
        a4Inventory.setUpPrerequisiteElements(negData, neData, nepData);
    }

    @AfterMethod
    public void cleanUp() {
        a4Inventory.deletePrerequisiteElements(negData.getUuid(), neData.getUuid(), nepData.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx NEMO creates new Termination Point with Preprovisioning")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("NEMO creates new Termination Point with Preprovisioning")
    public void newTpWithPreprovisioning() {
        // GIVEN / Arrange
        // all done in setUp() method

        // WHEN / Action
        a4Nemo.createTerminationPoint(tpData, nepData);

        // THEN
        // No further assertions here except the ones in the robots themselves

        // AFTER / Clean-up
        a4Inventory.deleteTerminationPoint(tpData.getUuid());
    }
}
