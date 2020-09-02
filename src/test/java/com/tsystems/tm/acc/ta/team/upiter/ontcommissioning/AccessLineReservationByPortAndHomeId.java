package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.businessinformation.BusinessInformationCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.SubscriberNeProfileDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.PortAndHomeIdDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;


@ServiceLog(ONT_OLT_ORCHESTRATOR_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(WG_ACCESS_PROVISIONING_MS)
public class AccessLineReservationByPortAndHomeId extends BaseTest {

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
    public void accessLineReservationByPortAndHomeId() throws InterruptedException {
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.OntRegistrationAccessLine);
        postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningStartEvent);
        postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningEndEvent);
        wgAccessProvisioningRobot.startWgAccessProvisioningLog();
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
        AccessLineDto.StatusEnum lineIdState = accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId());

        //Check that access line became assigned
        Assert.assertEquals(AccessLineDto.StatusEnum.ASSIGNED, lineIdState);

        //Create temp List to check business data
        List<BusinessInformation> businessInformationList = new ArrayList<>();
        businessInformationList.add(postprovisioningStart);
        businessInformationList.add(postprovisioningEnd);

        List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();
        Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList), "Business information is not found");
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
        Assert.assertEquals(subscriberNEProfile.getOntSerialNumber(), ontSerialNumber.getSerialNumber());
        Assert.assertEquals(subscriberNEProfile.getState(), SubscriberNeProfileDto.StateEnum.ACTIVE);
        Assert.assertEquals(subscriberNEProfile.getOntState(), SubscriberNeProfileDto.OntStateEnum.UNKNOWN);
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
        Assert.assertEquals(subscriberNEProfile2.getOntState(), SubscriberNeProfileDto.OntStateEnum.ONLINE);
    }
}
