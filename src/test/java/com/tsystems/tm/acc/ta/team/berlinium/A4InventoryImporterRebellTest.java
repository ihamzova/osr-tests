package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ImportCsvRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("OS&R")
@Feature("Tests API to Rebell triggerd by a ztpIdent Update for an A4 NetworkElement")
@TmsLink("DIGIHUB-xxxxx")
public class A4InventoryImporterRebellTest extends ApiTest {

    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private A4ResourceInventoryServiceRobot a4ResourceInventoryServiceRobot = new A4ResourceInventoryServiceRobot();
    private A4PreProvisioningRobot a4PreProvisioningRobot = new A4PreProvisioningRobot();
    private A4ImportCsvRobot a4ImportCsvRobot = new A4ImportCsvRobot();

    private A4NetworkElementGroup a4NetworkElementGroup;
    private A4NetworkElement a4NetworkElement;
    private A4NetworkElementPort a4NetworkElementPort;

    @BeforeClass
    public void init() {
        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        a4NetworkElement = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        a4NetworkElementPort = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);

    }

    @BeforeMethod
    public void setUp() {
        a4ResourceInventoryRobot.setUpPrerequisiteElements(a4NetworkElementGroup, a4NetworkElement, a4NetworkElementPort);
    }

    @AfterMethod
    public void cleanUp() {
        a4ResourceInventoryRobot.deletePrerequisiteElements(a4NetworkElementGroup.getUuid(), a4NetworkElement.getUuid(), a4NetworkElementPort.getUuid());
    }

    @Test(description = "DIGIHUB-57178: A4 Inventory Importer test interface to Rebell IRONMAN")
    @Owner("dl-berlinium@telekom.de; andre.riehl@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("A4 Inventory Importer test interface to Rebell IRONMAN. Request Links for a given NetworkElement")
    public void newTpWithPreprovisioning() {
        // GIVEN / Arrange
        // nothing to do

        // WHEN / Action
        a4ImportCsvRobot.sendZtpIdentUpdateViaRestInterface(a4NetworkElement);

        // THEN

        //a4PreProvisioningRobot.checkPostToPreprovisioningWiremock();

        // AFTER / Clean-up
        //a4ResourceInventoryRobot.deleteTerminationPoint(tpData.getUuid());
    }


}
