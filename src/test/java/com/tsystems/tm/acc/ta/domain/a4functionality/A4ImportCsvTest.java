package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.data.osr.models.a4importcsvdata.A4ImportCsvDataCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryImporterUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Epic("OS&R")
@Feature("Import Network Element (Group) CSV file into A4 Resource Inventory")
@TmsLink("DIGIHUB-xxxxx")
@ServiceLog(A4_INVENTORY_IMPORTER_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_UI_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_BFF_PROXY_MS)
@ServiceLog(A4_NEMO_UPDATER_MS)
public class A4ImportCsvTest extends BaseTest {

    private final OsrTestContext context = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryImporterUiRobot a4ResourceInventoryImporterUiRobot = new A4ResourceInventoryImporterUiRobot();
    private final A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();
    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    private A4ImportCsvData csvData;

    @BeforeClass
    public void init() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        csvData = context.getData().getA4ImportCsvDataDataProvider().get(A4ImportCsvDataCase.defaultCsvFile);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4ImportCsvTest"))
                .addNemoMock()
                .build();

        a4ResourceInventoryImporterUiRobot.openA4ImportPage();
        mappingsContext.publish();
    }

    @AfterMethod
    public void cleanup() {
        mappingsContext.deleteAll();

        a4ResourceInventoryRobot.deleteA4EntriesIncludingNeps(csvData);
    }

    @Test(description = "DIGIHUB-xxxxx Import Network Element (Group) CSV file into A4 Resource Inventory")
    @Owner("Phillip.Moeller@t-systems.com, Anita.Junge@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Import Network Element (Group) CSV file into A4 Resource Inventory")
    public void testImportCsvFile() {
        // When / Action
        a4ResourceInventoryImporterUiRobot.importCsvFileViaUi(csvData);

        // Then / Assert
        a4ResourceInventoryRobot.checkNetworkElementByCsvData(csvData);
        a4ResourceInventoryRobot.checkNetworkElementPortsByImportCsvData(csvData);
        a4NemoUpdaterRobot.checkAsyncNemoUpdatePutRequests(csvData);
    }
}
