package com.tsystems.tm.acc.ta.team.upiter.a4provisioning;

import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgA4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineProductionPlatform;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.AccessLineTechnology;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_25_0.client.model.LineIdStatus;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.TpRefDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@ServiceLog({WG_A4_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_PROFILE_CATALOG_MS,
        DECOUPLING_MS, GATEWAY_ROUTE_MS
        })

@Epic("WG A4 Provisioning")
public class WgA4Provisioning extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot;
    private WgA4PreProvisioningRobot wgA4PreProvisioningRobot;
    private OltDevice a4OltDevice7kh0;
    private OltDevice a4OltDevice7kh1;
    private OltDevice a4OltDevice7kh4;
    private PortProvisioning a4PortProvisioning7kh0;
    private PortProvisioning a4PortProvisioning7kh1;
    private PortProvisioning a4PortProvisioning7kh4;
    private PortProvisioning a4PortDeprovisioning;
    private A4TerminationPoint a4TerminationPoint;
    private AccessLine a4AccessLine;
    private PortProvisioning a4Port;
    private DefaultNetworkLineProfile defaultNetworkLineProfile;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot = new AccessLineRiRobot();
        wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();

        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);

        a4OltDevice7kh0 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.A4OltDevice7KH0);
        a4OltDevice7kh1 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.A4OltDevice7KH1);
        a4OltDevice7kh4 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.A4OltDevice7KH4);
        a4PortProvisioning7kh0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH0);
        a4PortProvisioning7kh1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH1);
        a4PortProvisioning7kh4 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH4);
        a4PortDeprovisioning = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4PortForDeprovisioning);
        a4TerminationPoint = new A4TerminationPoint();
        a4AccessLine = new AccessLine();
        a4Port = new PortProvisioning();
        defaultNetworkLineProfile = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
    }

    @Test
    @TmsLink("DIGIHUB-53171")
    @Description("A4 port provisioning")
    public void a4PortProvisioning() {
        a4TerminationPoint.setUuid(UUID.randomUUID().toString());
        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4OltDevice7kh0.getEndsz())
                        .slotNumber(a4PortProvisioning7kh0.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh0.getPortNumber())
                        .klsId(a4OltDevice7kh0.getKlsId())
                        .tpRef(a4TerminationPoint.getUuid())
                        .partyId((long)a4OltDevice7kh0.getCompositePartyId())
        );

        accessLineRiRobot.checkA4LineParameters(a4PortProvisioning7kh0, a4TerminationPoint.getUuid());
        accessLineRiRobot.checkDefaultNetworkLineProfiles(a4PortProvisioning7kh0, defaultNetworkLineProfile, 1);
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh0);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh0);
        accessLineRiRobot.checkPhysicalResourceRefCountA4(a4PortProvisioning7kh0, 1);
    }

    @Test(dependsOnMethods = "a4PortProvisioning")
    @TmsLink("DIGIHUB-124768")
    @Description("A4 AccessLine deprovisioning, last AccessLine on the port")
    public void a4PortDeprovisioning() {
        a4AccessLine.setLineId(accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh0).get(0).getLineId());
        a4TerminationPoint.setUuid(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getNetworkServiceProfileReference().getTpRef());
        a4PortDeprovisioning.setEndSz(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getEndSz());
        a4PortDeprovisioning.setSlotNumber(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getSlotNumber());
        a4PortDeprovisioning.setPortNumber(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getPortNumber());
        assertTrue(accessLineRiRobot.getAccessLinesByPort(a4PortDeprovisioning).size() == 1, "There are > 1 AccessLines on the port");

        wgA4PreProvisioningRobot.startAccessLineDeprovisioning(a4AccessLine.getLineId());
        accessLineRiRobot.checkPhysicalResourceRefCountA4(a4PortDeprovisioning, 0);
        assertTrue(accessLineRiRobot.getAccessLinesByPort(a4PortDeprovisioning).size() == 0, "There are AccessLines left on the port");
        accessLineRiRobot.checkHomeIdsCount(a4PortDeprovisioning);
        accessLineRiRobot.checkLineIdsCount(a4PortDeprovisioning);
    }

    @Test
    @TmsLink("DIGIHUB-87375")
    @Description("A4 AccessLine provisioning, A WALLED_GARDEN AccessLine with tpRef exists")
    public void a4PortProvisioningSameTpRef() {
        a4TerminationPoint.setUuid(UUID.randomUUID().toString());

        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4OltDevice7kh4.getEndsz())
                        .slotNumber(a4PortProvisioning7kh4.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh4.getPortNumber())
                        .klsId(a4OltDevice7kh4.getKlsId())
                        .tpRef(a4TerminationPoint.getUuid())
                        .partyId((long)a4OltDevice7kh4.getCompositePartyId())
        );

        accessLineRiRobot.checkA4LineParameters(a4PortProvisioning7kh4, a4TerminationPoint.getUuid());
        String lineId1 = accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh4).get(0).getLineId();
        accessLineRiRobot.checkA4LineParameters(a4PortProvisioning7kh4, a4TerminationPoint.getUuid());
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh4);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh4);
        accessLineRiRobot.checkPhysicalResourceRefCountA4(a4PortProvisioning7kh4, 1);

        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4OltDevice7kh4.getEndsz())
                        .slotNumber(a4PortProvisioning7kh4.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh4.getPortNumber())
                        .klsId(a4OltDevice7kh4.getKlsId())
                        .tpRef(a4TerminationPoint.getUuid())
                        .partyId((long)a4OltDevice7kh4.getCompositePartyId())
        );

        assertTrue(accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh4).size() == 1);
        String lineId2 = accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh4).get(0).getLineId();
        assertEquals(lineId1, lineId2);
        accessLineRiRobot.checkA4LineParameters(a4PortProvisioning7kh4, a4TerminationPoint.getUuid());
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh4);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh4);
        accessLineRiRobot.checkPhysicalResourceRefCountA4(a4PortProvisioning7kh4, 1);
    }

    @Test
    @TmsLink("DIGIHUB-124771")
    @Description("A4 AccessLine deprovisioning, there are AccessLine left on the port")
    public void a4PortDeprovisioningNotLastAccessLine() {
        A4TerminationPoint terminationPoint1 = new A4TerminationPoint();
        A4TerminationPoint terminationPoint2 = new A4TerminationPoint();
        terminationPoint1.setUuid(UUID.randomUUID().toString());
        terminationPoint2.setUuid(UUID.randomUUID().toString());

        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4OltDevice7kh1.getEndsz())
                        .slotNumber(a4PortProvisioning7kh1.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh1.getPortNumber())
                        .klsId(a4OltDevice7kh1.getKlsId())
                        .tpRef(terminationPoint1.getUuid())
                        .partyId((long)a4OltDevice7kh1.getCompositePartyId())
        );

        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4OltDevice7kh1.getEndsz())
                        .slotNumber(a4PortProvisioning7kh1.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh1.getPortNumber())
                        .klsId(a4OltDevice7kh1.getKlsId())
                        .tpRef(terminationPoint2.getUuid())
                        .partyId((long)a4OltDevice7kh1.getCompositePartyId())
        );

        assertTrue(accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh1).size() > 1, "There are <= 1 AccessLines on the port");
        a4AccessLine.setLineId(accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh1).get(0).getLineId());
        wgA4PreProvisioningRobot.startAccessLineDeprovisioning(a4AccessLine.getLineId());

        assertTrue(accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh1).size() > 0, "There are no AccessLines left on the port");
        accessLineRiRobot.checkA4LineParameters(a4PortProvisioning7kh1, terminationPoint2.getUuid());
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh1);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh1);
        assertEquals(accessLineRiRobot.getLineIdStateByLineId(a4AccessLine.getLineId()), LineIdStatus.FREE);
        accessLineRiRobot.checkPhysicalResourceRefCountA4(a4PortProvisioning7kh1, 1);
    }

    @Test
    @TmsLink("DIGIHUB-124769")
    @Description("A4 AccessLine deprovisioning, AccessLine Status = Assigned")
    public void a4PortDeprovisioningAssignedAccessLine() {
        a4AccessLine.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.A4, AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED).get(0).getLineId());
        a4Port.setEndSz(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getEndSz());
        a4Port.setSlotNumber(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getSlotNumber());
        a4Port.setPortNumber(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getPortNumber());
        a4Port.setLineIdPool(1);

        wgA4PreProvisioningRobot.startAccessLineDeprovisioning(a4AccessLine.getLineId());

        assertTrue(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).size() == 1);
        accessLineRiRobot.checkLineIdsCount(a4Port);
    }
}
