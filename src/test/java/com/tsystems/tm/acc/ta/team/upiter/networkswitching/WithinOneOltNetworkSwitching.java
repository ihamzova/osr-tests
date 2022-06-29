package com.tsystems.tm.acc.ta.team.upiter.networkswitching;

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

@Epic("Network Switching")
public class WithinOneOltNetworkSwitching extends GigabitTest {
    private AccessLineRiRobot accessLineRiRobot;
    private NetworkSwitchingRobot networkSwitchingRobot;
    private OntOltOrchestratorRobot ontOltOrchestratorRobot;
    private HomeIdManagementRobot homeIdManagementRobot;
    private OntUsageRobot ontUsageRobot;
    private UpiterTestContext context = UpiterTestContext.get();
    private PortProvisioning endSz_49_30_179_76H1_3_0;
    private PortProvisioning endSz_49_30_179_76H1_3_1;
    private PortProvisioning endSz_49_911_1100_76H1_1_0;
    private Ont ont;

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot = new AccessLineRiRobot();
        networkSwitchingRobot = new NetworkSwitchingRobot();
        ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
        homeIdManagementRobot = new HomeIdManagementRobot();
        ontUsageRobot = new OntUsageRobot();
        endSz_49_30_179_76H1_3_0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H1_3_0);
        endSz_49_30_179_76H1_3_1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_30_179_76H1_3_1);
        endSz_49_911_1100_76H1_1_0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.EndSz_49_911_1100_76H1_1_0);
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

    @Test
    @TmsLink("DIGIHUB-148709")
    @Description("FTTH PON NE3 Switching - switching within one OLT, Port to Port Preparation")
    public void networkSwitchingWithinOneOltPreparationTest() throws Exception {

        int numberOfAccessLinesForSwitching = 5;

        List<AccessLineDto> allAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_1);
        List<String> assignedHomeIds = allAccessLinesBeforePreparation.stream()
                .map(accessLineDto -> accessLineDto.getHomeId()).collect(Collectors.toList());

        int numberOfAccessLinesOnTargetPortBeforePreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0).size();

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();

        List<String> displayedHomeIds = networkSwitchingPage.clickPartialPortPreparation(endSz_49_30_179_76H1_3_1, endSz_49_30_179_76H1_3_0)
                .collectHomeIds().stream().map(homeIdElement -> homeIdElement.getText()).collect(Collectors.toList());
        accessLineRiRobot.compareLists(displayedHomeIds, assignedHomeIds);

        List<String> checkedHomeIds = networkSwitchingPage.selectHomeIdsForPreparation(numberOfAccessLinesForSwitching);
        List<AccessLineDto> accessLinesForSwitchingBeforePreparation = accessLineRiRobot.getAccessLinesByHomeIds(checkedHomeIds);
        List<AccessLineDto> accessLinesNotForSwitching = allAccessLinesBeforePreparation.stream()
                .filter(accessLineDto -> !accessLinesForSwitchingBeforePreparation.contains(accessLineDto)).collect(Collectors.toList());

        networkSwitchingPage.clickPrepareButton();

        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId();
        assertFalse(networkSwitchingPage.getExecutionButton().isDisplayed(), "Execution button is displayed during preparation");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during preparation");
        networkSwitchingPage.waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());
        assertTrue(networkSwitchingPage.getExecutionButton().isDisplayed(), "Execution button is not displayed after preparation");
        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after preparation");

        List<AccessLineDto> accessLinesForSwitchingAfterPreparation = accessLineRiRobot.getAccessLinesByHomeIds(checkedHomeIds);
        int numberOfAccessLinesOnTargetPortAfterPreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0).size();
        List<AllocatedAnpTagDto> targetAnpTags = accessLineRiRobot.getAllocatedAnpTagsFromNsProfileV2(accessLinesForSwitchingAfterPreparation);
        List<Integer> targetOnuIds = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, accessLinesForSwitchingAfterPreparation);

        assertTrue(accessLinesForSwitchingAfterPreparation.stream().allMatch(accessLineDto -> accessLineDto.getNetworkSwitchingProfile() != null),
                "Some of the switched AccessLine do not have a NetworkSwitchingProfile");
        assertTrue(accessLinesNotForSwitching.stream().allMatch(accessLineDto -> accessLineDto.getNetworkSwitchingProfile() == null),
                "Some of the not switched AccessLine have a NetworkSwitchingProfile");
        assertEquals(targetAnpTags.size(), numberOfAccessLinesForSwitching, "Some of the NetworkSwitchingProfiles do not have an anpTag");
        assertTrue(targetAnpTags.stream().allMatch(allocatedAnpTagDto ->
                allocatedAnpTagDto.getReference().getEndSz().equals(endSz_49_30_179_76H1_3_0.getEndSz())
                        && allocatedAnpTagDto.getReference().getSlotNumber().equals(endSz_49_30_179_76H1_3_0.getSlotNumber())
                        && allocatedAnpTagDto.getReference().getPortNumber().equals(endSz_49_30_179_76H1_3_0.getPortNumber())),
                "Some of the target anpTags have a wrong Reference");
        assertEquals(numberOfAccessLinesOnTargetPortAfterPreparation,
                numberOfAccessLinesOnTargetPortBeforePreparation - accessLinesForSwitchingAfterPreparation.size(),
                "Number of AccessLines on target port after preparation is incorrect");
        assertEquals(targetOnuIds.size(), numberOfAccessLinesForSwitching, "Number of target onuIds is not correct");
    }

    @Test(dependsOnMethods = "networkSwitchingWithinOneOltPreparationTest")
    @TmsLink("DIGIHUB-121792")
    @Description("FTTH PON NE3 Switching Execution and Commit")
    public void networkSwitchingWithinOneOltExecutionAndCommitTest() throws Exception {

        List<AccessLineDto> allSourceAccessLinesBeforeCommit = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_1);
        List<AccessLineDto> sourceAccessLinesBeforeCommit = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_1);
        List<AccessLineDto> sourceAccessLinesforSwitching = accessLineRiRobot.getAccessLinesWithSwitchingProfile(endSz_49_30_179_76H1_3_1);
        List<Integer> targetAnpTagsBeforeCommit = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforeCommit);
        List<Integer> targetOnuIdsBeforeCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_1, sourceAccessLinesBeforeCommit);

        int expectedNumberOfAccessLinesOnTargetPort = sourceAccessLinesforSwitching.size() +
                accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0).size();

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.clickSearchTab()
                .searchPackagesByDevice(endSz_49_30_179_76H1_3_0);
        String packageId = networkSwitchingPage.getPackageIdOnSearchTab();
        networkSwitchingPage.startExecution(packageId);
        networkSwitchingPage.waitUntilNeededStatus("EXECUTED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("EXECUTED"),
                "Wrong package status, expected EXECUTED, but found " + networkSwitchingPage.getPackageStatus());
        assertTrue(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is not displayed after execution");
        assertTrue(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is not displayed after execution");

        networkSwitchingPage.startCommit(packageId, "Portprovisionierung", "EXECUTED");
        assertTrue(networkSwitchingPage.getPackageStatus().contains("COMMIT_IN_PROGRESS"));
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed during commit phase");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during commit phase");
        networkSwitchingPage.waitUntilNeededStatus("FINISHED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("FINISHED"),
        "Wrong package status, expected FINISHED, but found " + networkSwitchingPage.getPackageStatus());

        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after commit phase");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed after commit phase");

        List<AccessLineDto> sourceAccessLinesAfterCommit = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_0);
        List<Integer> targetAnpTagsAfterCommit = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterCommit);
        List<Integer> sourceOnuIdsAfterCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_1, sourceAccessLinesAfterCommit);
        List<Integer> targetOnuIdsAfterCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, sourceAccessLinesBeforeCommit);

        int expectedNumberOfAccessLinesOnSourcePort;
        if (allSourceAccessLinesBeforeCommit.size() - sourceAccessLinesforSwitching.size() > 4) {
            expectedNumberOfAccessLinesOnSourcePort = allSourceAccessLinesBeforeCommit.size() - sourceAccessLinesforSwitching.size();
        } else {
            expectedNumberOfAccessLinesOnSourcePort = endSz_49_30_179_76H1_3_1.getAccessLinesCount();
        }

        assertEquals(accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0).size(), expectedNumberOfAccessLinesOnTargetPort,
                "Number of AccessLines on target port is incorrect");
        assertEquals(accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_1).size(), expectedNumberOfAccessLinesOnSourcePort,
                "Number of AccessLines on source port is incorrect");
        accessLineRiRobot.compareLists(targetAnpTagsAfterCommit, targetAnpTagsBeforeCommit);
        accessLineRiRobot.compareLists(targetOnuIdsAfterCommit, targetOnuIdsBeforeCommit);
        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterCommit).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null),
                "Some of the switched AccessLines still have a NetworkSwitchingProfile");
        assertEquals(sourceOnuIdsAfterCommit.size(), 0, "Some source AccessLines still have source onuIds after Commit");
    }

    @Test
    @TmsLink("DIGIHUB-151451")
    @Description("Network Switching Update, S/N of a prepared AccessLine is changed after Execution")
    public void networkSwitchingWithinOneOltUpdateSnTest() throws Exception {
        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startFullPortPreparation(endSz_49_30_179_76H1_3_1, endSz_49_30_179_76H1_3_0);
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId()
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        networkSwitchingPage.startExecution(packageId);
        networkSwitchingPage.waitUntilNeededStatus("EXECUTED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("EXECUTED"),
                "Wrong package status, expected EXECUTED, but found " + networkSwitchingPage.getPackageStatus());

        AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesWithSwitchingProfile(endSz_49_30_179_76H1_3_1).get(0);
        assertEquals(accessLineDto.getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber(),
                accessLineDto.getNetworkSwitchingProfile().getOntSerialNumber(),
                "OntSerialNumbers on SubscriberNeProfile and NetworkSwitchingProfile after Execution are different");
        ontOltOrchestratorRobot.changeOntSerialNumber(accessLineDto.getLineId(), ont.getSerialNumber());

        accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLineDto.getLineId()).get(0);
        assertEquals(accessLineDto.getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber(), ont.getSerialNumber(),
                "OntSerialNumber was not updated on SubscriberNeProfile");

        networkSwitchingPage
                .clickPaketverwaltungTab()
                .getPackageInfo(packageId)
                .waitUntilNeededStatus("UPDATING", packageId)
                .waitUntilNeededStatus("EXECUTED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("EXECUTED"),
                "Wrong package status, expected EXECUTED, but found " + networkSwitchingPage.getPackageStatus());

        accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLineDto.getLineId()).get(0);

        assertEquals(accessLineDto.getNetworkSwitchingProfile().getOntSerialNumber(),
                accessLineDto.getDefaultNeProfile().getSubscriberNeProfile().getOntSerialNumber(),
                "OntSerialNumber on NetworkSwitchingProfile was not updated after mirroring");
    }

    @Test
    @TmsLink("DIGIHUB-152166")
    @Description("Update, new AccessLine added, not enough WG lines on Port")
    public void networkSwitchingWithinOneUpdateNewAccessLineTest() throws Exception {
        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);

        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startFullPortPreparation(endSz_49_30_179_76H1_3_1, endSz_49_30_179_76H1_3_0);
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId()
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        List<AccessLineDto> targetAccessLinesBeforeUpdate = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0);
        AccessLineDto accessLineDto = accessLineRiRobot
                .getAccessLinesWithoutSwitchingProfile(endSz_49_30_179_76H1_3_1).stream()
                .filter(accessLine -> accessLine.getHomeId() == null).collect(Collectors.toList()).get(0);

        assertNull(accessLineDto.getHomeId(), "AccessLine for mirroring already has a HomeId");
        String homeId = homeIdManagementRobot.generateHomeid().getHomeId();
        accessLineRiRobot.updateHomeIdOnAccessLine(accessLineDto.getLineId(), homeId);
        networkSwitchingPage
                .clickPaketverwaltungTab()
                .getPackageInfo(packageId)
                .waitUntilNeededStatus("UPDATING", packageId)
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        accessLineDto
                = accessLineRiRobot.getAccessLinesByLineId(accessLineDto.getLineId()).get(0);

        assertNotNull(accessLineDto.getNetworkSwitchingProfile(), "Newly switched AccessLine doesn't have a NetworkSwitchingProfile");
        assertEquals(accessLineDto.getNetworkSwitchingProfile().getOntSerialNumber(), accessLineDto.getDefaultNeProfile().getOntSerialNumber(),
                "OntSerialNumbers on SubscriberNeProfile and NetworkSwitchingProfile after Mirroring process are different");
        assertNotNull(accessLineDto.getNetworkSwitchingProfile().getAnpTag(), "Newly switched AccessLine doesn't have an anpTag on the NetworkSwitchingProfile");
        assertEquals(accessLineRiRobot.getAllocatedOnuIdByDeviceAndLineId(endSz_49_30_179_76H1_3_1, accessLineDto.getLineId()).size(), 1,
                "Number of source AllocatedOnuIds for the newly switched AccessLine is incorrect");
        assertEquals(accessLineRiRobot.getAllocatedOnuIdByDeviceAndLineId(endSz_49_30_179_76H1_3_0, accessLineDto.getLineId()).size(), 1,
                "Number of target AllocatedOnuIds for the newly switched AccessLine is incorrect");

        List<AccessLineDto> targetAccessLinesAfterUpdate = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0);
        assertEquals(targetAccessLinesAfterUpdate.size(), targetAccessLinesBeforeUpdate.size(), "Number of AccessLine on the target port is incorrect");
    }

    @Test
    @TmsLink("DIGIHUB-153334")
    @Description("Network Switching Rollback within one OLT after Preparation")
    public void networkSwitchingWithinOneOltRollbackTest() throws Exception {

        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);

        List<AccessLineDto> sourceAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_1);
        List<AccessLineDto> targetAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0);
        List<Integer> sourceAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparation);
        List<Integer> sourceOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_1, sourceAccessLinesBeforePreparation);
        List<Integer> targetAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesBeforePreparation);
        List<Integer> targetOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, targetAccessLinesBeforePreparation);
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startFullPortPreparation(endSz_49_30_179_76H1_3_1, endSz_49_30_179_76H1_3_0);
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
        List<AccessLineDto> targetAccessLinesAfterRollback = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0);
        List<Integer> sourceAnpTagsAfterRollback = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterRollback);
        List<Integer> sourceOnuIdsAfterRollback = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_1, sourceAccessLinesAfterRollback);
        List<Integer> targetAnpTagsAfterRollback = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesAfterRollback);
        List<Integer> targetOnuIdsAfterRollback = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, targetAccessLinesAfterRollback);
        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterRollback).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null),
                "Some of the AccessLines still have NetworkSwitchingProfiles");
        assertTrue(targetAccessLinesBeforePreparation.size() == targetAccessLinesAfterRollback.size()
                && sourceAccessLinesBeforePreparation.size() == sourceAccessLinesAfterRollback.size(),
                "Number of source and target AccessLines after Rollback is incorrect");
        accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparation, sourceAnpTagsAfterRollback);
        accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparation, sourceOnuIdsAfterRollback);
        accessLineRiRobot.compareLists(targetAnpTagsBeforePreparation, targetAnpTagsAfterRollback);
        accessLineRiRobot.compareLists(targetOnuIdsBeforePreparation, targetOnuIdsAfterRollback);
    }

    @Test
    @TmsLink("DIGIHUB-153335")
    @Description("Network Switching Rollback within one OLT after Execution")
    public void networkSwitchingWithinOneOltRollbackAfterExecutionTest() throws Exception {
        accessLineRiRobot.clearDatabase();
        networkSwitchingRobot.clearDatabase();
        Thread.sleep(2000);
        accessLineRiRobot.fillDatabaseForNetworkSwitching(endSz_49_30_179_76H1_3_0, endSz_49_911_1100_76H1_1_0);

        List<AccessLineDto> sourceAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_1);
        List<AccessLineDto> targetAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0);
        List<Integer> sourceAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparation);
        List<Integer> sourceOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_1, sourceAccessLinesBeforePreparation);
        List<Integer> targetAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesBeforePreparation);
        List<Integer> targetOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, targetAccessLinesBeforePreparation);
        NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
        networkSwitchingPage.validateUrl();
        networkSwitchingPage.startFullPortPreparation(endSz_49_30_179_76H1_3_1, endSz_49_30_179_76H1_3_0);
        String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
        networkSwitchingPage.clickPackageId()
                .waitUntilNeededStatus("PREPARED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("PREPARED"),
                "Wrong package status, expected PREPARED, but found " + networkSwitchingPage.getPackageStatus());

        networkSwitchingPage.startExecution(packageId);
        networkSwitchingPage.waitUntilNeededStatus("EXECUTED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("EXECUTED"),
                "Wrong package status, expected EXECUTED, but found " + networkSwitchingPage.getPackageStatus());

        networkSwitchingPage.startRollback(packageId, "EXECUTED");
        networkSwitchingPage.waitUntilNeededStatus("IN_ROLLBACK", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("IN_ROLLBACK"),
                "Wrong package status, expected IN_ROLLBACK, but found " + networkSwitchingPage.getPackageStatus());
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed during rollback");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed during rollback");

        networkSwitchingPage.waitUntilNeededStatus("ROLLBACKED", packageId);
        assertTrue(networkSwitchingPage.getPackageStatus().contains("ROLLBACKED"),
                "Wrong package status, expected ROLLBACKED, but found " + networkSwitchingPage.getPackageStatus());
        assertFalse(networkSwitchingPage.getCommitButton().isDisplayed(), "Commit button is displayed after rollback");
        assertFalse(networkSwitchingPage.getRollbackButton().isDisplayed(), "Rollback button is displayed after rollback");

        List<AccessLineDto> sourceAccessLinesAfterRollback = accessLineRiRobot.getAccessLinesWithHomeId(endSz_49_30_179_76H1_3_1);
        List<AccessLineDto> targetAccessLinesAfterRollback = accessLineRiRobot.getAccessLinesByPort(endSz_49_30_179_76H1_3_0);
        List<Integer> sourceAnpTagsAfterRollback = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterRollback);
        List<Integer> sourceOnuIdsAfterRollback = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_1, sourceAccessLinesAfterRollback);
        List<Integer> targetAnpTagsAfterRollback = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesAfterRollback);
        List<Integer> targetOnuIdsAfterRollback = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(endSz_49_30_179_76H1_3_0, targetAccessLinesAfterRollback);
        assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterRollback).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null),
                "Some of the AccessLines still have NetworkSwitchingProfiles");
        assertTrue(targetAccessLinesBeforePreparation.size() == targetAccessLinesAfterRollback.size()
                && sourceAccessLinesBeforePreparation.size() == sourceAccessLinesAfterRollback.size(),
                "Number of source and target AccessLines after Rollback is incorrect");
        accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparation, sourceAnpTagsAfterRollback);
        accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparation, sourceOnuIdsAfterRollback);
        accessLineRiRobot.compareLists(targetAnpTagsBeforePreparation, targetAnpTagsAfterRollback);
        accessLineRiRobot.compareLists(targetOnuIdsBeforePreparation, targetOnuIdsAfterRollback);
    }
}
