package com.tsystems.tm.acc.ta.team.mercury.migration;

import com.tsystems.tm.acc.data.osr.models.ancpipsubnetdata.AncpIpSubnetDataCase;
import com.tsystems.tm.acc.data.osr.models.ancpsessiondata.AncpSessionDataCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.AncpIpSubnetData;
import com.tsystems.tm.acc.ta.data.osr.models.AncpSessionData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.FTTHMigrationRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.olt.discovery.v2_1_0.client.model.InventoryCompareResult;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Slf4j
@ServiceLog({ ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS })
public class FTTHMigrationTest extends GigabitTest {

    static final Long DISCOVERY_TIMEOUT = 10_000L;

    private OltDevice oltDevice;
    private AncpIpSubnetData ancpIpSubnetData;
    private AncpSessionData ancpSessionData;

    private FTTHMigrationRobot ftthMigrationRobot = new FTTHMigrationRobot();

    private WireMockMappingsContext mappingsContext;
    private WireMockMappingsContext mappingsContextCb;

    @BeforeMethod
    public void init() {

        OsrTestContext context = OsrTestContext.get();
        oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76GA_MA5600);
        ancpIpSubnetData = context.getData().getAncpIpSubnetDataDataProvider().get(AncpIpSubnetDataCase.ancpIpSubnet_49_8571_0_76GA_MA5600);
        ancpSessionData = context.getData().getAncpSessionDataDataProvider().get(AncpSessionDataCase.ancpSession_49_8571_0_76GA_MA5600);

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
    @TmsLink("DIGIHUB-100545")
    @Description("PUT FTTH1.7 Migration (device : MA5600T)")
    @Owner("DL-T-Magic.Mercury@telekom.de")
    public void ftthMigrationTest() {

        String uuid = UUID.randomUUID().toString();

        ftthMigrationRobot.deviceDiscoveryStartDiscoveryTask(oltDevice, uuid);
        ftthMigrationRobot.checkCallbackWiremock(uuid, DISCOVERY_TIMEOUT);
        ftthMigrationRobot.deviceDiscoveryGetDiscoveryStatusTask(oltDevice, uuid);
        InventoryCompareResult inventoryCompareResult = ftthMigrationRobot.deviceDiscoveryCreateDiscrepancyReportTask(oltDevice, uuid);
        ftthMigrationRobot.deviceDiscoveryApplyDiscrepancyToInventoryTask(inventoryCompareResult, uuid);
        Long oltDeviceId = ftthMigrationRobot.checkOltMigrationResult( oltDevice, false, null);

        ftthMigrationRobot.createEthernetLink(oltDevice);
        String ancpIpSubnetId = ftthMigrationRobot.createAncpIpSubnet(ancpIpSubnetData);
        ftthMigrationRobot.createAncpSession(ancpIpSubnetId, oltDevice, ancpSessionData);
        ftthMigrationRobot.patchDeviceLifeCycleState(oltDeviceId);
        ftthMigrationRobot.checkOltMigrationResult( oltDevice, true, ancpIpSubnetId);
    }
}
