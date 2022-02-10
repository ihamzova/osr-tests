package com.tsystems.tm.acc.ta.team.upiter.migration;

import com.tsystems.tm.acc.data.upiter.models.accesslinedto.AccessLineDtoCase;
import com.tsystems.tm.acc.data.upiter.models.allocatedonuiddto.AllocatedOnuIdDtoCase;
import com.tsystems.tm.acc.data.upiter.models.lineidmigrated.LineIdMigratedCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_34_0.client.model.*;
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
    private AccessLineDto migratedAccessLine;
    private AllocatedOnuIdDto migratedOnuId;
    private LineIdMigrated migratedLineId;
    private PortProvisioning migratedPort;

    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init(){
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
        migratedAccessLine = context.getData().getAccessLineDtoDataProvider().get(AccessLineDtoCase.migratedAccessLine);
        migratedOnuId = context.getData().getAllocatedOnuIdDtoDataProvider().get(AllocatedOnuIdDtoCase.migratedOnuId);
        migratedLineId = context.getData().getLineIdMigratedDataProvider().get(LineIdMigratedCase.migratedLineId);
        migratedPort = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.migratedPort);
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @Description("Migration of an Assigned AccessLine with a subscriber_ne_profile")
    public void ftthMigration() {

        //Step 1. Send access lines' DTOs to access-line-resource-inventory
        accessLineRiRobot.postAccessLine(migratedAccessLine);
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(migratedAccessLine.getLineId()).size(), 1);

        // Step 2. Assign onu Id to the line
        accessLineRiRobot.postOnuId(migratedOnuId);
        List<Integer> onuIds = accessLineRiRobot.getAllocatedOnuIdByDeviceAndLineId(migratedPort,
                migratedAccessLine.getLineId());
        assertEquals(onuIds.size(), 1);
        assertEquals(onuIds.get(0), migratedOnuId.getOnuId());

        // Step 3. Put lineId to the DB
        accessLineRiRobot.postLineId(migratedLineId);
        assertEquals(accessLineRiRobot.getLineIdPool(migratedPort).size(), 1);

        // Step 4. Preprovisioning
        wgAccessProvisioningRobot.startPortProvisioning(migratedPort);
        accessLineRiRobot.checkFtthPortParameters(migratedPort);
        accessLineRiRobot.checkPhysicalResourceRefCountFtth(migratedPort, 1, 1);

        // Step 5. Assign HomeIds and persist them on the line
        List<HomeIdDto> homeIdPool = accessLineRiRobot.getHomeIdPool(migratedPort);
        String homeId = accessLineRiRobot.changeHomeIdStatus(homeIdPool.get(0), HomeIdStatus.ASSIGNED);
        assertEquals(accessLineRiRobot.getHomeIdStateByHomeId(homeId), HomeIdStatus.ASSIGNED);
        accessLineRiRobot.updateHomeIdOnAccessLine(migratedAccessLine.getLineId(), homeId);
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(migratedAccessLine.getLineId()).get(0).getHomeId(), homeId);

        // Step 6. Optional reconfiguration of migrated access lines
        wgAccessProvisioningRobot.startReconfiguration(migratedAccessLine.getLineId());
        accessLineRiRobot.checkReconfigurationResult(migratedAccessLine.getLineId());
    }
}
