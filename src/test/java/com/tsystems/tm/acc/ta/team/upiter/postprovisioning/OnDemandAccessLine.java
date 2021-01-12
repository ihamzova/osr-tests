package com.tsystems.tm.acc.ta.team.upiter.postprovisioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.businessinformation.BusinessInformationCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.internal.v2_8_0.client.model.HomeIdDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.GATEWAY_ROUTE_MS;

@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
public class OnDemandAccessLine extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    private BusinessInformation postprovisioningStart;
    private BusinessInformation postprovisioningEnd;
    private AccessLine accessLine;
    private PortProvisioning portForOnDemand;
    private PortProvisioning portForPostProvisioningPrecondition;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        accessLineRiRobot.clearDatabase();
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-34827")
    @Description("OLT MA5600, 32/32, postprovisioning 32 --> on_demand")
    public void onDemandAccessLine() throws InterruptedException {
        portForOnDemand = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portForOnDemandLine);
        portForPostProvisioningPrecondition = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portForOnDemandLinePrecondition);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.assignAccessLine);
        postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningOnDemandStartInfo);
        postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningOnDemandEndInfo);
        //precondition
        wgAccessProvisioningRobot.startPortProvisioning(portForPostProvisioningPrecondition); //16 wg lines
        accessLineRiRobot.checkProvisioningResults(portForPostProvisioningPrecondition);

        accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
        HomeIdDto homeIdDto = new HomeIdDto().homeId(accessLine.getHomeId());
        wgAccessProvisioningRobot.prepareForPostprovisioning(32, portForPostProvisioningPrecondition, homeIdDto); //create 32 assigned lines

        wgAccessProvisioningRobot.startWgAccessProvisioningLog();
        wgAccessProvisioningRobot.startPostprovisioning(portForOnDemand); //33 wg line creation
        //List<BusinessInformation> businessInformationList = new ArrayList<>();
        //businessInformationList.add(postprovisioningStart);
        //businessInformationList.add(postprovisioningEnd);

        accessLineRiRobot.checkProvisioningResults(portForOnDemand);

        //List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();

        //Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList), "Business information is not found");

        accessLineRiRobot.checkPortParametersForLines(portForOnDemand);
        accessLineRiRobot.checkPortParametersForAssignedLines(portForOnDemand);
    }
}
