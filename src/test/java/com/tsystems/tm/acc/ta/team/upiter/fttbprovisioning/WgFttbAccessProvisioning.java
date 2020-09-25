package com.tsystems.tm.acc.ta.team.upiter.fttbprovisioning;

import com.tsystems.tm.acc.data.upiter.models.dpu.DpuCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgFttbAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog(WG_FTTB_ACCESS_PROVISIONING_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
public class WgFttbAccessProvisioning extends BaseTest {

    private WgFttbAccessProvisioningRobot wgFttbAccessProvisioningRobot;
    private AccessLineRiRobot accessLineRiRobot;
    private Dpu dpuDeviceFttbProvisioning;
    private PortProvisioning oltDeviceFttbProvisioning;
    private int numberOfAccessLinesForProvisioning;

    @BeforeMethod
    public void prepareData() throws InterruptedException {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForDpuPreprovisioning();
    }

    @AfterMethod
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @BeforeClass
    public void init() {
        wgFttbAccessProvisioningRobot = new WgFttbAccessProvisioningRobot();
        accessLineRiRobot = new AccessLineRiRobot();

        UpiterTestContext context = UpiterTestContext.get();
        dpuDeviceFttbProvisioning = context.getData().getDpuDataProvider().get(DpuCase.dpuDeviceForFttbProvisioning);
        oltDeviceFttbProvisioning = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbProvisioning);
        numberOfAccessLinesForProvisioning = oltDeviceFttbProvisioning.getAccessLinesWG();
    }

    @Test
    @TmsLink("DIGIHUB-69325")
    @Description("FTTB Provisioning for a Device")
    public void fttbDeviceProvisioningTest() {
        accessLineRiRobot.checkLineIdsCount(oltDeviceFttbProvisioning);
        wgFttbAccessProvisioningRobot.startWgFttbAccessProvisioningForDevice(dpuDeviceFttbProvisioning.getEndSz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceFttbProvisioning, numberOfAccessLinesForProvisioning);
    }
}
