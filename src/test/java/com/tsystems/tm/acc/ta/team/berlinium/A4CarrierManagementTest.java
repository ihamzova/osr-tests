package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4CarrierManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_CARRIER_MANAGEMENT_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;


@Slf4j
@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_CARRIER_MANAGEMENT_MS})
@Epic("OS&R domain")
@Feature("allocate one free L2BSA NSP for a dedicated AccessLine")
// müssen wir noch anlegen: @TmsLink("DIGIHUB-57771")
public class A4CarrierManagementTest extends BaseTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4CarrierManagementRobot a4CarrierManagement = new A4CarrierManagementRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpPonData;
    private A4TerminationPoint tpL2BsaData;
    private A4NetworkServiceProfileL2Bsa nspL2BsaData;
    private A4NetworkServiceProfileFtthAccess nspFtthAccess;

    @BeforeClass
    public void init() {

        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.NetworkElementGroupL2Bsa);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nspL2BsaData = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.NetworkServiceProfileL2BsaAllocate);
        nspFtthAccess = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.NetworkServiceProfileFtthAccessL2Bsa);
        tpL2BsaData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointL2Bsa);
        tpPonData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);

        // Ensure that no old test data is in the way
        cleanup();

    }

    @BeforeMethod
    public void setup() {

        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);
        a4Inventory.createTerminationPoint(tpPonData, nepData);
        a4Inventory.createNetworkServiceProfileFtthAccess(nspFtthAccess,tpPonData);
        a4Inventory.createTerminationPoint(tpL2BsaData,negData);
        a4Inventory.createNetworkServiceProfileL2Bsa(nspL2BsaData, tpL2BsaData);


    }

    @AfterMethod
    public void cleanup() {
        a4Inventory.deleteA4TestDataRecursively(negData);
    }

    @Test(description = "DIGIHUB-89261 allocateL2BsaNspTask")
    @Owner("anita.junge@t-systems.com")
    //@TmsLink("DIGIHUB-XXXXX")
    @Description("allocate one free L2BSA NSP for a dedicated AccessLine so that L2BSA products can be produced")
    public void testAllocateL2BsaNsp() {
        // THEN / Assert
        // noch ausarbeiten
        System.out.println("LineId:" + nspL2BsaData.getLineId());
        System.out.println("dataRateUp: " + nspL2BsaData.getDataRateUp());
        System.out.println("lifecycle: " + nspL2BsaData.getLifecycleState());
        nspL2BsaData.setDataRateDown("undefined");
        nspL2BsaData.setDataRateUp("undefined");
        a4CarrierManagement.sendPostForAllocateL2BsaNsp
                ("Autotest-LineId","Autotest-Carrier", 100, 1000);
    }

    @Test(description = "DIGIHUB-89180 determination of free L2BSA TP")
    @Owner("heiko.schwanke@t-systems.com")
    @Description("determination of free L2BSA TP on NEG")
    public void testDeterminationFreeL2BsaTP() {

        a4CarrierManagement.sendGetNegCarrierConnection(negData.getUuid());

        // in DB per sql: 711d393e-a007-49f2-a0cd-0d80195763b0
       // a4CarrierManagement.sendGetNegCarrierConnection("711d393e-a007-49f2-a0cd-0d80195763b0");

    }
    @Test(description = "DIGIHUB-89180 determination of free L2BSA TP")
    @Owner("heiko.schwanke@t-systems.com")
    @Description("determination of free L2BSA TP on NEG")
    public void testUnknownNegFreeL2BsaTP() throws InterruptedException {

        // unbekannte uuid
        a4CarrierManagement.sendGetNoNegCarrierConnection("711d393e-a007-49f2-a0cd-0d80195763b1");

    }
}
