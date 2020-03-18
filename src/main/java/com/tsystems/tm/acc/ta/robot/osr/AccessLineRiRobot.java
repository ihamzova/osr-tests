package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class AccessLineRiRobot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_WALLED_GARDEN = "WALLED_GARDEN";

    private ApiClient accessLineResourceInventory = new AccessLineResourceInventoryClient().getClient();

    @Step("Fills database with test data")
    public void clearDatabase() {
        accessLineResourceInventory.fillDatabase().deleteDatabase().execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Checks home id count for port")
    public void checkHomeIdsCount(PortProvisioning port) {
        List<HomeIdDto> homeIds = accessLineResourceInventory.homeIdInternalController().searchHomeIds().body(new SearchHomeIdDto()
                .endSz(port.getEndSz())
                .slotNumber(port.getSlotNumber())
                .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(homeIds.size(), port.getHomeIdPool().intValue(), "Home ids count");
    }

    @Step("Checks line id count for port")
    public void checkLineIdsCount(PortProvisioning port) {
        List<LineIdDto> lineIds = accessLineResourceInventory.lineIdController().searchLineIds().body(new SearchLineIdDto()
                .endSz(port.getEndSz())
                .slotNumber(port.getSlotNumber())
                .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(lineIds.size(), port.getLineIdPool().intValue(), "Line ids count");
    }

    @Step("Checks access lines parameters of port template (lines count and wg lines count, count od default NE and Network profiles)")
    public void checkPortParametersForLines(PortProvisioning port) {
        List<AccessLineDto> accessLines = getAccessLines(port);
        Assert.assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(),
                "Access lines count");

        long countDefaultNEProfileActive = accessLines.stream().map(AccessLineDto::getDefaultNeProfile)
                .filter(defaultNeProfile -> defaultNeProfile != null && defaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = accessLines.stream().map(AccessLineDto::getDefaultNetworkLineProfile)
                .filter(defaultNetworkLineProfile -> defaultNetworkLineProfile != null && defaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = accessLines.stream()
                .filter(accessLine -> accessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();
        Assert.assertEquals(countDefaultNetworkLineProfileActive, port.getDefaultNetworkLineProfilesActive().intValue(),
                "Default Network Line profile count");
        Assert.assertEquals(countDefaultNEProfileActive, port.getDefaultNEProfilesActive().intValue(),
                "Default NE profile count");
        Assert.assertEquals(countAccessLinesWG, port.getAccessLinesWG().intValue(),
                "WG access lines count");
    }

    @Step("Checks A4 specific parameters (NSP ref and phys ref exist, A4 prod platform")
    public void checkA4LineParameters(PortProvisioning port) {
        List<AccessLineDto> accessLines = getAccessLines(port);
        Assert.assertEquals(accessLines.size(), port.getAccessLinesCount().intValue(), "Line ids count");

        AccessLineDto accessLine = accessLines.get(0);

        Assert.assertNotNull(accessLine.getReference(), "Reference");
        Assert.assertEquals(accessLine.getProductionPlatform(), AccessLineDto.ProductionPlatformEnum.A4, "Production platform");
        Assert.assertNotNull(accessLine.getNetworkServiceProfileReference(), "NSP ref");
    }

    private List<AccessLineDto> getAccessLines(PortProvisioning port) {
        return accessLineResourceInventory
                .accessLineInternalController()
                .searchAccessLines()
                .body(new SearchAccessLineDto()
                        .endSz(port.getEndSz())
                        .slotNumber(port.getSlotNumber())
                        .portNumber(port.getPortNumber()))
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

}
