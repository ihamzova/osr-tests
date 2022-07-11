package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.DeviceType;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.Uplink;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_GFNW;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_MA5600;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS})
public class NewOltDeviceCommissioningManualProcessGFNW extends GigabitTest {

    private static final int WAIT_TIME_FOR_RENDERING = 5_000;

    private static final String KLS_ID_EXPECTED = "17056514";

    private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private final DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient();

    @BeforeClass
    public void init() {
        oltCommissioningRobot.enableFeatureToogleUiUplinkImport();
    }

    @Test(description = "DIGIHUB-53713 Manual commissioning for MA5600 with GFNW user on team environment")
    @TmsLink("DIGIHUB-53713") // Jira Id for this test in Xray
    @Description("Perform manual commissioning for not discovered MA5600 device as GFNW user")
    public void SearchAndDiscoverOlt() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());


        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z7_MA5600);
        String endSz = oltDevice.getEndsz();
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        Thread.sleep(WAIT_TIME_FOR_RENDERING); // During the pipeline test no EndSz Search can be selected for the user GFNW if the page is not yet finished.
        oltSearchPage.validateUrl();

        oltSearchPage.searchNotDiscoveredByParameters(oltDevice);
        oltSearchPage.pressManualCommissionigButton();
        OltDiscoveryPage oltDiscoveryPage = new OltDiscoveryPage();
        oltDiscoveryPage.makeOltDiscovery();
        oltDiscoveryPage.saveDiscoveryResults();
        oltDiscoveryPage.openOltSearchPage();

        Thread.sleep(WAIT_TIME_FOR_RENDERING); // During the pipeline test no EndSz Search can be selected for the user GFNW if the page is not yet finished.
        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(oltDevice);
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDetailsPage.startUplinkConfiguration();
        oltDetailsPage.saveUplinkConfiguration();

        oltDetailsPage.configureAncpSessionStart();
        oltCommissioningRobot.ancpSessionStateTest();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        checkPortState(oltDevice, oltDetailsPage);

        checkDeviceMA5600(endSz);
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
    }

    /**
     * check all port states from ethernet card
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
     * check device MA5600 data from olt-ressource-inventory
     */
    private void checkDeviceMA5600(String endSz) {

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(endSz).depthQuery(3).executeAs(checkStatus(HTTP_CODE_OK_200));

        Assert.assertEquals(deviceList.size(), 1L, "OLT deviceList.size mismatch");
        Device device = deviceList.get(0);
        Assert.assertEquals(device.getEndSz(), endSz, "OLT EndSz missmatch");

        Assert.assertEquals(device.getEmsNbiName(), EMS_NBI_NAME_MA5600, "EMS NBI name missmatch");
        Assert.assertEquals(device.getDeviceType(), DeviceType.OLT, "DeviceType missmatch");
        Assert.assertEquals(device.getRelatedParty().get(0).getId(), COMPOSITE_PARTY_ID_GFNW.toString(), "composite partyId GFNW missmatch");

        OltDetailsPage oltDetailsPage = new OltDetailsPage();
        oltDetailsPage.validateUrl();
        Assert.assertEquals(oltDetailsPage.getEndsz(), endSz);
        Assert.assertEquals(oltDetailsPage.getBezeichnung(), EMS_NBI_NAME_MA5600);
        Assert.assertEquals(oltDetailsPage.getKlsID(), KLS_ID_EXPECTED, "KlsId coming from PSL (Mock)");
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


