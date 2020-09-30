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
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.GATEWAY_ROUTE_MS;

@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
public class Postprovisioning16_24 extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    private BusinessInformation postprovisioningStart;
    private BusinessInformation postprovisioningEnd;
    private AccessLine accessLine;
    private PortProvisioning portFor16_24Case;
    private PortProvisioning portForPostProvisioningPrecondition;
    private PortProvisioning portFor24_32Case;
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
    @TmsLink("DIGIHUB-34825")
    @Description("OLT MA5600, 12/16, postprovisioning 16 --> 24")
    public void postProvisioning16to24Test() throws InterruptedException {
        portFor16_24Case = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFor16_24);
        portForPostProvisioningPrecondition = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFor16_24Precondition);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.assignAccessLines16_24);
        postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.Postprovisioning20_24StartInfo);
        postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.Postprovisioning20_24EndInfo);
        //precondition
        wgAccessProvisioningRobot.startPortProvisioning(portForPostProvisioningPrecondition);//create 16 wg access lines
        accessLineRiRobot.checkProvisioningResults(portForPostProvisioningPrecondition);

        accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
        HomeIdDto homeIdDto = new HomeIdDto().homeId(accessLine.getHomeId());

        //12 assigned lines
        wgAccessProvisioningRobot.prepareForPostprovisioning(12, portFor16_24Case, homeIdDto);

        wgAccessProvisioningRobot.startWgAccessProvisioningLog();
        //1 trigger postprovisioning
        wgAccessProvisioningRobot.prepareForPostprovisioning(1, portFor16_24Case, homeIdDto);

        //Create temp List to check business data
        List<BusinessInformation> businessInformationList = new ArrayList<>();
        businessInformationList.add(postprovisioningStart);
        businessInformationList.add(postprovisioningEnd);

        List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();
        Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList),"Business information is not found");

        accessLineRiRobot.checkPortParametersForLines(portFor16_24Case);
        accessLineRiRobot.checkPortParametersForAssignedLines(portFor16_24Case);
    }


    @Test(dependsOnMethods = "postProvisioning16to24Test")
    @TmsLink("DIGIHUB-34826")
    @Description("OLT MA5600, 20/24, postprovisioning 24 --> 32")
    public void postProvisioning24_32Test() {
        portForPostProvisioningPrecondition = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFor16_24_32Precondition);
        portFor24_32Case = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFor16_24_32);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.assignAccessLines16_24_32);
        //precondition
        accessLineRiRobot.checkProvisioningResults(portForPostProvisioningPrecondition);

        accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
        HomeIdDto homeIdDto = new HomeIdDto().homeId(accessLine.getHomeId());

        //13 + 7 assigned access lines
        wgAccessProvisioningRobot.prepareForPostprovisioning(7, portFor24_32Case, homeIdDto);

        //1 to trigger postprovisioning
        wgAccessProvisioningRobot.prepareForPostprovisioning(1, portFor24_32Case, homeIdDto);

        accessLineRiRobot.checkPortParametersForLines(portFor24_32Case);
        accessLineRiRobot.checkPortParametersForAssignedLines(portFor24_32Case);
    }
}
