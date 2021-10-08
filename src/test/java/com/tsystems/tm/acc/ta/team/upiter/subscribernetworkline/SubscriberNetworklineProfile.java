package com.tsystems.tm.acc.ta.team.upiter.subscribernetworkline;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.l2bsanspreference.L2BsaNspReferenceCase;
import com.tsystems.tm.acc.data.upiter.models.networklineprofiledata.NetworkLineProfileDataCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.data.upiter.models.subscribernetworklineprofile.SubscriberNetworkLineProfileCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkLineProfileManagementRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.ProfileState;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;


@ServiceLog({
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
public class SubscriberNetworklineProfile extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
  private NetworkLineProfileManagementRobot networkLineProfileManagementRobot = new NetworkLineProfileManagementRobot();
  private UpiterTestContext context = UpiterTestContext.get();
  private NetworkLineProfileData subscriberProfileRetail;
  private NetworkLineProfileData subscriberProfileRetailForDeletion;
  private NetworkLineProfileData subscriberProfileWSForDeletion;
  private NetworkLineProfileData subscriberProfileWBForDeletion;
  private NetworkLineProfileData subscriberProfileWS;
  private NetworkLineProfileData subscriberProfileWB;
  private AccessLine accessLineRetail;
  private AccessLine accessLineWB;
  private AccessLine accessLineWS;
  private AccessLine A4accessLine;
  private PortProvisioning a4Port;
  private NetworkLineProfileData A4l2BsaActivation;
  private NetworkLineProfileData A4l2BsaModification;
  private NetworkLineProfileData A4l2BsaDeactivation;
  private NetworkLineProfileData A4SubscriberProfileActivation;
  private NetworkLineProfileData A4SubscriberProfileChange;
  private NetworkLineProfileData A4SubscriberProfileDeletion;
  private SubscriberNetworkLineProfile expectedA4SubscriberProfileActivation;
  private SubscriberNetworkLineProfile expectedA4SubscriberProfileModification;
  private L2BsaNspReference expectedL2BsaNspReferenceActivation;
  private L2BsaNspReference expectedL2BsaNspReferenceModification;
  private DefaultNetworkLineProfile expectedDefaultNetworklineProfile;
  private SubscriberNetworkLineProfile expectedRetailSubscriberProfile;
  private SubscriberNetworkLineProfile expectedWBSubscriberProfile;
  private SubscriberNetworkLineProfile expectedWSSubscriberProfile;

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioningV1();
    accessLineRetail = context.getData().getAccessLineDataProvider().get(AccessLineCase.AccesslineForSubscriberNLP);
    accessLineWS = context.getData().getAccessLineDataProvider().get(AccessLineCase.WSAccesslineForSubscriberNLP);
    accessLineWB = context.getData().getAccessLineDataProvider().get(AccessLineCase.WBAccesslineForSubscriberNLPWB);
    A4accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.A4accessline);
    a4Port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.PortForA4);
    A4l2BsaActivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4L2BsaActivation);
    A4l2BsaModification = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4L2BsaModification);
    A4l2BsaDeactivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4L2BsaDeactivation);
    A4SubscriberProfileActivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4SubscriberNLCreation);
    A4SubscriberProfileChange = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4SubscriberNLChange);
    A4SubscriberProfileDeletion = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4SubscriberNLDeletion);
    expectedA4SubscriberProfileActivation = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.subscriberProfileA4Activation);
    expectedA4SubscriberProfileModification = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.subscriberProfileA4Change);
    expectedL2BsaNspReferenceActivation = context.getData().getL2BsaNspReferenceDataProvider().get(L2BsaNspReferenceCase.l2BsaNspReferenceActivation);
    expectedL2BsaNspReferenceModification = context.getData().getL2BsaNspReferenceDataProvider().get(L2BsaNspReferenceCase.l2BsaNspReferenceModification);
    expectedDefaultNetworklineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
    subscriberProfileRetail = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.NetworkLineProfileCreation);
    subscriberProfileRetailForDeletion = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.NetworkLineProfileDelete);
    subscriberProfileWS = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WSNetworkLineProfile);
    subscriberProfileWSForDeletion = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WSNetworkLineProfileDelete);
    subscriberProfileWB = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WBNetworkLineProfileCreation);
    subscriberProfileWBForDeletion = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WBNetworkLineProfileDeletion);
    expectedRetailSubscriberProfile = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.RetailSubscriberCreation);
    expectedWSSubscriberProfile = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WSSubscriberNetworkLineProfile);
    expectedWBSubscriberProfile = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WBSubscriberNetworkLineProfile);

  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-91193")
  @Description("Subscriber NetworkLine Profile creation: Retail case")
  public void createSubscriberNetworkLineProfileRetail() {
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfileRetail.getResourceOrder(), accessLineRetail);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineRetail, expectedRetailSubscriberProfile);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineRetail.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = "createSubscriberNetworkLineProfileRetail")
  @TmsLink("DIGIHUB-91193")
  @Description("Subscriber NetworkLine Profile deletion: Retail case")
  public void deleteSubscriberNetworkLineProfileRetail() {
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfileRetailForDeletion.getResourceOrder(), accessLineRetail);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(accessLineRetail, expectedDefaultNetworklineProfile);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineRetail.getLineId()).get(0).getSubscriberNetworkLineProfile());
  }

  @Test
  @TmsLink("DIGIHUB-91194")
  @Description("Subscriber NetworkLine Profile creation: Wholesale case")
  public void createSubscriberNetworkLineProfileWS() {
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfileWS.getResourceOrder(), accessLineWS);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineWS, expectedWSSubscriberProfile);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLineWS.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = "createSubscriberNetworkLineProfileWS")
  @TmsLink("DIGIHUB-91194")
  @Description("Subscriber NetworkLine Profile deletion: Wholesale case")
  public void deleteSubscriberNetworkLineProfileWS() {
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfileWSForDeletion.getResourceOrder(), accessLineWS);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(accessLineWS, expectedDefaultNetworklineProfile);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineWS.getLineId()).get(0).getSubscriberNetworkLineProfile());
  }

  @Test
  @TmsLink("DIGIHUB-49568")
  @Description("Subscriber NetworkLine Profile creation: Wholebuy case")
  public void createSubscriberNetworkLineProfileWB() {
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfileWB.getResourceOrder(), accessLineWB);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineWB, expectedWBSubscriberProfile);
  }

  @Test(dependsOnMethods = "createSubscriberNetworkLineProfileWB")
  @TmsLink("DIGIHUB-50901")
  @Description("Subscriber NetworkLine Profile deletion: Wholebuy case")
  public void deleteSubscriberNetworkLineProfileWB() {
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfileWBForDeletion.getResourceOrder(), accessLineWB);
    assertTrue(accessLineRiRobot.getAccessLinesByLineId(accessLineWB.getLineId()).isEmpty(), "WB Access line is not deleted");
  }

  @Test(priority = 1)
  @TmsLink("DIGIHUB-58724")
  @Description("Subscriber Network line profile creation A4")
  public void createA4networkLineProfile() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4SubscriberProfileActivation.getResourceOrder(), A4accessLine);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(A4accessLine, expectedA4SubscriberProfileActivation);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(A4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createA4networkLineProfile"}, priority = 1)
  @TmsLink("DIGIHUB-116590")
  @Description("Subscriber Network line profile сhange A4")
  public void changeA4networkLineProfile() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4SubscriberProfileChange.getResourceOrder(), A4accessLine);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(A4accessLine, expectedA4SubscriberProfileModification);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(A4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createA4networkLineProfile", "changeA4networkLineProfile"}, priority = 1)
  @TmsLink("DIGIHUB-58835")
  @Description("Subscriber Network line profile deletion A4")
  public void deleteA4networkLineProfile() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4SubscriberProfileDeletion.getResourceOrder(), A4accessLine);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(a4Port, expectedDefaultNetworklineProfile, 1);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getSubscriberNetworkLineProfile());
  }

  @Test
  @TmsLink("DIGIHUB-117879")
  @Description("A4 case with L2bsa Activation")
  public void createA4L2bsa() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4l2BsaActivation.getResourceOrder(), A4accessLine);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile());
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getSubscriberNetworkLineProfile());
    accessLineRiRobot.checkL2bsaNspReference(a4Port, expectedL2BsaNspReferenceActivation);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(A4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
  }

  @Test(dependsOnMethods = {"createA4L2bsa"})
  @TmsLink("DIGIHUB-117880")
  @Description("A4 case with L2bsa Modification")
  public void changeA4L2bsa() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4l2BsaModification.getResourceOrder(), A4accessLine);
    accessLineRiRobot.checkL2bsaNspReference(a4Port, expectedL2BsaNspReferenceModification);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile());
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getSubscriberNetworkLineProfile());
  }

  @Test(dependsOnMethods = {"createA4L2bsa", "changeA4L2bsa"})
  @TmsLink("DIGIHUB-117886")
  @Description("A4 case with L2bsa Deletion")
  public void deleteA4L2bsa() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4l2BsaDeactivation.getResourceOrder(), A4accessLine);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(A4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(a4Port, expectedDefaultNetworklineProfile, 1);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getL2BsaNspReference());
  }


}