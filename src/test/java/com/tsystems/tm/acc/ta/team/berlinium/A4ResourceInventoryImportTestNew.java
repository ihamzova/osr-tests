package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.models.Credentials;
import com.tsystems.tm.acc.data.osr.models.a4importcsvdata.A4ImportCsvDataCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryImportRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;

@Epic("OS&R") // Domain name
@Feature("Import Network Element (Group) CSV file into A4 Resource Inventory") // Feature under test
//@TmsLink("DIGIHUB-0") // Jira id of a TestSet (if applicable)
public class A4ResourceInventoryImportTestNew extends BaseTest {

    private A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private A4ResourceInventoryImportRobot a4InventoryImporter = new A4ResourceInventoryImportRobot();
    private OsrTestContext context = OsrTestContext.get();

    @BeforeMethod
    public void prepareData() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @Test(description = "DIGIHUB-0 Import Network Element (Group) CSV file into A4 Resource Inventory")
    @Owner("bela.kovac@t-systems.com, stefan.masztalerz@aoe.com") // Comma separated owners of this tests. Only required for Domain and higher test levels.
//    @TmsLink("DIGIHUB-12345") // Jira Id for this test in Xray
    @Description("Import Network Element (Group) CSV file into A4 Resource Inventory")
    public void testImportCsvFile() {
        // Given / Arrange
        A4ImportCsvData csvData = context.getData().getA4ImportCsvDataDataProvider()
                .get(A4ImportCsvDataCase.defaultCsvFile);
        File csvFile = Paths.get( "target/","a4Testcase1.csv").toFile();
        a4InventoryImporter.generateCsvFile(csvData, csvFile);

        // When / Action
        a4InventoryImporter.importCsvFileViaUi(csvFile);

        // Then / Assert
        a4Inventory.checkNetworkElementsViaUi(csvData);

        // After / Clean-up
        a4Inventory.deleteNetworkElements(csvData);
        a4Inventory.deleteGroupByName(csvData);
    }

}
