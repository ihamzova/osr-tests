package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_8_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_8_0.client.model.OntState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_8_0.client.model.ProfileState;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_8_0.client.model.SubscriberNeProfileDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.PortAndHomeIdDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


@ServiceLog(ONT_OLT_ORCHESTRATOR_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(APIGW_MS)
public class AdtranOntCommissioning extends BaseTest {

    private final AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private final OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
    private final UpiterTestContext context = UpiterTestContext.get();
    private AccessLine accessLine;
    private Ont ontSerialNumber;

    @BeforeClass
    public void loadContext() throws InterruptedException{
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForAdtranOltCommissioning();
        Thread.sleep(1000);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.adtranOntAccessLine);
        ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.adtranOntSerialNumber);
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-91170")
    @Description("Adtran Reservation ONT resource")
    public void adtranOntReservation() {
        //Get 1 Free HomeId from pool
        accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));

        //Start access line registration
        PortAndHomeIdDto portAndHomeIdDto = new PortAndHomeIdDto()
                .vpSz(accessLine.getOltDevice().getVpsz())
                .fachSz(accessLine.getOltDevice().getFsz())
                .portNumber(accessLine.getPortNumber())
                .homeId(accessLine.getHomeId());
        String lineId = ontOltOrchestratorRobot.reserveAccessLineByPortAndHomeId(portAndHomeIdDto);
        accessLine.setLineId(lineId);

        //Get Access line state
        AccessLineStatus lineIdState = accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId());

        //Check that access line became assigned
        Assert.assertEquals(AccessLineStatus.ASSIGNED, lineIdState);
}

    @Test(dependsOnMethods = "adtranOntReservation")
    @TmsLink("DIGIHUB-91173")
    @Description("Adtran Registeration ONT resource")
    public void adtranOntRegistration() {
        //Register ONT
        ontOltOrchestratorRobot.registerOnt(accessLine, ontSerialNumber);

        //Check subscriberNEProfile
        SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
        assertNotNull(subscriberNEProfile);
        Assert.assertEquals(subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
        Assert.assertEquals(subscriberNEProfile.getState(), ProfileState.ACTIVE);
        Assert.assertEquals(subscriberNEProfile.getOntState(), OntState.UNKNOWN);
    }

    @Test(dependsOnMethods = {"adtranOntRegistration"})
    @TmsLink("DIGIHUB-91174")
    @Description("Adtran ONT Connectivity test")
    public void adtranOntTest() {
        //test Ont
        ontOltOrchestratorRobot.testOnt(accessLine.getLineId());

        //update Ont state
        ontOltOrchestratorRobot.updateOntState(accessLine);
        SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
        assertNotNull(subscriberNEProfile);
        assertEquals(subscriberNEProfile.getOntState(), OntState.ONLINE);
    }

}