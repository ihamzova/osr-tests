package com.tsystems.tm.acc.ta.team.upiter.deprovisioning;

import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.api.osr.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.CardDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.DeviceDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.PortDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog(WG_ACCESS_PROVISIONING_MS)
@ServiceLog(ACCESS_LINE_RESOURCE_INVENTORY_MS)
@ServiceLog(NETWORK_LINE_PROFILE_MANAGEMENT_MS)
@ServiceLog(DECOUPLING_MS)
@ServiceLog(GATEWAY_ROUTE_MS)
public class DeprovisioningTest extends BaseTest {

    private AccessLineRiRobot accessLineRiRobot;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private PortProvisioning portDepr;
    private PortProvisioning cardDepr;
    private PortProvisioning deviceDepr;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        accessLineRiRobot = new AccessLineRiRobot();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();

        portDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portDeprovisioning);
        cardDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.cardDeprovisioning);
        deviceDepr = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.deviceDepovisioning);
    }

    @BeforeMethod
    public void prepareData() {
        accessLineRiRobot.clearDatabase();
        accessLineRiRobot.fillDatabaseForOltCommissioning();
    }

    @AfterMethod
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-36495")
    @Description("Port deprovisioning case")
    public void portDeprovisioningTest() {
        checkPreconditions(portDepr);

        wgAccessProvisioningClient.getClient()
                .deprovisioningProcess()
                .startPortDeprovisioning()
                .body(new PortDto()
                        .endSz(portDepr.getEndSz())
                        .portNumber(portDepr.getPortNumber())
                        .slotNumber(portDepr.getSlotNumber()))
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        checkPostConditions(portDepr);
    }

    @Test
    @TmsLink("DIGIHUB-36495")
    @Description("Card deprovisioning case")
    public void cardDeprovisioningTest() {
        checkPreconditions(cardDepr);

        wgAccessProvisioningClient.getClient()
                .deprovisioningProcess()
                .startCardsDeprovisioning()
                .body(Collections.singletonList(new CardDto()
                        .endSz(cardDepr.getEndSz())
                        .slotNumber(cardDepr.getSlotNumber())))
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        checkPostConditions(cardDepr);
    }

    @Test
    @TmsLink("DIGIHUB-36495")
    @Description("Device deprovisioning case")
    public void deviceDeprovisioningTest() {
        checkPreconditions(deviceDepr);

        wgAccessProvisioningClient.getClient()
                .deprovisioningProcess()
                .startDeviceDeprovisioning()
                .body(new DeviceDto().endSz(deviceDepr.getEndSz()))
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        checkPostConditions(deviceDepr);
    }

    private void checkPreconditions(PortProvisioning port) {
        accessLineRiRobot.prepareTestDataToDeprovisioning(port);
        accessLineRiRobot.checkDecommissioningPreconditions(port);
    }

    private void checkPostConditions(PortProvisioning port) {
        accessLineRiRobot.checkPortParametersForLines(port);
        if (port.getPortNumber() != null) {
            accessLineRiRobot.checkBackHaulIdAbsence(port);
        }
    }

}
