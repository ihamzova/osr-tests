package com.tsystems.tm.acc.ta.team.mercury.commissioning.auto;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.ANCPSession;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.v4_10_0.client.model.UplinkDTO;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;

@Slf4j
@ServiceLog({ ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS })
@Epic("OS&R")
@Feature("Description olt auto-commissioning incl. LC-Commissioning Testcase on Mercury Team-environment")
@TmsLink("DIGIHUB-52132") // This is the Jira id of TestSet
public class OltAutoCommissioning extends GigabitTest {

    private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 2 * 60_000;
    private static final int WAIT_TIME_FOR_RENDERING = 2_000;

    private static final String KLS_ID_EXPECTED = "17056514";

    private OltResourceInventoryClient oltResourceInventoryClient;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
    }

    @Test(description = "DIGIHUB-52130 OLT RI UI. Auto Commissioning MA5600 for DTAG user.")
    public void OltAutoCommissioningDTAGTest() throws Exception {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_2000_76H1_MA5600);
        String endSz = oltDevice.getEndsz();
        log.info("OltAutoCommissioningDTAGTest EndSz = {}, LSZ = {}", endSz, oltDevice.getLsz());
        deleteDeviceInResourceInventory(endSz);

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
        deleteDeviceInResourceInventory(endSz);

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

    /**
     * check all port states from ethernet card
     *
     * @param device
     * @param detailsPage
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

        List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
                .endszQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 1L);
        Device device = deviceList.get(0);
        Assert.assertEquals(device.getEndSz(), endSz);

        Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_MA5600);
        Assert.assertEquals(device.getTkz1(), "02351082");
        Assert.assertEquals(device.getTkz2(), "02353310");
        Assert.assertEquals(device.getType(), Device.TypeEnum.OLT);
        Assert.assertEquals(device.getCompositePartyId(), COMPOSITE_PARTY_ID_DTAG);

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

        List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
                .endszQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 1L);
        Device device = deviceList.get(0);
        Assert.assertEquals(device.getEndSz(), endSz);

        Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_MA5800);
        Assert.assertEquals(device.getTkz1(), "2352QCR");
        Assert.assertEquals(device.getTkz2(), "02353310");
        Assert.assertEquals(device.getType(), Device.TypeEnum.OLT);
        Assert.assertEquals(device.getCompositePartyId(), COMPOSITE_PARTY_ID_GFNW);

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
     * check uplink and ancp-session data from olt-resource-inventory
     */
    private void checkUplink(String endSz) {
        List<UplinkDTO> uplinkDTOList = oltResourceInventoryClient.getClient().ethernetLinkInternalController().findEthernetLinksByEndsz()
                .oltEndSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(uplinkDTOList.size(), 1L);
        Assert.assertEquals(uplinkDTOList.get(0).getAncpSessions().size(), 1L);
        Assert.assertEquals(uplinkDTOList.get(0).getAncpSessions().get(0).getSessionStatus(), ANCPSession.SessionStatusEnum.ACTIVE);
    }

    /**
     * clears a device in olt-resource-inventory database.
     * only one device will be deleted.
     *
     * @param endSz
     */
    private void deleteDeviceInResourceInventory(String endSz) {
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}


