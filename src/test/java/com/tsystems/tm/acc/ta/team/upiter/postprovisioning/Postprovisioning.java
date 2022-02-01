package com.tsystems.tm.acc.ta.team.upiter.postprovisioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.HomeIdDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;

@ServiceLog({
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        WG_ACCESS_PROVISIONING_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})

@Epic("Postprovisioning")
public class Postprovisioning extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    private AccessLine accessLine32;
    private AccessLine accessLine64;
    private PortProvisioning portForPostprovisioning32;
    private PortProvisioning portForPostprovisioning64;

    private int numberOfCreatedLines;
    private int numberOfWgLinesAfterPortprovisioning;

    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        accessLineRiRobot.clearDatabase();

        portForPostprovisioning32 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portForPostProvisioning32);
        portForPostprovisioning64 = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portForPostProvisioning64);
        accessLine32 = context.getData().getAccessLineDataProvider().get(AccessLineCase.assignAccessLines32);
        accessLine64 = context.getData().getAccessLineDataProvider().get(AccessLineCase.assignAccessLines64);

        numberOfCreatedLines = 4;
        numberOfWgLinesAfterPortprovisioning = 5;
    }

    @Test
    @TmsLink("DIGIHUB-136271")
    @Description("OLT MA5600, postprovisioning up to 32 AccessLines + onDemand")
    public void postProvisioningTo33Test() {
        wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(false);
        //precondition
        wgAccessProvisioningRobot.startPortProvisioning(portForPostprovisioning32);//create 16 wg access lines
        accessLineRiRobot.checkFtthPortParameters(portForPostprovisioning32);
        wgAccessProvisioningRobot.prepareForPostprovisioning(14, portForPostprovisioning32, getHomeIdFromAccessLine(accessLine32)); //13 assigned lines

//        assertEquals(accessLineRiRobot.getAccessLinesByPort(portForPostprovisioning32).size(),
//                portForPostprovisioning32.getAccessLinesCount().intValue());

        //1 trigger postprovisioning
        wgAccessProvisioningRobot.prepareForPostprovisioning(1, portForPostprovisioning32, getHomeIdFromAccessLine(accessLine32)); //13 + 1 assigned lines

        portForPostprovisioning32.setAccessLinesCount(portForPostprovisioning32.getAccessLinesCount() + numberOfCreatedLines);
        portForPostprovisioning32.setAccessLinesWG(numberOfWgLinesAfterPortprovisioning);

        accessLineRiRobot.checkFtthPortParameters(portForPostprovisioning32);
        accessLineRiRobot.checkPortParametersForAssignedLines(portForPostprovisioning32);

        // cycle for postprovisioning up to 32 AccessLines
        for (int i = portForPostprovisioning32.getAccessLinesCount(); i < 32; i = i + numberOfCreatedLines) {
            wgAccessProvisioningRobot.prepareForPostprovisioning(3, portForPostprovisioning32, getHomeIdFromAccessLine(accessLine32));

//            assertEquals(accessLineRiRobot.getAccessLinesByPort(portForPostprovisioning32).size(),
//                    portForPostprovisioning32.getAccessLinesCount().intValue());

            //1 to trigger postprovisioning
            wgAccessProvisioningRobot.prepareForPostprovisioning(1, portForPostprovisioning32, getHomeIdFromAccessLine(accessLine32));
            portForPostprovisioning32.setAccessLinesCount(portForPostprovisioning32.getAccessLinesCount() + numberOfCreatedLines);

            accessLineRiRobot.checkFtthPortParameters(portForPostprovisioning32);
            accessLineRiRobot.checkPortParametersForAssignedLines(portForPostprovisioning32);
        }

        wgAccessProvisioningRobot.prepareForPostprovisioning(5, portForPostprovisioning32, getHomeIdFromAccessLine(accessLine32)); // 28 + 6 assigned lines

//        assertEquals(accessLineRiRobot.getAccessLinesByPort(portForPostprovisioning32).size(),
//                portForPostprovisioning32.getAccessLinesCount().intValue());

        wgAccessProvisioningRobot.prepareForPostprovisioning(1, portForPostprovisioning32, getHomeIdFromAccessLine(accessLine32)); //33 wg line creation
        portForPostprovisioning32.setAccessLinesCount(portForPostprovisioning32.getAccessLinesCount() + 1);
        portForPostprovisioning32.setLineIdPool(portForPostprovisioning32.getAccessLinesCount());
        portForPostprovisioning32.setAccessLinesWG(0);

        accessLineRiRobot.checkFtthPortParameters(portForPostprovisioning32);
        accessLineRiRobot.checkPortParametersForAssignedLines(portForPostprovisioning32);
    }

    @Test
    @TmsLink("DIGIHUB-136260")
    @Description("OLT MA5600, postprovisioning up to 64 AccessLines + onDemand")
    public void postProvisioningTo65Test() {
        wgAccessProvisioningRobot.changeFeatureToogleEnable64PonSplittingState(true);
        //precondition
        wgAccessProvisioningRobot.startPortProvisioning(portForPostprovisioning64);//create 16 wg access lines
        accessLineRiRobot.checkFtthPortParameters(portForPostprovisioning64);
        wgAccessProvisioningRobot.prepareForPostprovisioning(14, portForPostprovisioning64, getHomeIdFromAccessLine(accessLine64)); //13 assigned lines

//        assertEquals(accessLineRiRobot.getAccessLinesByPort(portForPostprovisioning64).size(),
//                portForPostprovisioning64.getAccessLinesCount().intValue());

        //1 trigger postprovisioning
        wgAccessProvisioningRobot.prepareForPostprovisioning(1, portForPostprovisioning64, getHomeIdFromAccessLine(accessLine64)); //13 + 1 assigned lines

        portForPostprovisioning64.setAccessLinesCount(portForPostprovisioning64.getAccessLinesCount() + numberOfCreatedLines);
        portForPostprovisioning64.setAccessLinesWG(numberOfWgLinesAfterPortprovisioning);

        accessLineRiRobot.checkFtthPortParameters(portForPostprovisioning64);
        accessLineRiRobot.checkPortParametersForAssignedLines(portForPostprovisioning64);

        // cycle for postprovisioning up to 64 AccessLines
        for (int i = portForPostprovisioning64.getAccessLinesCount(); i < 64; i = i + numberOfCreatedLines) {
            wgAccessProvisioningRobot.prepareForPostprovisioning(3, portForPostprovisioning64, getHomeIdFromAccessLine(accessLine64));

//            assertEquals(accessLineRiRobot.getAccessLinesByPort(portForPostprovisioning64).size(),
//                    portForPostprovisioning64.getAccessLinesCount().intValue());

            //1 to trigger postprovisioning
            wgAccessProvisioningRobot.prepareForPostprovisioning(1, portForPostprovisioning64, getHomeIdFromAccessLine(accessLine64));
            portForPostprovisioning64.setAccessLinesCount(portForPostprovisioning64.getAccessLinesCount() + numberOfCreatedLines);

            if (portForPostprovisioning64.getAccessLinesCount() > 32) {
                portForPostprovisioning64.setLineIdPool(portForPostprovisioning64.getAccessLinesCount());
            }

            accessLineRiRobot.checkFtthPortParameters(portForPostprovisioning64);
            accessLineRiRobot.checkPortParametersForAssignedLines(portForPostprovisioning64);
        }

        wgAccessProvisioningRobot.prepareForPostprovisioning(5, portForPostprovisioning64, getHomeIdFromAccessLine(accessLine64));  //58 + 6 assigned lines

//        assertEquals(accessLineRiRobot.getAccessLinesByPort(portForPostprovisioning64).size(),
//                portForPostprovisioning64.getAccessLinesCount().intValue());

        wgAccessProvisioningRobot.prepareForPostprovisioning(1, portForPostprovisioning64, getHomeIdFromAccessLine(accessLine64)); //65 wg line creation
        portForPostprovisioning64.setAccessLinesCount(portForPostprovisioning64.getAccessLinesCount() + 1);
        portForPostprovisioning64.setLineIdPool(portForPostprovisioning64.getAccessLinesCount());
        portForPostprovisioning64.setAccessLinesWG(0);

        accessLineRiRobot.checkFtthPortParameters(portForPostprovisioning64);
        accessLineRiRobot.checkPortParametersForAssignedLines(portForPostprovisioning64);
    }

    private HomeIdDto getHomeIdFromAccessLine(AccessLine accessLine) {
        accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
        return new HomeIdDto().homeId(accessLine.getHomeId());
    }
}