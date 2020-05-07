package com.tsystems.tm.acc.ta.domain.provisioning;

import com.tsystems.tm.acc.data.models.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

public class NewTpFromNemoWithPreprovisioningAndNspCreation extends ApiTest {
    private static final int WAIT_TIME = 15_000;

    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4PreProvisioningRobot a4PreProvisioningRobot = new A4PreProvisioningRobot();
    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();

    @Test(description = "DIGIHUB-59383 NEMO creates new Termination Point with Preprovisioning and new network service profile creation")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-59383")
    @Description("NEMO creates new Termination Point with Preprovisioning and new network service profile creation")
    public void newTpWithPreprovisioning() throws InterruptedException {
        // GIVEN / Arrange
        A4NetworkElementGroup negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        A4NetworkElement neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        A4NetworkElementPort nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        A4TerminationPoint tpData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPoint);

        a4Inventory.setUpPrerequisiteElements(negData, neData, nepData);

        PortProvisioning port = osrTestContext.getData().getPortProvisioningDataProvider()
                .get(PortProvisioningCase.a4Port_domainTest);
        port.setEndSz(neData.getVpsz() + "/" + neData.getFsz());
        port.setPortNumber(nepData.getPort());

        // WHEN / Action
        a4Nemo.createTerminationPoint(tpData, nepData);
        Thread.sleep(WAIT_TIME);

        // THEN / Assert
        a4PreProvisioningRobot.checkResults(port);
        a4Inventory.checkNetworkServiceProfileConnectedToTerminationPointExists(tpData.getUuid());

        // AFTER / Clean-up
        a4Inventory.deleteNetworkServiceProfileConnectedToTerminationPoint(tpData.getUuid());
        a4Inventory.deleteTerminationPoint(tpData.getUuid());
        a4Inventory.deletePrerequisiteElements(negData.getUuid(), neData.getUuid(), nepData.getUuid());
    }
}
