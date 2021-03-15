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
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;

@Slf4j
@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_CARRIER_MANAGEMENT_MS})
@Epic("OS&R domain")
@Feature("allocate one free L2BSA NSP for a dedicated AccessLine")
// müssen wir noch anlegen: @TmsLink("DIGIHUB-57771")
public class A4CarrierManagementTest extends BaseTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4CarrierManagementRobot a4CarrierManagement = new A4CarrierManagementRobot();
    private final A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpPonData;
    private A4TerminationPoint tpL2BsaData;
    private A4NetworkServiceProfileL2Bsa nspL2Data;
    private A4NetworkServiceProfileFtthAccess nspFtthAccess;

    @BeforeClass
    public void init() {

        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nspL2Data = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.defaultNetworkServiceProfileL2Bsa);
        nspFtthAccess = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.defaultNetworkServiceProfileFtthAccess);
        tpL2BsaData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointL2Bsa);
        tpPonData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);



        // Ensure that no old test data is in the way
        cleanup();
        log.info("+++ cleanup beendet! ");
    }

    @BeforeMethod
    public void setup() {
        log.info("+++ create NEG, NE, Port, TP PON, NSP FTTH in DB startet! ");
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);
        a4Inventory.createTerminationPoint(tpPonData, nepData);
        a4Inventory.createNetworkServiceProfileFtthAccess(nspFtthAccess,tpPonData);
        log.info("+++ create NEG, NE, Port, TP PON, NSP FTTH in DB ok! ");

        // durch Anita auskommentiert:
        // TerminationPointL2Bsa muss zur NEG passen
        //a4Inventory.createTerminationPoint(tpL2BsaData,NEG);

        // durch Heiko auskommentiert:
        //log.info("+++ create NSP L2BSA in DB startet! ");
        //a4Inventory.createNetworkServiceProfileL2Bsa(nspL2Data, tpL2BsaData);

    }

    @AfterMethod
    public void cleanup() {
        a4Inventory.deleteA4TestData(negData, neData);
    }

    @Test(description = "DIGIHUB-89261 allocateL2BsaNspTask")
    @Owner("anita.junge@t-systems.com")
    //@TmsLink("DIGIHUB-XXXXX")
    @Description("allocate one free L2BSA NSP for a dedicated AccessLine so that L2BSA products can be produced")
    public void testAllocateL2BsaNsp() {
        // THEN / Assert
        // noch ausarbeiten
        a4CarrierManagement.sendPostForAllocateL2BsaNsp
                ("LineId","CarrierBsaReference", 100, 1000);
    }

    @Test(description = "DIGIHUB-89180 determination of free L2BSA TP")
    @Owner("heiko.schwanke@t-systems.com")
    @Description("determination of free L2BSA TP on NEG")
    public void testDeterminationFreeL2BsaTP() throws InterruptedException {
        log.info("+++ Test startet ");
        log.info("+++ NEG: "+negData);
        log.info("+++ NE: "+neData);
        log.info("+++ Port: "+nepData);
        log.info("+++ TP: "+tpL2BsaData);
       // a4CarrierManagement.sendGetForNegCarrierConnection(negData.getUuid()); // init: c0b5da61-5359-4134-9d22-dc731d8520c3
        a4CarrierManagement.sendGetForNegCarrierConnection("c0b5da61-5359-4134-9d22-dc731d8520c3"); // in AWS: 712d393e-da93-49f2-a0cd-0d80195763b0
        log.info("+++ Test endet ");
        Thread.sleep(2000);
    }

}
