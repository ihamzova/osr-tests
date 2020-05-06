package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

@Epic("OS&R domain")
@Feature("Sending update calls to NEMO")
@TmsLink("DIGIHUB-xxxxx")
public class A4NemoUpdateTest extends ApiTest {
    private OsrTestContext osrTestContext = OsrTestContext.get();
    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();

    @Test(description = "DIGIHUB-xxxxx Trigger an update call to NEMO for a Network Element Group")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Trigger an update call to NEMO for a Network Element Group")
    public void testNemoUpdateWithNeg() {
        // GIVEN / Arrange
        A4NetworkElementGroup negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        a4ResourceInventoryRobot.createNetworkElementGroupNew(negData);

        // WHEN / Action
        a4NemoUpdaterRobot.triggerNemoUpdate(negData.getUuid());

        // THEN / Assert
        // No further assertions here besides return code of NEMO update call which is checked in the trigger robot above

        // AFTER / Clean-up
        a4ResourceInventoryRobot.deleteNetworkElementGroupNew(negData);
    }
}
