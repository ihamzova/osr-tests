package com.tsystems.tm.acc.ta.team.mercury.commissioning.auto;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.*;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS})
@Epic("OS&R")
@Feature("Description olt auto-commissioning incl. LC-Commissioning Testcase on Mercury Team-environment")
@TmsLink("DIGIHUB-52132") // This is the Jira id of TestSet
public class OltAutoCommissioning extends GigabitTest {

    private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 2 * 60_000;
    private static final int WAIT_TIME_FOR_RENDERING = 5_000;

    private static final String KLS_ID_EXPECTED = "17056514";

    private OltDevice oltDeviceDTAG;
    private OltDevice oltDeviceGFNW;
    private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private final DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient();
    private WireMockMappingsContext mappingsContext;
    private WireMockMappingsContext mappingsContext2;

    @BeforeClass
    public void init() {
        oltCommissioningRobot.enableFeatureToogleUiUplinkImport();

        OsrTestContext context = OsrTestContext.get();
        oltDeviceDTAG = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HG_SDX_6320_16);
        oltDeviceGFNW = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z9_SDX_6320);

        mappingsContext = new OsrWireMockMappingsContextBuilder(WireMockFactory.get())
                .addSealMock(oltDeviceDTAG)
                .addSealMock(oltDeviceGFNW)
                .addPslMock(oltDeviceDTAG)
                .addPslMock(oltDeviceGFNW)
                .addPslMockXML(oltDeviceDTAG)
                .addPslMockXML(oltDeviceGFNW)
                .build();

        mappingsContext.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        mappingsContext2 = new MercuryWireMockMappingsContextBuilder(WireMockFactory.get()) //create mocks
                .addRebellUewegeMock(oltDeviceDTAG)
                .addRebellUewegeMock(oltDeviceGFNW)
                .build();

        mappingsContext2.publish()                                              //inject in WM
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());
    }

    @AfterClass
    public void cleanUp() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        mappingsContext2.close();
        mappingsContext2
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
    }

    @Test(description = "DIGIHUB-52130 OLT RI UI. Auto Commissioning MA5600 for DTAG user.")
    public void OltAutoCommissioningDTAGTest() {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_2000_76H1_MA5600);
        String endSz = oltDevice.getEndsz();
        log.info("OltAutoCommissioningDTAGTest EndSz = {}, LSZ = {}", endSz, oltDevice.getLsz());
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchNotDiscoveredByParameters(oltDevice);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();
        oltCommissioningPage.validateUrl();

        oltCommissioningPage.startOltCommissioning(oltDevice, TIMEOUT_FOR_OLT_COMMISSIONING);

        checkDeviceMA5600(oltDevice);
        checkUplink(endSz);
    }

    @Test(description = "DIGIHUB-52133 OLT RI UI. Auto Commissioning MA5800 for GFNW user.")
    public void OltAutoCommissioningGFNWTest() throws Exception {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_1100_76ZB_MA5800);
        String endSz = oltDevice.getEndsz();
        log.info("OltAutoCommissioningGFNWTest EndSz = {}, LSZ = {}", endSz, oltDevice.getLsz());
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        Thread.sleep(WAIT_TIME_FOR_RENDERING); // EndSz search can not be selected for the user GFNW if the page is not yet finished.
        oltSearchPage.validateUrl();
        oltSearchPage.searchNotDiscoveredByParameters(oltDevice);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();
        oltCommissioningPage.validateUrl();

        oltCommissioningPage.startOltCommissioning(oltDevice, TIMEOUT_FOR_OLT_COMMISSIONING);

        checkDeviceMA5800(oltDevice);
        checkUplink(endSz);
    }

    @Test(description = "DIGIHUB-104212 OLT RI UI. Auto Commissioning SDX 6320 16 for DTAG user.")
    public void OltAdtranAutoCommissioningDTAGTest() {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HG_SDX_6320_16);
        String endSz = oltDevice.getEndsz();
        log.info("OltAutoCommissioningDTAGTest EndSz = {}, LSZ = {}", endSz, oltDevice.getLsz());
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchNotDiscoveredByParameters(oltDevice);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();
        oltCommissioningPage.validateUrl();

        oltCommissioningPage.startOltCommissioning(oltDevice, TIMEOUT_FOR_OLT_COMMISSIONING);

        checkDeviceSDX3620(oltDevice, COMPOSITE_PARTY_ID_DTAG);
        checkUplink(endSz);
    }

    @Test(description = "DIGIHUB-104213 OLT RI UI. Auto Commissioning SDX 6320 for GFNW user.")
    public void OltAdtranAutoCommissioningGFNWTest() throws Exception {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z9_SDX_6320);
        String endSz = oltDeviceGFNW.getEndsz();
        log.info("OltAutoCommissioningGFNWTest EndSz = {}, LSZ = {}", endSz, oltDeviceGFNW.getLsz());
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        Thread.sleep(WAIT_TIME_FOR_RENDERING); // EndSz search can not be selected for the user GFNW if the page is not yet finished.
        oltSearchPage.validateUrl();
        oltSearchPage.searchNotDiscoveredByParameters(oltDeviceGFNW);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();
        oltCommissioningPage.validateUrl();

        oltCommissioningPage.startOltCommissioning(oltDeviceGFNW, TIMEOUT_FOR_OLT_COMMISSIONING);

        checkDeviceSDX3620(oltDevice, COMPOSITE_PARTY_ID_GFNW);
        checkUplink(endSz);
    }

    /**
     * check all port states from ethernet card
     */
    public void checkPortState(OltDevice device, OltDetailsPage detailsPage, int anzOfPorts) {

        for (int port = 0; port < anzOfPorts; ++port) {
            log.info("checkPortState() Port={}, Slot={}, PortLifeCycleState ={}", port, device.getOltSlot(), detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)));
            if (device.getOltPort().equals((Integer.toString(port)))) {
                Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), device.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());
            } else {
                Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
            }
        }
    }

    /**
     * check device MA5600 data from olt-resource-inventory and UI
     */
    private void checkDeviceMA5600(OltDevice oltDevice) {
        String endSz = oltDevice.getEndsz();

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(endSz).depthQuery(3).executeAs(checkStatus(HTTP_CODE_OK_200));

        Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
        Device device = deviceList.get(0);
        Assert.assertEquals(device.getEndSz(), endSz, "OLT EndSz missmatch");

        Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_MA5600, "EMS NBI name missmatch");
        Assert.assertEquals(device.getDeviceType(), DeviceType.OLT, "DeviceType missmatch");
        Assert.assertEquals(device.getRelatedParty().get(0).getId(), COMPOSITE_PARTY_ID_DTAG.toString(), "composite partyId DTAG missmatch");

        OltDetailsPage oltDetailsPage = new OltDetailsPage();
        oltDetailsPage.validateUrl();
        Assert.assertEquals(oltDetailsPage.getEndsz(), endSz);
        Assert.assertEquals(oltDetailsPage.getBezeichnung(), EMS_NBI_NAME_MA5600);
        Assert.assertEquals(oltDetailsPage.getKlsID(), KLS_ID_EXPECTED, "KlsId coming from PSL (Mock)");
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        checkPortState(oltDevice, oltDetailsPage, 2);
    }


    /**
     * check device MA5800 data from olt-resource-inventory and UI
     */
    private void checkDeviceMA5800(OltDevice oltDevice) {
        String endSz = oltDevice.getEndsz();

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(endSz).depthQuery(3).executeAs(checkStatus(HTTP_CODE_OK_200));

        Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
        Device device = deviceList.get(0);
        Assert.assertEquals(device.getEndSz(), endSz, "OLT EndSz missmatch");

        Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_MA5800, "EMS NBI name missmatch");
        Assert.assertEquals(device.getDeviceType(), DeviceType.OLT, "DeviceType missmatch");
        Assert.assertEquals(device.getRelatedParty().get(0).getId(), COMPOSITE_PARTY_ID_GFNW.toString(), "composite partyId GFNW missmatch");

        OltDetailsPage oltDetailsPage = new OltDetailsPage();
        oltDetailsPage.validateUrl();
        Assert.assertEquals(oltDetailsPage.getEndsz(), endSz);
        Assert.assertEquals(oltDetailsPage.getBezeichnung(), EMS_NBI_NAME_MA5800);
        Assert.assertEquals(oltDetailsPage.getKlsID(), KLS_ID_EXPECTED, "KlsId coming from PSL (Mock)");
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        checkPortState(oltDevice, oltDetailsPage, 4);
    }

    /**
     * check device SDX3620-16 data from olt-resource-inventory and UI
     */
    private void checkDeviceSDX3620(OltDevice oltDevice, Long compositePartyId) {

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(oltDevice.getEndsz()).depthQuery(3).executeAs(checkStatus(HTTP_CODE_OK_200));

        Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
        Device device = deviceList.get(0);
        Assert.assertEquals(device.getEndSz(), oltDevice.getEndsz(), "OLT EndSz missmatch");

        Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_SDX6320_16, "EMS NBI name missmatch");
        Assert.assertEquals(device.getDeviceType(), DeviceType.OLT, "DeviceType missmatch");
        Assert.assertEquals(device.getRelatedParty().get(0).getId(), compositePartyId.toString(), "composite partyId missmatch");

        OltDetailsPage oltDetailsPage = new OltDetailsPage();
        oltDetailsPage.validateUrl();
        Assert.assertEquals(oltDetailsPage.getEndsz(), oltDevice.getEndsz());
        Assert.assertEquals(oltDetailsPage.getBezeichnung(), EMS_NBI_NAME_SDX6320_16, "UI EMS NBI name missmatch");
        Assert.assertEquals(oltDetailsPage.getKlsID(), oltDeviceDTAG.getVst().getAddress().getKlsId(), "KlsId coming from PSL (dynamic Mock)");
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString(), "Device/ port lifecycle state missmatch");
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(null, oltDevice.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());
    }


    /**
     * check uplink and ancp-session data from olt-resource-inventory
     */
    private void checkUplink(String endSz) {
        List<Uplink> uplinkList = deviceResourceInventoryManagementClient.getClient().uplink().listUplink()
                .portsEquipmentBusinessRefEndSzQuery(endSz).executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(uplinkList.size(), 1L, "uplinkList.size missmatch");
        Assert.assertEquals(uplinkList.get(0).getState(), UplinkState.ACTIVE);

        List<AncpSession> ancpSessionList = deviceResourceInventoryManagementClient.getClient().ancpSession().listAncpSession()
                .accessNodeEquipmentBusinessRefEndSzQuery(endSz).executeAs(checkStatus(HTTP_CODE_OK_200));
        Assert.assertEquals(ancpSessionList.size(), 1L, "ancpSessionList.size missmatch");
        Assert.assertEquals(ancpSessionList.get(0).getConfigurationStatus(), "ACTIVE", "ANCP ConfigurationStatus missmatch");
    }
}


