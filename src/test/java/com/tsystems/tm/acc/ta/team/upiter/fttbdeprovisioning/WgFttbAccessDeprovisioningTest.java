package com.tsystems.tm.acc.ta.team.upiter.fttbdeprovisioning;

import com.tsystems.tm.acc.data.upiter.models.dpudevice.DpuDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
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
@ServiceLog(ACCESS_LINE_MANAGEMENT_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
@ServiceLog(APIGW_MS)

public class WgFttbAccessDeprovisioningTest extends BaseTest {

    private WgFttbAccessProvisioningRobot wgFttbAccessDeprovisioningRobot;
    private AccessLineRiRobot accessLineRiRobot;
    private DpuDevice dpuDeviceFttbDeprovisioning;
    private PortProvisioning oltDeviceForFttbDeprovisioning;

    @BeforeMethod
    public void prepareData() throws InterruptedException {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioning();
        accessLineRiRobot.fillDatabaseAddFttbLinesToOltDevice();
        Thread.sleep(1000);
    }

    @AfterMethod
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @BeforeClass
    public void init() {
        wgFttbAccessDeprovisioningRobot = new WgFttbAccessProvisioningRobot();
        accessLineRiRobot = new AccessLineRiRobot();

        UpiterTestContext context = UpiterTestContext.get();
        dpuDeviceFttbDeprovisioning = context.getData().getDpuDeviceDataProvider().get(DpuDeviceCase.dpuDeviceForFttbDeprovisioning);
        oltDeviceForFttbDeprovisioning = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.oltDeviceForFttbDeprovisioning);
    }

    @Test
    @TmsLink("DIGIHUB-77785")
    @Description("FTTB Deprovisioning for a Device")
    public void fttbDeviceDeprovisioningTest() {
        accessLineRiRobot.checkLineIdsCount(oltDeviceForFttbDeprovisioning);
        wgFttbAccessDeprovisioningRobot.startWgFttbAccessDeprovisioningForDevice(dpuDeviceFttbDeprovisioning.getEndsz());
        accessLineRiRobot.checkFttbLineParameters(oltDeviceForFttbDeprovisioning, 0);
    }
}
