package com.tsystems.tm.acc.ta.team.upiter.subscribernetworkline;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.l2bsanspreference.L2BsaNspReferenceCase;
import com.tsystems.tm.acc.data.upiter.models.networklineprofiledata.NetworkLineProfileDataCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkLineProfileManagementRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.ProfileState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_19_0.client.model.SubscriberNetworkLineProfileDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertNull;
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
  private NetworkLineProfileData subscriberProfile;
  private AccessLine accessLine;
  private AccessLine A4accessLine;
  private NetworkLineProfileData A4networkLineProfileActivation;
  private NetworkLineProfileData A4networkLineProfileModification;
  private NetworkLineProfileData A4networkLineProfileDeactivation;
  private L2BsaNspReference expectedL2BsaNspReferenceActivation;
  private L2BsaNspReference expectedL2BsaNspReferenceModification;
  private DefaultNetworkLineProfile expectedDefaultNetworklineProfile;
  private PortProvisioning a4Port;

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioning();
    A4accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.A4accessline);
    A4networkLineProfileActivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4L2BsaActivation);
    A4networkLineProfileModification = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4L2BsaModification);
    A4networkLineProfileDeactivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.a4L2BsaDeactivation);
    expectedL2BsaNspReferenceActivation = context.getData().getL2BsaNspReferenceDataProvider().get(L2BsaNspReferenceCase.l2BsaNspReferenceActivation);
    expectedL2BsaNspReferenceModification = context.getData().getL2BsaNspReferenceDataProvider().get(L2BsaNspReferenceCase.l2BsaNspReferenceModification);
    expectedDefaultNetworklineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
    a4Port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.PortForA4);
  }

  @AfterClass
  public void clearData() {
    accessLineRiRobot.clearDatabase();
  }

  @Test
  @TmsLink("DIGIHUB-91193")
  @Description("Subscriber NetworkLine Profile creation: Retail case")
  public void createSubscriberNetworkLineProfileRetail() {
    subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.NetworkLineProfileModifySuccess);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLP);
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfile.getResourceOrder(), accessLine);
    Assert.assertEquals(accessLineRiRobot.getSubscriberNLProfile(accessLine.getLineId()).getState(), ProfileState.ACTIVE);
    Assert.assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = "createSubscriberNetworkLineProfileRetail")
  @TmsLink("DIGIHUB-91193")
  @Description("Subscriber NetworkLine Profile deletion: Retail case")
  public void deleteSubscriberNetworkLineProfileRetail() {
    subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileDeleteSuccess);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLP);
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfile.getResourceOrder(), accessLine);
    AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0);
    assertNull(accessLineDto.getSubscriberNetworkLineProfile());
    Assert.assertEquals(accessLineDto.getDefaultNetworkLineProfile().getState(), ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-91194")
  @Description("Subscriber NetworkLine Profile creation: Wholesale case")
  public void createSubscriberNetworkLineProfileWS() {
    subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileModifySuccessWS);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLPWS);
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfile.getResourceOrder(), accessLine);
    SubscriberNetworkLineProfileDto subscriberNetworkLineProfile = accessLineRiRobot.getSubscriberNLProfile(accessLine.getLineId());
    Assert.assertEquals(subscriberNetworkLineProfile.getDownBandwidth().intValue(), 123000);
    Assert.assertEquals(subscriberNetworkLineProfile.getUpBandwidth().intValue(), 123000);
    Assert.assertEquals(subscriberNetworkLineProfile.getState(), ProfileState.ACTIVE);
    Assert.assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = "createSubscriberNetworkLineProfileWS")
  @TmsLink("DIGIHUB-91194")
  @Description("Subscriber NetworkLine Profile deletion: Wholesale case")
  public void deleteSubscriberNetworkLineProfileWS() {
    subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileDeleteSuccessWS);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLPWS);
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfile.getResourceOrder(), accessLine);
    AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0);
    assertNull(accessLineDto.getSubscriberNetworkLineProfile());
    Assert.assertEquals(accessLineDto.getDefaultNetworkLineProfile().getState(), ProfileState.ACTIVE);
  }

  @Test
  @TmsLink("DIGIHUB-49568")
  @Description("Subscriber NetworkLine Profile creation: Wholebuy case")
  public void createSubscriberNetworkLineProfileWB() {
    subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileAddSuccess);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLPWB);
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfile.getResourceOrder(), accessLine);
    AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesByLineId(this.accessLine.getLineId()).get(0);
    Assert.assertNotNull(accessLineDto.getSubscriberNetworkLineProfile(),
            "There is no Subscriber networkline profile");
    Assert.assertEquals(accessLineDto.getStatus(), AccessLineStatus.ASSIGNED);
    Assert.assertEquals(accessLineDto.getSubscriberNetworkLineProfile().getState(), ProfileState.ACTIVE);
  }


  @Test(dependsOnMethods = "createSubscriberNetworkLineProfileWB")
  @TmsLink("DIGIHUB-50901")
  @Description("Subscriber NetworkLine Profile deletion: Wholebuy case")
  public void deleteSubscriberNetworkLineProfileWB() {
    subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileDeleteSuccessWB);
    accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLPWB);
    networkLineProfileManagementRobot.createResourceOrderRequest(subscriberProfile.getResourceOrder(), accessLine);
    Assert.assertTrue(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).isEmpty(), "WB Access line is not deleted");
  }

  @Test
  @TmsLink("DIGIHUB-117879")
  @Description("NetworkLine Profile creation: A4 case with L2bsa Activation")
  public void createA4NetworkLineProfile() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4networkLineProfileActivation.getResourceOrder(), A4accessLine);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile());
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getSubscriberNetworkLineProfile());
    accessLineRiRobot.checkL2bsaNspReference(a4Port, expectedL2BsaNspReferenceActivation);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(A4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
  }

  @Test(dependsOnMethods = {"createA4NetworkLineProfile"})
  @TmsLink("DIGIHUB-117880")
  @Description("NetworkLine Profile modification: A4 case with L2bsa")
  public void changeA4NetworkLineProfile() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4networkLineProfileModification.getResourceOrder(), A4accessLine);
    accessLineRiRobot.checkL2bsaNspReference(a4Port, expectedL2BsaNspReferenceModification);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile());
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getSubscriberNetworkLineProfile());
  }

  @Test(dependsOnMethods = {"createA4NetworkLineProfile", "changeA4NetworkLineProfile"})
  @TmsLink("DIGIHUB-117886")
  @Description("NetworkLine Profile deletion: A4 case with L2bsa")
  public void deleteA4NetworkLineProfile() {
    networkLineProfileManagementRobot.createResourceOrderRequest(A4networkLineProfileDeactivation.getResourceOrder(), A4accessLine);
    assertEquals(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.ACTIVE);
    assertEquals(accessLineRiRobot.getAccessLineStateByLineId(A4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(a4Port, expectedDefaultNetworklineProfile, 1);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(A4accessLine.getLineId()).get(0).getL2BsaNspReference());
  }
}