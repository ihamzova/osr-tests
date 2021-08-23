package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilel2bsa.A4NetworkServiceProfileL2BsaCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.osr.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.osr.models.l2bsanspreference.L2BsaNspReferenceCase;
import com.tsystems.tm.acc.data.osr.models.networklineprofiledata.NetworkLineProfileDataCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.ta.robot.osr.*;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileFtthAccessDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileL2BsaDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceCharacteristic;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static org.testng.Assert.*;

@Epic("OS&R")
@Feature("Fulfillment L2BSA Product")
@TmsLink("DIGIHUB-xxxxx")
@ServiceLog({
        WG_A4_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        EA_EXT_ROUTE_MS,
        A4_RESOURCE_INVENTORY_MS,
        A4_RESOURCE_INVENTORY_SERVICE_MS,
        A4_NEMO_UPDATER_MS,
        ACCESS_LINE_MANAGEMENT})

public class FulfillmentL2BsaProduct extends GigabitTest {
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4Inventory = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryServiceRobot a4Nemo = new A4ResourceInventoryServiceRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final AccessLineRiRobot accessLineRi = new AccessLineRiRobot();
    private final A4CarrierManagementRobot a4CarrierManagement = new A4CarrierManagementRobot();
    private final NetworkLineProfileManagementRobot networkLineProfileManagementRobot = new NetworkLineProfileManagementRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;
    private A4NetworkElementPort nepData;
    private A4TerminationPoint tpFtthData;
    private A4TerminationPoint tpL2BsaData;
    private A4NetworkServiceProfileL2Bsa nspL2BsaData;
    private PortProvisioning port;
    private AccessLine accessLine;
    private DefaultNetworkLineProfile expectedDefaultNetworklineProfile;
    private L2BsaNspReference expectedL2BsaNspReferenceActivation;
    private L2BsaNspReference expectedL2BsaNspReferenceModification;
    private NetworkLineProfileData networkLineProfileDataActivation;
    private NetworkLineProfileData networkLineProfileDataModification;
    private NetworkLineProfileData networkLineProfileDataDeactivation;

    @BeforeClass
    public void init() {
        long SLEEP_TIMER = 15000;
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
        networkLineProfileDataActivation = osrTestContext.getData().getNetworkLineProfileDataDataProvider()
                .get(NetworkLineProfileDataCase.a4L2BsaActivation);
        networkLineProfileDataModification = osrTestContext.getData().getNetworkLineProfileDataDataProvider()
                .get(NetworkLineProfileDataCase.a4L2BsaModification);
        networkLineProfileDataDeactivation = osrTestContext.getData().getNetworkLineProfileDataDataProvider()
                .get(NetworkLineProfileDataCase.a4L2BsaDeactivation);
        expectedL2BsaNspReferenceActivation = osrTestContext.getData().getL2BsaNspReferenceDataProvider()
                .get(L2BsaNspReferenceCase.l2BsaNspReferenceActivation);
        expectedL2BsaNspReferenceModification = osrTestContext.getData().getL2BsaNspReferenceDataProvider()
                .get(L2BsaNspReferenceCase.l2BsaNspReferenceModification);
        expectedDefaultNetworklineProfile = osrTestContext.getData().getDefaultNetworkLineProfileDataProvider()
                .get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
        accessLine = new AccessLine();

        // Ensure that no old test data is in the way
        cleanup();

        a4Inventory.createNetworkElementGroup(negData);
        a4Inventory.createNetworkElement(neData, negData);
        a4Inventory.createNetworkElementPort(nepData, neData);
        a4Inventory.createTerminationPoint(tpL2BsaData,negData);
        a4Inventory.createNetworkServiceProfileL2Bsa(nspL2BsaData, tpL2BsaData);

        //Start with a4 preprovisioning to create NEG, NE, NEP, TP, NSP-Ftth-Access
        // and AccessLine with LineID  in a4-resource-inventory and al-resource-inventory.
        a4Nemo.createTerminationPoint(tpFtthData, nepData);
        try {
            TimeoutBlock timeoutBlock = new TimeoutBlock(SLEEP_TIMER); //set timeout in milliseconds
            timeoutBlock.setTimeoutInterval(1000);
            Supplier<Boolean> checkProvisioning = () -> accessLineRi.getAccessLinesByPort(port).size() == port.getAccessLinesWG();
            timeoutBlock.addBlock(checkProvisioning); // execute the runnable precondition
        } catch (Throwable e) {
            //catch the exception here . Which is block didn't execute within the time limit
        }

        //get lineId from created AccessLine
        accessLine.setLineId(accessLineRi.getAccessLinesByPort(port).get(0).getLineId());
        ResourceCharacteristic calId = new ResourceCharacteristic();
        calId.setName(ResourceCharacteristic.NameEnum.CALID);
        calId.setValue(accessLine.getLineId());

        networkLineProfileDataActivation
                .getResourceOrder().getResourceOrderItems().get(0).getResource().getResourceCharacteristics()
                .add(calId);
        networkLineProfileDataModification
                .getResourceOrder().getResourceOrderItems().get(0).getResource().getResourceCharacteristics()
                .add(calId);
        networkLineProfileDataDeactivation
                .getResourceOrder().getResourceOrderItems().get(0).getResource().getResourceCharacteristics()
                .add(calId);
    }

    @AfterClass
    public void cleanup() {
        accessLineRi.clearDatabase();
        a4Inventory.deleteA4TestDataRecursively(negData);
    }

    @Test(description = "DIGIHUB-100848 fulfillment - L2BSA Product")
    @Owner("DL-Berlinium@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Start with preprovisioning FTTH AccessLine and then perform activation of an L2BSA Product.")
    public void l2BsaProductActivationTest() {
        // WHEN / Action
        // check that preconditions are ok
        NetworkServiceProfileFtthAccessDto generatedNspFtthAccess = a4Inventory.getNetworkServiceProfilesFtthAccessByTerminationPoint(tpFtthData.getUuid()).get(0);
        String lineIdNspFtthAccess = generatedNspFtthAccess.getLineId();

        //check preprovisioning FTTH AccessLine
        //(fully checked in test: "NewTpFromNemoWithPreprovisioningAndNspCreation")
        assertEquals(accessLine.getLineId(), lineIdNspFtthAccess);

        //Activation L2BSA Product
        networkLineProfileManagementRobot.createResourceOrderRequest(networkLineProfileDataActivation.getResourceOrder(), accessLine);
        NetworkServiceProfileL2BsaDto allocatedL2BsaNSP = a4Inventory.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());

        // check L2BSA product - activation
        assertEquals(allocatedL2BsaNSP.getLineId(), accessLine.getLineId());
        assertEquals(allocatedL2BsaNSP.getServiceBandwidth().get(0).getDataRateDown(), expectedL2BsaNspReferenceActivation.getDownBandwidth().toString());
        assertEquals(allocatedL2BsaNSP.getServiceBandwidth().get(0).getDataRateUp(), expectedL2BsaNspReferenceActivation.getUpBandwidth().toString());
        //assertEquals(allocatedL2BsaNSP.getL2CcId(), expectedL2BsaNspReferenceActivation.getL2ccid());

        AccessLineDto actualAccessLine = accessLineRi.getAccessLinesByLineId(accessLine.getLineId()).get(0);
        assertEquals(actualAccessLine.getStatus(), AccessLineStatus.ASSIGNED);
        assertNull(actualAccessLine.getDefaultNetworkLineProfile());
        assertNull(actualAccessLine.getSubscriberNetworkLineProfile());
        accessLineRi.checkL2bsaNspReference(port, expectedL2BsaNspReferenceActivation);
    }

    @Test(description = "DIGIHUB-100848 fulfillment - L2BSA Product", dependsOnMethods = "l2BsaProductActivationTest")
    @Owner("DL-Berlinium@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Change of an L2BSA Product.")
    public void l2BsaProductModificationTest() {
        // Change L2BSA product (ServiceBandwidth)
        // start L2BSA product - change
        networkLineProfileManagementRobot.createResourceOrderRequest(networkLineProfileDataModification.getResourceOrder(), accessLine);

        NetworkServiceProfileL2BsaDto allocatedL2BsaNSP = a4Inventory.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());

        // check L2BSA product - change
        assertEquals(allocatedL2BsaNSP.getLineId(), accessLine.getLineId());
        assertEquals(allocatedL2BsaNSP.getServiceBandwidth().get(0).getDataRateDown(), expectedL2BsaNspReferenceModification.getDownBandwidth().toString());
        assertEquals(allocatedL2BsaNSP.getServiceBandwidth().get(0).getDataRateUp(), expectedL2BsaNspReferenceModification.getUpBandwidth().toString());
        // assertEquals(allocatedL2BsaNSP.getL2CcId(), expectedL2BsaNspReference.getL2ccid());

        AccessLineDto actualAccessLine = accessLineRi.getAccessLinesByLineId(accessLine.getLineId()).get(0);
        assertEquals(actualAccessLine.getStatus(), AccessLineStatus.ASSIGNED);
        assertNull(actualAccessLine.getDefaultNetworkLineProfile());
        assertNull(actualAccessLine.getSubscriberNetworkLineProfile());
        accessLineRi.checkL2bsaNspReference(port, expectedL2BsaNspReferenceModification);
    }

    @Test(description = "DIGIHUB-100848 fulfillment - L2BSA Product", dependsOnMethods = "l2BsaProductModificationTest")
    @Owner("DL-Berlinium@telekom.de, DL_T-Magic.U-Piter@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Deactivation of an L2BSA Product.")
    public void l2BsaProductDeactivationTest() {

        // Deactivation L2BSA product
        networkLineProfileManagementRobot.createResourceOrderRequest(networkLineProfileDataDeactivation.getResourceOrder(), accessLine);
        // check L2BSA product - deactivation
        NetworkServiceProfileL2BsaDto releasedL2BsaNSP = a4Inventory.getExistingNetworkServiceProfileL2Bsa(nspL2BsaData.getUuid());
        assertNull(releasedL2BsaNSP.getLineId());
        assertEquals(releasedL2BsaNSP.getServiceBandwidth().get(0).getDataRateDown(), "undefined");
        assertEquals(releasedL2BsaNSP.getServiceBandwidth().get(0).getDataRateUp(), "undefined");
        assertNull(releasedL2BsaNSP.getL2CcId());

        AccessLineDto actualAccessLine = accessLineRi.getAccessLinesByLineId(accessLine.getLineId()).get(0);
        assertEquals(actualAccessLine.getStatus(), AccessLineStatus.ASSIGNED);
        assertNull(actualAccessLine.getL2BsaNspReference());
        assertNotNull(actualAccessLine.getNetworkServiceProfileReference());
        //accessLineRi.checkDefaultNetworkLineProfiles(port, expectedDefaultNetworklineProfile, 1);
    }
}
