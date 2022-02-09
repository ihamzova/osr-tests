package com.tsystems.tm.acc.ta.team.mercury.rmk_at;


import com.tsystems.tm.acc.data.osr.models.equipmentbusinessref.EquipmentBusinessRefCase;
import com.tsystems.tm.acc.data.osr.models.oltuplinkbusinessreferencen.OltUplinkBusinessReferencenCase;
import com.tsystems.tm.acc.ta.data.osr.mappers.OltUplinkBusinessReferencenMapper;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef;
import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferencen;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.MpcSwitchRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.ChangeBngPort;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_UPLINK_MANAGEMENT_MS, OLT_RESOURCE_INVENTORY_MS})
@Epic("OS&R")
@Feature("Description MPC Switch: Bulk update in olt-uplink-management")
@TmsLink("DIGIHUB-128147") // This is the Jira id of TestSet
public class MpcSwitchTest extends GigabitTest {

    private OltUplinkBusinessReferencen defaultOltUplinkBusinessReferencen;
    private EquipmentBusinessRef secondOltEquipmentBusinessRef;
    private EquipmentBusinessRef thirdOltEquipmentBusinessRef;
    private MpcSwitchRobot mpcSwitchRobot = new MpcSwitchRobot();

    @BeforeMethod
    public void init() {

        defaultOltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkSuccess);

        secondOltEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.secondOltPortEquipmentBusinessRef);

        thirdOltEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.thirdOltPortEquipmentBusinessRef);

        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(thirdOltEquipmentBusinessRef.getEndSz());
    }

    @AfterClass
    public void teardown() {
        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(thirdOltEquipmentBusinessRef.getEndSz());
    }

    @Test(description = "DIGIHUB-127784 Check MPC Switch: happy case. BNG Port is free")
    public void changeBngPortBulkUplinkSuccess() {

        mpcSwitchRobot.createOltDeviceInResourceInventory(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.changeBngPortSuccess(OltUplinkBusinessReferencenMapper.getChangeBngPorts(defaultOltUplinkBusinessReferencen));
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());

        //if the request is repeated, the response should also be HTTP 200
        mpcSwitchRobot.changeBngPortSuccess(OltUplinkBusinessReferencenMapper.getChangeBngPorts(defaultOltUplinkBusinessReferencen));
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());
    }

    @Test(description = "DIGIHUB-127885 Check MPC Switch: happy case. Ring-Switch")
    public void changeBngPortBulkUplinkRingSwitchSuccess() {
        /*
                           OLT EndSz       BNG  source    target
        first uplink:   49/911/85/76H1 ====   ge-5/2/5 -> ge-7/8/7
        second uplink:  49/911/85/76H2 ====   ge-7/8/7 -> xe-0/0/1
        third uplink:   49/911/85/76H5 ====   xe-0/0/1 -> ge-5/2/5
        */

        // first uplink
        mpcSwitchRobot.createOltDeviceInResourceInventory(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        // second uplink
        OltUplinkBusinessReferencen secondOltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkRingSwitch1);

        mpcSwitchRobot.createOltDeviceInResourceInventory(secondOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                secondOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(secondOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                secondOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        // third uplink
        OltUplinkBusinessReferencen thirdOltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkRingSwitch2);

        mpcSwitchRobot.createOltDeviceInResourceInventory(thirdOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                thirdOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(thirdOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                thirdOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        // prepare request
        List<ChangeBngPort> changeBngPortList = OltUplinkBusinessReferencenMapper.getChangeBngPorts(defaultOltUplinkBusinessReferencen);
        changeBngPortList.add(OltUplinkBusinessReferencenMapper.getChangeBngPorts(secondOltUplinkBusinessReferencen).get(0));
        changeBngPortList.add(OltUplinkBusinessReferencenMapper.getChangeBngPorts(thirdOltUplinkBusinessReferencen).get(0));
        log.info("MPC Ring Switch changeBngPortList = {}", changeBngPortList);
        mpcSwitchRobot.changeBngPortSuccess(changeBngPortList);

        // check
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());
        mpcSwitchRobot.checkEquipmentBusinessRef(secondOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                secondOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());
        mpcSwitchRobot.checkEquipmentBusinessRef(thirdOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                thirdOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());

    }

    @Test(description = "DIGIHUB-128041 Check MPC Switch: happy case. different ports")
    public void changeBngPortBulkUplinkDifferentPortsSuccess() {
        OltUplinkBusinessReferencen adtranOltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkDifferentPorts1);

        mpcSwitchRobot.createAdtranOltDeviceInResourceInventory(adtranOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(adtranOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.changeBngPortSuccess(OltUplinkBusinessReferencenMapper.getChangeBngPorts(adtranOltUplinkBusinessReferencen));
        mpcSwitchRobot.checkEquipmentBusinessRef(adtranOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());
    }


    @Test(description = "DIGIHUB-127866 Check MPC Switch: unhappy case. bng port already in use")
    public void changeBngPortBulkUplinkError() {

        EquipmentBusinessRef secondBngEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.secondBngSourcePortEquipmentBusinessRef);

        OltUplinkBusinessReferencen oltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkSuccess);
        oltUplinkBusinessReferencen.setBngTargetPortEquipmentBusinessRef(secondBngEquipmentBusinessRef); // BNG target port is already in use

        mpcSwitchRobot.createOltDeviceInResourceInventory(secondOltEquipmentBusinessRef, secondBngEquipmentBusinessRef);
        mpcSwitchRobot.createOltDeviceInResourceInventory(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.changeBngPortError(oltUplinkBusinessReferencen);

        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
    }
}
