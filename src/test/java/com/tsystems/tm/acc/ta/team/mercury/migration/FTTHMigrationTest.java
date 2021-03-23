package com.tsystems.tm.acc.ta.team.mercury.migration;

import com.tsystems.tm.acc.data.osr.models.ancpipsubnetdata.AncpIpSubnetDataCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.AncpIpSubnetData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.FTTHMigrationRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Slf4j
@ServiceLog("olt-resource-inventory")
@ServiceLog("olt-discovery")
@ServiceLog("ancp-configuration")
public class FTTHMigrationTest extends BaseTest {

    private OltDevice oltDevice;
    private AncpIpSubnetData ancpIpSubnetData;

    private FTTHMigrationRobot ftthMigrationRobot = new FTTHMigrationRobot();

    private WireMockMappingsContext mappingsContext;
    private WireMockMappingsContext mappingsContextCb;

    @BeforeMethod
    public void init() {

        OsrTestContext context = OsrTestContext.get();
        oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76GA_MA5600);
        ancpIpSubnetData = context.getData().getAncpIpSubnetDataDataProvider().get(AncpIpSubnetDataCase.ancpSession_49_8571_0_76GA_MA5600);

        mappingsContextCb = new WireMockMappingsContext(WireMockFactory.get(), "FTTHMigration");
        new MercuryWireMockMappingsContextBuilder(mappingsContextCb)
                .addDiscoveryCallbackReceiver()
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        mappingsContext = new OsrWireMockMappingsContextBuilder(WireMockFactory.get())
                .addSealMock(oltDevice)
                .addPslMock(oltDevice)
                .build()
                .publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        ftthMigrationRobot.clearResourceInventoryDataBase(oltDevice);
    }

    @AfterClass
    public void teardown() {
        mappingsContextCb.close();
        mappingsContextCb
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        //ftthMigrationRobot.clearResourceInventoryDataBase(oltDevice);
    }

    @Test(description = "PUT FTTH1.7 Migration (device : MA5600T)")
    @TmsLink("DIGIHUB-xxxx")
    @Description("PUT FTTH1.7 Migration (device : MA5600T)")
    @Owner("DL-T-Magic.Mercury@telekom.de")
    public void ftthMigrationTest() {
        String uuid = UUID.randomUUID().toString();

        ftthMigrationRobot.deviceDiscoveryStartDiscoveryTask(oltDevice, uuid);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ftthMigrationRobot.deviceDiscoveryGetDiscoveryStatusTask(oltDevice, uuid);
        ftthMigrationRobot.createEthernetLink(oltDevice);
        Long ancpIpSubnetId = ftthMigrationRobot.createAncpIpSubnet(ancpIpSubnetData);
        ftthMigrationRobot.createAncpSession(ancpIpSubnetId, oltDevice);

    }

}
