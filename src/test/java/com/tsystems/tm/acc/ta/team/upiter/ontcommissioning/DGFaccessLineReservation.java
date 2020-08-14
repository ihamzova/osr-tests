package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.businessinformation.BusinessInformationCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.AccessLineDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.LineIdDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.client.model.HomeIdDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.HomeIdDto.StatusEnum;


@ServiceLog(ONT_OLT_ORCHESTRATOR_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(WG_ACCESS_PROVISIONING_MS)
public class DGFaccessLineReservation extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
    private WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    private AccessLine accessLine;
    private BusinessInformation postprovisioningStart;
    private BusinessInformation postprovisioningEnd;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabase();
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-60469")
    @Description("DGF: Access Line Reservation by HomeID")
    public void accessLineReservationByHomeId() throws InterruptedException {
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.DGFAccessLineRegistration);
        postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningStartEvent);
        postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningEndEvent);
        wgAccessProvisioningRobot.startWgAccessProvisioningLog();
        //Precondition port commissioning
        //Get 1 Free HomeId from pool
        accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));

        //Start access line registration
        HomeIdDto homeIdDto = new HomeIdDto().homeId(accessLine.getHomeId());

        String lineId = ontOltOrchestratorRobot.reserveAccessLineTask(homeIdDto);
        accessLine.setLineId(lineId);

        //Get HomeId and LineId state
        StatusEnum homeIdState = accessLineRiRobot.getHomeIdStateByHomeId(accessLine.getHomeId());
        LineIdDto.StatusEnum lineIdState = accessLineRiRobot.getLineIdStateByLineId(lineId);

        //Check that homeId and LineId change state
        Assert.assertEquals(homeIdState, StatusEnum.ASSIGNED);
        Assert.assertEquals(lineIdState, LineIdDto.StatusEnum.USED);

        //Get Access line state
        AccessLineDto.StatusEnum accesslineState = accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId());

        //Check that access line became assigned
        Assert.assertEquals(AccessLineDto.StatusEnum.ASSIGNED, accesslineState);

        //Create temp List to check business data
        List<BusinessInformation> businessInformationList = new ArrayList<>();
        businessInformationList.add(postprovisioningStart);
        businessInformationList.add(postprovisioningEnd);

        List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();
        Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList),"Business information is not found");
    }

}