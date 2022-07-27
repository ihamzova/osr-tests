package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.DeleteDevicePage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.OltDeCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_GFNW;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;


@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS})
public class OltDeviceCommissioningDecommissioningMA5800_GFNW extends GigabitTest {

    private static final int WAIT_TIME_FOR_RENDERING = 2_000;

    private static final Integer WAIT_TIME_FOR_DEVICE_DELETION = 2_000;
    private static final Integer WAIT_TIME_FOR_CARD_DELETION = 1_000;

    private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private final OltDeCommissioningRobot oltDeCommissioningRobot = new OltDeCommissioningRobot();

    OsrTestContext context = OsrTestContext.get();
    private final OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_1234_76ZC_MA5800);

    @BeforeClass
    public void init()
    {

    }

    @Test(description = "DIGIHUB-96865 Manual commissioning and decommissioning for not discovered MA5800 device as GFNW user")
    @TmsLink("DIGIHUB-96865") // Jira Id for this test in Xray
    @Description("Perform manual commissioning and decommissioning for not discovered MA5800 device as GFNW user on team environment")
    public void SearchAndDiscoverOlt() throws InterruptedException {

        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        String endSz = oltDevice.getEndsz();
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        Thread.sleep(WAIT_TIME_FOR_RENDERING); // During the pipeline test no EndSz Search can be selected for the user GFNW if the page is not yet finished.

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
        oltCommissioningRobot.checkUIEthernetPortStates(oltDevice);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDevice, COMPOSITE_PARTY_ID_GFNW);
        oltCommissioningRobot.checkUplink(oltDevice);

        Thread.sleep(1000); // prevent Init Deconfiguration of ANCP session runs in error
        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        // check uplink port life cycle state
        oltDetailsPage.openPortView(oltDevice.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(oltDevice.getOltSlot(), oltDevice.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION); // ensure that the resource inventory database is updated
        oltDeCommissioningRobot.checkUplinkIsDeleted(endSz);

        //DIGIHUB-55036 device and card deletion
        oltDetailsPage.deleteGponCard();
        Thread.sleep(WAIT_TIME_FOR_CARD_DELETION);
        oltDeCommissioningRobot.checkCardIsNotDeleted(endSz, "1");
        oltDetailsPage.deleteDevice();
        DeleteDevicePage deleteDevicePage = new DeleteDevicePage();
        deleteDevicePage.validateUrl();

        deleteDevicePage.DeleteOltDevice();
        Thread.sleep(WAIT_TIME_FOR_DEVICE_DELETION);
        oltDeCommissioningRobot.checkDeviceIsNotDeleted(endSz);
    }
}