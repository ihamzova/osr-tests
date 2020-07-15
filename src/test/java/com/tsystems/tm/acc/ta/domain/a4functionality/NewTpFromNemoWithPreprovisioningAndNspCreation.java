package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.team.upiter.common.UpiterConstants.*;


@ServiceLog(WG_A4_PROVISIONING_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(EA_EXT_ROUTE_MS)
public class NewTpFromNemoWithPreprovisioningAndNspCreation extends BaseTest {
    private static final int WAIT_TIME = 15_000;

    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();
    private A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private A4PreProvisioningRobot a4PreProvisioning = new A4PreProvisioningRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpData;
    private PortProvisioning port;

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
        port = osrTestContext.getData().getPortProvisioningDataProvider()
                .get(PortProvisioningCase.a4Port);

        // Ensure that no old test data is in the way
        a4PreProvisioning.clearData();
        a4Inventory.deleteA4NetworkElementsIncludingChildren(neData);
        a4Inventory.deleteNetworkElementGroups(negData);
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);
    }

    @AfterMethod
    public void cleanup() {
        a4PreProvisioning.clearData();
        a4Inventory.deleteA4NetworkElementsIncludingChildren(neData);
        a4Inventory.deleteNetworkElementGroups(negData);
    }

    @Test(description = "DIGIHUB-59383 NEMO creates new Termination Point with Preprovisioning and new network service profile creation")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-59383")
    @Description("NEMO creates new Termination Point with Preprovisioning and new network service profile creation")
    public void newTpWithPreprovisioning() throws InterruptedException {
        // GIVEN / Arrange
        // nothing to do

        // WHEN / Action
        a4Nemo.createTerminationPoint(tpData, nepData);
        Thread.sleep(WAIT_TIME);

        // THEN / Assert
        a4PreProvisioning.checkResults(port);
        a4Inventory.checkNetworkServiceProfileConnectedToTerminationPointExists(tpData.getUuid());
        a4NemoUpdater.checkNetworkServiceProfilePutRequestToNemoWiremock(tpData.getUuid());

        // AFTER / Clean-up
        // nothing to do
    }
}
