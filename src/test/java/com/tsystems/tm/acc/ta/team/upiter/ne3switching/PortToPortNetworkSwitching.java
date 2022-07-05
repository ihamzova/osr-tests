package com.tsystems.tm.acc.ta.team.upiter.ne3switching;

import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.pages.osr.networkswitching.NetworkSwitchingPage;
import com.tsystems.tm.acc.ta.robot.osr.*;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.AllocatedAnpTagDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

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

@Epic("NE3 Network Switching")
public class PortToPortNetworkSwitching extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot;
    private NetworkSwitchingRobot networkSwitchingRobot;
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private OntOltOrchestratorRobot ontOltOrchestratorRobot;
    private HomeIdManagementRobot homeIdManagementRobot;
    private OntUsageRobot ontUsageRobot;
    private UpiterTestContext context = UpiterTestContext.get();

    private PortProvisioning endSz_49_30_179_76H1_3_0;
    private PortProvisioning endSz_49_30_179_76H1_3_1;
    private PortProvisioning endSz_49_30_179_76H1_3_2;
    private PortProvisioning endSz_49_911_1100_76H1_1_0;
    private PortProvisioning endSz_49_911_1100_76H1_1_1;
    private PortProvisioning endSz_49_911_1100_76H1_1_2;
    private Ont ont;

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot = new AccessLineRiRobot();
        networkSwitchingRobot = new NetworkSwitchingRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
        homeIdManagementRobot = new HomeIdManagementRobot();
        ontUsageRobot = new OntUsageRobot();

        wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);

        endSz_49_30_179_76H1_3_0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H1_3_0);
        endSz_49_30_179_76H1_3_1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H1_3_1);
        endSz_49_30_179_76H1_3_2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H1_3_2);
        endSz_49_911_1100_76H1_1_0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_911_1100_76H1_1_0);
        endSz_49_911_1100_76H1_1_1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_911_1100_76H1_1_1);
        endSz_49_911_1100_76H1_1_2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_911_1100_76H1_1_2);
        ont = new Ont();
        ont = ontUsageRobot.randomizeSerialNumber(ont);

        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);
    }

    @BeforeMethod
    void setup() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test(priority = 1)
    @TmsLink("DIGIHUB-121792")
    @Description("Network Switching Preparation, full PON Port Switching")
    public void networkSwitchingFullPortPreparationTest() throws Exception {
        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);
        List<AccessLineDto> sourceAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_0);
        List<Integer> sourceAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparation);
        List<Integer> sourceOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, sourceAccessLinesBeforePreparation);

        int numberOfAccessLinesOnTargetPortBeforePreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_0).size();

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startFullPortPreparation(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed during preparation");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during preparation");

        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
        "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());
        assertTrue(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is not displayed after preparation");
        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after preparation");

        List<AccessLineDto> sourceAccessLinesAfterPreparation = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_0);
        List<Integer> sourceAnpTagsAfterPreparation = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterPreparation);
        List<Integer> sourceOnuIdsAfterPreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, sourceAccessLinesAfterPreparation);
        List<Integer> targetAnpTagsAfterPreparation = accessLineRiRobot.getAllocatedAnpTagsFromNsProfile(sourceAccessLinesAfterPreparation);
        List<Integer> targetOnuIdsAfterPreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H1_1_0, sourceAccessLinesAfterPreparation);

        int numberOfAccessLinesOnTargetPortAfterPreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_0).size();

        assertEquals(numberOfAccessLinesOnTargetPortAfterPreparation,
                numberOfAccessLinesOnTargetPortBeforePreparation - sourceAccessLinesBeforePreparation.size(),
                "Number of AccessLines on target port after preparation is incorrect");
        assertTrue(targetAnpTagsAfterPreparation.size() == sourceAccessLinesAfterPreparation.size(),
                "Number of target anpTags is incorrect");
        assertTrue(targetOnuIdsAfterPreparation.size() == sourceAccessLinesAfterPreparation.size(),
                "Number of target onuIds is incorrect");
        assertTrue(accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparation, sourceAnpTagsAfterPreparation));
        assertTrue(accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparation, sourceOnuIdsAfterPreparation));
    }

    @Test(dependsOnMethods = "networkSwitchingFullPortPreparationTest")
    @TmsLink("DIGIHUB-121792")
    @Description("Network Switching Commit")
    public void networkSwitchingCommitTest() throws Exception {
        endSz_49_30_179_76H1_3_0.setAccessLinesCount(0);
        endSz_49_30_179_76H1_3_0.setAccessLinesWG(0);
        endSz_49_30_179_76H1_3_0.setHomeIdPool(0);
        endSz_49_30_179_76H1_3_0.setBackhaulId(0);
        List<AccessLineDto> sourceAccessLinesBeforeCommit = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_0);
        List<Integer> targetAnpTagsBeforeCommit = accessLineRiRobot.getAllocatedAnpTagsFromNsProfile(sourceAccessLinesBeforeCommit);
        List<Integer> targetOnuIdsBeforeCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H1_1_0, sourceAccessLinesBeforeCommit);

        int expectedNumberofAccessLineOnTargetPort = sourceAccessLinesBeforeCommit.size() +
                accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_0).size();

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.clickSearchTab()
                .searchPackagesByDevice(endSz_49_911_1100_76H1_1_0);
        String packageId = networkSwitchingPage.getPackageIdOnSearchTab();
        networkSwitchingPage.startNe3Commit(packageId, "Portdeprovisionierung", "PREPARED");
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed during commit phase");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during commit phase");
        networkSwitchingPage.waitUntilNeededStatus("FINISHED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("FINISHED"),
        "Wrong package status, expected FINISHED, but found " + networkSwitchingPage.getPackageStatus());

        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after commit phase");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed after commit phase");

        List<AccessLineDto> sourceAccessLinesAfterCommit = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_911_1100_76H1_1_0);
        List<Integer> targetAnpTagsAfterCommit = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterCommit);
        List<Integer> sourceOnuIdsAfterCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, sourceAccessLinesAfterCommit);
        List<Integer> targetOnuIdsAfterCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H1_1_0, sourceAccessLinesBeforeCommit);

        int expectedNumberOfAccessLineOnSourcePort;
        if (accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0).size() > 4) {
            expectedNumberOfAccessLineOnSourcePort = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0).size();
        } else {
            expectedNumberOfAccessLineOnSourcePort = endSz_49_30_179_76H1_3_0.getAccessLinesCount();
        }

        assertEquals(accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0).size(), expectedNumberOfAccessLineOnSourcePort,
                "Number of AccessLines on source port is incorrect");
        assertEquals(accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_0).size(), expectedNumberofAccessLineOnTargetPort,
                "Number of AccessLines on target port is incorrect");

        assertTrue(accessLineRiRobot.compareLists(targetAnpTagsAfterCommit, targetAnpTagsBeforeCommit));
        assertTrue(accessLineRiRobot.compareLists(targetOnuIdsAfterCommit, targetOnuIdsBeforeCommit));

        assertEquals(sourceOnuIdsAfterCommit.size(), 0, "Number of source onuIds after commit is incorrect");
        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterCommit).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null),
                "Some of the switched AccessLines still have NetworkSwitchingProfiles after Commit");
        accessLineRiRobot.checkFtthPortParameters(endSz_49_30_179_76H1_3_0);
    }

    @Test
    @TmsLink("DIGIHUB-114664")
    @Description("Network Switching Rollback")
    public void networkSwitchingRollbackTest() throws Exception {
        List<AccessLineDto> sourceAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_1);
        List<AccessLineDto> targetAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_1);
        List<Integer> sourceAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparation);
        List<Integer> sourceOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_1, sourceAccessLinesBeforePreparation);
        List<Integer> targetAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesBeforePreparation);
        List<Integer> targetOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H1_1_1, targetAccessLinesBeforePreparation);
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startFullPortPreparation(endSz_49_30_179_76H1_3_1, endSz_49_911_1100_76H1_1_1);
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId()
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        networkSwitchingPage.startRollback(packageId, "PREPARED");
        networkSwitchingPage.waitUntilNeededStatus("IN_ROLLBACK", packageId);
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed during rollback");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during rollback");

        networkSwitchingPage.waitUntilNeededStatus("ROLLBACKED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("ROLLBACKED"),
                "Wrong package status, expected ROLLBACKED, but found " + networkSwitchingPage.getPackageStatus());
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after rollback");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed after rollback");

        List<AccessLineDto> sourceAccessLinesAfterRollback = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_1);
        List<AccessLineDto> targetAccessLinesAfterRollback = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_1);
        List<Integer> sourceAnpTagsAfterRollback = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterRollback);
        List<Integer> sourceOnuIdsAfterRollback = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_1, sourceAccessLinesAfterRollback);
        List<Integer> targetAnpTagsAfterRollback = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesAfterRollback);
        List<Integer> targetOnuIdsAfterRollback = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H1_1_1, targetAccessLinesAfterRollback);
        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterRollback).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null),
                "Some of the AccessLines still have NetworkSwitchingProfiles after Rollback");
        assertTrue(targetAccessLinesBeforePreparation.size() == targetAccessLinesAfterRollback.size()
                && sourceAccessLinesBeforePreparation.size() == sourceAccessLinesAfterRollback.size(),
                "Source AccessLine before Preparation and after Rollback are not identical");
        assertTrue(accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparation, sourceAnpTagsAfterRollback));
        assertTrue(accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparation, sourceOnuIdsAfterRollback));
        assertTrue(accessLineRiRobot.compareLists(targetAnpTagsBeforePreparation, targetAnpTagsAfterRollback));
        assertTrue(accessLineRiRobot.compareLists(targetOnuIdsBeforePreparation, targetOnuIdsAfterRollback));
    }

    @Test
    @TmsLink("DIGIHUB-109871")
    @Description("Network Switching Update, S/N of a prepared AccessLine is changed")
    public void networkSwitchingUpdateSnTest() throws Exception {
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startFullPortPreparation(endSz_49_30_179_76H1_3_2, endSz_49_911_1100_76H1_1_2);
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId()
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesWithSwitchingProfile(endSz_49_30_179_76H1_3_2).get(0);
        assertEquals(accessLineDto.getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber(),
                accessLineDto.getNetworkSwitchingProfile().getOntSerialNumber(),
                "OntSerialNumbers on SubscriberNeProfile and NetworkSwitchingProfile after Preparation are different");
        ontOltOrchestratorRobot.changeOntSerialNumber(accessLineDto.getLineId(), ont.getSerialNumber());

        accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLineDto.getLineId()).get(0);
        assertEquals(accessLineDto.getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber(), ont.getSerialNumber(),
                "OntSerialNumber was not updated on SubscriberNeProfile");

        networkSwitchingPage
                .clickPaketverwaltungNe3Tab()
                .getPackageInfo(packageId)
                .waitUntilNeededStatus("UPDATING", packageId)
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLineDto.getLineId()).get(0);

        assertEquals(accessLineDto.getNetworkSwitchingProfile().getOntSerialNumber(),
                accessLineDto.getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber(),
                "OntSerialNumber on NetworkSwitchingProfile was not updated after mirroring");
    }

    @Test(dependsOnMethods = "networkSwitchingUpdateSnTest")
    @TmsLink("DIGIHUB-109872")
    @Description("Network Switching Update, new AccessLine is added to the package")
    public void networkSwitchingUpdateNewAccessLineTest() throws Exception {
        List<AccessLineDto> targetAccessLinesBeforeUpdate = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_2);

        AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesWithoutSwitchingProfile(endSz_49_30_179_76H1_3_2).get(0);
        assertNull(accessLineDto.getHomeId(), "AccessLine for mirroring already has a HomeId");

        String homeId = homeIdManagementRobot.generateHomeid().getHomeId();
        accessLineRiRobot.updateHomeIdOnAccessLine(accessLineDto.getLineId(), homeId);

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.clickSearchTab()
                .searchPackagesByDevice(endSz_49_911_1100_76H1_1_2);
        String packageId = networkSwitchingPage.getPackageIdOnSearchTab();
        networkSwitchingPage
                .clickPaketverwaltungNe3Tab()
                .getPackageInfo(packageId)
                .waitUntilNeededStatus("UPDATING", packageId)
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLineDto.getLineId()).get(0);

        assertNotNull(accessLineDto.getNetworkSwitchingProfile(), "Newly switched AccessLine doesn't have a NetworkSwitchingProfile");
        assertEquals(accessLineDto.getNetworkSwitchingProfile().getOntSerialNumber(), accessLineDto.getDefaultNeProfile().getOntSerialNumber(),
                "OntSerialNumbers on SubscriberNeProfile and NetworkSwitchingProfile after Mirroring process are different");
        assertNotNull(accessLineDto.getNetworkSwitchingProfile().getAnpTag(), "Newly switched AccessLine doesn't have an anpTag on the NetworkSwitchingProfile");
        assertEquals(accessLineRiRobot.getAllocatedOnuIdByDeviceAndLineId(endSz_49_30_179_76H1_3_2, accessLineDto.getLineId()).size(), 1,
                "Number of source AllocatedOnuIds for the newly switched AccessLine is incorrect");
        assertEquals(accessLineRiRobot.getAllocatedOnuIdByDeviceAndLineId(endSz_49_911_1100_76H1_1_2, accessLineDto.getLineId()).size(), 1,
                "Number of target AllocatedOnuIds for the newly switched AccessLine is incorrect");

        List<AccessLineDto> targetAccessLinesAfterUpdate = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_2);
        assertEquals(targetAccessLinesAfterUpdate.size(), targetAccessLinesBeforeUpdate.size() - 1,
                "Number of AccessLine on the target port is incorrect");
    }

    @Test
    @TmsLink("DIGIHUB-140088")
    @Description("Partial Network Switching, Port to Port Preparation")
    public void networkSwitchingPartialPreparation() throws Exception {

        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);

        int numberOfAccessLinesForSwitching = 5;

        List<AccessLineDto> allAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_0);
        List<String> assignedHomeIds = allAccessLinesBeforePreparation.stream()
                .map(accessLineDto -> accessLineDto.getHomeId()).collect(Collectors.toList());

        int numberOfAccessLinesOnTargetPortBeforePreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_0).size();

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();

        List<String> displayedHomeIds = networkSwitchingPage.clickPartialPortPreparation(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0)
                .collectHomeIds().stream().map(homeIdElement -> homeIdElement.getText()).collect(Collectors.toList());
        assertTrue(accessLineRiRobot.compareLists(displayedHomeIds, assignedHomeIds));

        List<String> checkedHomeIds = networkSwitchingPage.selectHomeIdsForPreparation(numberOfAccessLinesForSwitching);
        List<AccessLineDto> accessLinesForSwitchingBeforePreparation = accessLineRiRobot.getAccessLinesByHomeIds(checkedHomeIds);
        List<AccessLineDto> accessLinesNotForSwitching = allAccessLinesBeforePreparation.stream()
                .filter(accessLineDto -> !accessLinesForSwitchingBeforePreparation.contains(accessLineDto)).collect(Collectors.toList());

        networkSwitchingPage.clickPrepareButton();

        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed during preparation");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during preparation");
        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());
        assertTrue(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is not displayed after preparation");
        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after preparation");

        List<AccessLineDto> accessLinesForSwitchingAfterPreparation = accessLineRiRobot.getAccessLinesByHomeIds(checkedHomeIds);
        int numberOfAccessLinesOnTargetPortAfterPreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_911_1100_76H1_1_0).size();
        List<AllocatedAnpTagDto> targetAnpTags = accessLineRiRobot.getAllocatedAnpTagsFromNsProfileV2(accessLinesForSwitchingAfterPreparation);
        List<Integer> targetOnuIds = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_911_1100_76H1_1_0, accessLinesForSwitchingAfterPreparation);

        assertTrue(accessLinesForSwitchingAfterPreparation.stream().allMatch(accessLineDto -> accessLineDto.getNetworkSwitchingProfile() != null),
                "Some of the switched AccessLine do not have a NetworkSwitchingProfile");
        assertTrue(accessLinesNotForSwitching.stream().allMatch(accessLineDto -> accessLineDto.getNetworkSwitchingProfile() == null),
                "Some of the not switched AccessLine have a NetworkSwitchingProfile");
        assertEquals(targetAnpTags.size(), numberOfAccessLinesForSwitching, "Some of the NetworkSwitchingProfiles do not have an anpTag");
        assertTrue(targetAnpTags.stream().allMatch(allocatedAnpTagDto ->
                allocatedAnpTagDto.getReference().getEndSz().equals(endSz_49_911_1100_76H1_1_0.getEndSz())
                        && allocatedAnpTagDto.getReference().getSlotNumber().equals(endSz_49_911_1100_76H1_1_0.getSlotNumber())
                        && allocatedAnpTagDto.getReference().getPortNumber().equals(endSz_49_911_1100_76H1_1_0.getPortNumber())),
                "Some of the target anpTags have a wrong Reference");
        assertEquals(numberOfAccessLinesOnTargetPortAfterPreparation,
                numberOfAccessLinesOnTargetPortBeforePreparation - accessLinesForSwitchingAfterPreparation.size(),
                "Number of AccessLines on target port after preparation is incorrect");
        assertEquals(targetOnuIds.size(), numberOfAccessLinesForSwitching, "Number of target onuIds is not correct");
    }

    @Test(dependsOnMethods = "networkSwitchingPartialPreparation")
    @TmsLink("DIGIHUB-140092")
    @Description("Partial Network Switching, Update, new AccessLine is not added to the package")
    public void networkPartialSwitchingUpdateNewAccessLineTest() throws Exception {
        List<AccessLineDto> targetAccessLinesBeforeUpdate = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0);

        AccessLineDto accessLineDto = accessLineRiRobot
                .getAccessLinesWithoutSwitchingProfile(endSz_49_911_1100_76H1_1_0).stream()
                .filter(accessLine -> accessLine.getHomeId() == null).collect(Collectors.toList()).get(0);
        assertNull(accessLineDto.getHomeId(), "AccessLine for mirroring already has a HomeId");

        String homeId = homeIdManagementRobot.generateHomeid().getHomeId();
        accessLineRiRobot.updateHomeIdOnAccessLine(accessLineDto.getLineId(), homeId);

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.clickSearchTab()
                .searchPackagesByDevice(endSz_49_30_179_76H1_3_0);
        String packageId = networkSwitchingPage.getPackageIdOnSearchTab();
        networkSwitchingPage
                .clickPaketverwaltungNe3Tab()
                .getPackageInfo(packageId)
                .waitUntilNeededStatus("UPDATING", packageId)
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLineDto.getLineId()).get(0);
        List<AccessLineDto> targetAccessLinesAfterUpdate = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0);

        assertNull(accessLineDto.getNetworkSwitchingProfile(), "New AccessLine was switched, although it shouldn't have");
        assertEquals(targetAccessLinesAfterUpdate.size(), targetAccessLinesBeforeUpdate.size(), "Number of AccessLines in the package has changed");
    }
}


