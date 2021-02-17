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
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_10_0.client.model.HomeIdDto;
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
public class Postprovisioning24_32 extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    private BusinessInformation postprovisioningStart;
    private BusinessInformation postprovisioningEnd;
    private AccessLine accessLine;
    private PortProvisioning portFor24_32Case;
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
    @TmsLink("DIGIHUB-34826")
    @Description("OLT MA5600, 20/24, postprovisioning 24 --> 32")
    public void postProvisioning24to32Test() throws InterruptedException {
        portForPostProvisioningPrecondition = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFor24_32Precondition);
        portFor24_32Case = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFor24_32);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.assignAccessLines24_32);
        postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.Postprovisioning24_32StartInfo);
        postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.Postprovisioning24_32EndInfo);
        //precondition
        wgAccessProvisioningRobot.startPortProvisioning(portForPostProvisioningPrecondition); //16 wg lines
        accessLineRiRobot.checkProvisioningResults(portForPostProvisioningPrecondition);


        accessLine.setHomeId(accessLineRiRobot.getHomeIdByPort(accessLine));
        HomeIdDto homeIdDto = new HomeIdDto().homeId(accessLine.getHomeId());

        //20 assigned access lines
        wgAccessProvisioningRobot.prepareForPostprovisioning(20, portFor24_32Case, homeIdDto);

        //wgAccessProvisioningRobot.startWgAccessProvisioningLog();
        //1 to trigger postprovisioning
        wgAccessProvisioningRobot.prepareForPostprovisioning(1, portFor24_32Case, homeIdDto);

/*
        //Create temp List to check business data
        List<BusinessInformation> businessInformationList = new ArrayList<>();
        businessInformationList.add(postprovisioningStart);
        businessInformationList.add(postprovisioningEnd);

        List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();

        System.out.println("/// businessInformationList: " + businessInformationList);
        System.out.println("/// businessInformationLogCollector: " + businessInformationLogCollector);
        Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList),"Business information is not found");
*/

        accessLineRiRobot.checkPortParametersForLines(portFor24_32Case);
        accessLineRiRobot.checkPortParametersForAssignedLines(portFor24_32Case);
    }
}
