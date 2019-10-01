package com.tsystems.tm.acc.ta.team.upiter.portprovisioning;

import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.AccessLine;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Port;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.wg.access.internal.client.model.PortDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.data.CommonTestData.*;

public class PortProvisioning5800 extends ApiTest {

    private static final Integer LATENCY = 3 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private PortProvisioning portProvisioning5800;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portProvisioning5800 = OsrTestContext.get().getData()
                .getPortProvisioningDataProvider()
                .get(PortProvisioningCase.portProvisioning5800);
    }

    @Test
    @TmsLink("DIGIHUB-28415")
    @Description("Port Provisioning with 32 WG Lines for OLT 5800")
    public void portProvisioning5800() throws InterruptedException {

        /* Clears DataBase before test */
        oltResourceInventoryClient.getClient().databaseInitializerController().initializeDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        /* Fills DataBase for Port Provisioning */
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().fillDatabaseForPortProvisioning()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        /* Gets Port before provisioning */
        Port portBeforeProvisioning = oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(portProvisioning5800.getEndSz())
                .slotNumberQuery(portProvisioning5800.getSlotNumber())
                .portNumberQuery(portProvisioning5800.getPortNumber())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        /* Checks empty port before provisioning */
        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portProvisioning5800.getEmptyPort().intValue());

        /* Start Port Provisioning */
        wgAccessProvisioningClient.getClient().provisioningProcessController().startPortProvisioning()
                .body(new PortDto()
                        .endSz(portProvisioning5800.getEndSz())
                        .slotNumber(portProvisioning5800.getSlotNumber())
                        .portNumber(portProvisioning5800.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        /* Latency for provisioning process */
        Thread.sleep(LATENCY);

        /* Gets port after provisioning process*/
        Port portAfterProvisioning = oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(portProvisioning5800.getEndSz())
                .slotNumberQuery(portProvisioning5800.getSlotNumber())
                .portNumberQuery(portProvisioning5800.getPortNumber())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        /* Clears DataBase after test */
        oltResourceInventoryClient.getClient().databaseInitializerController().initializeDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        long countDefaultNEProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = portAfterProvisioning.getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(portAfterProvisioning.getLineIdPools().size(), portProvisioning5800.getLineIdPool().intValue());
        Assert.assertEquals(portAfterProvisioning.getHomeIdPools().size(), portProvisioning5800.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portProvisioning5800.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portProvisioning5800.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portProvisioning5800.getAccessLinesWG().intValue());
    }
}
