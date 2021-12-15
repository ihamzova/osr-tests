package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileFtthAccessDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;


@ServiceLog({
        WG_A4_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        EA_EXT_ROUTE_MS,
        A4_RESOURCE_INVENTORY_MS,
        A4_RESOURCE_INVENTORY_SERVICE_MS,
        A4_NEMO_UPDATER_MS,
        ACCESS_LINE_MANAGEMENT})

public class NewTpFromNemoWithPreprovisioningAndNspCreationTest extends GigabitTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final AccessLineRiRobot accessLineRi = new AccessLineRiRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpFtthData;
    private PortProvisioning port;

    private NetworkServiceProfileFtthAccessDto nspFtth;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        tpFtthData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        port = osrTestContext.getData().getPortProvisioningDataProvider()
                .get(PortProvisioningCase.a4Port);

        // Ensure that no old test data is in the way
        cleanup();

        // Call test data setup method here. We cannot use @BeforeMethod because the teo test cases here are dependent
        // on each other; we cannot cleanup and setup new data in between test cases.
        setup();
    }

//    @BeforeClass
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);
    }

    @AfterClass
    public void cleanup() {
        accessLineRi.clearDatabase();
        a4Inventory.deleteA4TestDataRecursively(negData);
    }

    @Test(description = "DIGIHUB-59383 NEMO creates new Termination Point with Preprovisioning and new network service profile (FTTH Access) creation")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-59383")
    @Description("NEMO creates new Termination Point with Preprovisioning and new network service profile (FTTH Access) creation")
    public void newTpWithFtthAccessPreprovisioning() {
        // WHEN
        a4Nemo.createTerminationPoint(tpFtthData, nepData);
        sleepForSeconds(5);

        // THEN
        // U-Piter checks
        accessLineRi.checkHomeIdsCount(port);
        accessLineRi.checkLineIdsCount(port);
        accessLineRi.checkA4LineParameters(port, tpFtthData.getUuid());

        // Berlinium checks
        nspFtth = a4Inventory.checkNetworkServiceProfileFtthAccessConnectedToTerminationPointExists(tpFtthData.getUuid(), 1);
        a4NemoUpdater.checkNetworkServiceProfileFtthAccessPutRequestToNemoWiremock(tpFtthData.getUuid());
    }

    @Test(dependsOnMethods = "newTpWithFtthAccessPreprovisioning",
            description = "DIGIHUB-xxxxxx NEMO deletes a Termination Point with Deprovisioning and network service profile (FTTH Access) is deleted")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxxx")
    @Description("NEMO deletes a Termination Point with Deprovisioning and network service profile (FTTH Access) is deleted")
    public void deleteTpWithDeprovisioning() {
        // GIVEN
        assertEquals(accessLineRi.getAccessLinesByPort(port).size(), 1, "There are > 1 AccessLines on the port");

        // WHEN
        a4Nemo.deleteLogicalResource(tpFtthData.getUuid());
        sleepForSeconds(5);

        // THEN
        // Berlinium checks
        a4Inventory.checkNetworkServiceProfileFtthAccessIsDeleted(nspFtth.getUuid());
        a4Inventory.checkTerminationPointIsDeleted(tpFtthData.getUuid());
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nspFtth.getUuid(), "DELETE", 1);

        // U-Piter checks
        accessLineRi.checkPhysicalResourceRefCountA4(port, 0);
        assertEquals(accessLineRi.getAccessLinesByPort(port).size(), 0, "There are AccessLines left on the port");
//        accessLineRi.checkHomeIdsCount(port);
//        accessLineRi.checkLineIdsCount(port);
    }

}
