package com.tsystems.tm.acc.ta.team.upiter.provisioning;

import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.api.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.WgAccessProvisioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.CardDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.DeviceDto;
import com.tsystems.tm.acc.tests.osr.wg.access.provisioning.internal.client.model.PortDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.*;

public class OltProvisioning5800 extends ApiTest {

    private static final Integer LATENCY = 2 * 70_000;

    private OltResourceInventoryClient oltResourceInventoryClient;
    private WgAccessProvisioningClient wgAccessProvisioningClient;
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient;
    private PortProvisioning portEmpty;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();
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
        List<AccessLineDto> accessLinesBeforeProvisioning = getAccessLines(portEmpty);

        Assert.assertEquals(accessLinesBeforeProvisioning.size(), portEmpty.getAccessLinesCount().intValue());

        wgAccessProvisioningClient.getClient().provisioningProcess().startPortProvisioning()
                .body(new PortDto()
                        .endSz(portEmpty.getEndSz())
                        .slotNumber(portEmpty.getSlotNumber())
                        .portNumber(portEmpty.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY);

        checkResults(portEmpty);
    }

    @Test
    @TmsLink("DIGIHUB-30870")
    @Description("Card Provisioning with 1 port")
    public void cardProvisioning() throws InterruptedException {

        Card cardBeforeProvisioning = getCard();
        PortProvisioning port = getPortProvisioning("49/911/1100/76H2",
                "3",
                cardBeforeProvisioning.getPorts().get(0).getPortNumber());

        Assert.assertNotNull(cardBeforeProvisioning);
        Assert.assertEquals(cardBeforeProvisioning.getPorts().size(), 1);
        Assert.assertEquals(getAccessLines(port).size(), 0);

        wgAccessProvisioningClient.getClient().provisioningProcess().startCardsProvisioning()
                .body(Stream.of(new CardDto().endSz("49/911/1100/76H2").slotNumber("3")).collect(Collectors.toList()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY);

        checkResults(port);
    }

    @Test
    @TmsLink("DIGIHUB-30824")
    @Description("Device Provisioning with 1 card and 1 port")
    public void deviceProvisioning() throws InterruptedException {

        Device deviceBeforeProvisioning = getDevice();

        PortProvisioning port = getPortProvisioning("49/911/1100/76H2",
                deviceBeforeProvisioning.getEquipmentHolders().get(0).getSlotNumber(),
                deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortNumber());

        Assert.assertNotNull(deviceBeforeProvisioning);
        Assert.assertEquals(deviceBeforeProvisioning.getEmsNbiName(), "MA5800-X7");
        Assert.assertEquals(deviceBeforeProvisioning.getEquipmentHolders().get(0).getCard().getPorts().size(), 1);
        Assert.assertEquals(getAccessLines(port).size(), 0);

        wgAccessProvisioningClient.getClient().provisioningProcess().startDeviceProvisioning()
                .body(new DeviceDto().endSz("49/911/1100/76H2")).executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

        Thread.sleep(LATENCY);

        checkResults(port);
    }

    private void checkResults(PortProvisioning port) {
        List<AccessLineDto> accessLinesAfterProvisioning = getAccessLines(port);

        long countDefaultNEProfileActive = accessLinesAfterProvisioning.stream().map(AccessLineDto::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = accessLinesAfterProvisioning.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = accessLinesAfterProvisioning.stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(getLineIdPools(port).size(), portEmpty.getLineIdPool().intValue());
        Assert.assertEquals(getHomeIdPools(port).size(), portEmpty.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portEmpty.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portEmpty.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portEmpty.getAccessLinesWG().intValue());
    }

    private List<AccessLineDto> getAccessLines(PortProvisioning port) {
        return accessLineResourceInventoryClient.getClient().accessLineInternalController().searchAccessLines().body(
                new SearchAccessLineDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private List<LineIdDto> getLineIdPools(PortProvisioning port) {
        return accessLineResourceInventoryClient.getClient().lineIdController().searchLineIds().body(
                new SearchLineIdDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private PortProvisioning getPortProvisioning(String endSz, String slotNumber, String portNumber) {
        PortProvisioning port = new PortProvisioning();
        port.setEndSz(endSz);
        port.setSlotNumber(slotNumber);
        port.setPortNumber(portNumber);
        port.setLineIdPool(portEmpty.getLineIdPool());
        port.setHomeIdPool(portEmpty.getHomeIdPool());
        port.setDefaultNEProfilesActive(portEmpty.getDefaultNEProfilesActive());
        port.setDefaultNetworkLineProfilesActive(portEmpty.getDefaultNetworkLineProfilesActive());
        port.setAccessLinesWG(portEmpty.getAccessLinesWG());
        return port;
    }

    private List<HomeIdDto> getHomeIdPools(PortProvisioning port) {
        return accessLineResourceInventoryClient.getClient().homeIdInternalController().searchHomeIds().body(
                new SearchHomeIdDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private Device getDevice() {
        return oltResourceInventoryClient.getClient().deviceInternalController()
                .getOltByEndSZ().endSZQuery("49/911/1100/76H2").executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private Card getCard() {
        return oltResourceInventoryClient.getClient().cardController().findCard()
                .endSzQuery("49/911/1100/76H2")
                .slotNumberQuery("3")
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void fillDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().fillDatabaseForPortProvisioning()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        accessLineResourceInventoryClient.getClient().fillDatabase().fillDatabaseForOltCommissioning()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void clearDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        accessLineResourceInventoryClient.getClient().fillDatabase().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
