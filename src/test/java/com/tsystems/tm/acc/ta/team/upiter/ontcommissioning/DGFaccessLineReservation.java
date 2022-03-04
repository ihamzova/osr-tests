package com.tsystems.tm.acc.ta.team.upiter.ontcommissioning;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.businessinformation.BusinessInformationCase;
import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.HomeIdManagementRobot;
import com.tsystems.tm.acc.ta.robot.osr.OntOltOrchestratorRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgAccessProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.AccessLineStatus;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.HomeIdStatus;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.HomeIdDto;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.OperationResultLineIdDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

@ServiceLog({
        ONT_OLT_ORCHESTRATOR_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        WG_ACCESS_PROVISIONING_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
@Epic("ONT Processes DeutcheGlasFaser")
public class DGFaccessLineReservation extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private OntOltOrchestratorRobot ontOltOrchestratorRobot = new OntOltOrchestratorRobot();
    private HomeIdManagementRobot homeIdManagementRobot = new HomeIdManagementRobot();
    private WgAccessProvisioningRobot wgAccessProvisioningRobot = new WgAccessProvisioningRobot();
    private AccessLine accessLine;
    private PortProvisioning portProvisioning;
    private BusinessInformation postprovisioningStart;
    private BusinessInformation postprovisioningEnd;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() throws InterruptedException {
        wgAccessProvisioningRobot.changeFeatureToggleHomeIdPoolState(false);
        accessLineRiRobot.clearDatabase();
        Thread.sleep(5000);
        portProvisioning = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portForDgfReservation);
    }

    @Test
    @TmsLink("DIGIHUB-60469")
    @Description("DGF: Access Line Reservation by HomeID")
    public void accessLineReservationByHomeId() {
        wgAccessProvisioningRobot.startPortProvisioning(portProvisioning);
        accessLineRiRobot.checkFtthPortParameters(portProvisioning);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.DGFAccessLineRegistration);
        accessLine.setHomeId(accessLineRiRobot.getHomeIdPool(portProvisioning).get(0).getHomeId());
        postprovisioningStart = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningStartEvent);
        postprovisioningEnd = context.getData().getBusinessInformationDataProvider().get(BusinessInformationCase.PostprovisioningEndEvent);
        //Precondition port commissioning
        //Get 1 Free HomeId from pool
        HomeIdDto homeIdDto = new HomeIdDto().homeId(accessLine.getHomeId());

        OperationResultLineIdDto callback = ontOltOrchestratorRobot.reserveAccessLineTask(homeIdDto);

        //check callback
        assertNull(callback.getError());
        assertTrue(callback.getSuccess());
        assertNotNull(callback.getResponse().getLineId());
        assertEquals(accessLine.getHomeId(), callback.getResponse().getHomeId());

        accessLine.setLineId(callback.getResponse().getLineId());

        // get data from alri
        HomeIdStatus homeIdState = accessLineRiRobot.getHomeIdStateByHomeId(accessLine.getHomeId());
        AccessLineStatus accesslineState = accessLineRiRobot.getAccessLineStateByLineId(accessLine.getLineId());

        // check alri
        assertEquals(homeIdState, HomeIdStatus.ASSIGNED);
        assertEquals(AccessLineStatus.ASSIGNED, accesslineState);

/*        //Create temp List to check business data
        List<BusinessInformation> businessInformationList = new ArrayList<>();
        businessInformationList.add(postprovisioningStart);
        businessInformationList.add(postprovisioningEnd);

        List<BusinessInformation> businessInformationLogCollector = wgAccessProvisioningRobot.getBusinessInformation();
        Assert.assertTrue(businessInformationLogCollector.containsAll(businessInformationList),"Business information is not found");*/
    }
}
