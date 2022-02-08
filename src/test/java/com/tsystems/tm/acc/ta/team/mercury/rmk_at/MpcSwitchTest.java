package com.tsystems.tm.acc.ta.team.mercury.rmk_at;


import com.tsystems.tm.acc.data.osr.models.equipmentbusinessref.EquipmentBusinessRefCase;
import com.tsystems.tm.acc.data.osr.models.oltuplinkbusinessreferencen.OltUplinkBusinessReferencenCase;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef;
import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferencen;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.MpcSwitchRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_UPLINK_MANAGEMENT_MS, OLT_RESOURCE_INVENTORY_MS})
@Epic("OS&R")
@Feature("Description MPC Switch: Bulk update in olt-uplink-management")
@TmsLink("DIGIHUB-128147") // This is the Jira id of TestSet
public class MpcSwitchTest extends GigabitTest {

    private OltUplinkBusinessReferencen defaultOltUplinkBusinessReferencen;
    private EquipmentBusinessRef secondOltEquipmentBusinessRef;
    private MpcSwitchRobot mpcSwitchRobot = new MpcSwitchRobot();

    @BeforeMethod
    public void init() {

        defaultOltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkSuccess);

        secondOltEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.secondOltPortEquipmentBusinessRef);

        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
    }

    @AfterClass
    public void teardown() {
        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
    }

    @Test(description = "DIGIHUB-127784 Check MPC Switch: happy case. BNG Port is free")
    public void changeBngPortBulkUplinkSuccess() {

        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.fillDeviceInResourceInventory(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.changeBngPortSuccess(defaultOltUplinkBusinessReferencen);

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());

        //if the request is repeated, the response should also be HTTP 200
        mpcSwitchRobot.changeBngPortSuccess(defaultOltUplinkBusinessReferencen);

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());

        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());

    }

    @Test(description = "DIGIHUB-127866 Check MPC Switch: unhappy case. bng port already in use")
    public void changeBngPortBulkUplinkError() {

        EquipmentBusinessRef secondBngEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.secondBngSourcePortEquipmentBusinessRef);

        OltUplinkBusinessReferencen oltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkSuccess);
        oltUplinkBusinessReferencen.setBngTargetPortEquipmentBusinessRef(secondBngEquipmentBusinessRef); // BNG target port is already in use

        mpcSwitchRobot.fillDeviceInResourceInventory(secondOltEquipmentBusinessRef, secondBngEquipmentBusinessRef);
        mpcSwitchRobot.fillDeviceInResourceInventory(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.changeBngPortError(oltUplinkBusinessReferencen);

        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
    }
}
