package com.tsystems.tm.acc.ta.team.upiter.provisioning;

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
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.*;

public class Olt5600 extends ApiTest {

    private static final Integer LATENCY_FOR_PORT_PROVISIONING = 2 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private PortProvisioning portEmpty5600;
    private PortProvisioning portProvisioningPartly;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portEmpty5600 = OsrTestContext.get().getData()
                .getPortProvisioningDataProvider()
                .get(PortProvisioningCase.portEmpty5600);
        portProvisioningPartly = OsrTestContext.get().getData()
                .getPortProvisioningDataProvider()
                .get(PortProvisioningCase.portPartlyOccupied);
    }

    @Test
    @TmsLink("DIGIHUB-29664")
    @Description("Port Provisioning with 16 WG Lines")
    public void portProvisioningEmpty() throws InterruptedException {

        clearDataBase();

        fillDataBase();

        Port portBeforeProvisioning = getPort(portEmpty5600);

        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portEmpty5600.getAccessLinesCount().intValue());

        startPortProvisioning(portEmpty5600);

        Thread.sleep(LATENCY_FOR_PORT_PROVISIONING);

        Port portAfterProvisioning = getPort(portEmpty5600);

        clearDataBase();

        long countDefaultNEProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = portAfterProvisioning.getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(portAfterProvisioning.getLineIdPools().size(), portEmpty5600.getLineIdPool().intValue());
        Assert.assertEquals(portAfterProvisioning.getHomeIdPools().size(), portEmpty5600.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portEmpty5600.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portEmpty5600.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portEmpty5600.getAccessLinesWG().intValue());
    }

    @Test
    @TmsLink("DIGIHUB-32288")
    @Description("Port provisioning case when port partly occupied")
    public void portProvisioningPartly() throws InterruptedException {

        clearDataBase();

        fillDataBase();

        Port portBeforeProvisioning = getPort(portProvisioningPartly);

        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portProvisioningPartly.getAccessLinesCount().intValue());

        startPortProvisioning(portProvisioningPartly);

        Thread.sleep(LATENCY_FOR_PORT_PROVISIONING);

        Port portAfterProvisioning = getPort(portProvisioningPartly);

        clearDataBase();

        long countDefaultNEProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = portAfterProvisioning.getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(portAfterProvisioning.getLineIdPools().size(), portProvisioningPartly.getLineIdPool().intValue());
        Assert.assertEquals(portAfterProvisioning.getHomeIdPools().size(), portProvisioningPartly.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portProvisioningPartly.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portProvisioningPartly.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portProvisioningPartly.getAccessLinesWG().intValue());
    }

    private void startPortProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    private Port getPort(PortProvisioning port) {
        return oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(port.getEndSz())
                .slotNumberQuery(port.getSlotNumber())
                .portNumberQuery(port.getPortNumber())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void fillDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().fillDatabaseForOltCommissioning()
                .END_SZQuery("49/30/179/76H1")
                .KLS_IDQuery("14653")
                .slOTNUMBER1Query("3")
                .slOTNUMBER2Query("4")
                .slOTNUMBER3Query("5")
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void clearDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase();
    }
}
