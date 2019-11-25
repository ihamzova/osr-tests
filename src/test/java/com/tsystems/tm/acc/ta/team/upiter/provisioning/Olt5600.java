package com.tsystems.tm.acc.ta.team.upiter.provisioning;

import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.AccessLine;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Card;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Port;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.wg.access.internal.client.model.CardDto;
import com.tsystems.tm.acc.wg.access.internal.client.model.DeviceDto;
import com.tsystems.tm.acc.wg.access.internal.client.model.PortDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.*;

public class Olt5600 extends ApiTest {

    private static final Integer LATENCY_FOR_PORT_PROVISIONING = 60_000;
    private static final Integer LATENCY_FOR_DEVICE_PROVISIONING = 15 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private PortProvisioning portEmpty;
    private PortProvisioning portProvisioningPartly;
    private PortProvisioning portProvisioningFully;
    private PortProvisioning portWithInActiveLines;

    @BeforeMethod
    public void prepareData() throws InterruptedException {
        clearDataBase();
        Thread.sleep(1000);
        fillDataBase();
    }

    @AfterMethod
    public void clearData() {
        clearDataBase();
    }

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portEmpty = OsrTestContext.get().getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portEmpty5600);
        portProvisioningPartly = OsrTestContext.get().getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portPartlyOccupied);
        portProvisioningFully = OsrTestContext.get().getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portFullyOccupied);
        portWithInActiveLines = OsrTestContext.get().getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portWithInActiveLines);
    }

    @Test
    @TmsLink("DIGIHUB-29664")
    @Description("Port provisioning case when port completely free")
    public void portProvisioningEmpty() throws InterruptedException {

        Port portBeforeProvisioning = getPort(portEmpty);

        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portEmpty.getAccessLinesCount().intValue());

        startPortProvisioning(portEmpty);

        Thread.sleep(LATENCY_FOR_PORT_PROVISIONING);

        Port portAfterProvisioning = getPort(portEmpty);

        long countDefaultNEProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = portAfterProvisioning.getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(portAfterProvisioning.getLineIdPools().size(), portEmpty.getLineIdPool().intValue());
        Assert.assertEquals(portAfterProvisioning.getHomeIdPools().size(), portEmpty.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portEmpty.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portEmpty.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portEmpty.getAccessLinesWG().intValue());
    }

    @Test
    @TmsLink("DIGIHUB-32288")
    @Description("Port provisioning case when port partly occupied")
    public void portProvisioningPartly() throws InterruptedException {

        Port portBeforeProvisioning = getPort(portProvisioningPartly);

        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portProvisioningPartly.getAccessLinesCount().intValue());

        startPortProvisioning(portProvisioningPartly);

        Thread.sleep(40_000);

        Port portAfterProvisioning = getPort(portProvisioningPartly);

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

    @Test
    @TmsLink("DIGIHUB-40631")
    @Description("Port provisioning case when port completely occupied")
    public void portProvisioningFully() throws InterruptedException {

        Port portBeforeProvisioning = getPort(portProvisioningFully);

        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portProvisioningFully.getAccessLinesCount().intValue());

        startPortProvisioning(portProvisioningFully);

        Port portAfterProvisioning = getPort(portProvisioningFully);

        long countDefaultNEProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = portAfterProvisioning.getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(portAfterProvisioning.getLineIdPools().size(), portProvisioningFully.getLineIdPool().intValue());
        Assert.assertEquals(portAfterProvisioning.getHomeIdPools().size(), portProvisioningFully.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portProvisioningFully.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portProvisioningFully.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portProvisioningFully.getAccessLinesWG().intValue());
    }

    @Test
    @TmsLink("DIGIHUB-32026")
    @Description("Port provisioning case when port has InActive Lines")
    public void portProvisioningWithInactiveLines() throws InterruptedException {

        Port portBeforeProvisioning = getPort(portWithInActiveLines);

        long countInActiveAccessLines = portBeforeProvisioning.getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_INACTIVE)).count();

        Assert.assertEquals(countInActiveAccessLines, portWithInActiveLines.getAccessLinesCount().intValue());

        startPortProvisioning(portWithInActiveLines);

        Thread.sleep(60_000);

        Port portAfterProvisioning = getPort(portWithInActiveLines);

        long countDefaultNEProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = portAfterProvisioning.getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = portAfterProvisioning.getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(portAfterProvisioning.getLineIdPools().size(), portWithInActiveLines.getLineIdPool().intValue());
        Assert.assertEquals(portAfterProvisioning.getHomeIdPools().size(), portWithInActiveLines.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portWithInActiveLines.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portWithInActiveLines.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portWithInActiveLines.getAccessLinesWG().intValue());
    }

    @Test
    @TmsLink("DIGIHUB-29666")
    @Description("Card provisioning case with 1 empty port")
    public void cardProvisioning() throws InterruptedException {

        Card cardBeforeProvisioning = getCard();

        Assert.assertNotNull(cardBeforeProvisioning);
        Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 1);

        wgAccessProvisioningClient.getClient().provisioningProcess().startCardsProvisioning()
                .body(Stream.of(new CardDto().endSz("49/30/179/76H1").slotNumber("5")).collect(Collectors.toList()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY_FOR_PORT_PROVISIONING);

        Card cardAfterProvisioning = getCard();

        long countDefaultNEProfileActive = cardAfterProvisioning.getPorts().get(0).getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = cardAfterProvisioning.getPorts().get(0).getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = cardAfterProvisioning.getPorts().get(0).getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(cardAfterProvisioning.getPorts().get(0).getLineIdPools().size(), portEmpty.getLineIdPool().intValue());
        Assert.assertEquals(cardAfterProvisioning.getPorts().get(0).getHomeIdPools().size(), portEmpty.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portEmpty.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portEmpty.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portEmpty.getAccessLinesWG().intValue());
    }

    @Test
    @TmsLink("DIGIHUB-29667")
    @Description("Device provisioning case")
    public void deviceProvisioning() throws InterruptedException {

        Device deviceBeforeProvisioning = getDevice();

        Assert.assertNotNull(deviceBeforeProvisioning);
        Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(),"MA5600T");
        Assert.assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().size(), 8);

        wgAccessProvisioningClient.getClient().provisioningProcess().startDeviceProvisioning()
                .body(new DeviceDto().endSz("49/30/179/76H1")).executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY_FOR_DEVICE_PROVISIONING);

        Device deviceAfterProvisioning = getDevice();

        long countDefaultNEProfileActive = deviceAfterProvisioning.getEquipmentHolders().get(2).getCard().getPorts().get(0).getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = deviceAfterProvisioning.getEquipmentHolders().get(2).getCard().getPorts().get(0).getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = deviceAfterProvisioning.getEquipmentHolders().get(2).getCard().getPorts().get(0).getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(deviceAfterProvisioning.getEquipmentHolders().get(2).getCard().getPorts().get(0).getLineIdPools().size(), portEmpty.getLineIdPool().intValue());
        Assert.assertEquals(deviceAfterProvisioning.getEquipmentHolders().get(2).getCard().getPorts().get(0).getHomeIdPools().size(), portEmpty.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portEmpty.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portEmpty.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portEmpty.getAccessLinesWG().intValue());
    }

    private Device getDevice() {
        return oltResourceInventoryClient.getClient().deviceInternalController()
                .getOltByEndSZ().endSZQuery("49/30/179/76H1").executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private Card getCard() {
        return oltResourceInventoryClient.getClient().cardController().findCard()
                .endSzQuery("49/30/179/76H1")
                .slotNumberQuery("5")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private Port getPort(PortProvisioning port) {
        return oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(port.getEndSz())
                .slotNumberQuery(port.getSlotNumber())
                .portNumberQuery(port.getPortNumber())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void startPortProvisioning(PortProvisioning port) {
        wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
                .body(new PortDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
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
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
