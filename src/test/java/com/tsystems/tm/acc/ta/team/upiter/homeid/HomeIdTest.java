package com.tsystems.tm.acc.ta.team.upiter.homeid;

import com.tsystems.tm.acc.ta.robot.osr.HomeIdManagementRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.home.id.management.v1_3_0.client.model.PoolHomeId;
import com.tsystems.tm.acc.tests.osr.home.id.management.v1_3_0.client.model.SingleHomeId;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Objects;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.HOME_ID_MANAGEMENT_MS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@ServiceLog(HOME_ID_MANAGEMENT_MS)

@Epic("HomeId Management")
public class HomeIdTest extends GigabitTest {

    private HomeIdManagementRobot homeIdManagementRobot;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        homeIdManagementRobot = new HomeIdManagementRobot();
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 1 Home Id")
    public void createSingleHomeId() {
        SingleHomeId response = homeIdManagementRobot.generateHomeid();
        assertNotNull(response);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 32 Home Ids")
    public void createPoolHomeIds() {
        PoolHomeId response = homeIdManagementRobot.generateBatchHomeids(32);
        assertEquals(Objects.requireNonNull(response.getHomeIds()).size(), 32);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Invalid number for Creation Pool of Home Ids")
    public void failCreatePoolHomeIdOver() {
        homeIdManagementRobot.generateBatchHomeidsNeg(33);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Invalid number for Creation Pool of Home Ids")
    public void failCreatePoolHomeIdMinus() {
        homeIdManagementRobot.generateBatchHomeidsNeg(-1);
    }
}
