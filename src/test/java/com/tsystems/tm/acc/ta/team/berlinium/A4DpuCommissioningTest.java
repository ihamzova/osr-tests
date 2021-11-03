package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4DpuCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Epic("OS&R")
@Feature("A4 DPU Commissioning")
@ServiceLog({
        A4_INVENTORY_IMPORTER_MS,
        A4_RESOURCE_INVENTORY_MS,
        A4_RESOURCE_INVENTORY_SERVICE_MS,
        A4_NEMO_UPDATER_MS})

public class A4DpuCommissioningTest extends GigabitTest {

    private final OsrTestContext context = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4DpuCommissioningRobot a4DpuCommissioning = new A4DpuCommissioningRobot();
    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;

    private String dpuEndSz = "dpuEndSz";
    private String dpuSerialNumber = "dpuSerialNumber";
    private String dpuMaterialNumber = "dpuMaterialNumber";
    private String dpuKlsId = "dpuKlsId";
    private String dpuFiberOnLocationId = "dpuFiberOnLocationId";
    private String oltEndSz = "oltEndSz";
    private String oltPonPort = "oltPonPort";


    @BeforeClass
    public void init() {



        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
     /*   mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4ImportCsvTest"))
                .addNemoMock()
                .build();


        mappingsContext.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

      */
    }

    @AfterMethod
    public void cleanup() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());


    }

    @Test(description = "DIGIHUB-118479 Create NetworkElement for requested DPU in Resource-Inventory and synchronize with NEMO")
    @Owner("xxxxxx@t-systems.com")
    @TmsLink("DIGIHUB-xxxxxx")
    @Description("NetworkElement (DPU) is created and NEMO is triggerd")
    public void testDpuIsCreated() {

        //Given

        // When / Action


        // Then / Assert

    }

    @Test(description = "DIGIHUB-118479 if NetworkElementGroup not found then throw an error")
    @Owner("xxxxxx@t-systems.com")
    @TmsLink("DIGIHUB-xxxxxx")
    @Description("If NetworkElementGroup not found then throw an error.")
    public void testDpuCannotCreatedNegNotFound() {

        //Given

        // When / Action


        // Then / Assert

    }

    @Test(description = "DIGIHUB-118479 if any of attributes in Task are null or empty then throw an error")
    @Owner("xxxxxx@t-systems.com")
    @TmsLink("DIGIHUB-126199")
    @Description("If any of attributes in Task are null or empty then throw an error.")
    public void testDpuCannotCreatedValidationError() {

        //Given: one or more attributes are missing

        // When: Request for CommissioningDpuA4Task is not complete
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                "",
                oltEndSz,
                oltPonPort);

        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                null,
                oltEndSz,
                oltPonPort);

        // Then: Bad Request is required

    }

    @Test(description = "DIGIHUB-118479 if DPU already existing and NetworkElementLink is OLT then update DPU")
    @Owner("xxxxxx@t-systems.com")
    @TmsLink("DIGIHUB-xxxxxx")
    @Description("If DPU already existing and NetworkElementLink is OLT then update DPU.")
    public void testDpuIsUpdated() {

        //Given

        // When / Action


        // Then / Assert

    }

    @Test(description = "DIGIHUB-118479 if DPU already existing and NetworkElementLink is not OLT then throw an error")
    @Owner("xxxxxx@t-systems.com")
    @TmsLink("DIGIHUB-xxxxxx")
    @Description("If DPU already existing and NetworkElementLink is not OLT then throw an error.")
    public void testDpuCannotUpdatedWrongNel() {

        //Given

        // When / Action


        // Then / Assert

    }
}
