package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.businessinformation.BusinessInformationCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_8_0.client.model.*;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.v2_8_0.client.model.PortAndHomeIdDto;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.*;


@ServiceLog(ONT_OLT_ORCHESTRATOR_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(ACCESS_LINE_MANAGEMENT_MS)
@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
public class OntCommissioning extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
    private WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    private AccessLine accessLine;
    private BusinessInformation postprovisioningStart;
    private BusinessInformation postprovisioningEnd;
    private Ont ontSerialNumber;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioning();
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-71918")
    @Description("ONT Access Line Reservation by HomeID")
    public void accessLineReservationByPortAndHomeId() {
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.OntRegistrationAccessLine);
        postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningStartEvent);
        postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningEndEvent);
        //wgAccessProvisioningRobot.startWgAccessProvisioningLog();
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
        AccessLineStatus lineIdState = accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId());

        //Check that access line became assigned
        assertEquals(AccessLineStatus.ASSIGNED, lineIdState);

/*        //Create temp List to check business data
        List<BusinessInformation> businessInformationList = new ArrayList<>();
        businessInformationList.add(postprovisioningStart);
        businessInformationList.add(postprovisioningEnd);

        List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();
        Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList), "Business information is not found");*/
    }

    @Test(dependsOnMethods = "accessLineReservationByPortAndHomeId")
    @TmsLink("DIGIHUB-47257")
    @Description("Register ONT resource")
    public void ontRegistration() {
        ontSerialNumber = context.getData().getOntDataProvider().get(OntCase.OntSerialNumber);
        //Register ONT
        ontOltOrchestratorRobot.registerOnt(accessLine, ontSerialNumber);

        //Check subscriberNEProfile
        SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
        assertNotNull(subscriberNEProfile);
        assertEquals(subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
        assertEquals(subscriberNEProfile.getState(), ProfileState.ACTIVE);
        assertEquals(subscriberNEProfile.getOntState(), OntState.UNKNOWN);
    }

    @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeId", "ontRegistration"})
    @TmsLink("DIGIHUB-33938")
    @Description("ONT Connectivity test")
    public void ontTest() {
        //test Ont
        ontOltOrchestratorRobot.testOnt(accessLine.getLineId());

        //update Ont state
        ontOltOrchestratorRobot.updateOntState(accessLine);
        SubscriberNeProfileDto subscriberNEProfile2 = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
        assertNotNull(subscriberNEProfile2);
        assertEquals(subscriberNEProfile2.getOntState(), OntState.ONLINE);
    }

    @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeId", "ontRegistration", "ontTest"})
    @TmsLink("DIGIHUB-53891")
    @Description("ONT Change")
    public void ontChangeTest() {
        assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber(),
                ontSerialNumber.getSerialNumber());
        ontOltOrchestratorRobot.changeOntSerialNumber(accessLine, ontSerialNumber.getNewSerialNumber());
        assertEquals(accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId()).getOntSerialNumber(),
                ontSerialNumber.getNewSerialNumber());
    }

    @Test(dependsOnMethods = {"accessLineReservationByPortAndHomeId", "ontRegistration", "ontTest", "ontChangeTest"})
    @TmsLink("DIGIHUB-53292")
    @Description("ONT Decommissioning, rollback to reservation = false")
    public void ontDecommissioningTest() {
        ontOltOrchestratorRobot.decommissionOnt(accessLine);
        SubscriberNeProfileDto subscriberNEProfile = accessLineRiRobot.getSubscriberNEProfile(accessLine.getLineId());
        assertNull(subscriberNEProfile);
        assertEquals(accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId()),
                AccessLineStatus.WALLED_GARDEN);
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getHomeId(),
                accessLine.getHomeId());
        assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNeProfile().getState(),
                ProfileState.ACTIVE);
    }
}
