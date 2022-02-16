package com.tsystems.tm.acc.ta.team.mercury.rmk_at;


import com.tsystems.tm.acc.data.osr.models.equipmentbusinessref.EquipmentBusinessRefCase;
import com.tsystems.tm.acc.data.osr.models.oltuplinkbusinessreferences.OltUplinkBusinessReferencesCase;
import com.tsystems.tm.acc.ta.data.osr.mappers.OltUplinkBusinessReferencesMapper;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef;
import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferences;
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

    private OltUplinkBusinessReferences defaultOltUplinkBusinessReferences;
    private EquipmentBusinessRef secondOltEquipmentBusinessRef;
    private EquipmentBusinessRef thirdOltEquipmentBusinessRef;
    private EquipmentBusinessRef adtranOltEquipmentBusinessRef;
    private EquipmentBusinessRef dpuEquipmentBusinessRef;
    private MpcSwitchRobot mpcSwitchRobot = new MpcSwitchRobot();

    @BeforeMethod
    public void init() {

        defaultOltUplinkBusinessReferences = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencesDataProvider()
                .get(OltUplinkBusinessReferencesCase.OltUplinkSuccess);

        secondOltEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.secondOltPortEquipmentBusinessRef);

        thirdOltEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.thirdOltPortEquipmentBusinessRef);

        adtranOltEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.adtranOltPortEquipmentBusinessRef);

        dpuEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.adtranDpuPortEquipmentBusinessRef);

        mpcSwitchRobot.clearResourceInventoryDataBase(dpuEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(thirdOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(adtranOltEquipmentBusinessRef.getEndSz());
    }

    @AfterClass
    public void teardown() {
        mpcSwitchRobot.clearResourceInventoryDataBase(dpuEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef().getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(secondOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(thirdOltEquipmentBusinessRef.getEndSz());
        mpcSwitchRobot.clearResourceInventoryDataBase(adtranOltEquipmentBusinessRef.getEndSz());
    }

    @Test(description = "DIGIHUB-138412 Check MPC Switch: happy case. BNG Port is free")
    public void changeBngPortBulkUplinkSuccess() {
        /*
                           OLT EndSz       BNG  source    target
              uplink:   49/911/85/76H1 ====   ge-5/2/5 -> ge-7/8/7
        */

        mpcSwitchRobot.createOltDeviceInResourceInventory(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.changeBngPortSuccess(OltUplinkBusinessReferencesMapper.getChangeBngPorts(defaultOltUplinkBusinessReferences));
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef());

        //if the request is repeated, the response should also be HTTP 200
        mpcSwitchRobot.changeBngPortSuccess(OltUplinkBusinessReferencesMapper.getChangeBngPorts(defaultOltUplinkBusinessReferences));
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef());
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
        mpcSwitchRobot.createOltDeviceInResourceInventory(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        // second uplink
        OltUplinkBusinessReferences secondOltUplinkBusinessReferences = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencesDataProvider()
                .get(OltUplinkBusinessReferencesCase.OltUplinkRingSwitch1);

        mpcSwitchRobot.createOltDeviceInResourceInventory(secondOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                secondOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(secondOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                secondOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        // third uplink
        OltUplinkBusinessReferences thirdOltUplinkBusinessReferences = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencesDataProvider()
                .get(OltUplinkBusinessReferencesCase.OltUplinkRingSwitch2);

        mpcSwitchRobot.createOltDeviceInResourceInventory(thirdOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                thirdOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(thirdOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                thirdOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        // prepare request
        List<ChangeBngPort> changeBngPortList = OltUplinkBusinessReferencesMapper.getChangeBngPorts(defaultOltUplinkBusinessReferences);
        changeBngPortList.add(OltUplinkBusinessReferencesMapper.getChangeBngPorts(secondOltUplinkBusinessReferences).get(0));
        changeBngPortList.add(OltUplinkBusinessReferencesMapper.getChangeBngPorts(thirdOltUplinkBusinessReferences).get(0));
        mpcSwitchRobot.changeBngPortSuccess(changeBngPortList);

        // check
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef());
        mpcSwitchRobot.checkEquipmentBusinessRef(secondOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                secondOltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef());
        mpcSwitchRobot.checkEquipmentBusinessRef(thirdOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                thirdOltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef());

    }

    @Test(description = "DIGIHUB-138492 Check MPC Switch: happy case. different devices")
    public void changeBngPortBulkUplinkDifferentDevicesSuccess() {

        /*
                       OLT Type    EndSz              BNG  source    target
        first uplink:      ADTRAN  49/911/85/76H4 ====   ge-5/2/8 -> xe-0/0/1
        second uplink:     HUAWEI  49/911/85/76H1 ====   ge-5/2/5 -> ge-7/8/7
           DPU  49/911/85/76G2 ====|
        */

        OltUplinkBusinessReferences adtranOltUplinkBusinessReferences = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencesDataProvider()
                .get(OltUplinkBusinessReferencesCase.OltUplinkDifferentPorts1);

        // Adtran OLT
        mpcSwitchRobot.createAdtranOltDeviceInResourceInventory(adtranOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(adtranOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        //  OLT with DPU
        mpcSwitchRobot.createOltDeviceInResourceInventory(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.createDpuDeviceInResourceInventory(dpuEquipmentBusinessRef.getEndSz(),
                defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkAncpSession(dpuEquipmentBusinessRef,
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        // prepare request
        List<ChangeBngPort> changeBngPortList = OltUplinkBusinessReferencesMapper.getChangeBngPorts(adtranOltUplinkBusinessReferences);
        changeBngPortList.add(OltUplinkBusinessReferencesMapper.getChangeBngPorts(defaultOltUplinkBusinessReferences).get(0));
        mpcSwitchRobot.changeBngPortSuccess(changeBngPortList);

        // check
        mpcSwitchRobot.checkEquipmentBusinessRef(adtranOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                adtranOltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef());

        mpcSwitchRobot.checkAncpSession(dpuEquipmentBusinessRef,
                defaultOltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef());

    }


    @Test(description = "DIGIHUB-138498 Check MPC Switch: unhappy case. bng port already in use")
    public void changeBngPortBulkUplinkError() {

        /*
        EquipmentBusinessRef  === OLT EndSz ===  BNG source port BNG target port
               first uplink:  49/911/85/76H2      ge-5/2/8
              second uplink:  49/911/85/76H1      ge-5/2/5    ->   ge-5/2/8
         */

        EquipmentBusinessRef secondBngEquipmentBusinessRef = OsrTestContext.get().getData()
                .getEquipmentBusinessRefDataProvider().get(EquipmentBusinessRefCase.secondBngSourcePortEquipmentBusinessRef);

        OltUplinkBusinessReferences localOltUplinkBusinessReferences = new OltUplinkBusinessReferences();
        localOltUplinkBusinessReferences.setOltPortEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef());
        localOltUplinkBusinessReferences.setBngSourcePortEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());
        localOltUplinkBusinessReferences.setBngTargetPortEquipmentBusinessRef(secondBngEquipmentBusinessRef); // BNG target port is already in use

        mpcSwitchRobot.createOltDeviceInResourceInventory(secondOltEquipmentBusinessRef, secondBngEquipmentBusinessRef);
        mpcSwitchRobot.createOltDeviceInResourceInventory(localOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(secondOltEquipmentBusinessRef, secondBngEquipmentBusinessRef);

        mpcSwitchRobot.changeBngPortError(localOltUplinkBusinessReferences);

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

        OltUplinkBusinessReferences localOltUplinkBusinessReferences = new OltUplinkBusinessReferences();
        localOltUplinkBusinessReferences.setOltPortEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef());
        localOltUplinkBusinessReferences.setBngSourcePortEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());
        localOltUplinkBusinessReferences.setBngTargetPortEquipmentBusinessRef(newBngTargetPortEquipmentBusinessRef); // new BNG EndSz

        mpcSwitchRobot.createOltDeviceInResourceInventory(localOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.changeBngPortError(localOltUplinkBusinessReferences);
    }

    @Test(description = "DIGIHUB-138500 Check MPC Switch: unhappy case. two links (on different OLTs with same bngs) are to be switched on same target port")
    public void changeBngPortBulkSameTargetPortError() {

        /*
                           OLT EndSz       BNG  source    target
        first uplink:   49/911/85/76H1 ====   ge-5/2/5 -> ge-7/8/7
        second uplink:  49/911/85/76H5 ====   xe-0/0/1 -> ge-7/8/7
        */
        // first uplink
        mpcSwitchRobot.createOltDeviceInResourceInventory(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        // second uplink
        OltUplinkBusinessReferences oltUplinkRingSwitch2References = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencesDataProvider()
                .get(OltUplinkBusinessReferencesCase.OltUplinkRingSwitch2);

        OltUplinkBusinessReferences localOltUplinkBusinessReferences = new OltUplinkBusinessReferences();
        localOltUplinkBusinessReferences.setOltPortEquipmentBusinessRef(oltUplinkRingSwitch2References.getOltPortEquipmentBusinessRef());
        localOltUplinkBusinessReferences.setBngSourcePortEquipmentBusinessRef(oltUplinkRingSwitch2References.getBngSourcePortEquipmentBusinessRef());
        // set second uplink to same target port
        localOltUplinkBusinessReferences.setBngTargetPortEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getBngTargetPortEquipmentBusinessRef());

        mpcSwitchRobot.createOltDeviceInResourceInventory(localOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        // check before switch
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(localOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        // prepare request
        List<ChangeBngPort> changeBngPortList = OltUplinkBusinessReferencesMapper.getChangeBngPorts(defaultOltUplinkBusinessReferences);
        changeBngPortList.add(OltUplinkBusinessReferencesMapper.getChangeBngPorts(localOltUplinkBusinessReferences).get(0));
        mpcSwitchRobot.changeBngPortError(changeBngPortList);

        // check
        mpcSwitchRobot.checkEquipmentBusinessRef(defaultOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                defaultOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());

        mpcSwitchRobot.checkEquipmentBusinessRef(localOltUplinkBusinessReferences.getOltPortEquipmentBusinessRef(),
                localOltUplinkBusinessReferences.getBngSourcePortEquipmentBusinessRef());
    }
}