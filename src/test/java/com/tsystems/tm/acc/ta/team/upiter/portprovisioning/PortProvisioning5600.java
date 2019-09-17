package com.tsystems.tm.acc.ta.team.upiter.portprovisioning;

import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.AccessLine;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Port;
import com.tsystems.tm.acc.ta.api.*;
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

public class PortProvisioning5600 extends ApiTest {

    private OltResourceInventoryClient oltResourceInventoryClient;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private PortProvisioning portProvisioning5600;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portProvisioning5600 = OsrTestContext.get().getData()
                .getPortProvisioningDataProvider()
                .get(PortProvisioningCase.portProvisioning5600);
    }

    @Test
    @TmsLink("DIGIHUB-28415")
    @Description("Port Provisioning for OLT 5600")
    public void portProvisioning5600() throws InterruptedException {

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

        /* Gets port before provisioning */
        Port portBeforeProvisioning = oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(portProvisioning5600.getEndSz())
                .slotNumberQuery(portProvisioning5600.getSlotNumber())
                .portNumberQuery(portProvisioning5600.getPortNumber())
                .executeAs(validatedWith(shouldBeCode(200)));

        /* Check empty port before provisioning */
        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portProvisioning5600.getEmptyPort().intValue());

        /* Starts Port Provisioning */
        wgAccessProvisioningClient.getClient().provisioningProcessController().startPortProvisioning()
                .body(new PortDto()
                        .endSz(portProvisioning5600.getEndSz())
                        .slotNumber(portProvisioning5600.getSlotNumber())
                        .portNumber(portProvisioning5600.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(201)));

        /* Latency for provisioning process */
        Thread.sleep(2 * 60000);

        /* Gets Port after provisioning */
        Port portAfterProvisioning = oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(portProvisioning5600.getEndSz())
                .slotNumberQuery(portProvisioning5600.getSlotNumber())
                .portNumberQuery(portProvisioning5600.getPortNumber())
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

        Assert.assertEquals(portAfterProvisioning.getLineIdPools().size(), portProvisioning5600.getLineIdPool().intValue());
        Assert.assertEquals(portAfterProvisioning.getHomeIdPools().size(), portProvisioning5600.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portProvisioning5600.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portProvisioning5600.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portProvisioning5600.getAccessLinesWG().intValue());
    }
}
