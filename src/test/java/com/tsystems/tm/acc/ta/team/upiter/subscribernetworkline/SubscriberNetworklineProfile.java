package com.tsystems.tm.acc.ta.team.upiter.subscribernetworkline;

import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.l2bsanspreference.L2BsaNspReferenceCase;
import com.tsystems.tm.acc.data.upiter.models.networklineprofiledata.NetworkLineProfileDataCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.data.upiter.models.subscribernetworklineprofile.SubscriberNetworkLineProfileCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkLineProfileManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceCharacteristic;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
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
        ACCESS_LINE_PROFILE_CATALOG_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})

@Epic("SubscriberNetworklineProfile")
public class SubscriberNetworklineProfile extends GigabitTest {

  private AccessLineRiRobot accessLineRiRobot;
  private NetworkLineProfileManagementRobot networkLineProfileManagementRobot;
  private WgAccessProvisioningRobot wgAccessProvisioningRobot;
  private UpiterTestContext context = UpiterTestContext.get();

  private AccessLine accessLineRetail;
  private AccessLine accessLineWbGfnw;
  private AccessLine accessLineWbRkom;
  private AccessLine accessLineWs;
  private AccessLine a4accessLine;
  private AccessLine a4AccessLineL2Bsa;
  private AccessLine accessLineFttbCoax;
  private AccessLine accessLineFttbTp;

  private NetworkLineProfileData resourceOrderRetailActivation;
  private NetworkLineProfileData resourceOrderWsActivation;
  private NetworkLineProfileData resourceOrderWsModification;
  private NetworkLineProfileData resourceOrderWbActivation;
  private NetworkLineProfileData resourceOrderWbModification;
  private NetworkLineProfileData resourcerOrderA4Activation;
  private NetworkLineProfileData resourcerOrderA4Modification;
  private NetworkLineProfileData resourceOrderA4l2BsaActivation;
  private NetworkLineProfileData resourceOrderA4l2BsaModification;
  private NetworkLineProfileData resourceOrderDeletion;

  private DefaultNetworkLineProfile expectedDefaultNetworklineProfileFtth;
  private DefaultNetworkLineProfile expectedDefaultNetworklineProfileFtthV2;
  private DefaultNetworkLineProfile expectedDefaultNetworklineProfileFttbCoax;
  private DefaultNetworkLineProfile expectedDefaultNetworklineProfileFttbTp;

  private SubscriberNetworkLineProfile expectedWsSubscriberProfileActivationFtth;
  private SubscriberNetworkLineProfile expectedWsSubscriberProfileModificationFtth;
  private SubscriberNetworkLineProfile expectedWsSubscriberProfileActivationFttb;
  private SubscriberNetworkLineProfile expectedWsSubscriberProfileModificationFttb;

  private SubscriberNetworkLineProfile expectedWbSubscriberProfileActivation;
  private SubscriberNetworkLineProfile expectedWbSubscriberProfileModification;

  private SubscriberNetworkLineProfile expectedA4SubscriberProfileActivation;
  private SubscriberNetworkLineProfile expectedA4SubscriberProfileModification;
  private L2BsaNspReference expectedL2BsaNspReferenceActivation;
  private L2BsaNspReference expectedL2BsaNspReferenceModification;

  private SubscriberNetworkLineProfile expectedRetailSubscriberProfileFtth;
  private SubscriberNetworkLineProfile expectedRetailSubscriberProfileFttb;

  private ResourceCharacteristic calId;

  private PortProvisioning oltDeviceTwistedPair;
  private DpuDevice dpuDeviceTwistedPair;
  private PortProvisioning oltDeviceCoax;
  private DpuDevice dpuDeviceCoax;

  @BeforeClass
  public void init() throws InterruptedException {
    accessLineRiRobot = new AccessLineRiRobot();
    networkLineProfileManagementRobot = new NetworkLineProfileManagementRobot();
    wgAccessProvisioningRobot = new WgAccessProvisioningRobot();

    wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(true);

    oltDeviceTwistedPair = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningTwistedPair);
    dpuDeviceTwistedPair = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningTwistedPair);
    oltDeviceCoax = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningCoax);
    dpuDeviceCoax = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningCoax);

    accessLineRiRobot.clearDatabase();
    Thread.sleep(1000);
    accessLineRiRobot.fillDatabaseForOltCommissioningWithDpu(true, AccessTransmissionMedium.TWISTED_PAIR, 1, 1,
            oltDeviceTwistedPair.getEndSz(), dpuDeviceTwistedPair.getEndsz(), oltDeviceTwistedPair.getSlotNumber(), oltDeviceTwistedPair.getPortNumber());
    accessLineRiRobot.fillDatabaseForOltCommissioningWithDpu(true, AccessTransmissionMedium.COAX, 1000, 1000,
            oltDeviceCoax.getEndSz(), dpuDeviceCoax.getEndsz(), oltDeviceCoax.getSlotNumber(), oltDeviceCoax.getPortNumber());
    accessLineRetail = new AccessLine();
    accessLineWs = new AccessLine();
    accessLineWbGfnw = new AccessLine();
    accessLineWbRkom = new AccessLine();
    a4accessLine = new AccessLine();
    a4AccessLineL2Bsa = new AccessLine();
    accessLineFttbCoax = new AccessLine();
    accessLineFttbTp = new AccessLine();

    calId = new ResourceCharacteristic();
    calId.setName(ResourceCharacteristic.NameEnum.CALID);

    if (wgAccessProvisioningRobot.getFeatureToogleEnable64PonSplittingState()) {
      expectedRetailSubscriberProfileFtth = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.RetailSubscriberNetworkLineProfileFtthV2);
      expectedWsSubscriberProfileActivationFtth = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WsSubscriberNetworkLineProfileActivationFtthV2);
      expectedWsSubscriberProfileModificationFtth = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WsSubscriberNetworkLineProfileModificationFtthV2);
      expectedA4SubscriberProfileActivation = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.A4SubscriberNetworkLineProfileActivationV2);
      expectedA4SubscriberProfileModification = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.A4SubscriberNetworkLineProfileModificationV2);
    } else {
      expectedRetailSubscriberProfileFtth = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.RetailSubscriberNetworkLineProfileFtth);
      expectedWsSubscriberProfileActivationFtth = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WsSubscriberNetworkLineProfileActivationFtth);
      expectedWsSubscriberProfileModificationFtth = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WsSubscriberNetworkLineProfileModificationFtth);
      expectedA4SubscriberProfileActivation = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.A4SubscriberNetworkLineProfileActivation);
      expectedA4SubscriberProfileModification = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.A4SubscriberNetworkLineProfileModification);
    }

    expectedDefaultNetworklineProfileFtth = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
    expectedDefaultNetworklineProfileFtthV2 = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtthV2);
    resourceOrderRetailActivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.NetworkLineProfileActivation);
    resourceOrderWsActivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WsNetworkLineProfileActivation);
    resourceOrderWsModification = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WsNetworkLineProfileModification);
    resourceOrderWbActivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WbNetworkLineProfileActivation);
    resourceOrderWbModification = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WbNetworkLineProfileModification);
    resourcerOrderA4Activation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.A4NetworkLineProfileActivation);
    resourcerOrderA4Modification = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.A4NetworkLineProfileModification);
    resourceOrderA4l2BsaActivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.A4L2BsaActivation);
    resourceOrderA4l2BsaModification = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.A4L2BsaModification);
    resourceOrderDeletion = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.NetworkLineProfileDeactivation);

    expectedDefaultNetworklineProfileFttbCoax = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFttbCoax);
    expectedDefaultNetworklineProfileFttbTp = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFttbTP);
    expectedWsSubscriberProfileActivationFttb = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WsSubscriberNetworkLineProfileActivationFttb);
    expectedWsSubscriberProfileModificationFttb = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WsSubscriberNetworkLineProfileModificationFttb);

    expectedWbSubscriberProfileActivation = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WbSubscriberNetworkLineProfileActivation);
    expectedWbSubscriberProfileModification = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.WbSubscriberNetworkLineProfileModification);
    expectedL2BsaNspReferenceActivation = context.getData().getL2BsaNspReferenceDataProvider().get(L2BsaNspReferenceCase.L2BsaNspReferenceActivation);
    expectedL2BsaNspReferenceModification = context.getData().getL2BsaNspReferenceDataProvider().get(L2BsaNspReferenceCase.L2BsaNspReferenceModification);
    expectedRetailSubscriberProfileFttb = context.getData().getSubscriberNetworkLineProfileDataProvider().get(SubscriberNetworkLineProfileCase.RetailSubscriberNetworkLineProfileFttb);
  }

  @Test
  @TmsLink("DIGIHUB-91193")
  @Description("Subscriber NetworkLine Profile creation for FTTH OLT_BNG: Retail case")
  public void createSubscriberNetworkLineProfileRetail() {
    accessLineRetail.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.OLT_BNG, AccessLineTechnology.GPON, AccessLineStatus.WALLED_GARDEN).get(0).getLineId());
    resourceOrderRetailActivation = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderRetailActivation, accessLineRetail, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderRetailActivation.getResourceOrder(), accessLineRetail);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineRetail, expectedRetailSubscriberProfileFtth);
    assertEquals("DefaultNetworkLineProfile State is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLineRetail.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = "createSubscriberNetworkLineProfileRetail")
  @TmsLink("DIGIHUB-91193")
  @Description("Subscriber NetworkLine Profile deletion for FTTH OLT_BNG: Retail case")
  public void deleteSubscriberNetworkLineProfileRetail() {
    resourceOrderDeletion = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderDeletion, accessLineRetail, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderDeletion.getResourceOrder(), accessLineRetail);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(accessLineRetail, expectedDefaultNetworklineProfileFtth);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineRetail.getLineId()).get(0).getSubscriberNetworkLineProfile(),
            "SubscriberNetworkLineProfile wasn't deleted");
  }

  @Test
  @TmsLink("DIGIHUB-91194")
  @Description("Subscriber NetworkLine Profile creation for FTTH OLT_BNG: Wholesale case")
  public void createSubscriberNetworkLineProfileWS() {
    accessLineWs.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.OLT_BNG, AccessLineTechnology.GPON, AccessLineStatus.WALLED_GARDEN).get(0).getLineId());
    resourceOrderWsActivation = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWsActivation, accessLineWs, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWsActivation.getResourceOrder(), accessLineWs);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineWs, expectedWsSubscriberProfileActivationFtth);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineWs.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals("DefaultNetworkLineProfile State is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLineWs.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileWS"})
  @TmsLink("DIGIHUB-91194")
  @Description("Subscriber NetworkLine Profile modification for FTTH OLT_BNG: Wholesale case")
  public void changeSubscriberNetworkLineProfileWS() {
    resourceOrderWsModification = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWsModification, accessLineWs, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWsModification.getResourceOrder(), accessLineWs);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineWs, expectedWsSubscriberProfileModificationFtth);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineWs.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals("DefaultNetworkLineProfile State is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLineWs.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileWS", "changeSubscriberNetworkLineProfileWS"})
  @TmsLink("DIGIHUB-91194")
  @Description("Subscriber NetworkLine Profile deletion for FTTH OLT_BNG: Wholesale case")
  public void deleteSubscriberNetworkLineProfileWS() {
    resourceOrderDeletion = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderDeletion, accessLineWs, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderDeletion.getResourceOrder(), accessLineWs);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(accessLineWs, expectedDefaultNetworklineProfileFtth);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineWs.getLineId()), AccessLineStatus.ASSIGNED);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineWs.getLineId()).get(0).getSubscriberNetworkLineProfile(),
            "SubscriberNetworkLineProfile wasn't deleted");
  }

  @Test
  @TmsLink("DIGIHUB-49568")
  @Description("Subscriber NetworkLine Profile creation: Wholebuy GFNW case")
  public void createSubscriberNetworkLineProfileWbGfnw() {
    accessLineWbGfnw.setLineId("DEU.GFNW.SQA0000777");
    resourceOrderWbActivation = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWbActivation, accessLineWbGfnw, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWbActivation.getResourceOrder(), accessLineWbGfnw);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineWbGfnw, expectedWbSubscriberProfileActivation);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineWbGfnw.getLineId()), AccessLineStatus.ASSIGNED);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineWbGfnw.getLineId()).get(0).getDefaultNetworkLineProfile(),
            "DefaultNetworkLineProfile is present on a Wholebuy AccessLine");
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileWbGfnw"})
  @TmsLink("DIGIHUB-49938")
  @Description("Subscriber NetworkLine Profile modification: Wholebuy GFNW case")
  public void changeSubscriberNetworkLineProfileWbGfnw() {
    resourceOrderWbModification = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWbModification, accessLineWbGfnw, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWbModification.getResourceOrder(), accessLineWbGfnw);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineWbGfnw, expectedWbSubscriberProfileModification);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineWbGfnw.getLineId()), AccessLineStatus.ASSIGNED);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineWbGfnw.getLineId()).get(0).getDefaultNetworkLineProfile(),
            "DefaultNetworkLineProfile is present on a Wholebuy AccessLine");
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileWbGfnw", "changeSubscriberNetworkLineProfileWbGfnw"})
  @TmsLink("DIGIHUB-50901")
  @Description("Subscriber NetworkLine Profile deletion: Wholebuy GFNW case")
  public void deleteSubscriberNetworkLineProfileWbGfnw() {
    resourceOrderDeletion = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderDeletion, accessLineWbGfnw, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderDeletion.getResourceOrder(), accessLineWbGfnw);
    assertTrue(accessLineRiRobot.getAccessLinesByLineId(accessLineWbGfnw.getLineId()).isEmpty(), "WB Access line is not deleted");
  }

  @Test
  @TmsLink("DIGIHUB-*****")
  @Description("Subscriber NetworkLine Profile creation: Wholebuy RKOM case")
  public void createSubscriberNetworkLineProfileWbRkom() {
    accessLineWbRkom.setLineId("DEU.RKOM.SQA0000777");
    resourceOrderWbActivation = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWbActivation, accessLineWbRkom, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWbActivation.getResourceOrder(), accessLineWbRkom);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineWbRkom, expectedWbSubscriberProfileActivation);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineWbRkom.getLineId()), AccessLineStatus.ASSIGNED);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineWbRkom.getLineId()).get(0).getDefaultNetworkLineProfile(),
            "DefaultNetworkLineProfile is present on a Wholebuy AccessLine");
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileWbRkom"})
  @TmsLink("DIGIHUB-*****")
  @Description("Subscriber NetworkLine Profile modification: Wholebuy RKOM case")
  public void changeSubscriberNetworkLineProfileWbRkom() {
    resourceOrderWbModification = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWbModification, accessLineWbRkom, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWbModification.getResourceOrder(), accessLineWbRkom);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineWbRkom, expectedWbSubscriberProfileModification);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineWbRkom.getLineId()), AccessLineStatus.ASSIGNED);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineWbRkom.getLineId()).get(0).getDefaultNetworkLineProfile(),
            "DefaultNetworkLineProfile is present on a Wholebuy AccessLine");
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileWbRkom", "changeSubscriberNetworkLineProfileWbRkom"})
  @TmsLink("DIGIHUB-*****")
  @Description("Subscriber NetworkLine Profile deletion: Wholebuy RKOM case")
  public void deleteSubscriberNetworkLineProfileWbRkom() {
    resourceOrderDeletion = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderDeletion, accessLineWbRkom, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderDeletion.getResourceOrder(), accessLineWbRkom);
    assertTrue(accessLineRiRobot.getAccessLinesByLineId(accessLineWbRkom.getLineId()).isEmpty(), "WB Access line is not deleted");
  }

  @Test
  @TmsLink("DIGIHUB-58724")
  @Description("Subscriber Network line profile creation A4")
  public void createA4networkLineProfile() {
    a4accessLine.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.A4, AccessLineTechnology.GPON, AccessLineStatus.WALLED_GARDEN).get(0).getLineId());
    resourcerOrderA4Activation = networkLineProfileManagementRobot.setResourceOrderData(resourcerOrderA4Activation, a4accessLine, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourcerOrderA4Activation.getResourceOrder(), a4accessLine);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(a4accessLine, expectedA4SubscriberProfileActivation);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(a4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals("DefaultNetworkLineProfile State is incorrect", accessLineRiRobot.getAccessLinesByLineId(a4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createA4networkLineProfile"})
  @TmsLink("DIGIHUB-116590")
  @Description("Subscriber Network line profile сhange A4")
  public void changeA4networkLineProfile() {
    resourcerOrderA4Modification = networkLineProfileManagementRobot.setResourceOrderData(resourcerOrderA4Modification, a4accessLine, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourcerOrderA4Modification.getResourceOrder(), a4accessLine);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(a4accessLine, expectedA4SubscriberProfileModification);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(a4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    assertEquals("DefaultNetworkLineProfile State is incorrect", accessLineRiRobot.getAccessLinesByLineId(a4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createA4networkLineProfile", "changeA4networkLineProfile"})
  @TmsLink("DIGIHUB-58835")
  @Description("Subscriber Network line profile deletion A4")
  public void deleteA4networkLineProfile() {
    resourceOrderDeletion = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderDeletion, a4accessLine, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderDeletion.getResourceOrder(), a4accessLine);
    System.out.println(accessLineRiRobot.getAccessLinesByLineId(a4accessLine.getLineId()).get(0).getDefaultNetworkLineProfile());
    accessLineRiRobot.checkDefaultNetworkLineProfiles(a4accessLine, expectedDefaultNetworklineProfileFtth);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(a4accessLine.getLineId()), AccessLineStatus.ASSIGNED);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(a4accessLine.getLineId()).get(0).getSubscriberNetworkLineProfile(),
            "SubscriberNetworkLineProfile wasn't deleted");
  }

  @Test
  @TmsLink("DIGIHUB-117879")
  @Description("A4 case with L2bsa Activation")
  public void createA4L2bsa() {
    a4AccessLineL2Bsa.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.A4, AccessLineTechnology.GPON, AccessLineStatus.WALLED_GARDEN).get(0).getLineId());
    resourceOrderA4l2BsaActivation = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderA4l2BsaActivation, a4AccessLineL2Bsa, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderA4l2BsaActivation.getResourceOrder(), a4AccessLineL2Bsa);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(a4AccessLineL2Bsa.getLineId()).get(0).getDefaultNetworkLineProfile(),
            "DefaultNetworkLineProfile is present on an L2BSA AccessLine after activation");
    assertNull(accessLineRiRobot.getAccessLinesByLineId(a4AccessLineL2Bsa.getLineId()).get(0).getSubscriberNetworkLineProfile(),
            "SubscriberNetworkLineProfile is present on an L2BSA AccessLine after activation");
    accessLineRiRobot.checkL2bsaNspReference(a4AccessLineL2Bsa, expectedL2BsaNspReferenceActivation);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(a4AccessLineL2Bsa.getLineId()), AccessLineStatus.ASSIGNED);
  }

  @Test(dependsOnMethods = {"createA4L2bsa"})
  @TmsLink("DIGIHUB-117880")
  @Description("A4 case with L2bsa Modification")
  public void changeA4L2bsa() {
    resourceOrderA4l2BsaModification = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderA4l2BsaModification, a4AccessLineL2Bsa, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderA4l2BsaModification.getResourceOrder(), a4AccessLineL2Bsa);
    accessLineRiRobot.checkL2bsaNspReference(a4AccessLineL2Bsa, expectedL2BsaNspReferenceModification);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(a4AccessLineL2Bsa.getLineId()).get(0).getDefaultNetworkLineProfile(),
            "DefaultNetworkLineProfile is present on an L2BSA AccessLine after modification");
    assertNull(accessLineRiRobot.getAccessLinesByLineId(a4AccessLineL2Bsa.getLineId()).get(0).getSubscriberNetworkLineProfile(),
            "SubscriberNetworkLineProfile is present on an L2BSA AccessLine after modification");
  }

  @Test(dependsOnMethods = {"createA4L2bsa", "changeA4L2bsa"})
  @TmsLink("DIGIHUB-117886")
  @Description("A4 case with L2bsa Deletion")
  public void deleteA4L2bsa() {
    resourceOrderDeletion = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderDeletion, a4AccessLineL2Bsa, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderDeletion.getResourceOrder(), a4AccessLineL2Bsa);
    assertEquals("DefaultNetworkLineProfile state is incorrect", accessLineRiRobot.getAccessLinesByLineId(a4AccessLineL2Bsa.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.ACTIVE);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(a4AccessLineL2Bsa.getLineId()), AccessLineStatus.ASSIGNED);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(a4AccessLineL2Bsa, expectedDefaultNetworklineProfileFtthV2);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(a4AccessLineL2Bsa.getLineId()).get(0).getL2BsaNspReference(),
            "L2BsaNspReference is present on an A4 AccessLine after deletion");
  }

  @Test
  @TmsLink("DIGIHUB-115530")
  @Description("Subscriber Network line profile creation for FTTB Coax (Retail)")
  public void createSubscriberNetworkLineProfileFttbCoax() {
    accessLineFttbCoax.setLineId(accessLineRiRobot.getFttbAccessLines(AccessTransmissionMedium.COAX, AccessLineStatus.WALLED_GARDEN, AccessLineProductionPlatform.OLT_BNG).get(0).getLineId());
    resourceOrderRetailActivation = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderRetailActivation, accessLineFttbCoax, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderRetailActivation.getResourceOrder(), accessLineFttbCoax);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineFttbCoax.getLineId()), AccessLineStatus.ASSIGNED);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineFttbCoax, expectedRetailSubscriberProfileFttb);
    assertEquals("DefaultNetworkLineProfile State is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLineFttbCoax.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileFttbCoax"})
  @TmsLink("DIGIHUB-115530")
  @Description("Subscriber Network line profile deletion for FTTB Coax (Retail)")
  public void deleteSubscriberNetworkLineProfileFttbCoax() {
    resourceOrderDeletion = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderDeletion, accessLineFttbCoax, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderDeletion.getResourceOrder(), accessLineFttbCoax);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(accessLineFttbCoax, expectedDefaultNetworklineProfileFttbCoax);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineFttbCoax.getLineId()).get(0).getSubscriberNetworkLineProfile(),
            "SubscriberNetworkLineProfile wasn't deleted");
  }

  @Test
  @TmsLink("DIGIHUB-115854")
  @Description("Subscriber Network line profile creation for FTTB Twisted_Pair (Wholesale)")
  public void createSubscriberNetworkLineProfileFttbTp() {
    accessLineFttbTp.setLineId(accessLineRiRobot.getFttbAccessLines(AccessTransmissionMedium.TWISTED_PAIR, AccessLineStatus.WALLED_GARDEN, AccessLineProductionPlatform.OLT_BNG).get(0).getLineId());
    resourceOrderWsActivation = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWsActivation, accessLineFttbTp, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWsActivation.getResourceOrder(), accessLineFttbTp);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineFttbTp.getLineId()), AccessLineStatus.ASSIGNED);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineFttbTp, expectedWsSubscriberProfileActivationFttb);
    assertEquals("DefaultNetworkLineProfile State is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLineFttbTp.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileFttbTp"})
  @TmsLink("DIGIHUB-115854")
  @Description("Subscriber Network line profile modification for FTTB Twisted_Pair (Wholesale)")
  public void changeSubscriberNetworkLineProfileFttbTp() {
    accessLineFttbTp.setLineId(accessLineRiRobot.getFttbAccessLines(AccessTransmissionMedium.TWISTED_PAIR, AccessLineStatus.WALLED_GARDEN, AccessLineProductionPlatform.OLT_BNG).get(0).getLineId());
    resourceOrderWsModification = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWsModification, accessLineFttbTp, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWsModification.getResourceOrder(), accessLineFttbTp);
    assertEquals("AccessLine Status is incorrect", accessLineRiRobot.getAccessLineStateByLineId(accessLineFttbTp.getLineId()), AccessLineStatus.ASSIGNED);
    accessLineRiRobot.checkSubscriberNetworkLineProfiles(accessLineFttbTp, expectedWsSubscriberProfileModificationFttb);
    assertEquals("DefaultNetworkLineProfile State is incorrect", accessLineRiRobot.getAccessLinesByLineId(accessLineFttbTp.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
  }

  @Test(dependsOnMethods = {"createSubscriberNetworkLineProfileFttbTp", "changeSubscriberNetworkLineProfileFttbTp"})
  @TmsLink("DIGIHUB-115854")
  @Description("Subscriber Network line profile deletion for FTTB Twisted_Pair (Wholesale)")
  public void deleteSubscriberNetworkLineProfileFttbTp() {
    resourceOrderDeletion = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderDeletion, accessLineFttbTp, calId);
    networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderDeletion.getResourceOrder(), accessLineFttbTp);
    accessLineRiRobot.checkDefaultNetworkLineProfiles(accessLineFttbTp, expectedDefaultNetworklineProfileFttbTp);
    assertNull(accessLineRiRobot.getAccessLinesByLineId(accessLineFttbTp.getLineId()).get(0).getSubscriberNetworkLineProfile(),
            "SubscriberNetworkLineProfile wasn't deleted");
  }
}