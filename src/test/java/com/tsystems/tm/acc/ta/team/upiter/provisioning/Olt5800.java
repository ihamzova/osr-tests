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

public class Olt5800 extends ApiTest {

    private static final Integer LATENCY = 2 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private PortProvisioning portEmpty;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        wgAccessProvisioningClient = new WgAccessProvisioningClient();
        portEmpty = OsrTestContext.get().getData()
                .getPortProvisioningDataProvider()
                .get(PortProvisioningCase.portEmpty5800);
    }

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

    @Test
    @TmsLink("DIGIHUB-30877")
    @Description("Port Provisioning with 32 WG Lines")
    public void portProvisioning() throws InterruptedException {

        Port portBeforeProvisioning = getPort(portEmpty);

        Assert.assertEquals(portBeforeProvisioning.getAccessLines().size(), portEmpty.getAccessLinesCount().intValue());

        wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
                .body(new PortDto()
                        .endSz(portEmpty.getEndSz())
                        .slotNumber(portEmpty.getSlotNumber())
                        .portNumber(portEmpty.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY);

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
    @TmsLink("DIGIHUB-30870")
    @Description("Card Provisioning with 1 port")
    public void cardProvisioning() throws InterruptedException {

        Card cardBeforeProvisioning = getCard();

        Assert.assertNotNull(cardBeforeProvisioning);
        Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 1);
        Assert.assertEquals(cardBeforeProvisioning.getPorts().get(0).getAccessLines().size(), 0);

        wgAccessProvisioningClient.getClient().provisioningProcess().startCardsProvisioning()
                .body(Stream.of(new CardDto().endSz("49/911/1100/76H2").slotNumber("3")).collect(Collectors.toList()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY);

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
    @TmsLink("DIGIHUB-30824")
    @Description("Device Provisioning with 1 card and 1 port")
    public void deviceProvisioning() throws InterruptedException {

        Device deviceBeforeProvisioning = getDevice();

        Assert.assertNotNull(deviceBeforeProvisioning);
        Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(),"MA5800-X7");
        Assert.assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().size(), 1);
        Assert.assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getAccessLines().size(), 0);

        wgAccessProvisioningClient.getClient().provisioningProcess().startDeviceProvisioning()
                .body(new DeviceDto().endSz("49/911/1100/76H2")).executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY);

        Device deviceAfterProvisioning = getDevice();

        long countDefaultNEProfileActive = deviceAfterProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = deviceAfterProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = deviceAfterProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(deviceAfterProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getLineIdPools().size(), portEmpty.getLineIdPool().intValue());
        Assert.assertEquals(deviceAfterProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getHomeIdPools().size(), portEmpty.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portEmpty.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portEmpty.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portEmpty.getAccessLinesWG().intValue());
    }

    private Device getDevice() {
        return oltResourceInventoryClient.getClient().deviceInternalController()
                .getOltByEndSZ().endSZQuery("49/911/1100/76H2").executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private Card getCard(){
        return oltResourceInventoryClient.getClient().cardController().findCard()
                .endSzQuery("49/911/1100/76H2")
                .slotNumberQuery("3")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private Port getPort(PortProvisioning port) {
        return oltResourceInventoryClient.getClient().portController().findPortByDeviceEndSzSlotNumPortNum()
                .endSzQuery(port.getEndSz())
                .slotNumberQuery(port.getSlotNumber())
                .portNumberQuery(port.getPortNumber())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void fillDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().fillDatabaseForPortProvisioning()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void clearDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
