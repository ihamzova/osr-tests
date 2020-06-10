package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.models.stable.Credentials;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4FrontEndInventoryImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ImportCsvRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryUiRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class ZtpIdentUiTest  extends BaseTest {


    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ImportCsvRobot a4ImportCsvRobot = new A4ImportCsvRobot();
    private final A4ResourceInventoryUiRobot a4ResourceInventoryUiRobot = new A4ResourceInventoryUiRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    A4FrontEndInventoryImporterRobot a4FrontEndInventoryImporterRobot = new A4FrontEndInventoryImporterRobot();

    private A4NetworkElementGroup a4NetworkElementGroup;
    private A4NetworkElement a4NetworkElement;
    private A4NetworkElementPort a4NetworkElementPort;

    @BeforeClass
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup_NEG_A12784);
        a4NetworkElement = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement_49_30_11_7KH0);
        a4NetworkElementPort = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort_logicalLabel_10G_001);
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);
        a4ResourceInventoryRobot.createNetworkElement(a4NetworkElement, a4NetworkElementGroup);
        a4ResourceInventoryRobot.createNetworkElementPort(a4NetworkElementPort,a4NetworkElement);
    }

    @AfterMethod
    public void cleanUp() {
        a4ResourceInventoryRobot.deleteNetworkElementPort(a4NetworkElementPort.getUuid());
        a4ResourceInventoryRobot.deleteNetworkElement(a4NetworkElement.getUuid());
        a4ResourceInventoryRobot.deleteNetworkElementGroup(a4NetworkElementGroup.getUuid());

    }

    @Test(description = "DIGIHUB-xxxxx Installation user enters ZTP Ident for Network Element in UI")
    void ztpTest() {
        // GIVEN / Arrange
        String ztpIdent = "ZTP Ident UI Test " + UUID.randomUUID().toString().substring(1, 4);

        // WHEN / Action
        a4ResourceInventoryUiRobot.openNetworkElement(a4NetworkElement);
        a4ResourceInventoryUiRobot.enterZtpIdent(ztpIdent);

        // THEN

        a4ResourceInventoryUiRobot.checkMonitoringPage(a4NetworkElement, ztpIdent);
//        a4FrontEndInventoryImporterRobot.checkNetworkElementLinksExist(a4NetworkElementPort.getUuid());


        // AFTER / Clean-up
        a4FrontEndInventoryImporterRobot.cleanUpNetworkElementLinks(a4NetworkElementPort.getUuid());

    }
}
