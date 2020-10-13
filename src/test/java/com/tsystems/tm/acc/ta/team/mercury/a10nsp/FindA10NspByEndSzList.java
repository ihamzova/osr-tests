package com.tsystems.tm.acc.ta.team.mercury.a10nsp;

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
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;


@Slf4j
@ServiceLog(A10NSP_INVENTORY_MS)
@ServiceLog(OLT_RESOURCE_INVENTORY_MS)
@Epic("OS&R")
@Feature("Description a10nsp check LineId")
@TmsLink("DIGIHUB-54117") // This is the Jira id of TestSet
public class FindA10NspByEndSzList extends BaseTest {

    private CheckLineIdA10nsp checkLineIdA10nsp;
    private CheckLineIdA10nsp checkLineIdA10nspNotFound;

    private A10nspCheckRobot a10nspCheckRobot = new A10nspCheckRobot();

    @BeforeClass
    public void init() {

        // load test data
        checkLineIdA10nsp = OsrTestContext.get().getData()
                .getCheckLineIdA10nspDataProvider()
                .get(CheckLineIdA10nspCase.findA10NspByEndSzListFound);

        checkLineIdA10nspNotFound = OsrTestContext.get().getData()
                .getCheckLineIdA10nspDataProvider()
                .get(CheckLineIdA10nspCase.findA10NspByEndSzListNotFound);

        OltDevice oltDevice = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_179_76H1);
        OltDevice oltDevice2 = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_179_76H5);

        // init test data
        a10nspCheckRobot.deleteDeviceInResourceInventory(checkLineIdA10nsp.getOltEndSz());
        a10nspCheckRobot.fillDeviceInResourceInventory(oltDevice);
        a10nspCheckRobot.fillDeviceInResourceInventory(oltDevice2);
    }

    @AfterClass
    public void clear() {
        OltDevice oltDevice = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_179_76H1);
        OltDevice oltDevice2 = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_179_76H5);
        a10nspCheckRobot.deleteDeviceInResourceInventory(oltDevice.getEndsz());
        a10nspCheckRobot.deleteDeviceInResourceInventory(oltDevice2.getEndsz());
    }

    @Test(description = "DIGIHUB-54119 test carrierConnection was found")
    public void findA10NspByEndSzListFound() {
        a10nspCheckRobot.findA10NspByEndSzListFound(checkLineIdA10nsp);
    }

    @Test(description = "DIGIHUB-xxxx ")
    public void findA10NspByEndSzListNotFound() {
        a10nspCheckRobot.findA10NspByEndSzListNotFound(checkLineIdA10nspNotFound);
    }

    @Test(description = "DIGIHUB-xxxx ")
    public void findA10NspByEndSzListEmpty() {
        a10nspCheckRobot.findA10NspByEndSzListNotFound(checkLineIdA10nspNotFound);
    }
}
