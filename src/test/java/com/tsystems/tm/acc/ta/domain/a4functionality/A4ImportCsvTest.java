package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.models.stable.Credentials;
import com.tsystems.tm.acc.data.osr.models.a4importcsvdata.A4ImportCsvDataCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryImporterUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
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
    private OsrTestContext context = OsrTestContext.get();
    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private A4ResourceInventoryImporterUiRobot a4ResourceInventoryImporterUiRobot
            = new A4ResourceInventoryImporterUiRobot();
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

//        // Ensure clean state before start
        a4ResourceInventoryRobot.deleteNetworkElements(csvData);
        a4ResourceInventoryRobot.deleteGroupByName(csvData);
    }

    @Test(description = "DIGIHUB-xxxxx Import Network Element (Group) CSV file into A4 Resource Inventory")
    @Owner("bela.kovac@t-systems.com, stefan.masztalerz@aoe.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Import Network Element (Group) CSV file into A4 Resource Inventory")
    public void testImportCsvFile() {
        // Given / Arrange
        // nothing to do

        // When / Action
        a4ResourceInventoryImporterUiRobot.importCsvFileViaUi(csvData);

        // Then / Assert
        a4ResourceInventoryImporterUiRobot.checkNetworkElementsViaUi(csvData);
        a4NemoUpdaterRobot.checkAsyncNemoUpdatePutRequests(csvData);

        // After / Clean-up
        a4ResourceInventoryRobot.deleteNetworkElements(csvData);
        a4ResourceInventoryRobot.deleteGroupByName(csvData);
    }
}
