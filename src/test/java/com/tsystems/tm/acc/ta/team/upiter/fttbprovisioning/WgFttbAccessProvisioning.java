package com.tsystems.tm.acc.ta.team.upiter.fttbprovisioning;

import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgFttbAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog({
        WG_FTTB_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
public class WgFttbAccessProvisioning extends GigabitTest {

    private WgFttbAccessProvisioningRobot wgFttbAccessProvisioningRobot;
    private AccessLineRiRobot accessLineRiRobot;
    private PortProvisioning oltDeviceFttbProvisioningTwistedPair;
    private PortProvisioning oltDeviceFttbProvisioningCoax;
    private DpuDevice dpuDeviceFttbProvisioningTwistedPair;
    private DpuDevice dpuDeviceFttbProvisioningCoax;
    private int numberOfAccessLinesForProvisioningTP;
    private int numberOfAccessLinesForProvisioningCoax;

    @BeforeClass
    public void init() throws InterruptedException {
        wgFttbAccessProvisioningRobot = new WgFttbAccessProvisioningRobot();
        accessLineRiRobot = new AccessLineRiRobot();
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        UpiterTestContext context = UpiterTestContext.get();
        dpuDeviceFttbProvisioningTwistedPair = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningTwistedPair);
        dpuDeviceFttbProvisioningCoax = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbProvisioningCoax);
        oltDeviceFttbProvisioningTwistedPair = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningTwistedPair);
        oltDeviceFttbProvisioningCoax = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioningCoax);
        numberOfAccessLinesForProvisioningTP = dpuDeviceFttbProvisioningTwistedPair.getNumberOfAccessLines();
        if (numberOfAccessLinesForProvisioningTP > 16) {
            numberOfAccessLinesForProvisioningTP = 16;
        }
        numberOfAccessLinesForProvisioningCoax = dpuDeviceFttbProvisioningCoax.getNumberOfAccessLines();
        if (numberOfAccessLinesForProvisioningCoax > 16) {
            numberOfAccessLinesForProvisioningCoax = 16;
        }
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-115527")
    @Description("FTTB Provisioning for a Device, Twisted Pair")
    public void fttbDeviceProvisioningTwistedPairTest() {
        accessLineRiRobot.fillDatabaseForDpuPreprovisioning(dpuDeviceFttbProvisioningTwistedPair, oltDeviceFttbProvisioningTwistedPair);
        accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioningTwistedPair);
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningTwistedPair.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningTwistedPair, numberOfAccessLinesForProvisioningTP);
        accessLineRiRobot.checkAccessTransmissionMedium(dpuDeviceFttbProvisioningTwistedPair, oltDeviceFttbProvisioningTwistedPair, numberOfAccessLinesForProvisioningTP);
    }

    @Test(dependsOnMethods = "fttbDeviceProvisioningTwistedPairTest", priority = 1)
    @TmsLink("DIGIHUB-115622")
    @Description("FTTB Deprovisioning for a Device, Twisted Pair")
    public void fttbDeviceDeprovisioningTwistedPairTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbProvisioningTwistedPair.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningTwistedPair, 0);
    }

    @Test(priority = 2)
    @TmsLink("DIGIHUB-115526")
    @Description("FTTB Provisioning for a Device, Coax")
    public void fttbDeviceProvisioningCoaxTest() {
        accessLineRiRobot.clearDatabase();
        accessLineRiRobot.fillDatabaseForDpuPreprovisioning(dpuDeviceFttbProvisioningCoax, oltDeviceFttbProvisioningCoax);
        accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioningCoax);
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioningCoax.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningCoax, numberOfAccessLinesForProvisioningCoax);
        accessLineRiRobot.checkAccessTransmissionMedium(dpuDeviceFttbProvisioningCoax, oltDeviceFttbProvisioningCoax, numberOfAccessLinesForProvisioningCoax);
    }

    @Test(dependsOnMethods = "fttbDeviceProvisioningCoaxTest", priority = 2)
    @TmsLink("DIGIHUB-115623")
    @Description("FTTB Deprovisioning for a Device, Coax")
    public void fttbDeviceDeprovisioningCoaxTest() {
        wgFttbAccessProvisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbProvisioningCoax.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioningCoax, 0);
    }
}
