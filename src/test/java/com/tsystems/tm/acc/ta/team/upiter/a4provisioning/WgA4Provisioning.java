package com.tsystems.tm.acc.ta.team.upiter.a4provisioning;

import com.tsystems.tm.acc.data.upiter.models.defaultnetworklineprofile.DefaultNetworkLineProfileCase;
import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
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
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_10_0.client.model.TpRefDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.*;

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
    private OltDevice a4OltDevice7kh5;
    private DpuDevice a4DpuDevice7kh6;
    private DpuDevice a4DpuDevice7kh7;
    private PortProvisioning a4PortProvisioning7kh0;
    private PortProvisioning a4PortProvisioning7kh1;
    private PortProvisioning a4PortProvisioning7kh4;
    private PortProvisioning a4PortProvisioning7kh5;
    private PortProvisioning a4PortProvisioning7kh6;
    private PortProvisioning a4PortProvisioning7kh7;
    private PortProvisioning a4PortDeprovisioning;
    private A4TerminationPoint a4TerminationPoint;
    private AccessLine a4AccessLine;
    private PortProvisioning a4Port;
    private DefaultNetworkLineProfile defaultNetworkLineProfileFtth;
    private DefaultNetworkLineProfile defaultNetworkLineProfileFttbTp;
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
        a4OltDevice7kh5 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.A4OltDevice7KH5);
        a4DpuDevice7kh6 = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.A4DpuDevice7KH6);
        a4DpuDevice7kh7 = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.A4DpuDevice7KH7);
        a4PortProvisioning7kh0 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH0);
        a4PortProvisioning7kh1 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH1);
        a4PortProvisioning7kh4 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH4);
        a4PortProvisioning7kh5 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH5);
        a4PortProvisioning7kh6 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH6);
        a4PortProvisioning7kh7 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4Port7KH7);
        a4PortDeprovisioning = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4PortForDeprovisioning);
        a4TerminationPoint = new A4TerminationPoint();
        a4AccessLine = new AccessLine();
        a4Port = new PortProvisioning();
        defaultNetworkLineProfileFtth = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFtth);
        defaultNetworkLineProfileFttbTp = context.getData().getDefaultNetworkLineProfileDataProvider().get(DefaultNetworkLineProfileCase.defaultNLProfileFttbTP);
    }

    @Test
    @TmsLink("DIGIHUB-53171")
    @Description("A4 FTTH port provisioning, tp_type = null")
    public void a4FtthProvisioningWithoutTpType() {
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
        accessLineRiRobot.checkDefaultNetworkLineProfiles(a4PortProvisioning7kh0, defaultNetworkLineProfileFtth, 1);
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh0);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh0);
        accessLineRiRobot.checkPhysicalResourceRefCountA4Ftth(a4PortProvisioning7kh0, 1);
        assertNull(accessLineRiRobot.getGponPorts(a4PortProvisioning7kh5).get(0).getAccessTransmissionMedium());
    }

    @Test(dependsOnMethods = "a4FtthProvisioningWithoutTpType")
    @TmsLink("DIGIHUB-124768")
    @Description("A4 FTTH AccessLine deprovisioning, last AccessLine on the port")
    public void a4FtthDeprovisioning() {
        a4AccessLine.setLineId(accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh0).get(0).getLineId());
        a4TerminationPoint.setUuid(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getNetworkServiceProfileReference().getTpRef());
        a4PortDeprovisioning.setEndSz(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getEndSz());
        a4PortDeprovisioning.setSlotNumber(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getSlotNumber());
        a4PortDeprovisioning.setPortNumber(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getPortNumber());
        assertTrue(accessLineRiRobot.getAccessLinesByPort(a4PortDeprovisioning).size() == 1, "There are > 1 AccessLines on the port");

        wgA4PreProvisioningRobot.startAccessLineDeprovisioning(a4AccessLine.getLineId());
        accessLineRiRobot.checkPhysicalResourceRefCountA4Ftth(a4PortDeprovisioning, 0);
        assertTrue(accessLineRiRobot.getAccessLinesByPort(a4PortDeprovisioning).size() == 0, "There are AccessLines left on the port");
        accessLineRiRobot.checkHomeIdsCount(a4PortDeprovisioning);
        accessLineRiRobot.checkLineIdsCount(a4PortDeprovisioning);
    }

    @Test
    @TmsLink("DIGIHUB-130985")
    @Description("A4 FTTH port provisioning, tp_type = PON_TP")
    public void a4FtthProvisioningWithTpType() {
        a4TerminationPoint.setUuid(UUID.randomUUID().toString());
        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4OltDevice7kh5.getEndsz())
                        .slotNumber(a4PortProvisioning7kh5.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh5.getPortNumber())
                        .klsId(a4OltDevice7kh5.getKlsId())
                        .tpRef(a4TerminationPoint.getUuid())
                        .partyId((long)a4OltDevice7kh5.getCompositePartyId())
                        .tpType(TpRefDto.TpTypeEnum.PON_TP)
        );

        accessLineRiRobot.checkA4LineParameters(a4PortProvisioning7kh5, a4TerminationPoint.getUuid());
        accessLineRiRobot.checkDefaultNetworkLineProfiles(a4PortProvisioning7kh5, defaultNetworkLineProfileFtth, 1);
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh5);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh5);
        accessLineRiRobot.checkPhysicalResourceRefCountA4Ftth(a4PortProvisioning7kh5, 1);
        assertNull(accessLineRiRobot.getGponPorts(a4PortProvisioning7kh5).get(0).getAccessTransmissionMedium());
    }

    @Test
    @TmsLink("DIGIHUB-87375")
    @Description("A4 FTTH AccessLine provisioning, a WALLED_GARDEN AccessLine with tpRef exists")
    public void a4FtthProvisioningSameTpRef() {
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
        accessLineRiRobot.checkPhysicalResourceRefCountA4Ftth(a4PortProvisioning7kh4, 1);

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
        accessLineRiRobot.checkPhysicalResourceRefCountA4Ftth(a4PortProvisioning7kh4, 1);
    }

    @Test
    @TmsLink("DIGIHUB-124771")
    @Description("A4 FTTH AccessLine deprovisioning, there are AccessLine left on the port")
    public void a4FtthDeprovisioningNotLastAccessLine() throws InterruptedException {
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
                        .partyId((long) a4OltDevice7kh1.getCompositePartyId())
        );

        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4OltDevice7kh1.getEndsz())
                        .slotNumber(a4PortProvisioning7kh1.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh1.getPortNumber())
                        .klsId(a4OltDevice7kh1.getKlsId())
                        .tpRef(terminationPoint2.getUuid())
                        .partyId((long) a4OltDevice7kh1.getCompositePartyId())
        );

        Thread.sleep(5000);

        int accessLinesBeforeDeprovisioning = accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh1).size();
        assertTrue(accessLinesBeforeDeprovisioning > 1, "There are <= 1 AccessLines on the port");

        a4AccessLine.setLineId(accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh1).get(0).getLineId());
        wgA4PreProvisioningRobot.startAccessLineDeprovisioning(a4AccessLine.getLineId());
        int accessLinesAfterDeprovisioning = accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh1).size();

        String tpUuid = accessLineRiRobot.getAccessLinesByPort(a4PortProvisioning7kh1).get(0).getNetworkServiceProfileReference().getTpRef();

        assertTrue(accessLinesAfterDeprovisioning > 0 && accessLinesAfterDeprovisioning == accessLinesBeforeDeprovisioning-1,
                "There are no AccessLines left on the port");
        accessLineRiRobot.checkA4LineParameters(a4PortProvisioning7kh1, tpUuid);
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh1);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh1);
        assertEquals(accessLineRiRobot.getLineIdStateByLineId(a4AccessLine.getLineId()), LineIdStatus.FREE);
        accessLineRiRobot.checkPhysicalResourceRefCountA4Ftth(a4PortProvisioning7kh1, 1);
    }

    @Test
    @TmsLink("DIGIHUB-124769")
    @Description("A4 FTTH AccessLine deprovisioning, AccessLine Status = Assigned")
    public void a4FtthDeprovisioningAssignedAccessLine() {
        a4AccessLine.setLineId(accessLineRiRobot.getAccessLinesByType(AccessLineProductionPlatform.A4, AccessLineTechnology.GPON, AccessLineStatus.ASSIGNED).get(0).getLineId());
        a4Port.setEndSz(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getEndSz());
        a4Port.setSlotNumber(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getSlotNumber());
        a4Port.setPortNumber(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).get(0).getReference().getPortNumber());
        a4Port.setLineIdPool(1);

        wgA4PreProvisioningRobot.startAccessLineDeprovisioning(a4AccessLine.getLineId());

        assertTrue(accessLineRiRobot.getAccessLinesByLineId(a4AccessLine.getLineId()).size() == 1);
        accessLineRiRobot.checkLineIdsCount(a4Port);
    }

    @Test
    @TmsLink("DIGIHUB-130986")
    @Description("A4 FTTB Provisioning")
    public void a4FttbProvisioning() {
        a4TerminationPoint.setUuid(UUID.randomUUID().toString());
        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4DpuDevice7kh6.getEndsz())
                        .slotNumber(a4PortProvisioning7kh6.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh6.getPortNumber())
                        .klsId(a4DpuDevice7kh6.getKlsId())
                        .tpRef(a4TerminationPoint.getUuid())
                        .partyId((long) a4DpuDevice7kh6.getCompositePartyId())
                        .tpType(TpRefDto.TpTypeEnum.G_FAST_TP)
        );

        accessLineRiRobot.checkA4FttbLineParameters(a4PortProvisioning7kh6, a4TerminationPoint.getUuid());
        //accessLineRiRobot.checkDefaultNetworkLineProfiles(a4PortProvisioning7kh6, defaultNetworkLineProfileFttbTp, 1);
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh6);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh6);
        accessLineRiRobot.checkPhysicalResourceRefCountA4Fttb(a4DpuDevice7kh6, a4PortProvisioning7kh6, 1);
        accessLineRiRobot.checkAccessTransmissionMedium(a4DpuDevice7kh6, 1);
    }

    @Test
    @TmsLink("DIGIHUB-130991")
    @Description("A4 FTTB AccessLine provisioning, a WALLED_GARDEN AccessLine with tpRef exists")
    public void a4FttbProvisioningSameTpRef() {
        a4TerminationPoint.setUuid(UUID.randomUUID().toString());

        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4DpuDevice7kh7.getEndsz())
                        .slotNumber(a4PortProvisioning7kh7.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh7.getPortNumber())
                        .klsId(a4DpuDevice7kh7.getKlsId())
                        .tpRef(a4TerminationPoint.getUuid())
                        .partyId((long)a4DpuDevice7kh7.getCompositePartyId())
                        .tpType(TpRefDto.TpTypeEnum.G_FAST_TP)
        );

        accessLineRiRobot.checkA4FttbLineParameters(a4PortProvisioning7kh7, a4TerminationPoint.getUuid());
        String lineId1 = accessLineRiRobot.getAccessLinesByGfastPort(a4PortProvisioning7kh7).get(0).getLineId();
        accessLineRiRobot.checkA4FttbLineParameters(a4PortProvisioning7kh7, a4TerminationPoint.getUuid());
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh7);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh7);
        accessLineRiRobot.checkPhysicalResourceRefCountA4Fttb(a4DpuDevice7kh7, a4PortProvisioning7kh7, 1);
        accessLineRiRobot.checkAccessTransmissionMedium(a4DpuDevice7kh7, 1);

        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4DpuDevice7kh7.getEndsz())
                        .slotNumber(a4PortProvisioning7kh7.getSlotNumber())
                        .portNumber(a4PortProvisioning7kh7.getPortNumber())
                        .klsId(a4DpuDevice7kh7.getKlsId())
                        .tpRef(a4TerminationPoint.getUuid())
                        .partyId((long)a4DpuDevice7kh7.getCompositePartyId())
                        .tpType(TpRefDto.TpTypeEnum.G_FAST_TP)
        );

        assertTrue(accessLineRiRobot.getAccessLinesByGfastPort(a4PortProvisioning7kh7).size() == 1);
        String lineId2 = accessLineRiRobot.getAccessLinesByGfastPort(a4PortProvisioning7kh7).get(0).getLineId();
        assertEquals(lineId1, lineId2);
        accessLineRiRobot.checkA4FttbLineParameters(a4PortProvisioning7kh7, a4TerminationPoint.getUuid());
        accessLineRiRobot.checkHomeIdsCount(a4PortProvisioning7kh7);
        accessLineRiRobot.checkLineIdsCount(a4PortProvisioning7kh7);
        accessLineRiRobot.checkPhysicalResourceRefCountA4Fttb(a4DpuDevice7kh7, a4PortProvisioning7kh7, 1);
        accessLineRiRobot.checkAccessTransmissionMedium(a4DpuDevice7kh7, 1);
    }

}
