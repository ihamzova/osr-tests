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
@TmsLink("DIGIHUB-81608") // This is the Jira id of TestSet
public class FindA10NspByEndSzList extends GigabitTest {

    private A10nspCheckData checkLineIdA10nsp;
    private A10nspCheckData checkLineIdA10nspNotFound;
    private A10nspCheckData checkLineIdA10nspByEndSzListEmpty;

    private A10nspCheckRobot a10nspCheckRobot = new A10nspCheckRobot();
    OltDevice oltDevice;
    OltDevice oltDevice2;

    @BeforeClass
    public void init() {

        // load test data
        checkLineIdA10nsp = OsrTestContext.get().getData()
                .getA10nspCheckDataDataProvider()
                .get(A10nspCheckDataCase.findA10NspByEndSzListFound);

        checkLineIdA10nspNotFound = OsrTestContext.get().getData()
                .getA10nspCheckDataDataProvider()
                .get(A10nspCheckDataCase.findA10NspByEndSzListNotFound);

        checkLineIdA10nspByEndSzListEmpty = OsrTestContext.get().getData()
                .getA10nspCheckDataDataProvider()
                .get(A10nspCheckDataCase.findA10NspByEndSzListEmpty);

        oltDevice = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_179_76H1);
        oltDevice2 = OsrTestContext.get().getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_179_76H5);

        // delete device should device still be present
        a10nspCheckRobot.deleteDeviceInResourceInventory(oltDevice.getEndsz());
        a10nspCheckRobot.deleteDeviceInResourceInventory(oltDevice2.getEndsz());

        // init test data
        a10nspCheckRobot.deleteDeviceInResourceInventory(checkLineIdA10nsp.getOltEndSz());
        a10nspCheckRobot.fillDeviceInResourceInventory(oltDevice);
        a10nspCheckRobot.fillDeviceInResourceInventory(oltDevice2);
    }

    @AfterClass
    public void clear() {
        a10nspCheckRobot.deleteDeviceInResourceInventory(oltDevice.getEndsz());
        a10nspCheckRobot.deleteDeviceInResourceInventory(oltDevice2.getEndsz());
    }

    @Test(description = "DIGIHUB-81715 Find A10Nsp By EndSz List. Found")
    public void findA10NspByEndSzListFound() {
        a10nspCheckRobot.findA10NspByEndSzListFound(checkLineIdA10nsp);
    }

    @Test(description = "DIGIHUB-81609 Find A10Nsp By EndSz List. NotFound")
    public void findA10NspByEndSzListNotFound() {
        a10nspCheckRobot.findA10NspByEndSzListNotFound(checkLineIdA10nspNotFound);
    }

    @Test(description = "DIGIHUB-81611 Find A10Nsp By EndSz List. Empty list is returned")
    public void findA10NspByEndSzListEmpty() {
        a10nspCheckRobot.findA10NspByEndSzListEmpty(checkLineIdA10nspByEndSzListEmpty);
    }
}
