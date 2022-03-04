package com.tsystems.tm.acc.ta.team.upiter.migration;

import com.tsystems.tm.acc.data.upiter.models.accesslinedto.AccessLineDtoCase;
import com.tsystems.tm.acc.data.upiter.models.allocatedonuiddto.AllocatedOnuIdDtoCase;
import com.tsystems.tm.acc.data.upiter.models.lineidmigrated.LineIdMigratedCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.LineIdMigrated;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.HomeIdManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.AllocatedOnuIdDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.HomeIdDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.HomeIdStatus;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

@Epic("FTTH 1.7 Migration")
public class FtthMigration {

    private AccessLineRiRobot accessLineRiRobot;
    private WgAccessProvisioningRobot wgAccessProvisioningRobot;
    private HomeIdManagementRobot homeIdManagementRobot;

    private AccessLineDto migratedAccessLineWithHomeIdPool;
    private AllocatedOnuIdDto migratedOnuIdWithHomeIdPool;
    private LineIdMigrated migratedLineIdWithHomeIdPool;
    private PortProvisioning migratedPortWithHomeIdPool;
    private AccessLineDto migratedAccessLineWithoutHomeIdPool;
    private AllocatedOnuIdDto migratedOnuIdWithoutHomeIdPool;
    private LineIdMigrated migratedLineIdWithoutHomeIdPool;
    private PortProvisioning migratedPortWithoutHomeIdPool;

    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init(){
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        homeIdManagementRobot = new HomeIdManagementRobot();

        migratedAccessLineWithHomeIdPool = context.getData().getAccessLineDtoDataProvider().get(AccessLineDtoCase.migratedAccessLineWithHomeIdPool);
        migratedOnuIdWithHomeIdPool = context.getData().getAllocatedOnuIdDtoDataProvider().get(AllocatedOnuIdDtoCase.migratedOnuIdWithHomeIdPool);
        migratedLineIdWithHomeIdPool = context.getData().getLineIdMigratedDataProvider().get(LineIdMigratedCase.migratedLineIdWithHomeIdPool);
        migratedPortWithHomeIdPool = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.migratedPortWithHomeIdPool);

        migratedAccessLineWithoutHomeIdPool = context.getData().getAccessLineDtoDataProvider().get(AccessLineDtoCase.migratedAccessLineWithoutHomeIdPool);
        migratedOnuIdWithoutHomeIdPool = context.getData().getAllocatedOnuIdDtoDataProvider().get(AllocatedOnuIdDtoCase.migratedOnuIdWithoutHomeIdPool);
        migratedLineIdWithoutHomeIdPool = context.getData().getLineIdMigratedDataProvider().get(LineIdMigratedCase.migratedLineIdWithoutHomeIdPool);
        migratedPortWithoutHomeIdPool = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.migratedPortWithoutHomeIdPool);

        accessLineRiRobot.clearDatabase();
    }

    @Test
    @Description("Migration of an Assigned AccessLine with a subscriber_ne_profile, homeIdPool is created")
    public void ftthMigrationWithHomeIdPool() {

        wgAccessProvisioningRobot.changeFeatureToggleHomeIdPoolState(false);

        //Step 1. Send access lines' DTOs to access-line-resource-inventory
        accessLineRiRobot.postAccessLine(migratedAccessLineWithHomeIdPool);
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(migratedAccessLineWithHomeIdPool.getLineId()).size(), 1);

        // Step 2. Assign onu Id to the line
        accessLineRiRobot.postOnuId(migratedOnuIdWithHomeIdPool);
        List<Integer> onuIds = accessLineRiRobot.getAllocatedOnuIdByDeviceAndLineId(migratedPortWithHomeIdPool,
                migratedAccessLineWithHomeIdPool.getLineId());
        assertEquals(onuIds.size(), 1);
        assertEquals(onuIds.get(0), migratedOnuIdWithHomeIdPool.getOnuId());

        // Step 3. Put lineId to the DB
        accessLineRiRobot.postLineId(migratedLineIdWithHomeIdPool);
        assertEquals(accessLineRiRobot.getLineIdPool(migratedPortWithHomeIdPool).size(), 1);

        // Step 4. Preprovisioning
        wgAccessProvisioningRobot.startPortProvisioning(migratedPortWithHomeIdPool);
        accessLineRiRobot.checkFtthPortParameters(migratedPortWithHomeIdPool);
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(migratedPortWithHomeIdPool, 1, 1);

        // Step 5. Assign HomeIds and persist them on the line
        List<HomeIdDto> homeIdPool = accessLineRiRobot.getHomeIdPool(migratedPortWithHomeIdPool);
        String homeId = accessLineRiRobot.changeHomeIdStatus(homeIdPool.get(0), HomeIdStatus.ASSIGNED);
        assertEquals(accessLineRiRobot.getHomeIdStateByHomeId(homeId), HomeIdStatus.ASSIGNED);
        accessLineRiRobot.updateHomeIdOnAccessLine(migratedAccessLineWithHomeIdPool.getLineId(), homeId);
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(migratedAccessLineWithHomeIdPool.getLineId()).get(0).getHomeId(), homeId);

        // Step 6. Optional reconfiguration of migrated access lines
        wgAccessProvisioningRobot.startReconfiguration(migratedAccessLineWithHomeIdPool.getLineId());
        accessLineRiRobot.checkReconfigurationResult(migratedAccessLineWithHomeIdPool.getLineId());
    }

    @Test
    @Description("Migration of an Assigned AccessLine with a subscriber_ne_profile, homeIdPool is not created")
    public void ftthMigrationWithoutHomeIdPool() {

        wgAccessProvisioningRobot.changeFeatureToggleHomeIdPoolState(true);

        //Step 1. Send access lines' DTOs to access-line-resource-inventory
        accessLineRiRobot.postAccessLine(migratedAccessLineWithoutHomeIdPool);
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(migratedAccessLineWithoutHomeIdPool.getLineId()).size(), 1);

        // Step 2. Assign onu Id to the line
        accessLineRiRobot.postOnuId(migratedOnuIdWithoutHomeIdPool);
        List<Integer> onuIds = accessLineRiRobot.getAllocatedOnuIdByDeviceAndLineId(migratedPortWithoutHomeIdPool,
                migratedAccessLineWithoutHomeIdPool.getLineId());
        assertEquals(onuIds.size(), 1);
        assertEquals(onuIds.get(0), migratedOnuIdWithoutHomeIdPool.getOnuId());

        // Step 3. Put lineId to the DB
        accessLineRiRobot.postLineId(migratedLineIdWithoutHomeIdPool);
        assertEquals(accessLineRiRobot.getLineIdPool(migratedPortWithoutHomeIdPool).size(), 1);

        // Step 4. Preprovisioning
        wgAccessProvisioningRobot.startPortProvisioning(migratedPortWithoutHomeIdPool);
        accessLineRiRobot.checkFtthPortParameters(migratedPortWithoutHomeIdPool);
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(migratedPortWithoutHomeIdPool, 1, 1);

        // Step 5. Assign HomeIds and persist them on the line
        String homeId = homeIdManagementRobot.generateHomeid().getHomeId();
        accessLineRiRobot.updateHomeIdOnAccessLine(migratedAccessLineWithoutHomeIdPool.getLineId(), homeId);
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(migratedAccessLineWithoutHomeIdPool.getLineId()).get(0).getHomeId(), homeId);

        // Step 6. Optional reconfiguration of migrated access lines
        wgAccessProvisioningRobot.startReconfiguration(migratedAccessLineWithoutHomeIdPool.getLineId());
        accessLineRiRobot.checkReconfigurationResult(migratedAccessLineWithoutHomeIdPool.getLineId());
    }
}
