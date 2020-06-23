package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.models.stable.Credentials;
import com.tsystems.tm.acc.data.osr.models.a4importcsvdata.A4ImportCsvDataCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ImportCsvRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryUiRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("OS&R")
@Feature("Import Network Element (Group) CSV file into A4 Resource Inventory")
@TmsLink("DIGIHUB-xxxxx")
public class A4ImportCsvTest extends BaseTest {
    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private A4ImportCsvRobot a4InventoryImporter = new A4ImportCsvRobot();
    private A4ResourceInventoryUiRobot a4ResourceInventoryUiRobot = new A4ResourceInventoryUiRobot();
    private OsrTestContext context = OsrTestContext.get();
    private A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();

    private A4ImportCsvData csvData;

    @BeforeClass
    public void init() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @BeforeMethod
    public void setup() {
        csvData = context.getData().getA4ImportCsvDataDataProvider().get(A4ImportCsvDataCase.defaultCsvFile);

        // Ensure clean state before start
        // TODO Also delete all ports (and children) potentially belonging to network elements
        a4Inventory.deleteNetworkElements(csvData);
        a4Inventory.deleteGroupByName(csvData);
    }

    @Test(description = "DIGIHUB-xxxxx Import Network Element (Group) CSV file into A4 Resource Inventory")
    @Owner("bela.kovac@t-systems.com, stefan.masztalerz@aoe.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Import Network Element (Group) CSV file into A4 Resource Inventory")
    public void testImportCsvFile() {
        // Given / Arrange
        // nothing to do

        // When / Action
        a4InventoryImporter.importCsvFileViaUi(csvData);

        // Then / Assert
        a4ResourceInventoryUiRobot.checkNetworkElementsViaUi(csvData);
        a4NemoUpdaterRobot.checkAsyncNemoUpdateCalls();

        // After / Clean-up
        a4Inventory.deleteNetworkElements(csvData);
        a4Inventory.deleteGroupByName(csvData);
    }
}
