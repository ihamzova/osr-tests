package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
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
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.apache.commons.lang.RandomStringUtils;
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

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4DpuCommissioningRobot a4DpuCommissioning = new A4DpuCommissioningRobot();
    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neOltData;
    private A4NetworkElement neDpuData;
    private A4NetworkElement neNotDpuOltData;

    private final String dpuEndSz = "49/" + RandomStringUtils.randomNumeric(4) + "/444/7KD2";
    private final String dpuSerialNumber = "ztp_ident-IntegrationTest";
    private final String dpuMaterialNumber = "MatNumberIntegrationTest";
    private final String dpuKlsId = "dpuKlsIdIntegrationTest";
    private final String dpuFiberOnLocationId = "dpuFiberOnLocationIdIntegrationTest";
    private final String noExistingEndSz = "11/22/333/4444";
    private final String oltPonPort = "oltPonPortIntegrationTest";


    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neOltData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        neDpuData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementDPU);
        neNotDpuOltData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01);



        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {

        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neOltData, negData);
        a4ResourceInventory.createNetworkElement(neDpuData, negData);
        a4ResourceInventory.createNetworkElement(neNotDpuOltData, negData);


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

        a4ResourceInventory.deleteA4TestDataRecursively(negData);

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
    @TmsLink("DIGIHUB-126295")
    @Description("If NetworkElementGroup not found then throw an error.")
    public void testDpuCannotCreatedNegNotFound() {

        //Given
        //Scenario 1: for oltEndSz does not exists any NetworkElement
        //Scenario 2: for oltEndSz exists NetworkElement but is not an OLT

        // When / Action

        //Scenario 1:
        //Request for CommissioningDpuA4Task with not existing NE for required oltEndSz
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                noExistingEndSz,
                oltPonPort);

        //Scenario 2:
        //Request for CommissioningDpuA4Task with existing no OLT-NE for required oltEndSz
        NetworkElementDto noOltNetworkElement = a4ResourceInventory.getExistingNetworkElement(neNotDpuOltData.getUuid());
        String existingNonOltEndSz = noOltNetworkElement.getVpsz() + "/" + noOltNetworkElement.getFsz();
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                existingNonOltEndSz,
                oltPonPort);


        // Then / Assert
        //HTTP return code is 400/ Bad Request and  no DPU-NetworkElement is created

    }


    @Test(description = "DIGIHUB-118479 if DpuEndSz is not an DPU-NE then throw an error")
    @Owner("xxxxxx@t-systems.com")
    @TmsLink("DIGIHUB-126423")
    @Description("If DpuEndSz is not an DPU-NE then throw an error.")
    public void testDpuCorruptData() {

        //Given
        // for oltEndSz exists OLT NetworkElement
        // and for dpuEndSz exists NetworkElement but is not an DPU

        // When / Action

        NetworkElementDto OltNetworkElement = a4ResourceInventory.getExistingNetworkElement(neOltData.getUuid());
        String existingOltEndSz = OltNetworkElement.getVpsz() + "/" + OltNetworkElement.getFsz();
        NetworkElementDto noDpuNetworkElement = a4ResourceInventory.getExistingNetworkElement(neNotDpuOltData.getUuid());
        String existingNonDpuEndSz = noDpuNetworkElement.getVpsz() + "/" + noDpuNetworkElement.getFsz();
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksServerError(
                existingNonDpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                existingOltEndSz,
                oltPonPort);


        // Then / Assert
        //HTTP return code is 500 (Server Error)

    }


    @Test(description = "DIGIHUB-118479 if any of attributes in Task are null or empty then throw an error")
    @Owner("xxxxxx@t-systems.com")
    @TmsLink("DIGIHUB-126199")
    @Description("If any of attributes in Task are null or empty then throw an error.")
    public void testDpuCannotCreatedValidationError() {

        //Given: NE and NEG exists but in request-call one or more attributes are missing

        NetworkElementDto OltNetworkElement = a4ResourceInventory.getExistingNetworkElement(neOltData.getUuid());
        String existingOltEndSz = OltNetworkElement.getVpsz() + "/" + OltNetworkElement.getFsz();

        // When: Request for CommissioningDpuA4Task is not complete
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                "",
                existingOltEndSz,
                oltPonPort);

        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                null,
                existingOltEndSz,
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
