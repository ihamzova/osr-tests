package com.tsystems.tm.acc.ta.team.upiter.ne2switching;

import com.codeborne.selenide.CollectionCondition;
import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.dpudemand.DpuDemandCase;
import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDemand;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.pages.osr.networkswitching.NetworkSwitchingPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkSwitchingRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgFttbAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachStubsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.savePublishedToDefaultDir;
import static org.testng.Assert.*;

@ServiceLog({
        NETWORK_SWITCHING_CONGIF_MGT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})

@Epic("NE2 Network Switching")
public class FttbNetworkSwitching extends GigabitTest {

    private NetworkSwitchingRobot networkSwitchingRobot;
    private WgFttbAccessProvisioningRobot wgFttbAccessProvisioningRobot;
    private AccessLineRiRobot accessLineRiRobot;
    private UpiterTestContext context = UpiterTestContext.get();
    private PortProvisioning endSz_49_911_1100_76H1;
    private PortProvisioning endSz_49_911_1100_76H3;
    private DpuDevice endSz_49_812_179_71G2;
    private DpuDemand dpuDemand;
    private WireMockMappingsContext mappingsContext;
    private int numberOfAccessLinesForProvisioning;

    @BeforeClass
    public void init() throws InterruptedException {
        networkSwitchingRobot = new NetworkSwitchingRobot();
        wgFttbAccessProvisioningRobot = new WgFttbAccessProvisioningRobot();
        accessLineRiRobot = new AccessLineRiRobot();
        networkSwitchingRobot.clearDatabase();
        accessLineRiRobot.clearDatabase();

        networkSwitchingRobot.changeFeatureToogleEnableFttbNe2NetworkSwitchingState(true);
        endSz_49_911_1100_76H1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningCoax);
        endSz_49_911_1100_76H3 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.adtranDeviceForFttbProvisioningTwistedPair);
        endSz_49_812_179_71G2 = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningOnAdtranTwistedPair);
        accessLineRiRobot.fillDatabaseForDpuPreprovisioningV2(1, 1, endSz_49_812_179_71G2, endSz_49_911_1100_76H3);

        dpuDemand = context.getData().getDpuDemandDataProvider().get(DpuDemandCase.dpuDemand);
        numberOfAccessLinesForProvisioning = Integer.parseInt(dpuDemand.getNumberOfNeededDpuPorts());
        if (numberOfAccessLinesForProvisioning > 16) {
            numberOfAccessLinesForProvisioning = 16;
        }
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(endSz_49_812_179_71G2.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(endSz_49_911_1100_76H3, numberOfAccessLinesForProvisioning);
    }

    @BeforeMethod
    void setup() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @TmsLink("DIGIHUB-152728")
    @Description("NE2 FTTB Network Switching Preparation, Adtran OLT")
    public void ne2FttbPreparationTest() {
        String state1 = "ACTIVE";
        String state2 = "PLANNED";
        List<String> expectedUplinksStates = Arrays.asList(state1, state2);

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "GetUplinks"))
                .addUplinksMock(endSz_49_911_1100_76H3.getEndSz(), expectedUplinksStates, "Adtran")
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "findAndImportUplinks"))
                .addFindAndImportUplinksMock(endSz_49_911_1100_76H3.getEndSz(), expectedUplinksStates, "Adtran")
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.switchToNe2Switching()
                .clickGetUplinks(endSz_49_911_1100_76H3.getEndSz());
        networkSwitchingPage.getUplinks().should(CollectionCondition.size(2));
        assertTrue(accessLineRiRobot.compareLists(networkSwitchingPage.getUplinksStates(), expectedUplinksStates), "Uplinks states are incorrect");

        networkSwitchingPage.clickPrepareButton();
        mappingsContext.deleteStubs();
        assertTrue(networkSwitchingPage.getNotification().equals("Die Vorbereitung f??r den Zielport hat begonnen"), "Notification is incorrect");
        networkSwitchingPage.closeNotificationButton();
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"));
        assertTrue(networkSwitchingPage.getExecutionButton().isDisplayed(), "Execution button is not displayed after preparation phase");
        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after preparation phase");
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after preparation phase");
    }

    @Test(dependsOnMethods = "ne2FttbPreparationTest")
    @TmsLink("DIGIHUB-158285")
    @Description("NE2 FTTB Network Switching Execution, Adtran OLT")
    public void ne2FttbExecutionTest(){
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.switchToNe2Switching()
                .clickGetUplinks(endSz_49_911_1100_76H3.getEndSz()).clickPrepareButton();
        assertTrue(networkSwitchingPage.getNotification().equals("Die Vorbereitung ist bereits abgeschlossen"), "Notification is incorrect");
        networkSwitchingPage.closeNotificationButton();
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();

        networkSwitchingPage.startNe2Execution(packageId);
        networkSwitchingPage.waitUntilNeededStatus("IN_EXECUTION", packageId);
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed during execution phase");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during execution phase");

        networkSwitchingPage.waitUntilNeededStatus("EXECUTED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("EXECUTED"));
        assertTrue(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is not displayed after execution phase");
        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after execution phase");
    }

    @Test(dependsOnMethods = {"ne2FttbPreparationTest", "ne2FttbExecutionTest"})
    @TmsLink("DIGIHUB-163877")
    @Description("NE2 FTTB Network Switching Commit, Adtran OLT")
    public void ne2FttbCommitTest(){
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.switchToNe2Switching()
                .clickGetUplinks(endSz_49_911_1100_76H3.getEndSz()).clickPrepareButton();
        assertTrue(networkSwitchingPage.getNotification().equals("Der Vorbereitungsprozess f??r das angegebene Paket kann nicht gestartet werden"), "Notification is incorrect");
        networkSwitchingPage.closeNotificationButton();
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.startNe2Commit(packageId);
        mappingsContext.deleteStubs();
        networkSwitchingPage.waitUntilNeededStatus("FINISHED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("FINISHED"));
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after commit phase");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed after commit phase");
    }

    @Test
    @TmsLink("DIGIHUB-147825")
    @Description("NE2 FTTB Network Switching Preparation, Uplink statuses are inconsistent for switching")
    public void ne2FttbWrongUplinkStatusesTest() {
        String state1 = "ACTIVE";
        String state2 = "ACTIVE";
        String state3 = "PLANNED";

        List<String> expectedUplinksStates = Arrays.asList(state1, state2, state3);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "GetUplinks"))
                .addUplinksMock(endSz_49_911_1100_76H1.getEndSz(), expectedUplinksStates, "Huawei")
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.switchToNe2Switching()
                .clickGetUplinks(endSz_49_911_1100_76H1.getEndSz());
        networkSwitchingPage.getUplinks().should(CollectionCondition.size(3));
        mappingsContext.deleteStubs();
        assertTrue(accessLineRiRobot.compareLists(networkSwitchingPage.getUplinksStates(), expectedUplinksStates), "Uplinks states are incorrect");
        assertFalse(networkSwitchingPage.getPreparationButton().isEnabled());
        assertTrue(networkSwitchingPage.getUplinksErrorMessage().isDisplayed());
        assertEquals(networkSwitchingPage.getUplinksErrorMessage().getText(),
                "Es soll 1 AKTIVER und mindestens 1 GEPLANTER Uplink vorhanden sein, um den Vorbereitungsprozess zu starten");
    }
}
