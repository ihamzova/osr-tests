package com.tsystems.tm.acc.ta.team.upiter.a4provisioning;

import com.tsystems.tm.acc.data.upiter.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.upiter.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.WgA4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_6_0.client.model.TpRefDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.GATEWAY_ROUTE_MS;

@ServiceLog({WG_A4_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        DECOUPLING_MS, GATEWAY_ROUTE_MS
        })

public class WgA4Provisioning extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot;
    private WgA4PreProvisioningRobot wgA4PreProvisioningRobot;
    private OltDevice a4OltDevice;
    private PortProvisioning a4Port;
    private A4TerminationPoint a4TerminationPoint;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeMethod
    public void prepareData() {
        accessLineRiRobot.clearDatabase();
    }

    @AfterMethod
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();
        a4OltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.A4OltDevice);
        a4Port = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4PortForProvisioning);
        a4TerminationPoint = context.getData().getA4TerminationPointDataProvider().get(A4TerminationPointCase.A4TerminationPoint);
    }

    @Test
    @TmsLink("DIGIHUB-53171")
    @Description("A4 port provisioning")
    public void a4PortProvisioning() {
        wgA4PreProvisioningRobot.startPreProvisioning(
                new TpRefDto()
                        .endSz(a4OltDevice.getEndsz())
                        .slotNumber(a4Port.getSlotNumber())
                        .portNumber(a4Port.getPortNumber())
                        .klsId(a4OltDevice.getKlsId())
                        .tpRef(a4TerminationPoint.getUuid())
                        .partyId((long)a4OltDevice.getCompositePartyId())
        );

        accessLineRiRobot.checkA4LineParameters(a4Port, a4TerminationPoint.getUuid());
        accessLineRiRobot.checkHomeIdsCount(a4Port);
        accessLineRiRobot.checkLineIdsCount(a4Port);
    }
}
