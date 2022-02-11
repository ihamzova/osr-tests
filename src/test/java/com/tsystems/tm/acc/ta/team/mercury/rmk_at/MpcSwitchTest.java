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
@TmsLink("DIGIHUB-138416") // This is the Jira id of TestSet
public class MpcSwitchTest extends GigabitTest {

    private OltUplinkBusinessReferencen defaultOltUplinkBusinessReferencen;
    private EquipmentBusinessRef secondOltEquipmentBusinessRef;
    private EquipmentBusinessRef thirdOltEquipmentBusinessRef;
    private EquipmentBusinessRef adtranOltEquipmentBusinessRef;
    private EquipmentBusinessRef dpuEquipmentBusinessRef;
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

        adtranOltEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.adtranOltPortEquipmentBusinessRef);

        dpuEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.adtranDpuPortEquipmentBusinessRef);

        mpcSwitchRobot.clearResourceInventoryDataBase(dpuEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(thirdOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(adtranOltEquipmentBusinessRef.getEndSz());
    }

    @AfterClass
    public void teardown() {
        mpcSwitchRobot.clearResourceInventoryDataBase(dpuEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(thirdOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(adtranOltEquipmentBusinessRef.getEndSz());
    }

    @Test(description = "DIGIHUB-138412 Check MPC Switch: happy case. BNG Port is free")
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

    @Test(description = "DIGIHUB-138490 Check MPC Switch: happy case. Ring-Switch")
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
        mpcSwitchRobot.changeBngPortSuccess(changeBngPortList);

        // check
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());
        mpcSwitchRobot.checkEquipmentBusinessRef(secondOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                secondOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());
        mpcSwitchRobot.checkEquipmentBusinessRef(thirdOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                thirdOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());

    }

    @Test(description = "DIGIHUB-138492 Check MPC Switch: happy case. different devices")
    public void changeBngPortBulkUplinkDifferentDevicesSuccess() {

        /*
                       OLT Type    EndSz              BNG  source    target
        first uplink:      ADTRAN  49/911/85/76H4 ====   ge-5/2/8 -> xe-0/0/1
        second uplink:     HUAWEI  49/911/85/76H1 ====   ge-5/2/5 -> ge-7/8/7
           DPU  49/911/85/76HC ====|
        */

        OltUplinkBusinessReferencen adtranOltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkDifferentPorts1);

        // Adtran OLT
        mpcSwitchRobot.createAdtranOltDeviceInResourceInventory(adtranOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(adtranOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        //  OLT with DPU
        mpcSwitchRobot.createOltDeviceInResourceInventory(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.createDpuDeviceInResourceInventory(dpuEquipmentBusinessRef.getEndSz(),
                defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkAncpSession(dpuEquipmentBusinessRef,
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        // prepare request
        List<ChangeBngPort> changeBngPortList = OltUplinkBusinessReferencenMapper.getChangeBngPorts(adtranOltUplinkBusinessReferencen);
        changeBngPortList.add(OltUplinkBusinessReferencenMapper.getChangeBngPorts(defaultOltUplinkBusinessReferencen).get(0));
        mpcSwitchRobot.changeBngPortSuccess(changeBngPortList);

        // check
        mpcSwitchRobot.checkEquipmentBusinessRef(adtranOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());

        mpcSwitchRobot.checkAncpSession(dpuEquipmentBusinessRef,
                defaultOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());

    }


    @Test(description = "DIGIHUB-138498 Check MPC Switch: unhappy case. bng port already in use")
    public void changeBngPortBulkUplinkError() {

        EquipmentBusinessRef secondBngEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.secondBngSourcePortEquipmentBusinessRef);

        OltUplinkBusinessReferencen localOltUplinkBusinessReferencen = new OltUplinkBusinessReferencen();
        localOltUplinkBusinessReferencen.setOltPortEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef());
        localOltUplinkBusinessReferencen.setBngSourcePortEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());
        localOltUplinkBusinessReferencen.setBngTargetPortEquipmentBusinessRef(secondBngEquipmentBusinessRef); // BNG target port is already in use

        mpcSwitchRobot.createOltDeviceInResourceInventory(secondOltEquipmentBusinessRef, secondBngEquipmentBusinessRef);
        mpcSwitchRobot.createOltDeviceInResourceInventory(localOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(secondOltEquipmentBusinessRef, secondBngEquipmentBusinessRef);

        mpcSwitchRobot.changeBngPortError(localOltUplinkBusinessReferencen);

        mpcSwitchRobot.checkEquipmentBusinessRef(secondOltEquipmentBusinessRef, secondBngEquipmentBusinessRef);
    }

    @Test(description = "DIGIHUB-138499 Check MPC Switch: unhappy case. bng switch")
    public void changeBngPortBulkBngSwitchError() {

        /*
                       OLT    EndSz       BNG       source            target
               uplink:  49/911/85/76H1 ====    49/911/85/43G1 --> 49/911/85/43G2
        */

        EquipmentBusinessRef newBngTargetPortEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.secondBngBngTargetPortEquipmentBusinessRef);

        OltUplinkBusinessReferencen localOltUplinkBusinessReferencen = new OltUplinkBusinessReferencen();
        localOltUplinkBusinessReferencen.setOltPortEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef());
        localOltUplinkBusinessReferencen.setBngSourcePortEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());
        localOltUplinkBusinessReferencen.setBngTargetPortEquipmentBusinessRef(newBngTargetPortEquipmentBusinessRef); // new BNG EndSz

        mpcSwitchRobot.createOltDeviceInResourceInventory(localOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.changeBngPortError(localOltUplinkBusinessReferencen);
    }

    @Test(description = "DIGIHUB-138500 Check MPC Switch: unhappy case. two links (on different OLTs with same bngs) are to be switched on same target port")
    public void changeBngPortBulkSameTargetPortError() {

        /*
                           OLT EndSz       BNG  source    target
        first uplink:   49/911/85/76H1 ====   ge-5/2/5 -> ge-7/8/7
        second uplink:  49/911/85/76H5 ====   xe-0/0/1 -> ge-7/8/7
        */
        // first uplink
        mpcSwitchRobot.createOltDeviceInResourceInventory(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        // second uplink
        OltUplinkBusinessReferencen oltUplinkRingSwitch2Referencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkRingSwitch2);

        OltUplinkBusinessReferencen localOltUplinkBusinessReferencen = new OltUplinkBusinessReferencen();
        localOltUplinkBusinessReferencen.setOltPortEquipmentBusinessRef(oltUplinkRingSwitch2Referencen.getOltPortEquipmentBusinessRef());
        localOltUplinkBusinessReferencen.setBngSourcePortEquipmentBusinessRef(oltUplinkRingSwitch2Referencen.getBngSourcePortEquipmentBusinessRef());
        // set second uplink to same target port
        localOltUplinkBusinessReferencen.setBngTargetPortEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getBngTargetPortEquipmentBusinessRef());

        mpcSwitchRobot.createOltDeviceInResourceInventory(localOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        // check before switch
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(localOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        // prepare request
        List<ChangeBngPort> changeBngPortList = OltUplinkBusinessReferencenMapper.getChangeBngPorts(defaultOltUplinkBusinessReferencen);
        changeBngPortList.add(OltUplinkBusinessReferencenMapper.getChangeBngPorts(localOltUplinkBusinessReferencen).get(0));
        mpcSwitchRobot.changeBngPortError(changeBngPortList);

        // check
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(localOltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());
    }
}