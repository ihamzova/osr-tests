package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.models.stable.Credentials;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.*;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ZtpIdentUiTest  extends BaseTest {


    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ImportCsvRobot a4ImportCsvRobot = new A4ImportCsvRobot();
    private final A4ResourceInventoryUiRobot a4ResourceInventoryUiRobot = new A4ResourceInventoryUiRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    A4FrontEndInventoryImporterRobot a4FrontEndInventoryImporterRobot = new A4FrontEndInventoryImporterRobot();
    private WiremockRobot wiremockRobot = new WiremockRobot();

    private A4NetworkElementGroup a4NetworkElementGroup;
    private A4NetworkElement a4NetworkElement;
    private A4NetworkElementPort a4NetworkElementPort;

    @BeforeClass
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        a4NetworkElement = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        a4NetworkElementPort = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);
        a4ResourceInventoryRobot.createNetworkElement(a4NetworkElement, a4NetworkElementGroup);

        List<A4NetworkElement> ne = Collections.singletonList(a4NetworkElement);
        File stubsPath = Paths.get(System.getProperty("user.dir"), "target/order/stubs").toFile();
        wiremockRobot.createMockForRebell(stubsPath, ne);
    }

    @AfterMethod
    public void cleanUp() {
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
        // ...
//        a4FrontEndInventoryImporterRobot.checkNetworkElementLinksExist(a4NetworkElementPort.getUuid());


        // AFTER / Clean-up
        //TODO: Links wieder löschen
        // nothing to do
    }
}
