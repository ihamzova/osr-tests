package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofileftthaccess.A4NetworkServiceProfileFtthAccessCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4CarrierManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileA10NspDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileL2BsaDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Objects;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_CARRIER_MANAGEMENT_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_MS;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;


@Slf4j
@ServiceLog({A4_RESOURCE_INVENTORY_MS, A4_CARRIER_MANAGEMENT_MS})
@Epic("OS&R domain")
@Feature("allocate one free L2BSA NSP for a dedicated AccessLine")
// müssen wir noch anlegen: @TmsLink("DIGIHUB-57771")
public class A4CarrierManagementTest extends GigabitTest {

    private static String LINE_ID_FROM_UPITER;

    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4CarrierManagementRobot a4CarrierManagement = new A4CarrierManagementRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpPonData;
    private A4TerminationPoint tpL2BsaData;
    private A4TerminationPoint tpA10NspData;
    private A4NetworkServiceProfileL2Bsa nspL2BsaData;
    private A4NetworkServiceProfileFtthAccess nspFtthAccess;
    private A4NetworkServiceProfileA10Nsp nspA10Nsp;

    @BeforeClass
    public void init() {
        LINE_ID_FROM_UPITER = "upiter-" + getRandomDigits(6);

        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.NetworkElementGroupL2Bsa);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);

        nspL2BsaData = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.NetworkServiceProfileL2BsaAllocate);
        nspL2BsaData.setLineId(null); // important for the test to work!! NSP L2BSA has to be "unclaimed"

        nspFtthAccess = osrTestContext.getData().getA4NetworkServiceProfileFtthAccessDataProvider()
                .get(A4NetworkServiceProfileFtthAccessCase.NetworkServiceProfileFtthAccessL2Bsa);
        nspFtthAccess.setLineId(LINE_ID_FROM_UPITER);

        tpL2BsaData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointL2Bsa);
        tpPonData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        tpA10NspData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);
        nspA10Nsp = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);
        a4Inventory.createTerminationPoint(tpPonData, nepData);
        a4Inventory.createNetworkServiceProfileFtthAccess(nspFtthAccess, tpPonData);
        a4Inventory.createTerminationPoint(tpL2BsaData, negData);
        a4Inventory.createNetworkServiceProfileL2Bsa(nspL2BsaData, tpL2BsaData);
        a4Inventory.createTerminationPoint(tpA10NspData, negData);
        a4Inventory.createNetworkServiceProfileA10Nsp(nspA10Nsp, tpA10NspData);
    }

    @AfterMethod
    public void cleanup() {
        // Delete all A4 data which might provoke problems because of unique constraints
        a4Inventory.deleteA4NetworkElementGroupsRecursively(negData);
        a4Inventory.deleteA4NetworkElementsRecursively(neData);
        a4Inventory.deleteA4NetworkElementPortsRecursively(nepData, neData);
        a4Inventory.deleteNspFtthAccess(nspFtthAccess);

        // Hack: Since line id of NSP L2BSA is null by default (see above) we create a dummy NSP with line id which will later be set into the NSP
        A4NetworkServiceProfileL2Bsa nspL2Dummy = new A4NetworkServiceProfileL2Bsa();
        nspL2Dummy.setLineId(LINE_ID_FROM_UPITER);
        a4Inventory.deleteNspsL2Bsa(nspL2Dummy);
    }

    @Test(description = "test allocateL2BsaNspTask")
    @Owner("anita.junge@t-systems.com")
    @TmsLink("DIGIHUB-111721")
    @Description("DIGIHUB-89261 allocate one free L2BSA NSP for a dedicated AccessLine so that L2BSA products can be produced")
    public void testAllocateL2BsaNsp() {
        // THEN / Assert

        a4CarrierManagement.sendPostForAllocateL2BsaNsp
                (LINE_ID_FROM_UPITER, "Autotest-Carrier", 100, 1000,
                        "Dienstvertrag");

        NetworkServiceProfileL2BsaDto allocatedL2BsaNSP = a4Inventory.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());
        Assert.assertEquals(allocatedL2BsaNSP.getLineId(), LINE_ID_FROM_UPITER);
        Assert.assertEquals(Objects.requireNonNull(allocatedL2BsaNSP.getServiceBandwidth()).get(0).getDataRateDown(), "1000");
        Assert.assertEquals(allocatedL2BsaNSP.getServiceBandwidth().get(0).getDataRateUp(), "100");
        Assert.assertEquals(allocatedL2BsaNSP.getL2CcId(), "Dienstvertrag");
    }

    @Test(description = "test allocateL2BsaNspTask with Error by wrong LineId")
    @Owner("anita.junge@t-systems.com")
    //@TmsLink("DIGIHUB-XXXXX")
    @Description("DIGIHUB-89261 dont found one free L2BSA NSP for a dedicated AccessLine")
    public void testAllocateL2BsaNspWrongLineId() {
        // THEN / Assert

        a4CarrierManagement.sendPostForAllocateL2BsaNspBadRequest
                ("Wrong-LineId", "Autotest-Carrier", 100, 1000,
                        "Dienstvertrag");
    }

    @Test(description = "test allocateL2BsaNspTask with Error by wrong CarrierBsaReference")
    @Owner("anita.junge@t-systems.com")
    //@TmsLink("DIGIHUB-XXXXX")
    @Description("DIGIHUB-89261 dont found one free L2BSA NSP for a dedicated AccessLine")
    public void testAllocateL2BsaNspWrongCarrierBsaReference() {
        // THEN / Assert

        a4CarrierManagement.sendPostForAllocateL2BsaNspNotFound
                (nspFtthAccess.getLineId(), "Wrong-Carrier", 100, 1000,
                        "Dienstvertrag");
    }

    @Test(description = "test ReleaseL2BsaNspTask")
    @Owner("anita.junge@t-systems.com")
    //@TmsLink("DIGIHUB-XXXXX")
    @Description("DIGIHUB-89266 release L2BSA NSP for a dedicated AccessLine so that L2BSA products can be deleted")
    public void testReleaseL2BsaNsp() {
        // THEN / Assert

        a4CarrierManagement.sendPostForReleaseL2BsaNsp(nspL2BsaData.getUuid());
        NetworkServiceProfileL2BsaDto allocatedL2BsaNSP = a4Inventory.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());
        Assert.assertNull(allocatedL2BsaNSP.getLineId());
        Assert.assertEquals(Objects.requireNonNull(allocatedL2BsaNSP.getServiceBandwidth()).get(0).getDataRateDown(), "undefined");
        Assert.assertEquals(allocatedL2BsaNSP.getServiceBandwidth().get(0).getDataRateUp(), "undefined");
        Assert.assertNull(allocatedL2BsaNSP.getL2CcId());
    }

    @Test(description = "test determination of free L2BSA TP")
    @Owner("heiko.schwanke@t-systems.com")
    @Description("DIGIHUB-89180 determination of free L2BSA TP on NEG")
    public void testDeterminationFreeL2BsaTP() {

        a4CarrierManagement.sendGetNegCarrierConnection(negData.getUuid());

        // in DB per sql: 711d393e-a007-49f2-a0cd-0d80195763b0
        // a4CarrierManagement.sendGetNegCarrierConnection("711d393e-a007-49f2-a0cd-0d80195763b0");

    }

    @Test(description = "test determination of free L2BSA TP with unknown NEG")
    @Owner("heiko.schwanke@t-systems.com")
    @Description("DIGIHUB-89180 determination of free L2BSA TP on NEG")
    public void testDeterminationFreeL2BsaTPUnknownNeg() {

        // unbekannte uuid
        a4CarrierManagement.sendGetNoNegCarrierConnection("711d393e-a007-49f2-a0cd-0d80195763b1");

    }

    @Test(description = "test ReleaseA10NspNspTask")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-130673")
    @Description("DIGIHUB-76370 build A10NSP delete use case for Resource Order Item -  set attribute of A10Nsp to default values")
    public void testReleaseA10Nsp() {
        // THEN / Assert
        a4CarrierManagement.sendPostForReleaseA10NspNsp(nspA10Nsp.getUuid());
        NetworkServiceProfileA10NspDto releasedA10Nsp = a4Inventory.getExistingNetworkServiceProfileA10Nsp(nspA10Nsp.getUuid());
        Assert.assertNull(releasedA10Nsp.getNetworkElementLinkUuid());
        Assert.assertEquals(releasedA10Nsp.getLifecycleState(), "PLANNING");
        Assert.assertEquals(releasedA10Nsp.getOperationalState(), "NOT_WORKING");
        Assert.assertNull(releasedA10Nsp.getNumberOfAssociatedNsps());
    }

    @Test(description = "test ReleaseA10NspNspTask with wrong uuid")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-130795")
    @Description("DIGIHUB-76370 build A10NSP delete use case for Resource Order Item -  set attribute of A10Nsp to default values")
    public void testReleaseA10Nsp_WrongUuid() {
        // THEN / Assert
        String wrongA10NspUuid = "nspA10Nsp";
        a4CarrierManagement.sendPostForReleaseA10NspNspBadRequest(wrongA10NspUuid);
    }

    @Test(description = "test ReleaseA10NspNspTask - NspA10Nsp not exists in RI")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-130796")
    @Description("DIGIHUB-76370 build A10NSP delete use case for Resource Order Item -  set attribute of A10Nsp to default values")
    public void testReleaseA10Nsp_A10NspNotFound() {
        // THEN / Assert
        String notExistingA10NspUuid = "69702762-6952-48aa-9c51-02bf84e7b26e";
        a4CarrierManagement.sendPostForReleaseA10NspNspA10NspNotFound(notExistingA10NspUuid);
    }
}
