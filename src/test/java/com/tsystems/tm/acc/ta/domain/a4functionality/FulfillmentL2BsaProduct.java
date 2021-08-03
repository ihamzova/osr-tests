package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.*;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileFtthAccessDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileL2BsaDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.LineIdDto;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Epic("OS&R")
@Feature("Fulfillment L2BSA Product")
@TmsLink("DIGIHUB-xxxxx")
/*@ServiceLog(WG_A4_PROVISIONING_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(EA_EXT_ROUTE_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_SERVICE_MS)
@ServiceLog(A4_NEMO_UPDATER_MS)
@ServiceLog(ACCESS_LINE_MANAGEMENT)*/
public class FulfillmentL2BsaProduct extends GigabitTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final AccessLineRiRobot accessLineRi = new AccessLineRiRobot();
    private final A4CarrierManagementRobot a4CarrierManagement = new A4CarrierManagementRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpFtthData;
    private A4TerminationPoint tpL2BsaData;
    private A4NetworkServiceProfileL2Bsa nspL2BsaData;
    private PortProvisioning port;

    @BeforeClass
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        tpFtthData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointFtthAccess);
        nspL2BsaData = osrTestContext.getData().getA4NetworkServiceProfileL2BsaDataProvider()
                .get(A4NetworkServiceProfileL2BsaCase.NetworkServiceProfileL2BsaAllocate);
        tpL2BsaData = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointL2Bsa);
        port = osrTestContext.getData().getPortProvisioningDataProvider()
                .get(PortProvisioningCase.a4Port);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);
        a4Inventory.createTerminationPoint(tpL2BsaData,negData);
        a4Inventory.createNetworkServiceProfileL2Bsa(nspL2BsaData, tpL2BsaData);
    }

    @AfterMethod
    public void cleanup() {
        accessLineRi.clearDatabase();
        a4Inventory.deleteA4TestDataRecursively(negData);
    }

    @Test(description = "DIGIHUB-100848 fulfillment - L2BSA Product")
    @Owner("anita.junge@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Start with preprovisioning FTTH AccessLine and then perform activation, change and deactivation of L2BSA Product.")
    public void fulfillmentL2BsaProduct() throws InterruptedException {
        // WHEN / Action

        //Start with a4 preprovisioning to create NEG, NE, NEP, TP, NSP-Ftth-Access
        // and AccessLine with LineID  in a4-resource-inventory and al-resource-inventory.
        a4Nemo.createTerminationPoint(tpFtthData, nepData);
        long SLEEP_TIMER = 15;
        TimeUnit.SECONDS.sleep(SLEEP_TIMER);

        //get lineId from created AccessLine
        List<LineIdDto> lineIdDtoList = accessLineRi.getLineIdPool(port);
        LineIdDto lineIdDto = lineIdDtoList.get(0);
        String lineIdAccessLine = lineIdDto.getLineId();

        //get lineId from created NetworkServiceProfileFtthAccess via AccessLine
        NetworkServiceProfileFtthAccessDto generatedNspFtthAccess = a4Inventory.getNetworkServiceProfilesFtthAccessByTerminationPoint(tpFtthData.getUuid()).get(0);
        String lineIdNspFtthAccess = generatedNspFtthAccess.getLineId();

        //check preprovisioning FTTH AccessLine
        //(fully checked in test: "NewTpFromNemoWithPreprovisioningAndNspCreation")
        accessLineRi.checkLineIdsCount(port);
        Assert.assertEquals(lineIdAccessLine,lineIdNspFtthAccess);


        //Activation L2BSA Product
        a4CarrierManagement.sendPostForAllocateL2BsaNsp(lineIdAccessLine,"Autotest-Carrier",
                10000, 30000, "Dienstvertrag");
        NetworkServiceProfileL2BsaDto allocatedL2BsaNSP = a4Inventory.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());

        // check L2BSA product - activation
        Assert.assertEquals(allocatedL2BsaNSP.getLineId(),lineIdAccessLine);
        Assert.assertEquals(allocatedL2BsaNSP.getServiceBandwidth().get(0).getDataRateDown(), "30000");
        Assert.assertEquals(allocatedL2BsaNSP.getServiceBandwidth().get(0).getDataRateUp(), "10000");
        Assert.assertEquals(allocatedL2BsaNSP.getL2CcId(), "Dienstvertrag");

        // Change L2BSA product (ServiceBandwidth)
        // check L2BSA product - change


        // Deactivation L2BSA product
        a4CarrierManagement.sendPostForReleaseL2BsaNsp(nspL2BsaData.getUuid());
        NetworkServiceProfileL2BsaDto releasedL2BsaNSP = a4Inventory.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());

        // check L2BSA product - deactivation
        Assert.assertNull(releasedL2BsaNSP.getLineId());
        Assert.assertEquals(releasedL2BsaNSP.getServiceBandwidth().get(0).getDataRateDown(), "undefined");
        Assert.assertEquals(releasedL2BsaNSP.getServiceBandwidth().get(0).getDataRateUp(), "undefined");
        Assert.assertNull(releasedL2BsaNSP.getL2CcId());
    }

}
