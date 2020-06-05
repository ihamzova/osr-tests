package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.models.stable.Credentials;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ImportCsvRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4UiRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class ZtpIdentUiTest  extends BaseTest {
    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private A4ImportCsvRobot a4InventoryImporter = new A4ImportCsvRobot();
    private A4UiRobot a4UiRobot = new A4UiRobot();
    private OsrTestContext osrTestContext = OsrTestContext.get();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;

    @BeforeClass
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
    }

    @AfterMethod
    public void cleanUp() {
        a4Inventory.deleteNetworkElement(neData.getUuid());
        a4Inventory.deleteNetworkElementGroup(negData.getUuid());
    }

    @Test(description = "DIGIHUB-xxxxx Installation user enters ZTP Ident for Network Element in UI")
    void ztpTest() {
        // GIVEN / Arrange
        String ztpIdent = "ZTP Ident UI Test " + UUID.randomUUID().toString().substring(1, 4);

        // WHEN / Action
        a4UiRobot.openNetworkElement(neData);
        a4UiRobot.enterZtpIdent(ztpIdent);

        // THEN
        a4UiRobot.checkMonitoringPage(neData, ztpIdent);

        // AFTER / Clean-up
        // nothing to do
    }
}
