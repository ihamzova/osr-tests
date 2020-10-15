package com.tsystems.tm.acc.ta.domain.a10nsp;

import com.tsystems.tm.acc.data.osr.models.checklineida10nsp.CheckLineIdA10nspCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.CheckLineIdA10nsp;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A10nspCheckRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@ServiceLog(A10NSP_INVENTORY_MS)
@ServiceLog(OLT_RESOURCE_INVENTORY_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@Epic("OS&R")
@Feature("Description Check if an a10nsp exists for a given LineId")
@TmsLink("DIGIHUB-54117") // This is the Jira id of TestSet
public class CheckLineIdTest extends BaseTest {

    private CheckLineIdA10nsp checkLineIdA10nsp;
    private CheckLineIdA10nsp checkLineIdA10nspNotFound;

    private A10nspCheckRobot a10nspCheckRobot = new A10nspCheckRobot();
    OltDevice oltDevice;

    @BeforeClass
    public void init() {

        // load test data
        checkLineIdA10nsp = OsrTestContext.get().getData()
                .getCheckLineIdA10nspDataProvider()
                .get(CheckLineIdA10nspCase.checkLineIdA10nsp);

        checkLineIdA10nspNotFound = OsrTestContext.get().getData()
                .getCheckLineIdA10nspDataProvider()
                .get(CheckLineIdA10nspCase.checkLineIdA10nspNotFound);

         oltDevice = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_179_76H1);

        // init test data
        a10nspCheckRobot.restoreOsrDbState();
        a10nspCheckRobot.prepareAceessLineResourceInventoryDataBase();
        a10nspCheckRobot.deleteDeviceInResourceInventory(checkLineIdA10nsp.getOltEndSz());
        a10nspCheckRobot.fillDeviceInResourceInventory(oltDevice);
    }

    @AfterClass
    public void clear() {
        a10nspCheckRobot.restoreOsrDbState();
        a10nspCheckRobot.deleteDeviceInResourceInventory(oltDevice.getEndsz());
    }

    @Test(description = "DIGIHUB-54119 test carrierConnection was found")
    public void CheckLineId() {
        a10nspCheckRobot.checkLineIdTestFound(checkLineIdA10nsp);
    }

    @Test(description = "DIGIHUB-54120  test carrierConnection was not found")
    public void CheckLineIdTestNotFound() {
        a10nspCheckRobot.checkLineIdTestNotFound(checkLineIdA10nspNotFound);
    }
}
