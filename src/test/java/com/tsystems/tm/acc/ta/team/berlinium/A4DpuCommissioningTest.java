package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4DpuCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResilienceRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.inventory.importer.client.model.CommissioningDpuA4Task;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementLinkDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementPortDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndsz;

@Epic("OS&R")
@Feature("A4 DPU Commissioning")
@ServiceLog({
        A4_INVENTORY_IMPORTER_MS,
        A4_RESOURCE_INVENTORY_MS,
        A4_RESOURCE_INVENTORY_SERVICE_MS,
        A4_NEMO_UPDATER_MS})

public class A4DpuCommissioningTest extends GigabitTest {

    private final String nemoUpdaterRouteName = "resource-order-resource-inventory.v1.asyncUpdateNemoTask";
    private final String riPortSpecsRouteName = "resource-order-resource-inventory.v1.a4PortSpecs";

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4DpuCommissioningRobot a4DpuCommissioning = new A4DpuCommissioningRobot();
    private final A4ResilienceRobot a4ResilienceRobot = new A4ResilienceRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neOltData;
    private A4NetworkElement neDpuData;
    private A4NetworkElement neNotDpuOltData;
    private A4NetworkElementPort nepOltData;
    private A4NetworkElementPort nepDpuData;
    private A4NetworkElementPort nepDpuGfast01;
    private A4NetworkElementPort nepDpuGfast02;

    private final String dpuEndSz = "49/" + RandomStringUtils.randomNumeric(4) + "/444/7KU7";
    private final int numberOfDpuPorts = 5; // number of Ports for FSZ 7KU7
    private final String dpuSerialNumber = "ztp_ident-IntegrationTest";
    private final String dpuMaterialNumber = "MatNumberIntegrationTest";
    private final String dpuKlsId = "dpuKlsIdIntegrationTest";
    private final String dpuFiberOnLocationId = "dpuFiberOnLocationIdIntegrationTest";
    private final String noExistingEndSz = "11/22/333/4444";
    private final String noExistingOltPonPort = "123456789";

    @BeforeClass
    public void init() throws IOException {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neOltData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        neDpuData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementDPU);
        neNotDpuOltData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01);
        nepOltData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort);
        nepDpuData = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        nepDpuGfast01 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_G_FAST_01);
        nepDpuGfast02 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_G_FAST_02);

        // Ensure that no old test data is in the way
        cleanup();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neOltData, negData);
        a4ResourceInventory.createNetworkElement(neDpuData, negData);
        a4ResourceInventory.createNetworkElement(neNotDpuOltData, negData);
        a4ResourceInventory.createNetworkElementPort(nepOltData, neOltData);
        a4ResourceInventory.createNetworkElementPort(nepDpuGfast01, neDpuData);
        a4ResourceInventory.createNetworkElementPort(nepDpuGfast02, neDpuData);
    }

    @AfterMethod
    public void cleanup() throws IOException {
        a4ResourceInventory.deleteA4TestDataRecursively(negData);

        a4ResilienceRobot.changeRouteToMicroservice(nemoUpdaterRouteName, A4_NEMO_UPDATER_MS);
        a4ResilienceRobot.changeRouteToMicroservice(riPortSpecsRouteName, A4_RESOURCE_INVENTORY_MS);
    }

    @Test(description = "test DPU-NE is created and NEMO is triggerd")
    @Owner("Anita.Junge@t-systems.com")
    @TmsLink("DIGIHUB-126432")
    @Description("DIGIHUB-118479,118482 Create NetworkElement with Ports for requested DPU in Resource-Inventory and synchronize with NEMO")
    public void testDpuIsCreated() {
        //Given
        //NetworkElementGroup by oltEndSz exists
        //OLT-NE with GPON Port exists
        //DPU- NetworkElement by dpuEndSz not yet exists
        String existingOltEndSz = neOltData.getVpsz() + "/" + neOltData.getFsz();
        String existingOltPonPort = nepOltData.getUuid();

        // When / Action
        // call A4-DPU-Commissioning-Task
        a4DpuCommissioning.sendPostForCommissioningDpuA4Tasks(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                existingOltEndSz,
                existingOltPonPort);

        // Then / Assert

        //check if DPU is correct created
        String dpuFsz = dpuEndSz.substring(dpuEndSz.length() - 4);
        String dpuVpsz = dpuEndSz.substring(0, dpuEndSz.length() - 5);
        NetworkElementDto createdDpuNe = a4ResourceInventory.getExistingNetworkElementByVpszFsz(dpuVpsz, dpuFsz);
        Assert.assertEquals(createdDpuNe.getCategory(), "DPU");
        Assert.assertEquals(createdDpuNe.getZtpIdent(), dpuSerialNumber);
        Assert.assertEquals(createdDpuNe.getPlannedMatNumber(), dpuMaterialNumber);
        Assert.assertEquals(createdDpuNe.getKlsId(), dpuKlsId);
        Assert.assertEquals(createdDpuNe.getFiberOnLocationId(), dpuFiberOnLocationId);
        Assert.assertEquals(createdDpuNe.getLifecycleState(), "INSTALLING");
        Assert.assertEquals(createdDpuNe.getOperationalState(), "NOT_WORKING");
        Assert.assertEquals(createdDpuNe.getType(), "A4-DPU-4P-TP-v1");

        //check if Ports are correct created
        AtomicInteger numberGponPorts = new AtomicInteger(0);
        AtomicInteger numberGfPorts = new AtomicInteger(0);
        AtomicInteger index = new AtomicInteger(0);
        final int[] indexGponPort = new int[1];
        List<NetworkElementPortDto> createdDpuPortList = a4ResourceInventory
                .getNetworkElementPortsByNetworkElement(createdDpuNe.getUuid());
        Assert.assertEquals(createdDpuPortList.size(), numberOfDpuPorts);

        createdDpuPortList.forEach(nep -> {
            Assert.assertEquals(nep.getAdministrativeState(), "ACTIVATED");
            Assert.assertEquals(nep.getOperationalState(), "NOT_WORKING");
            index.getAndIncrement();
            if ("GPON".equals(nep.getType())) {numberGponPorts.getAndIncrement();
                    indexGponPort[0] = index.intValue();};
            if ("G_FAST_TP".equals(nep.getType())) numberGfPorts.getAndIncrement();
        });
        Assert.assertEquals(numberGponPorts.intValue(), 1);
        Assert.assertEquals(numberGfPorts.intValue(), numberOfDpuPorts - 1);

        //Check if NetworkElementlink is correct created
        List<NetworkElementLinkDto> createdNeLinks = a4ResourceInventory
                .getNetworkElementLinksByNePort(nepOltData.getUuid());
        String lbz = "DPU/1/" + existingOltEndSz + "/" + dpuEndSz;
        int indexOfGponPort = indexGponPort[0]-1;

        Assert.assertEquals(createdNeLinks.size(),1);
        Assert.assertEquals(createdNeLinks.get(0).getLifecycleState(),"INSTALLING");
        Assert.assertEquals(createdNeLinks.get(0).getOperationalState(),"NOT_WORKING");
        Assert.assertEquals(createdNeLinks.get(0).getLbz(),lbz);
        Assert.assertEquals(createdNeLinks.get(0).getEndszA(),dpuEndSz);
        Assert.assertEquals(createdNeLinks.get(0).getEndszB(),existingOltEndSz);
        Assert.assertEquals(createdNeLinks.get(0).getNetworkElementPortBUuid(),nepOltData.getUuid());
        Assert.assertEquals(createdNeLinks.get(0).getNetworkElementPortAUuid(),createdDpuPortList.
                get(indexOfGponPort).getUuid());

        //Check if NemoUpdater is triggered
        a4NemoUpdater.checkNetworkElementPutRequestToNemoWiremock(dpuVpsz, dpuFsz);
    }

    @Test(description = "test DPU-NE cannot created when NEG is not found")
    @Owner("Anita.Junge@t-systems.com")
    @TmsLink("DIGIHUB-126295")
    @Description("DIGIHUB-118479 If NetworkElementGroup not found then throw an error.")
    public void testDpuCannotCreatedNegNotFound() {
        //Given
        //for oltEndSz does not exists any NetworkElement

        // When / Action
        //Request for CommissioningDpuA4Task with not existing NE for required oltEndSz
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                noExistingEndSz,
                noExistingOltPonPort);

        // Then / Assert
        //HTTP return code is 400/ Bad Request and  no DPU-NetworkElement is created
    }

    @Test(description = "test DPU-NE cannot created with wrong oltEndSz")
    @Owner("Anita.Junge@t-systems.com")
    @TmsLink("DIGIHUB-126295")
    @Description("DIGIHUB-118479 If NetworkElementGroup not found then throw an error.")
    public void testDpuCannotCreatedWrongOltEndSz() {
        //Given
        //for oltEndSz exists NetworkElement but it is not an OLT

        // When / Action
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
                noExistingOltPonPort);

        // Then / Assert
        //HTTP return code is 400/ Bad Request and  no DPU-NetworkElement is created
    }

    @Test(description = "test DPU-NE of corrupt data")
    @Owner("Anita.Junge@t-systems.com")
    @TmsLink("DIGIHUB-126423")
    @Description("DIGIHUB-118479 If DpuEndSz is not found in catalogue or not DPU-NE Type then throw an error.")
    public void testDpuCorruptData() {
        //Given
        // for oltEndSz exists OLT NetworkElement
        // and for dpuEndSz exists NetworkElement but it is not an DPU
        String existingNonDpuEndSz = neNotDpuOltData.getVpsz() + "/" + neNotDpuOltData.getFsz();
        String existingOltEndSz = neOltData.getVpsz() + "/" + neOltData.getFsz();
        String existingOltPonPort = nepOltData.getUuid();

        // When / Action
        //Request for CommissioningDpuA4Task with existing OLT-NE for required oltEndSz
        //Scenario 1:
        // but DPU FSZ is not found in catalogue
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                "49/333/0/8KC1",
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                existingOltEndSz,
                existingOltPonPort);
        //Scenario 2:
        //but DPU FSZ is not DPU Type
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                existingNonDpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                existingOltEndSz,
                existingOltPonPort);

        // Then / Assert
        //HTTP return code is 400 (Bad Request)
    }

    @DataProvider(name = "notValidRequestParameter")
    public static Object[] isNotValidRequestParameter(){
        return new Object[]{"","null",null};
    }
    @Test(dataProvider = "notValidRequestParameter", description = "test DPU-NE cannot created of validation error")
    @Owner("Anita.Junge@t-systems.com")
    @TmsLink("DIGIHUB-126199")
    @Description("DIGIHUB-118479 If any of attributes in Task are null or empty then throw an error.")
    public void testDpuCannotCreatedValidationError(String emptyDpuFiberOnLocationId) {
        //Given
        // NE and NEG exists but in request-call one or more attributes are missing or null
        String existingOltEndSz = neOltData.getVpsz() + "/" + neOltData.getFsz();
        String existingOltPonPort = nepOltData.getUuid();

        // When
        // several Requests for CommissioningDpuA4Task with request parameter that is empty or null
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                emptyDpuFiberOnLocationId,
                existingOltEndSz,
                existingOltPonPort);

        // Then
        // Bad Request is required
    }

    @Test(description = "test DPU-NE is updated")
    @Owner("Anita.Junge@t-systems.com")
    @TmsLink("DIGIHUB-126534")
    @Description("DIGIHUB-118479,118482 If DPU already existing and NetworkElementLink is OLT then update DPU.")
    public void testDpuIsUpdated() {
        //Given
        //NetworkElementGroup by oltEndSz exists
        //OLT-NE with GPON Port exists
        //DPU- NetworkElement by dpuEndSz already exists
        //DPU- NetworkElement has Ports but some are missing
        String existingDpuEndSz = neDpuData.getVpsz() + "/" + neDpuData.getFsz();
        String existingOltEndSz = neOltData.getVpsz() + "/" + neOltData.getFsz();
        String existingOltPonPort = nepOltData.getUuid();

        // When / Action
        // call A4-DPU-Commissioning-Task
        a4DpuCommissioning.sendPostForCommissioningDpuA4Tasks(
                existingDpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                existingOltEndSz,
                existingOltPonPort);

        // Then / Assert

        //check if DPU-NE is updated
        NetworkElementDto updatedDpuNe = a4ResourceInventory.getExistingNetworkElement(neDpuData.getUuid());
        Assert.assertEquals(updatedDpuNe.getCategory(), "DPU");
        Assert.assertEquals(updatedDpuNe.getZtpIdent(), dpuSerialNumber);
        Assert.assertEquals(updatedDpuNe.getPlannedMatNumber(), dpuMaterialNumber);
        Assert.assertEquals(updatedDpuNe.getKlsId(), dpuKlsId);
        Assert.assertEquals(updatedDpuNe.getFiberOnLocationId(), dpuFiberOnLocationId);
        Assert.assertEquals(updatedDpuNe.getLifecycleState(), "INSTALLING");
        Assert.assertEquals(updatedDpuNe.getOperationalState(), "NOT_WORKING");
        Assert.assertEquals(updatedDpuNe.getType(), "A4-DPU-4P-TP-v1");

        //check if missing Ports are created and attributes of already existing ports are not updated
        AtomicInteger numberGponPorts = new AtomicInteger(0);
        AtomicInteger numberGfPorts = new AtomicInteger(0);
        AtomicInteger numberWorking = new AtomicInteger(0);
        AtomicInteger numberNotWorking = new AtomicInteger(0);

        List<NetworkElementPortDto> existingDpuPortList = a4ResourceInventory
                .getNetworkElementPortsByNetworkElement(neDpuData.getUuid());

        Assert.assertEquals(existingDpuPortList.size(), 5);
        existingDpuPortList.forEach(nep -> {
            if ("GPON".equals(nep.getType())) numberGponPorts.getAndIncrement();
            if ("G_FAST_TP".equals(nep.getType())) numberGfPorts.getAndIncrement();
            if ("WORKING".equals(nep.getOperationalState())) numberWorking.getAndIncrement();
            if ("NOT_WORKING".equals(nep.getOperationalState())) numberNotWorking.getAndIncrement();
        });
        Assert.assertEquals(numberGponPorts.intValue(), 1);
        Assert.assertEquals(numberGfPorts.intValue(), numberOfDpuPorts - 1);
        Assert.assertEquals(numberWorking.intValue(), 2);
        Assert.assertEquals(numberNotWorking.intValue(), 3);


        //Check if NemoUpdater is triggered
        String dpuFsz = existingDpuEndSz.substring(existingDpuEndSz.length() - 4);
        String dpuVpsz = existingDpuEndSz.substring(0, existingDpuEndSz.length() - 5);
        a4NemoUpdater.checkNetworkElementPutRequestToNemoWiremock(dpuVpsz, dpuFsz);
    }

    @Test(description = "test DPU-NE cannot updated with wrong NEL")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-126609")
    @Description("DIGIHUB-126609 If DPU already existing and NetworkElementLink is not OLT then throw an error.")
    public void testDpuCannotUpdatedWrongNel() {
        // GIVEN
        // First create NEP for NE with type != DPU and != OLT (in this case we use type = POD_SERVER)
        A4NetworkElementPort nepPodServer = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        a4ResourceInventory.createNetworkElementPort(nepPodServer, neNotDpuOltData);

        // NEP for NE DPU is connected to above NEP -> invalid constellation for CommissioningDpuA4Task
        A4NetworkElementLink invalidNel = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        a4ResourceInventory.createNetworkElementLink(invalidNel, nepDpuData, nepPodServer, neDpuData, neNotDpuOltData);

        CommissioningDpuA4Task comDpuTask = new CommissioningDpuA4Task()
                .dpuEndSz(getEndsz(neDpuData))
                .dpuFiberOnLocationId(dpuFiberOnLocationId)
                .dpuKlsId(dpuKlsId)
                .dpuMaterialNumber(dpuMaterialNumber)
                .dpuSerialNumber(dpuSerialNumber)
                .oltEndSz(getEndsz(neOltData))
                .oltPonPort(noExistingOltPonPort);

        // WHEN & THEN
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksBadRequest(comDpuTask);
        // Expected error msg: "A4 DPU network element link has not the same OLT"
    }

    @Test(description = "test NemoUpdater is not reachable")
    @Owner("Anita.Junge@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-126611")
    @Description("DIGIHUB-118479 If NemoUpdater is not reachable then throw Server Error.")
    public void testNemoNotReachableServerError() {
        //GIVEN
        // NE and NEG exists but Nemo is not reachable
        String existingOltEndSz = neOltData.getVpsz() + "/" + neOltData.getFsz();
        String existingOltPonPort = nepOltData.getUuid();

        a4ResilienceRobot.changeRouteToWiremock(nemoUpdaterRouteName);

        // WHEN & THEN
        // call A4-DPU-Commissioning-Task
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksServerError(
                dpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                existingOltEndSz,
                existingOltPonPort);
    }

    @Test(description = "test Resource Inventoty is not reachable")
    @Owner("Anita.Junge@t-systems.com")
    @TmsLink("DIGIHUB-127907")
    @Description("DIGIHUB-118482 If Resource Inventory is not reachable then throw Server Error.")
    public void testRiNotReachableServerError() {
        //GIVEN
        // DPU already exists
        // but for find PortSpec-List Resource Inventory is not reachable
        String existingDpuEndSz = neDpuData.getVpsz() + "/" + neDpuData.getFsz();
        String existingOltEndSz = neOltData.getVpsz() + "/" + neOltData.getFsz();
        String existingOltPonPort = nepOltData.getUuid();
        a4ResilienceRobot.changeRouteToWiremock(riPortSpecsRouteName);

        // WHEN & THEN
        // call A4-DPU-Commissioning-Task
        a4DpuCommissioning.sendPostForCommissioningDpuA4TasksServerError(
                existingDpuEndSz,
                dpuSerialNumber,
                dpuMaterialNumber,
                dpuKlsId,
                dpuFiberOnLocationId,
                existingOltEndSz,
                existingOltPonPort);
    }

}
