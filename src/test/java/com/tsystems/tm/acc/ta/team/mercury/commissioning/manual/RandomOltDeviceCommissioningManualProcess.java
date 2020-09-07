package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.ANCPSession;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.UplinkDTO;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

@Slf4j
@ServiceLog("olt-resource-inventory")
@ServiceLog("ea-ext-route")
@ServiceLog("olt-discovery")
@ServiceLog("ancp-configuration")
public class RandomOltDeviceCommissioningManualProcess extends BaseTest {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private OltResourceInventoryClient oltResourceInventoryClient;
    private OltDevice oltDevice;

    private WireMockMappingsContext mappingsContext;

    @BeforeMethod
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();

        OsrTestContext context = OsrTestContext.get();
        oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HC_MA5600);
        Random rnd = new Random();
        char c = (char) ('B' + rnd.nextInt(25));
        oltDevice.setFsz("76H" + c);
        oltDevice.setVpsz("49/8571/" + rnd.nextInt(1000));
        //oltDevice.setFsz("76HC");

        mappingsContext = new OsrWireMockMappingsContextBuilder(WireMockFactory.get())
                .addSealMock(oltDevice)
                .addPslMock(oltDevice)
                .build();

        mappingsContext.publish();

        String endSz = oltDevice.getVpsz() + "/" + oltDevice.getFsz();
        clearResourceInventoryDataBase(endSz);
    }

    @AfterMethod
    public void cleanUp() {
        mappingsContext.deleteAll();

        String endSz = oltDevice.getVpsz() + "/" + oltDevice.getFsz();
        log.info("+++ cleanUp delete device endsz={}", endSz);
        clearResourceInventoryDataBase(endSz);
    }

    @Test(description = "DIGIHUB-53694 Manual commissioning for MA5800 with DTAG user on team environment")
    @TmsLink("DIGIHUB-53694") // Jira Id for this test in Xray
    @Description("Perform manual commissioning for not discovered MA5800 device as DTAG user")
    public void SearchAndDiscoverOlt() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        String endSz = oltDevice.getVpsz() + "/" + oltDevice.getFsz();
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
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDetailsPage.startUplinkConfiguration();
        oltDetailsPage.inputUplinkParameters(oltDevice);
        oltDetailsPage.saveUplinkConfiguration();
        oltDetailsPage.modifyUplinkConfiguration();

        oltDetailsPage.configureAncpSessionStart();
        oltDetailsPage.updateAncpSessionStatus();
        oltDetailsPage.checkAncpSessionStatus();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        checkPortState(oltDevice, oltDetailsPage);

        checkDeviceMA5800(endSz);
        checkUplink(endSz);

        //Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // check uplink port life cycle state
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        Thread.sleep(1000); // ensure that the resource inventory database is updated
        checkUplinkDeleted(endSz);

    }

    /**
     * check all port states from ethernet card
     *
     * @param device
     * @param detailsPage
     */
    public void checkPortState(OltDevice device, OltDetailsPage detailsPage) {

        for (int port = 0; port <= 1; ++port) {
            log.info("checkPortState() Port={}, Slot={}, PortLifeCycleState ={}", port, device.getOltSlot(), detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)));
            if (device.getOltPort().equals((Integer.toString(port)))) {
                Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), device.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());
            } else {
                Assert.assertEquals(detailsPage.getPortLifeCycleState(device.getOltSlot(), Integer.toString(port)), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
            }
        }
    }

    /**
     * check device MA5800 data from olt-resource-inventory and UI
     */
    private void checkDeviceMA5800(String endSz) {
        List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
                .endszQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 1L);
        Assert.assertEquals(deviceList.get(0).getType(), Device.TypeEnum.OLT);
        Assert.assertEquals(deviceList.get(0).getEndSz(), endSz);
    }

    /**
     * check uplink and ancp-session data from olt-ressource-inventory
     */
    private void checkUplink(String endSz) {
        List<UplinkDTO> uplinkDTOList = oltResourceInventoryClient.getClient().ethernetLinkInternalController().findEthernetLinksByEndsz()
                .oltEndSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(uplinkDTOList.size(), 1L);
        Assert.assertEquals(uplinkDTOList.get(0).getAncpSessions().size(), 1L);
        Assert.assertEquals(uplinkDTOList.get(0).getAncpSessions().get(0).getSessionStatus(), ANCPSession.SessionStatusEnum.ACTIVE);
    }

    /**
     * check uplink is not exist in olt-resource-inventory
     */
    private void checkUplinkDeleted(String endSz) {
        List<UplinkDTO> uplinkDTOList = oltResourceInventoryClient.getClient().ethernetLinkInternalController().findEthernetLinksByEndsz()
                .oltEndSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertTrue(uplinkDTOList.isEmpty());
    }

    /**
     * clears complete olt-resource-invemtory database
     */
    private void clearResourceInventoryDataBase(String endSz) {
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

}


