package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_SDX6320_16;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS})
public class RandomOltDeviceCommissioningManualProcess extends GigabitTest {

    private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private final DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient();
    private OltDevice oltDevice;

    private WireMockMappingsContext mappingsContext;
    private WireMockMappingsContext mappingsContext2;

    @BeforeMethod
    public void init() {
        oltCommissioningRobot.enableFeatureToogleUiUplinkImport();

        OsrTestContext context = OsrTestContext.get();
        oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HC_MA5600);
        //oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HE_SDX_6320_16);
        Random rnd = new Random();
        char c = (char) ('B' + rnd.nextInt(25));
        ///oltDevice.setFsz("76H" + c);
        oltDevice.setFsz("76HL");

        // WireMockFactory.get().resetToDefaultMappings();

        mappingsContext = new OsrWireMockMappingsContextBuilder(WireMockFactory.get())
                .addSealMock(oltDevice)
                .addPslMock(oltDevice)
                .addPslMockXML(oltDevice)
                .build();

        mappingsContext.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        mappingsContext2 = new MercuryWireMockMappingsContextBuilder(WireMockFactory.get()) //create mocks
                .addAccessLineInventoryMock()
                .addPonInventoryMock(oltDevice)
                .addRebellUewegeMock(oltDevice)
                .build();

        mappingsContext2.publish()                                              //inject in WM
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);
    }

    @AfterMethod
    public void cleanUp() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        mappingsContext2.close();
        mappingsContext2
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        String endSz = oltDevice.getEndsz();
        log.info("+++ cleanUp delete device endsz={}", endSz);
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);
    }

    @Test(description = "DIGIHUB-53694 Manual commissioning for MA5800 with DTAG user on team environment")
    @TmsLink("DIGIHUB-53694") // Jira Id for this test in Xray
    @Description("Perform manual commissioning for not discovered MA5800 device as DTAG user")
    public void SearchAndDiscoverOlt() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        //Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        String endSz = oltDevice.getEndsz();
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();

        oltSearchPage.searchNotDiscoveredByParameters(oltDevice);
        oltSearchPage.pressManualCommissionigButton();
        OltDiscoveryPage oltDiscoveryPage = new OltDiscoveryPage();
        oltDiscoveryPage.makeOltDiscovery();
        oltDiscoveryPage.saveDiscoveryResults();
        oltDiscoveryPage.openOltSearchPage();

        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(oltDevice);
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
/*
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDetailsPage.startUplinkConfiguration();
        oltDetailsPage.saveUplinkConfiguration();

        oltDetailsPage.configureAncpSessionStart();
        oltDetailsPage.updateAncpSessionStatus();
        oltDetailsPage.checkAncpSessionStatus();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        checkPortState(oltDevice, oltDetailsPage);
        checkPorts(oltDevice);

        checkDeviceMA5800(endSz);
        oltCommissioningRobot.checkUplink(oltDevice);

        //Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // check uplink port life cycle state
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        Thread.sleep(1000); // ensure that the resource inventory database is updated
        checkUplinkDeleted(endSz);
*/
    }

    /**
     * check all port states from ethernet card
     *
     * @param device      device
     * @param detailsPage details
     */
    public void checkPortState(OltDevice device, OltDetailsPage detailsPage) {

        int startPort = 0;
        if (oltDevice.getBezeichnung().equals("SDX 6320-16")) {
            startPort = 1;
        }
        for (int port = startPort; port <= 1; ++port) {
            log.info("checkPortState() Port={}, Slot={}, PortLifeCycleState ={}", port, device.getOltSlot(), detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)));
            if (device.getOltPort().equals((Integer.toString(port)))) {
                Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), device.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString(), "active uplink portstate");
            } else {
                Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)), DevicePortLifeCycleStateUI.NOTOPERATING.toString(), "non active uplink portstate");
            }
        }
    }

    public void checkPorts(OltDevice oltDevice) {
        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(oltDevice.getEndsz()).depthQuery(3).executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(deviceList.size(), 1L, "Device is not present");

        List<Port> portList = deviceResourceInventoryManagementClient.getClient().port().listPort()
                .parentEquipmentRefEndSzQuery(oltDevice.getEndsz()).executeAs(checkStatus(HTTP_CODE_OK_200));

        // check uplink port lifecycle state
        if (deviceList.get(0).getEmsNbiName().equals(EMS_NBI_NAME_SDX6320_16)) {
            Optional<Port> uplinkPort = portList.stream()
                    .filter(port -> port.getPortName().equals(oltDevice.getOltPort()))
                    .filter(port -> port.getPortType().equals(PortType.ETHERNET))
                    .findFirst();
            Assert.assertTrue(uplinkPort.isPresent(), "ADTRAN No uplink port is present");
            Assert.assertEquals(uplinkPort.get().getLifeCycleState(), LifeCycleState.OPERATING, "Uplink port state after commissioning is not in operating state");
        } else {
            Optional<Port> uplinkPort = portList.stream()
                    .filter(port -> port.getParentEquipmentRef().getSlotName().equals(oltDevice.getOltSlot()))
                    .filter(port -> port.getPortName().equals(oltDevice.getOltPort()))
                    .filter(port -> port.getPortType().equals(PortType.ETHERNET))
                    .findFirst();
            Assert.assertTrue(uplinkPort.isPresent(), "HUAWEI No uplink port is present");
            Assert.assertEquals(uplinkPort.get().getLifeCycleState(), LifeCycleState.OPERATING, "Uplink port state after commissioning is not in operating state");
        }
    }

    /**
     * check device MA5800 data from olt-resource-inventory and UI
     */
    private void checkDeviceMA5800(String endSz) {

        if (oltDevice.getBezeichnung().equals("SDX 6320-16")) {
            return;
        }

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(endSz).depthQuery(3).executeAs(checkStatus(HTTP_CODE_OK_200));

        Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
        Device device = deviceList.get(0);
        Assert.assertEquals(device.getEndSz(), endSz, "OLT EndSz missmatch");
    }

    /**
     * check uplink is not exist in olt-resource-inventory
     */
    private void checkUplinkDeleted(String endSz) {
        List<Uplink> uplinkList = deviceResourceInventoryManagementClient.getClient().uplink().listUplink()
                .portsEquipmentBusinessRefEndSzQuery(endSz).executeAs(checkStatus(HTTP_CODE_OK_200));

        Assert.assertTrue(uplinkList.isEmpty());
    }
}


