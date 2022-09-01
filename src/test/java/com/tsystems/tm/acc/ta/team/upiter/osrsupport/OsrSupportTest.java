package com.tsystems.tm.acc.ta.team.upiter.osrsupport;

import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.networklineprofiledata.NetworkLineProfileDataCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.pages.osr.osrsupport.OsrSupportPage;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkLineProfileManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_38_1.client.model.*;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceCharacteristic;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.AssertJUnit.assertTrue;

@ServiceLog({
        OSR_SUPPORT_UI,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS
})

@Epic("Osr Support UI")
public class OsrSupportTest extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot;
    private PortProvisioning port5600v2;
    private UpiterTestContext context = UpiterTestContext.get();
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private DefaultNetworkLineProfile expectedDefaultNetworklineProfileFtth;
    private DefaultNetworkLineProfile expectedDefaultNetworklineProfileFtthV2;
    private AccessLine accessLine;
    private AccessLine accessLineWs;
    private NetworkLineProfileData resourceOrderWsActivation;
    private NetworkLineProfileManagementRobot networkLineProfileManagementRobot;
    private ResourceCharacteristic calId;
    private PortProvisioning oltDeviceCoax;
    private DpuDevice dpuDeviceCoax;
    private PortProvisioning port5800;


    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot = new AccessLineRiRobot();
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
        networkLineProfileManagementRobot = new NetworkLineProfileManagementRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        accessLine = new AccessLine();
        accessLineWs = new AccessLine();
        calId = new ResourceCharacteristic();
        calId.setName(ResourceCharacteristic.NameEnum.CALID);
        expectedDefaultNetworklineProfileFtth = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtthAssigned);
        expectedDefaultNetworklineProfileFtthV2 = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtthAssignedV2);
        resourceOrderWsActivation = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.WsNetworkLineProfileActivation);
        oltDeviceCoax = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningCoax);
        dpuDeviceCoax = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningCoax);
        port5800 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.port5800);
        port5600v2 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.port5600v2);
    }


    @BeforeMethod
    void setup() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.DTAGTelekomOsrSupport);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @TmsLink("DIGIHUB-164077")
    @Description("Olt force delete in Osr Support UI")
    public void deleteOltByEndsz() {
        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
        osrSupportPage.validateUrl();
        osrSupportPage.deleteOltByEndsz(port5600v2, "true", "true");
    }

    @Test
    @TmsLink("DIGIHUB-164083")
    @Description("Delete Access Line by Line ID in Osr Support UI")
    public void deleteAccessLineByLineId() throws Exception {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
        osrSupportPage.validateUrl();
        String lineId = accessLineRiRobot.getAccessLinesByPort(port5600v2).get(0).getLineId();
        osrSupportPage.deleteAccessLineByLineId(lineId);
        assertTrue(accessLineRiRobot.getAccessLinesByLineId(lineId).size() == 0);
    }

    @Test
    @TmsLink("DIGIHUB-164078")
    @Description("Delete FTTH 1.7.Access Lines by OLT in Osr Support UI")
    public void deleteFtthAccessLines() throws Exception {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
        osrSupportPage.validateUrl();
        osrSupportPage.deleteFtthLinesByOlt(port5600v2);
        assertTrue(accessLineRiRobot.getPhysicalResourceRef(port5600v2.getEndSz(),
                port5600v2.getPortNumber(), AccessLineTechnology.GPON.toString()).size() == 0);
    }

    @Test
    @TmsLink("DIGIHUB-164080")
    @Description("Force delete of ANCP Session")
    public void deleteAncpSession() {
        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
        osrSupportPage.validateUrl();
        osrSupportPage.deleteAncpSession(port5600v2);
    }

    @Test
    @TmsLink("DIGIHUB-158986")
    @Description("TOPAS migration")
    public void topasMigration() throws Exception {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioningWithDpu(true, AccessTransmissionMedium.COAX, 1000, 1000,
                oltDeviceCoax.getEndSz(), dpuDeviceCoax.getEndsz(), oltDeviceCoax.getSlotNumber(), oltDeviceCoax.getPortNumber());
        accessLineWs.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.OLT_BNG, AccessLineTechnology.GPON, AccessLineStatus.WALLED_GARDEN).get(0).getLineId());
        resourceOrderWsActivation = networkLineProfileManagementRobot.setResourceOrderData(resourceOrderWsActivation, accessLineWs, calId);
        networkLineProfileManagementRobot.createResourceOrderRequest(resourceOrderWsActivation.getResourceOrder(), accessLineWs);
        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
        osrSupportPage.validateUrl();
        osrSupportPage.migrateTopasAccessLines(port5800);
        assertTrue(accessLineRiRobot.getAccessLinesByLineId(accessLineWs.getLineId()).size() == 0);

    }

    @Test
    @TmsLink("DIGIHUB-164081")
    @Description("Force delete of ANCP IPSubnet")
    public void deleteANCPIPSubnet() {
        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
        osrSupportPage.validateUrl();
        osrSupportPage.deleteIpSubnet(port5600v2);
    }

    @Test
    @TmsLink("DIGIHUB-164082")
    @Description("Change Network Line Profiles for 1:64 migration")
    public void changeNetworkLineProfiles() throws Exception {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
        accessLine.setLineId(accessLineRiRobot.getAccessLinesByTypeV2(AccessLineProductionPlatform.OLT_BNG,
                        AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED, ProfileState.ACTIVE, ProfileState.ACTIVE)
                .get(0).getLineId());
        accessLineRiRobot.checkDefaultNetworkLineProfiles(accessLine, expectedDefaultNetworklineProfileFtth);
        wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(true);
        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
        osrSupportPage.validateUrl();
        osrSupportPage.changeNetworkLineProfiles("*", "GPON", "OLT_BNG");
        accessLineRiRobot.checkDefaultNetworkLineProfiles(accessLine, expectedDefaultNetworklineProfileFtthV2);
    }

    //    @Test
//    @TmsLink("DIGIHUB-137632")
//    @Description("Modify RMK EndpointId and AccessId")
//    public void modifyRmkEndpointID() throws Exception {
//        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
//        osrSupportPage.validateUrl();
//        osrSupportPage.modifyRmkEndpointID(port5600v2);
//    }


//
//    @Test
//    @TmsLink("DIGIHUB-XXXXX")
//    @Description("Access Lines cleanup")
//    public void accessLinesCleanup() throws Exception {
//        OsrSupportPage osrSupportPage = OsrSupportPage.openPage();
//        osrSupportPage.validateUrl();
//        osrSupportPage.cleanupAccessLines(port5600v2);
//    }
}
