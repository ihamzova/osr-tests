package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.PortAndHomeIdDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AccessLineReservationByPortAndHomeId extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
    private AccessLine accessLine;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabase();
    }

    @AfterMethod
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-71918")
    @Description("ONT Access Line Reservation by HomeID")
    public void accessLineReservationByPortAndHomeId() throws InterruptedException {
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.OntRegistrationAccessLine);
        //Precondition port commissioning
        //Get 1 HomeId from pool
        accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
        //Start access line registration
        PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
                .vpSz(accessLine.getOltDevice().getVpsz())
                .fachSz(accessLine.getOltDevice().getFsz())
                .slotNumber(accessLine.getSlotNumber())
                .portNumber(accessLine.getPortNumber())
                .homeId(accessLine.getHomeId());
        String lineId = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);
        accessLine.setLineId(lineId);
        //Get Access line state
        AccessLineDto.StatusEnum lineIdState = accessLineRiRobot.getLineIdStateByLineId(accessLine.getLineId());
        //Check that access line became assigned
        Assert.assertEquals(lineIdState, AccessLineDto.StatusEnum.ASSIGNED);
    }
}
