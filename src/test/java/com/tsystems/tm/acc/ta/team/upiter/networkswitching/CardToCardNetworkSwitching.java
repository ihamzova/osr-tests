package com.tsystems.tm.acc.ta.team.upiter.networkswitching;

import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.pages.osr.networkswitching.NetworkSwitchingPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkSwitchingRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.AccessLineDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.*;

@ServiceLog({
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        NETWORK_SWITCHING_CONGIF_MGT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})

@Epic("Network Switching")
public class CardToCardNetworkSwitching extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot;
    private NetworkSwitchingRobot networkSwitchingRobot;
    private UpiterTestContext context = UpiterTestContext.get();
    private PortProvisioning endSz_49_30_179_76H2_3_0;
    private PortProvisioning endSz_49_30_179_76H2_3_1;
    private PortProvisioning endSz_49_911_1100_76H2_1_0;
    private PortProvisioning endSz_49_911_1100_76H2_1_1;

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot = new AccessLineRiRobot();
        networkSwitchingRobot = new NetworkSwitchingRobot();
        endSz_49_30_179_76H2_3_0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H2_3_0);
        endSz_49_30_179_76H2_3_1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H2_3_1);
        endSz_49_911_1100_76H2_1_0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_911_1100_76H2_1_0);
        endSz_49_911_1100_76H2_1_1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_911_1100_76H2_1_1);
    }

    @BeforeMethod
    void setup() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @TmsLink("DIGIHUB-128414")
    @Description("Network Switching Preparation, Card to Card Switching")
    public void networkSwitchingCardtoCardPreparationTest() throws Exception {

        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H2_3_0, endSz_49_911_1100_76H2_1_0);

        List<AccessLineDto> sourceAccessLinesBeforePreparationPort1 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_0);
        List<AccessLineDto> sourceAccessLinesBeforePreparationPort2 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_1);

        List<Integer> sourceAnpTagsBeforePreparationPort1 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparationPort1);
        List<Integer> sourceAnpTagsBeforePreparationPort2 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparationPort2);
        List<Integer> sourceOnuIdsBeforePreparationPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_0, sourceAccessLinesBeforePreparationPort1);
        List<Integer> sourceOnuIdsBeforePreparationPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_1, sourceAccessLinesBeforePreparationPort2);

        int numberOfAccessLinesOnTargetPort1BeforePreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_0).size();
        int numberOfAccessLinesOnTargetPort2BeforePreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_1).size();

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startCardPreparation(endSz_49_30_179_76H2_3_0, endSz_49_30_179_76H2_3_1, endSz_49_911_1100_76H2_1_0, endSz_49_911_1100_76H2_1_1);
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        assertTrue(networkSwitchingPage.getCommitButton().isEnabled());
        assertTrue(networkSwitchingPage.getRollbackButton().isEnabled());

        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);

        assert (networkSwitchingPage.getPackageStatus().contains("PREPARED"));

        List<AccessLineDto> sourceAccessLinesAfterPreparationPort1 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_0);
        List<AccessLineDto> sourceAccessLinesAfterPreparationPort2 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_1);
        List<Integer> sourceAnpTagsAfterPreparationPort1 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterPreparationPort1);
        List<Integer> sourceAnpTagsAfterPreparationPort2 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterPreparationPort2);

        List<Integer> sourceOnuIdsAfterPreparationPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_0, sourceAccessLinesAfterPreparationPort1);
        List<Integer> sourceOnuIdsAfterPreparationPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_1, sourceAccessLinesAfterPreparationPort2);
        List<Integer> targetAnpTagsAfterPreparationPort1 = accessLineRiRobot.getAllocatedAnpTagsFromNsProfile(sourceAccessLinesAfterPreparationPort1);
        List<Integer> targetAnpTagsAfterPreparationPort2 = accessLineRiRobot.getAllocatedAnpTagsFromNsProfile(sourceAccessLinesAfterPreparationPort2);
        List<Integer> targetOnuIdsAfterPreparationPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_0, sourceAccessLinesAfterPreparationPort1);
        List<Integer> targetOnuIdsAfterPreparationPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_1, sourceAccessLinesAfterPreparationPort2);
        int numberOfAccessLinesOnTargetPort1AfterPreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_0).size();
        int numberOfAccessLinesOnTargetPort2AfterPreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_1).size();

        assertEquals(numberOfAccessLinesOnTargetPort1AfterPreparation,
                numberOfAccessLinesOnTargetPort1BeforePreparation - sourceAccessLinesBeforePreparationPort1.size());
        assertEquals(numberOfAccessLinesOnTargetPort2AfterPreparation,
                numberOfAccessLinesOnTargetPort2BeforePreparation - sourceAccessLinesBeforePreparationPort2.size());
        assertTrue(targetAnpTagsAfterPreparationPort1.size() == sourceAccessLinesAfterPreparationPort1.size());
        assertTrue(targetAnpTagsAfterPreparationPort2.size() == sourceAccessLinesAfterPreparationPort2.size());

        assertTrue(targetOnuIdsAfterPreparationPort1.size() == sourceAccessLinesAfterPreparationPort1.size());
        assertTrue(targetOnuIdsAfterPreparationPort2.size() == sourceAccessLinesAfterPreparationPort2.size());
        accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparationPort1, sourceAnpTagsAfterPreparationPort1);
        accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparationPort2, sourceAnpTagsAfterPreparationPort2);
        accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparationPort1, sourceOnuIdsAfterPreparationPort1);
        accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparationPort2, sourceOnuIdsAfterPreparationPort2);
    }

    @Test(dependsOnMethods = "networkSwitchingCardtoCardPreparationTest")
    @TmsLink("DIGIHUB-128425")
    @Description("Network Switching Commit")
    public void networkSwitchingCardToCardCommitTest() throws Exception {
        List<AccessLineDto> sourceAccessLinesBeforeCommitPort1 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_0);
        List<AccessLineDto> sourceAccessLinesBeforeCommitPort2 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_1);
        List<Integer> targetAnpTagsBeforeCommitPort1 = accessLineRiRobot.getAllocatedAnpTagsFromNsProfile(sourceAccessLinesBeforeCommitPort1);
        List<Integer> targetAnpTagsBeforeCommitPort2 = accessLineRiRobot.getAllocatedAnpTagsFromNsProfile(sourceAccessLinesBeforeCommitPort2);
        List<Integer> targetOnuIdsBeforeCommitPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_0, sourceAccessLinesBeforeCommitPort1);
        List<Integer> targetOnuIdsBeforeCommitPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_1, sourceAccessLinesBeforeCommitPort2);

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.clickSearchTab()
                .searchPackagesByDevice(endSz_49_911_1100_76H2_1_0);
        String packageId = networkSwitchingPage.getPackageIdOnSearchTab();
        networkSwitchingPage.startCommitWithDeprovisioning(packageId);
        assertTrue(networkSwitchingPage.getCommitButton().isEnabled());
        assertFalse(networkSwitchingPage.getRollbackButton().isEnabled());
        networkSwitchingPage.waitUntilNeededStatus("FINISHED", packageId);
        assert (networkSwitchingPage.getPackageStatus().contains("FINISHED"));

        List<AccessLineDto> sourceAccessLinesAfterCommitPort1 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_911_1100_76H2_1_0);
        List<AccessLineDto> sourceAccessLinesAfterCommitPort2 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_911_1100_76H2_1_1);
        List<Integer> targetAnpTagsAfterCommitPort1 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterCommitPort1);
        List<Integer> targetAnpTagsAfterCommitPort2 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterCommitPort2);
        List<Integer> sourceOnuIdsAfterCommitPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_0, sourceAccessLinesAfterCommitPort1);
        List<Integer> sourceOnuIdsAfterCommitPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_1, sourceAccessLinesAfterCommitPort2);
        List<Integer> targetOnuIdsAfterCommitPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_0, sourceAccessLinesBeforeCommitPort1);
        List<Integer> targetOnuIdsAfterCommitPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_1, sourceAccessLinesBeforeCommitPort2);

        accessLineRiRobot.compareLists(targetAnpTagsAfterCommitPort1, targetAnpTagsBeforeCommitPort1);
        accessLineRiRobot.compareLists(targetAnpTagsAfterCommitPort2, targetAnpTagsBeforeCommitPort2);
        accessLineRiRobot.compareLists(targetOnuIdsAfterCommitPort1, targetOnuIdsBeforeCommitPort1);
        accessLineRiRobot.compareLists(targetOnuIdsAfterCommitPort2, targetOnuIdsBeforeCommitPort2);


        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterCommitPort1).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null));
        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterCommitPort2).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null));
        assertEquals(sourceOnuIdsAfterCommitPort1.size(), 0);
        assertEquals(sourceOnuIdsAfterCommitPort2.size(), 0);
        assertEquals(accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_0).size(), 32);
        assertEquals(accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_1).size(), 32);
        assertEquals(accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H2_3_0).size(), 0);
        assertEquals(accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H2_3_1).size(), 0);
        accessLineRiRobot.checkFtthPortParameters(endSz_49_30_179_76H2_3_0);
        accessLineRiRobot.checkFtthPortParameters(endSz_49_30_179_76H2_3_1);
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(endSz_49_30_179_76H2_3_0, 0, 1);
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(endSz_49_30_179_76H2_3_1, 0, 1);

    }

    @Test
    @TmsLink("DIGIHUB-128422")
    @Description("Network Switching Rollback")
    public void networkSwitchingCardToCardRollbackTest() throws Exception {
        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H2_3_0, endSz_49_911_1100_76H2_1_0);

        List<AccessLineDto> sourceAccessLinesBeforePreparationPort1 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_0);
        List<AccessLineDto> sourceAccessLinesBeforePreparationPort2 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_1);
        List<AccessLineDto> targetAccessLinesBeforePreparationPort1 = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_0);
        List<AccessLineDto> targetAccessLinesBeforePreparationPort2 = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_1);
        List<Integer> targetAnpTagsBeforePreparationPort1 = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesBeforePreparationPort1);
        List<Integer> targetAnpTagsBeforePreparationPort2 = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesBeforePreparationPort2);
        List<Integer> targetOnuIdsBeforePreparationPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_0, targetAccessLinesBeforePreparationPort1);
        List<Integer> targetOnuIdsBeforePreparationPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_1, targetAccessLinesBeforePreparationPort2);
        List<Integer> sourceAnpTagsBeforePreparationPort1 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparationPort1);
        List<Integer> sourceAnpTagsBeforePreparationPort2 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparationPort2);
        List<Integer> sourceOnuIdsBeforePreparationPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_0, sourceAccessLinesBeforePreparationPort1);
        List<Integer> sourceOnuIdsBeforePreparationPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_1, sourceAccessLinesBeforePreparationPort2);


        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startCardPreparation(endSz_49_30_179_76H2_3_0, endSz_49_30_179_76H2_3_1, endSz_49_911_1100_76H2_1_0, endSz_49_911_1100_76H2_1_1);
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        assertTrue(networkSwitchingPage.getCommitButton().isEnabled());
        assertTrue(networkSwitchingPage.getRollbackButton().isEnabled());

        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);

        assert (networkSwitchingPage.getPackageStatus().contains("PREPARED"));

        networkSwitchingPage.startRollback(packageId);
        networkSwitchingPage.waitUntilNeededStatus("ROLLBACKED", packageId);
        assert (networkSwitchingPage.getPackageStatus().contains("ROLLBACKED"));

        List<AccessLineDto> sourceAccessLinesAfterRollbackPort1 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_0);
        List<AccessLineDto> sourceAccessLinesAfterRollbackPort2 = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H2_3_1);
        List<AccessLineDto> targetAccessLinesAfterRollbackPort1 = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_0);
        List<AccessLineDto> targetAccessLinesAfterRollbackPort2 = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H2_1_0);
        List<Integer> sourceAnpTagsAfterRollbackPort1 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterRollbackPort1);
        List<Integer> sourceAnpTagsAfterRollbackPort2 = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterRollbackPort2);

        List<Integer> sourceOnuIdsAfterRollbackPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_0, sourceAccessLinesAfterRollbackPort1);
        List<Integer> sourceOnuIdsAfterRollbackPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H2_3_1, sourceAccessLinesAfterRollbackPort2);
        List<Integer> targetAnpTagsAfterRollbackPort1 = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesAfterRollbackPort1);
        List<Integer> targetAnpTagsAfterRollbackPort2 = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesAfterRollbackPort2);
        List<Integer> targetOnuIdsAfterRollbackPort1 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_0, targetAccessLinesAfterRollbackPort1);
        List<Integer> targetOnuIdsAfterRollbackPort2 = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H2_1_1, targetAccessLinesAfterRollbackPort2);
        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterRollbackPort1).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null));
        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterRollbackPort2).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null));


        assertTrue(targetAccessLinesBeforePreparationPort1.size() == targetAccessLinesAfterRollbackPort1.size()
                && targetAccessLinesBeforePreparationPort2.size() == targetAccessLinesAfterRollbackPort2.size() &&
                sourceAccessLinesBeforePreparationPort1.size() == sourceAccessLinesAfterRollbackPort1.size() &&
                sourceAccessLinesBeforePreparationPort2.size() == sourceAccessLinesAfterRollbackPort2.size());
        accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparationPort1, sourceAnpTagsAfterRollbackPort1);
        accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparationPort2, sourceAnpTagsAfterRollbackPort2);
        accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparationPort1, sourceOnuIdsAfterRollbackPort1);
        accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparationPort2, sourceOnuIdsAfterRollbackPort2);
        accessLineRiRobot.compareLists(targetAnpTagsBeforePreparationPort1, targetAnpTagsAfterRollbackPort1);
        accessLineRiRobot.compareLists(targetAnpTagsBeforePreparationPort2, targetAnpTagsAfterRollbackPort2);
        accessLineRiRobot.compareLists(targetOnuIdsBeforePreparationPort1, targetOnuIdsAfterRollbackPort1);
        accessLineRiRobot.compareLists(targetOnuIdsBeforePreparationPort2, targetOnuIdsAfterRollbackPort2);

    }
}