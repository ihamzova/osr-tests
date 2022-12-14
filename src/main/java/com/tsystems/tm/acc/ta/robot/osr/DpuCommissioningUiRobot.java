package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.*;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.pages.osr.dpucommissioning.DpuInfoPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.AncpIpSubnetType;
import com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.model.AncpIpSubnet;
import com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.model.AncpSession;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.Uplink;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;
import java.util.Optional;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_DTAG;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_MA5600;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class DpuCommissioningUiRobot {

    private final DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient();
    private final AncpResourceInventoryManagementClient ancpResourceInventoryManagementClient = new AncpResourceInventoryManagementClient();
    private final UplinkResourceInventoryManagementClient uplinkResourceInventoryManagementClient = new UplinkResourceInventoryManagementClient();
    private final DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();
    private final AccessLineResourceInventoryFillDbClient accessLineResourceInventoryFillDbClient = new AccessLineResourceInventoryFillDbClient();

    @Step("Checks data in ri after commissioning process")
    public void checkDpuCommissioningResult(DpuDevice dpuDevice) {

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(dpuDevice.getEndsz()).depthQuery(1).executeAs(checkStatus(HTTP_CODE_OK_200));
        assertEquals(deviceList.size(), 1L, "DPU deviceList.size mismatch");
        assertEquals(deviceList.get(0).getDeviceType(), DeviceType.DPU, "DPU DeviceType mismatch");
        assertEquals(deviceList.get(0).getEndSz(), dpuDevice.getEndsz(), "DPU endSz mismatch");
        Device deviceAfterCommissioning = deviceList.get(0);

        assertEquals(deviceAfterCommissioning.getKlsId(), dpuDevice.getKlsId(), "DPU KlsId missmatch");
        assertEquals(deviceAfterCommissioning.getFiberOnLocationId(), dpuDevice.getFiberOnLocationId(), "DPU FiberOnLocationId mismatch");
        assertEquals(deviceAfterCommissioning.getSerialNumber(), dpuDevice.getSeriennummer(), "DPU Serialnumber mismatch");
        assertEquals(deviceAfterCommissioning.getManufacturer(), dpuDevice.getHersteller(), "DPU Manufacturer mismatch");

        // device lifecycle state
        assertEquals(deviceAfterCommissioning.getLifeCycleState(), LifeCycleState.OPERATING, "DPU LifeCycleState mismatch");

        // PON port lifecycle state
        List<Port> portList = deviceResourceInventoryManagementClient.getClient().port().listPort()
                .parentEquipmentRefEndSzQuery(dpuDevice.getEndsz()).executeAs(checkStatus(HTTP_CODE_OK_200));
        Optional<Port> ponPort = portList.stream()
                .filter(port -> port.getPortName().equals("1"))
                .filter(port -> port.getPortType().equals(PortType.PON))
                .findFirst();
        assertTrue(ponPort.isPresent(), "DPU no PON port is present");
        assertEquals( ponPort.get().getLifeCycleState(), LifeCycleState.OPERATING, "DPU PON port state after commissioning is not in operating state");

        // GFAST port lifecycle state
        Optional<Port> gfastPort = portList.stream()
                .filter(port -> port.getPortName().equals("1"))
                .filter(port -> port.getPortType().equals(PortType.GFAST))
                .findFirst();
        assertTrue(gfastPort.isPresent(), "DPU no GFAST port is present");
        assertEquals( gfastPort.get().getLifeCycleState(), LifeCycleState.OPERATING, "DPU GFAST port state after commissioning is not in operating state");

        List<AncpIpSubnet> ancpIpSubnetList = ancpResourceInventoryManagementClient.getClient().ancpIpSubnet().listAncpIpSubnet()
                .bngDownlinkPortEquipmentBusinessRefEndSzQuery(dpuDevice.getBngEndsz())
                .bngDownlinkPortEquipmentBusinessRefPortNameQuery(dpuDevice.getBngDownlinkPort())
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(ancpIpSubnetList.size(), 2L, "AncpIpSubnet size missmatch exist after commissioning");

        // DpuOltConfiguration
        List<DpuOltConfiguration> dpuOltConfigurations = deviceResourceInventoryManagementClient.getClient()
                .dpuOltConfiguration().listDpuOltConfiguration().dpuEndSzQuery(dpuDevice.getEndsz())
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        assertEquals(dpuOltConfigurations.size(), 1L, "DPU dpuEmsConfigurations.size mismatch");
        DpuOltConfiguration dpuOltConfiguration = dpuOltConfigurations.get(0);
        assertEquals(dpuOltConfiguration.getConfigurationState(), "ACTIVE", "DPU dpuOltConfigurations state mismatch" );
        assertEquals(dpuOltConfiguration.getDpuEndSz(), dpuDevice.getEndsz(),"DPU dpuOltConfigurations EndSz mismatch");
        assertEquals(dpuOltConfiguration.getSerialNumber(), dpuDevice.getSeriennummer(),"DPU dpuOltConfigurations SerialNumber mismatch");

        // DpuEmsConfiguration
        List<DpuEmsConfiguration> dpuEmsConfigurations = deviceResourceInventoryManagementClient.getClient()
                .dpuEmsConfiguration().listDpuEmsConfiguration().dpuEndSzQuery(dpuDevice.getEndsz())
                .executeAs(checkStatus(HTTP_CODE_OK_200));

        assertEquals(dpuEmsConfigurations.size(), 1L, "DPU dpuEmsConfigurations.size mismatch");
        DpuEmsConfiguration dpuEmsConfiguration = dpuEmsConfigurations.get(0);
        assertEquals(dpuEmsConfiguration.getConfigurationState(), "ACTIVE", "DPU dpuEmsConfigurations state mismatch" );
        assertEquals(dpuEmsConfiguration.getDpuEndSz(), dpuDevice.getEndsz(),"DPU dpuEmsConfigurations EndSz mismatch");
        assertEquals(dpuEmsConfiguration.getEmsNbiName(), dpuDevice.getBezeichnung(),"DPU dpuEmsConfigurations EmsNbiName mismatch");
        assertEquals(dpuEmsConfiguration.getSerialNumber(), dpuDevice.getSeriennummer(),"DPU dpuEmsConfigurations SerialNumber mismatch");
    }

    @Step("Start DPU decommissioning process v2")
    public void startDpuDecommissioningV2(DpuDevice dpuDevice) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchDiscoveredByEndSz(dpuDevice.getEndsz());
        DpuInfoPage dpuInfoPage = new DpuInfoPage();
        dpuInfoPage.validateUrl();
        dpuInfoPage.startDpuDecommissioningV2();
    }

    @Step("Checks data in ri after dpu decommissioning process")
    public void checkDpuDecommissioningResult(DpuDevice dpuDevice) {

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(dpuDevice.getEndsz()).depthQuery(1).executeAs(checkStatus(HTTP_CODE_OK_200));
        assertEquals(deviceList.size(), 1L, "DPU deviceList.size mismatch");
        assertEquals(deviceList.get(0).getDeviceType(), DeviceType.DPU, "DPU DeviceType mismatch");
        assertEquals(deviceList.get(0).getEndSz(), dpuDevice.getEndsz(), "DPU endSz mismatch");

        List<DpuEmsConfiguration> dpuEmsConfigurationList = deviceResourceInventoryManagementClient.getClient().dpuEmsConfiguration().listDpuEmsConfiguration()
                .dpuEndSzQuery(dpuDevice.getEndsz()).executeAs(checkStatus(HTTP_CODE_OK_200));
        assertEquals(dpuEmsConfigurationList.size(), 0L, "DpuEmsConfiguration exist after decommissioning");

        List<DpuOltConfiguration> dpuOltConfigurationList = deviceResourceInventoryManagementClient.getClient().dpuOltConfiguration().listDpuOltConfiguration()
                .dpuEndSzQuery(dpuDevice.getEndsz()).executeAs(checkStatus(HTTP_CODE_OK_200));
        assertEquals(dpuOltConfigurationList.size(), 0L, "DpuOltConfiguration exist after decommissioning");

        List<AncpSession> ancpSessionList = ancpResourceInventoryManagementClient.getClient().ancpSession().listAncpSession()
                .accessNodeEquipmentBusinessRefEndSzQuery(dpuDevice.getEndsz()).executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(ancpSessionList.size(), 0L, "AncpSession exist after decommissioning");

        List<AncpIpSubnet> ancpIpSubnetList = ancpResourceInventoryManagementClient.getClient().ancpIpSubnet().listAncpIpSubnet()
                .bngDownlinkPortEquipmentBusinessRefEndSzQuery(dpuDevice.getBngEndsz())
                .bngDownlinkPortEquipmentBusinessRefPortNameQuery(dpuDevice.getBngDownlinkPort())
                .executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(ancpIpSubnetList.size(), 1L, "AncpIpSubnet size missmatch exist after decommissioning");
        Assert.assertEquals(ancpIpSubnetList.get(0).getAncpIpSubnetType(), AncpIpSubnetType.OLT.toString(), "AncpIpSubnetType not OLT after decommissioning");

        List<Uplink> uplinkList = uplinkResourceInventoryManagementClient.getClient().uplink().listUplink()
                .portsEquipmentBusinessRefEndSzQuery(dpuDevice.getEndsz()).executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(uplinkList.size(), 0L, "Uplink exist after decommissioning");
    }

    @Step("Manual deletion of the DPU device")
    public void deleteDpuDevice(DpuDevice dpuDevice) {
        DpuInfoPage dpuInfoPage = new DpuInfoPage();
        dpuInfoPage.validateUrl();
        dpuInfoPage.openDpuDeletionDialog();
        dpuInfoPage.deleteDevice();
    }

    @Step("Checks DPU Device deletion")
    public void checkDpuDeviceDeletionResult(DpuDevice dpuDevice) {
        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(dpuDevice.getEndsz()).depthQuery(1).executeAs(checkStatus(HTTP_CODE_OK_200));
        assertEquals(deviceList.size(), 0L, "DPU exist after deletion");
    }

    public int countOfDevices(String endSz) {
        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(endSz).depthQuery(1).executeAs(checkStatus(HTTP_CODE_OK_200));
        return deviceList.size();
    }

    @Step("Restore accessline-resource-inventory Database state")
    public void restoreOsrDbState() {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase().deleteDatabase()
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("Clear devices (DPU and OLT) in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(DpuDevice dpuDevice) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(dpuDevice.getOltEndsz())
                .execute(checkStatus(HTTP_CODE_NO_CONTENT_204));
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(dpuDevice.getEndsz())
                .execute(checkStatus(HTTP_CODE_NO_CONTENT_204));
    }

    @Step("Create the precondition olt-resource-inventory and access-line-resource-inventory database")
    public void prepareResourceInventoryDataBase(DpuDevice dpuDevice) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().createTestData()
                .deviceEmsNbiNameQuery(EMS_NBI_NAME_MA5600)
                .deviceEndSzQuery(dpuDevice.getOltEndsz())
                .deviceSlotNumbersQuery("3,4,5,19")
                .deviceKlsIdQuery("12377812")
                .deviceCompositePartyIdQuery(COMPOSITE_PARTY_ID_DTAG.toString())
                .uplinkEndSzQuery(dpuDevice.getBngEndsz())
                .uplinkTargetPortQuery(dpuDevice.getBngDownlinkPort())
                .uplinkAncpConfigurationQuery("1")
                .executeSqlQuery("1")
                .execute(checkStatus(HTTP_CODE_OK_200));
    }
}
