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
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.HomeIdStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.LineIdStatus;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
public class NetworkSwitching extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot;
  private NetworkSwitchingRobot networkSwitchingRobot;
  private UpiterTestContext context = UpiterTestContext.get();

  private PortProvisioning sourcePort;
  private PortProvisioning targetPort;
  private PortProvisioning sourcePortForRollback;
  private PortProvisioning targetPortForRollback;

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot = new AccessLineRiRobot();
    networkSwitchingRobot = new NetworkSwitchingRobot();
    sourcePort = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.Source_49_30_179_76H1);
    targetPort = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.Target_49_911_1100_76H1);
    sourcePortForRollback = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.SourceRB_49_30_179_76H1);
    targetPortForRollback = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.TargetRB_49_911_1100_76H1);
    Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOTelekomNSOOpsRW);
    setCredentials(loginData.getLogin(), loginData.getPassword());
    accessLineRiRobot.clearDatabase();
    networkSwitchingRobot.clearDatabase();
    Thread.sleep(2000);
    accessLineRiRobot.fillDatabaseForNetworkSwitching(sourcePort, targetPort);

  }

  @Test
  @TmsLink("DIGIHUB-121792")
  @Description("Network Switching Preparation")
  public void networkSwitchingPreparationTest() throws Exception {

    List<AccessLineDto> sourceAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesWithHomeId(sourcePort);
    List<Integer> sourceAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparation);
    List<Integer> sourceOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(sourcePort, sourceAccessLinesBeforePreparation);
    List<String> lineIdsBeforePreparation = accessLineRiRobot.getLineIds(sourcePort);
    List<String> homeIdsBeforePreparation = accessLineRiRobot.getHomeIds(sourcePort);

    int numberOfAccessLinesOnTargetPortBeforePreparation = accessLineRiRobot.getAccessLinesByPort(targetPort).size();

    NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
    networkSwitchingPage.validateUrl();
    networkSwitchingPage.startPreparation(sourcePort, targetPort);
    String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
    networkSwitchingPage.clickPackageId()
            .waitUntilNeededStatus("PREPARED", packageId);

    assert (networkSwitchingPage.getPackageStatus().contains("PREPARED"));

    List<AccessLineDto> sourceAccessLinesAfterPreparation = accessLineRiRobot.getAccessLinesWithHomeId(sourcePort);
    List<String> lineIdsAfterPreparation = accessLineRiRobot.getLineIds(sourcePort);
    List<String> homeIdsAfterPreparation = accessLineRiRobot.getHomeIds(sourcePort);
    List<Integer> sourceAnpTagsAfterPreparation = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterPreparation);
    List<Integer> sourceOnuIdsAfterPreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(sourcePort, sourceAccessLinesAfterPreparation);
    List<Integer> targetAnpTagsAfterPreparation = accessLineRiRobot.getAllocatedAnpTagsFromNsProfile(sourceAccessLinesAfterPreparation);
    List<Integer> targetOnuIdsAfterPreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(targetPort, sourceAccessLinesAfterPreparation);

    int numberOfAccessLinesOnTargetPortAfterPreparation = accessLineRiRobot.getAccessLinesByPort(targetPort).size();

    assertEquals(numberOfAccessLinesOnTargetPortAfterPreparation,
            numberOfAccessLinesOnTargetPortBeforePreparation - sourceAccessLinesBeforePreparation.size());
    assertTrue(targetAnpTagsAfterPreparation.size() == sourceAccessLinesAfterPreparation.size());
    assertTrue(targetOnuIdsAfterPreparation.size() == sourceAccessLinesAfterPreparation.size());
    accessLineRiRobot.compareLists(lineIdsBeforePreparation, lineIdsAfterPreparation);
    accessLineRiRobot.compareLists(homeIdsBeforePreparation, homeIdsAfterPreparation);
    accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparation, sourceAnpTagsAfterPreparation);
    accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparation, sourceOnuIdsAfterPreparation);
  }

  @Test(dependsOnMethods = "networkSwitchingPreparationTest")
  @TmsLink("DIGIHUB-121792")
  @Description("Network Switching Commit")
  public void networkSwitchingCommitTest() throws Exception {

    List<AccessLineDto> sourceAccessLinesBeforeCommit = accessLineRiRobot.getAccessLinesWithHomeId(sourcePort);
    List<Integer> targetAnpTagsBeforeCommit = accessLineRiRobot.getAllocatedAnpTagsFromNsProfile(sourceAccessLinesBeforeCommit);
    List<Integer> targetOnuIdsBeforeCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(targetPort, sourceAccessLinesBeforeCommit);

    List<String> usedHomeIdsBeforeCommit = accessLineRiRobot.getHomeIdsByStatus(sourcePort, HomeIdStatus.ASSIGNED);
    List<String> usedLineIdsBeforeCommit = accessLineRiRobot.getLineIdsByAccessLinesStatus(sourcePort, AccessLineStatus.ASSIGNED);
    List<String> freeHomeIdsBeforeCommit = accessLineRiRobot.getHomeIdsByStatus(sourcePort, HomeIdStatus.FREE);
    List<String> wgLineIdsBeforeCommitOnSourcePort = accessLineRiRobot.getLineIdsByAccessLinesStatus(sourcePort, AccessLineStatus.WALLED_GARDEN);
    List<String> freeLineIdsBeforeCommitOnTargetPort = accessLineRiRobot.getLineIdsByStatus(targetPort, LineIdStatus.FREE);

    NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
    networkSwitchingPage.validateUrl();
    networkSwitchingPage.clickSearchTab()
            .searchPackagesByDevice(targetPort);
    String packageId = networkSwitchingPage.getPackageIdOnSearchTab();
    networkSwitchingPage.startCommit(packageId)
            .waitUntilNeededStatus("FINISHED", packageId);
    assert (networkSwitchingPage.getPackageStatus().contains("FINISHED"));

    List<AccessLineDto> sourceAccessLinesAfterCommit = accessLineRiRobot.getAccessLinesWithHomeId(targetPort);
    List<Integer> targetAnpTagsAfterCommit = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterCommit);
    List<Integer> sourceOnuIdsAfterCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(sourcePort, sourceAccessLinesAfterCommit);
    List<Integer> targetOnuIdsAfterCommit = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(targetPort, sourceAccessLinesBeforeCommit);
    List<String> usedHomeIdsAfterCommit = accessLineRiRobot.getHomeIdsByStatus(targetPort, HomeIdStatus.ASSIGNED);
    List<String> usedLineIdsAfterCommit = accessLineRiRobot.getLineIdsByAccessLinesStatus(targetPort, AccessLineStatus.ASSIGNED);
    List<String> freeHomeIdsAfterCommit = accessLineRiRobot.getHomeIdsByStatus(sourcePort, HomeIdStatus.FREE);
    List<String> wgLineIdsAfterCommitOnSourcePort = accessLineRiRobot.getLineIdsByAccessLinesStatus(sourcePort, AccessLineStatus.WALLED_GARDEN);
    List<String> freeLineIdsAfterCommitOnSourcePort = accessLineRiRobot.getLineIdsByStatus(sourcePort, LineIdStatus.FREE);

    assertEquals(accessLineRiRobot.getAccessLinesByPort(sourcePort).size(), sourcePort.getAccessLinesCount().intValue());
    assertEquals(accessLineRiRobot.getAccessLinesByPort(targetPort).size(), targetPort.getAccessLinesCount().intValue());
    assertEquals(accessLineRiRobot.getHomeIdPool(sourcePort).size(), sourcePort.getHomeIdPool().intValue());
    assertEquals(accessLineRiRobot.getLineIdPool(sourcePort).size(), sourcePort.getLineIdPool().intValue());
    assertEquals(accessLineRiRobot.getHomeIdPool(targetPort).size(), targetPort.getHomeIdPool().intValue());
    assertEquals(accessLineRiRobot.getLineIdPool(targetPort).size(), targetPort.getLineIdPool().intValue());

    assertTrue(targetAnpTagsAfterCommit.size() == targetAnpTagsBeforeCommit.size()
            && targetAnpTagsBeforeCommit.containsAll(targetAnpTagsAfterCommit)
            && targetAnpTagsAfterCommit.containsAll(targetAnpTagsBeforeCommit));

    assertTrue(targetOnuIdsAfterCommit.size() == targetOnuIdsBeforeCommit.size()
            && targetOnuIdsBeforeCommit.containsAll(targetOnuIdsAfterCommit)
            && targetOnuIdsAfterCommit.containsAll(targetOnuIdsBeforeCommit));

    assertEquals(sourceOnuIdsAfterCommit.size(), 0);
    assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterCommit).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null));
    assertTrue(usedHomeIdsAfterCommit.containsAll(usedHomeIdsBeforeCommit));
    assertTrue(usedLineIdsAfterCommit.containsAll(usedLineIdsBeforeCommit));
    assertTrue(freeHomeIdsAfterCommit.containsAll(freeHomeIdsBeforeCommit));
    accessLineRiRobot.compareLists(wgLineIdsBeforeCommitOnSourcePort, wgLineIdsAfterCommitOnSourcePort);
    accessLineRiRobot.compareLists(freeLineIdsBeforeCommitOnTargetPort, freeLineIdsAfterCommitOnSourcePort);

  }

  @Test
  @TmsLink("DIGIHUB-114664")
  @Description("Network Switching Rollback")
  public void networkSwitchingRollbackTest() throws Exception {

    List<AccessLineDto> sourceAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesWithHomeId(sourcePortForRollback);
    List<AccessLineDto> targetAccessLinesBeforePreparation = accessLineRiRobot.getAccessLinesByPort(targetPortForRollback);
    List<Integer> sourceAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesBeforePreparation);
    List<Integer> sourceOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(sourcePortForRollback, sourceAccessLinesBeforePreparation);
    List<Integer> targetAnpTagsBeforePreparation = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesBeforePreparation);
    List<Integer> targetOnuIdsBeforePreparation = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(targetPortForRollback, targetAccessLinesBeforePreparation);
    NetworkSwitchingPage networkSwitchingPage = NetworkSwitchingPage.openPage();
    networkSwitchingPage.validateUrl();
    networkSwitchingPage.startPreparation(sourcePortForRollback, targetPortForRollback);
    String packageId = networkSwitchingPage.getPackageIdOnPreparationTab();
    networkSwitchingPage.clickPackageId()
            .waitUntilNeededStatus("PREPARED", packageId);

    assert (networkSwitchingPage.getPackageStatus().contains("PREPARED"));
    networkSwitchingPage.startRollback(packageId)
            .waitUntilNeededStatus("ROLLBACKED", packageId);
    assert (networkSwitchingPage.getPackageStatus().contains("ROLLBACKED"));

    List<AccessLineDto> sourceAccessLinesAfterRollback = accessLineRiRobot.getAccessLinesWithHomeId(sourcePortForRollback);
    List<AccessLineDto> targetAccessLinesAfterRollback = accessLineRiRobot.getAccessLinesByPort(targetPortForRollback);
    List<Integer> sourceAnpTagsAfterRollback = accessLineRiRobot.getAllocatedAnpTags(sourceAccessLinesAfterRollback);
    List<Integer> sourceOnuIdsAfterRollback = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(sourcePortForRollback, sourceAccessLinesAfterRollback);
    List<Integer> targetAnpTagsAfterRollback = accessLineRiRobot.getAllocatedAnpTags(targetAccessLinesAfterRollback);
    List<Integer> targetOnuIdsAfterRollback = accessLineRiRobot.getAllocatedOnuIdsFromAccessLines(targetPortForRollback, targetAccessLinesAfterRollback);
    assertTrue(accessLineRiRobot.getNsProfile(sourceAccessLinesAfterRollback).stream().allMatch(networkSwitchingProfile -> networkSwitchingProfile == null));
    assertTrue(targetAccessLinesBeforePreparation.size() == targetAccessLinesAfterRollback.size()
            && sourceAccessLinesBeforePreparation.size() == sourceAccessLinesAfterRollback.size());
    accessLineRiRobot.compareLists(sourceAnpTagsBeforePreparation, sourceAnpTagsAfterRollback);
    accessLineRiRobot.compareLists(sourceOnuIdsBeforePreparation, sourceOnuIdsAfterRollback);
    accessLineRiRobot.compareLists(targetAnpTagsBeforePreparation, targetAnpTagsAfterRollback);
    accessLineRiRobot.compareLists(targetOnuIdsBeforePreparation, targetOnuIdsAfterRollback);

  }
}
