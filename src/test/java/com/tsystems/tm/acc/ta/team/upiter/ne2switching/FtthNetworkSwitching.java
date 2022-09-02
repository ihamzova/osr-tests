package com.tsystems.tm.acc.ta.team.upiter.ne2switching;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.NetworkSwitchingUplinkElement;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.pages.osr.networkswitching.NetworkSwitchingPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkSwitchingRobot;
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
public class FtthNetworkSwitching extends GigabitTest {

    private NetworkSwitchingRobot networkSwitchingRobot;
    private AccessLineRiRobot accessLineRiRobot;
    private UpiterTestContext context = UpiterTestContext.get();
    private PortProvisioning endSz_49_30_179_76H2;
    private PortProvisioning endSz_49_30_179_76H3;
    private PortProvisioning endSz_49_911_1100_76H1;
    private WireMockMappingsContext mappingsContext;

    @BeforeClass
    public void init() throws InterruptedException {
        networkSwitchingRobot = new NetworkSwitchingRobot();
        accessLineRiRobot = new AccessLineRiRobot();
        networkSwitchingRobot.clearDatabase();

        networkSwitchingRobot.changeFeatureToogleEnableFttbNe2NetworkSwitchingState(false);
        Thread.sleep(5000);
        endSz_49_30_179_76H2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H2);
        endSz_49_30_179_76H3 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H3);
        endSz_49_911_1100_76H1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_911_1100_76H1);
    }

    @BeforeMethod
    void setup() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @TmsLink("DIGIHUB-147818")
    @Description("NE2 FTTH Network Switching Preparation, Huawei OLT")
    public void ne2FtthPreparationTest(){
        String state1 = "ACTIVE";
        String state2 = "PLANNED";
        String state3 = "PLANNED";

        List<String> expectedUplinksStates = Arrays.asList(state1, state2, state3);
        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "GetUplinks"))
                .addUplinksMock(endSz_49_30_179_76H3.getEndSz(), expectedUplinksStates, "Huawei")
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "findAndImportUplinks"))
                .addFindAndImportUplinksMock(endSz_49_30_179_76H3.getEndSz(), expectedUplinksStates, "Huawei")
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.switchToNe2Switching()
                .clickGetUplinks(endSz_49_30_179_76H3.getEndSz());

        networkSwitchingPage.getUplinks().should(CollectionCondition.size(3));
        List<NetworkSwitchingUplinkElement> uplinks = networkSwitchingPage.getUplinkInformation();
        assertTrue(accessLineRiRobot.compareLists(networkSwitchingPage.getUplinksStates(), expectedUplinksStates), "Uplinks states are incorrect");

        networkSwitchingPage.clickPrepareButton();
        assertTrue(networkSwitchingPage.getNotification().equals("Die Vorbereitung für den Zielport hat begonnen"), "Notification is incorrect");
        mappingsContext.deleteStubs();
        networkSwitchingPage.closeNotificationButton();
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"));
        assertTrue(networkSwitchingPage.getExecutionButton().isDisplayed(), "Execution button is not displayed after preparation phase");
        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after preparation phase");
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after preparation phase");

        networkSwitchingPage.clickDetailedInfoButton()
                .checkSourceAndTargetDevices(uplinks);
    }

    @Test(dependsOnMethods = "ne2FtthPreparationTest")
    @TmsLink("DIGIHUB-147819")
    @Description("NE2 FTTH Network Switching Execution, Huawei OLT")
    public void ne2FtthExecutionTest(){
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.searchPackagesByDevice(endSz_49_30_179_76H3);
        String packageId = networkSwitchingPage.getPackageIdOnSearchTab();

        networkSwitchingPage.startNe2Execution(packageId);
        networkSwitchingPage.waitUntilNeededStatus("IN_EXECUTION", packageId);
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed during execution phase");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during execution phase");

        networkSwitchingPage.waitUntilNeededStatus("EXECUTED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("EXECUTED"));
        assertTrue(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is not displayed after execution phase");
        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after execution phase");
    }

    @Test(dependsOnMethods = {"ne2FtthPreparationTest", "ne2FtthExecutionTest"})
    @TmsLink("DIGIHUB-147820")
    @Description("NE2 FTTH Network Switching Commit, Huawei OLT")
    public void ne2FtthCommitTest(){
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.searchPackagesByDevice(endSz_49_30_179_76H3);
        String packageId = networkSwitchingPage.getPackageIdOnSearchTab();
        networkSwitchingPage.startNe2Commit(packageId);

        networkSwitchingPage.waitUntilNeededStatus("FINISHED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("FINISHED"));
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after commit phase");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed after commit phase");
    }

    @Test
    @TmsLink("DIGIHUB-154011")
    @Description("NE2 FTTH Network Switching Rollback after Preparation, Huawei OLT")
    public void ne2FtthRollbackAfterPreparationTest(){
        String state1 = "ACTIVE";
        String state2 = "PLANNED";
        List<String> stateList = List.of(state1, state2);

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "findAndImportUplinks"))
                .addFindAndImportUplinksMock(endSz_49_30_179_76H2.getEndSz(),stateList, "Huawei")
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startNe2Preparation(endSz_49_30_179_76H2.getEndSz());
        mappingsContext.deleteStubs();
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"));

        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after preparation phase");
        networkSwitchingPage.clickRollbackButton();
        assertTrue(networkSwitchingPage.getNotification().equals("Der Rollback-Prozess hat begonnen"), "Notification is incorrect");
        networkSwitchingPage.closeNotificationButton();
        networkSwitchingPage.waitUntilNeededStatus("ROLLBACKED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("ROLLBACKED"));
        assertFalse(networkSwitchingPage.getExecutionButton().isDisplayed(), "Execution button is displayed after rollback");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed after rollback");
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after rollback");
    }

    @Test
    @TmsLink("DIGIHUB-154013")
    @Description("NE2 FTTH Network Switching Rollback after Execution, Huawei OLT")
    public void ne2FtthRollbackAfterExecutionTest(){
        String state1 = "ACTIVE";
        String state2 = "PLANNED";
        List<String> stateList = List.of(state1, state2);

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "findAndImportUplinks"))
                .addFindAndImportUplinksMock(endSz_49_30_179_76H2.getEndSz(), stateList, "Huawei")
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startNe2Preparation(endSz_49_30_179_76H2.getEndSz());
        mappingsContext.deleteStubs();
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"));

        networkSwitchingPage.clickExecutionButton()
                        .waitUntilNeededStatus("EXECUTED", packageId);

        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after preparation phase");
        networkSwitchingPage.clickRollbackButton()
                .waitUntilNeededStatus("IN_ROLLBACK", packageId);
        assertTrue(networkSwitchingPage.getNotification().equals("Der Rollback-Prozess hat begonnen"), "Notification is incorrect");
        networkSwitchingPage.waitUntilNeededStatus("ROLLBACKED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("ROLLBACKED"));
        assertFalse(networkSwitchingPage.getExecutionButton().isDisplayed(), "Execution button is displayed after rollback");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed after rollback");
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after rollback");
    }

    @Test
    @TmsLink("DIGIHUB-147827")
    @Description("NE2 FTTH Network Switching, DPU found, process runs into an error, Huawei OLT")
    public void ne2FtthPreparationDpuFoundTest() {
        String state1 = "ACTIVE";
        String state2 = "PLANNED";
        List<String> stateList = List.of(state1, state2);

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "findAndImportUplinks"))
                .addFindAndImportUplinksMock(endSz_49_911_1100_76H1.getEndSz(), stateList,  "Huawei")
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.switchToNe2Switching()
                .clickGetUplinks(endSz_49_911_1100_76H1.getEndSz())
                .clickPrepareButton();
        mappingsContext.deleteStubs();
        assertTrue(networkSwitchingPage.getNotification().equals("Der Prozess kann nicht gestartet werden, weil eine DPU auf dem Gerät gefunden wurde"));
    }
}
