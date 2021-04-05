package com.tsystems.tm.acc.ta.team.mercury.a10nsp;

import com.tsystems.tm.acc.data.osr.models.a10nspcheckdata.A10nspCheckDataCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.A10nspCheckData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A10nspCheckRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Slf4j
@ServiceLog({A10NSP_INVENTORY_MS, OLT_RESOURCE_INVENTORY_MS})
@Epic("OS&R")
@Feature("Description a10nsp check LineId")
@TmsLink("DIGIHUB-54117") // This is the Jira id of TestSet
public class CheckLineIdTest extends GigabitTest {

    private A10nspCheckData checkLineIdA10nsp;
    private A10nspCheckData checkLineIdA10nspWrongLineId;
    private A10nspCheckData checkLineIdA10nspNotFound;

    private A10nspCheckRobot a10nspCheckRobot = new A10nspCheckRobot();

    @BeforeClass
    public void init() {

        // load test data
        checkLineIdA10nsp = OsrTestContext.get().getData()
                .getA10nspCheckDataDataProvider()
                .get(A10nspCheckDataCase.checkLineIdA10nsp);

        checkLineIdA10nspWrongLineId = OsrTestContext.get().getData()
                .getA10nspCheckDataDataProvider()
                .get(A10nspCheckDataCase.checkLineIdA10nspWrongLineId);

        checkLineIdA10nspNotFound = OsrTestContext.get().getData()
                .getA10nspCheckDataDataProvider()
                .get(A10nspCheckDataCase.checkLineIdA10nspNotFound);

        OltDevice oltDevice = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_179_76H1);

        // init test data
        a10nspCheckRobot.deleteDeviceInResourceInventory(checkLineIdA10nsp.getOltEndSz());
        a10nspCheckRobot.fillDeviceInResourceInventory(oltDevice);
    }

    @AfterClass
    public void clear() {
        a10nspCheckRobot.deleteDeviceInResourceInventory(checkLineIdA10nsp.getOltEndSz());
    }

    @Test(description = "DIGIHUB-54119 test carrierConnection was found")
    public void checkLineIdFound() {
        a10nspCheckRobot.checkLineIdTestFound(checkLineIdA10nsp);
    }

    @Test(description = "DIGIHUB-54120  test carrierConnection was not found")
    public void checkLineIdTestNotFound() {
        a10nspCheckRobot.checkLineIdTestNotFound(checkLineIdA10nspNotFound);
    }

    @Test(description = "DIGIHUB-54205 test invalid input parameter")
    public void checkLineIdTestWrongLine() {
        a10nspCheckRobot.checkLineIdTestWrongLineId(checkLineIdA10nspWrongLineId);
    }

}
