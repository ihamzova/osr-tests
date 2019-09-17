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

public class PortProvisioning5800 extends ApiTest {

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
    @Description("Port Provisioning for OLT 5800")
    public void portProvisioning5800() throws InterruptedException {

        /* Clears DataBase before test */
        oltResourceInventoryClient.getClient().databaseInitializerController().initializeDatabase().execute(validatedWith(shouldBeCode(200)));

        /* Fills DataBase for Port Provisioning */
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().fillDatabase()
                .END_SZQuery("49/30/179/76H1")
                .KLS_IDQuery("14653")
                .slOTNUMBER1Query("3")
                .slOTNUMBER2Query("4")
                .slOTNUMBER3Query("5")
                .execute(validatedWith(shouldBeCode(200)));

        /* Gets Port before provisioning */
        Port portBeforeProvisioning = oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(portProvisioning5800.getEndSz())
                .slotNumberQuery(portProvisioning5800.getSlotNumber())
                .portNumberQuery(portProvisioning5800.getPortNumber())
                .executeAs(validatedWith(shouldBeCode(200)));

        /* Checks empty port before provisioning */
        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portProvisioning5800.getEmptyPort().intValue());

        /* Start Port Provisioning */
        wgAccessProvisioningClient.getClient().provisioningProcessController().startPortProvisioning()
                .body(new PortDto()
                        .endSz(portProvisioning5800.getEndSz())
                        .slotNumber(portProvisioning5800.getSlotNumber())
                        .portNumber(portProvisioning5800.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(201)));

        /* Latency for provisioning process */
        Thread.sleep(3 * 60000);

        /* Gets port after provisioning */
        Port portAfterProvisioning = oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(portProvisioning5800.getEndSz())
                .slotNumberQuery(portProvisioning5800.getSlotNumber())
                .portNumberQuery(portProvisioning5800.getPortNumber())
                .executeAs(validatedWith(shouldBeCode(200)));

        /* Clears DataBase after test */
        oltResourceInventoryClient.getClient().databaseInitializerController().initializeDatabase().execute(validatedWith(shouldBeCode(200)));

        long countDefaultNEProfileActive = portAfterProvisioning.getAccessLines()
                .stream().map(AccessLine::getDefaultNeProfile).filter(DefaultNeProfile ->
                        DefaultNeProfile.getState().equals
                                (com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.DefaultNeProfile.StateEnum.ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = portAfterProvisioning.getAccessLines()
                .stream().map(AccessLine::getDefaultNetworkLineProfile).filter(DefaultNetworkLineProfile ->
                        DefaultNetworkLineProfile.getState().equals
                                (com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.DefaultNetworkLineProfile.StateEnum.ACTIVE)).count();

        long countAccessLinesWG = portAfterProvisioning.getAccessLines()
                .stream().filter(AccessLine -> AccessLine.getStatus().equals
                        (com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.AccessLine.StatusEnum.WALLED_GARDEN)).count();

        Assert.assertEquals(portAfterProvisioning.getLineIdPools().size(), portProvisioning5800.getLineIdPool().intValue());
        Assert.assertEquals(portAfterProvisioning.getHomeIdPools().size(), portProvisioning5800.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portProvisioning5800.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portProvisioning5800.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portProvisioning5800.getAccessLinesWG().intValue());
    }
}
