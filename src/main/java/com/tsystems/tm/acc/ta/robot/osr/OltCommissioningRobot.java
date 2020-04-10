package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Card;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.EquipmentHolder;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;
import java.util.Optional;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class OltCommissioningRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 40 * 60_000;
    private static final Integer TIMEOUT_FOR_CARD_PROVISIONING = 25 * 60_000;
    private static final Integer ACCESS_LINE_PER_PORT = 16;
    private static final Integer LINE_ID_POOL_PER_PORT = 32;
    private static final Integer HOME_ID_POOL_PER_PORT = 32;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();

    @Step("Starts automatic olt commissioning process")
    public void startAutomaticOltCommissioning(OltDevice olt) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(olt);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();

        oltCommissioningPage.validateUrl();
        oltCommissioningPage.startOltCommissioning(olt, TIMEOUT_FOR_OLT_COMMISSIONING);
    }

    @Step("Starts manual olt commissioning process")
    public void startManualOltCommissioning(OltDevice olt) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(olt);

        OltDiscoveryPage oltDiscoveryPage = oltSearchPage.pressManualCommissionigButton();

        oltDiscoveryPage.validateUrl();
        int successfullyDiscoveriesBeforeStart = oltDiscoveryPage.getSuccessfullyDiscoveriesCount();
        oltDiscoveryPage = oltDiscoveryPage.makeOltDiscovery();
        Assert.assertEquals(oltDiscoveryPage.getSuccessfullyDiscoveriesCount(), successfullyDiscoveriesBeforeStart + 1);
        oltDiscoveryPage = oltDiscoveryPage.saveDiscoveryResults();

        oltSearchPage = oltDiscoveryPage.openOltSearchPage();

        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
        oltDetailsPage.validateUrl();

        oltDetailsPage.startUplinkConfiguration();
        oltDetailsPage.inputUplinkParameters(olt);
        oltDetailsPage.saveUplinkConfiguration();

        oltDetailsPage = oltDetailsPage.configureAncpSession();
        oltDetailsPage.startAccessLinesProvisioning(TIMEOUT_FOR_CARD_PROVISIONING);
    }

    @Step("Checks olt data in olt-ri after commissioning process")
    public void checkOltCommissioningResult(OltDevice olt) {
        String oltEndSz = olt.getVpsz() + "/" + olt.getFsz();
        long portsCount;

        Device deviceAfterCommissioning = oltResourceInventoryClient.getClient().deviceInternalController()
                .getOltByEndSZ().endSZQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Optional<Integer> portsCountOptional = deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard)
                .filter(card -> card.getCardType().equals(Card.CardTypeEnum.GPON)).map(card -> card.getPorts().size()).reduce(Integer::sum);
        portsCount = portsCountOptional.orElse(0);

        long wgLinesCount = accessLineResourceInventoryClient.getClient().accessLineInternalController().searchAccessLines()
                .body(new SearchAccessLineDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                .stream().filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineDto.StatusEnum.WALLED_GARDEN)).count();

        Assert.assertEquals(wgLinesCount, portsCount * ACCESS_LINE_PER_PORT);

        long homeIdCount = accessLineResourceInventoryClient.getClient().homeIdInternalController().searchHomeIds()
                .body(new SearchHomeIdDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                .stream().filter(homeIdDto -> homeIdDto.getStatus().equals(HomeIdDto.StatusEnum.FREE)).count();

        Assert.assertEquals(homeIdCount, portsCount * HOME_ID_POOL_PER_PORT);

        long backhaulIdCount = accessLineResourceInventoryClient.getClient().backhaulIdController().searchBackhaulIds()
                .body(new SearchBackhaulIdDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                .stream().filter(backhaulIdDto -> BackhaulIdDto.StatusEnum.CONFIGURED.equals(backhaulIdDto.getStatus())).count();

        Assert.assertEquals(backhaulIdCount, portsCount);

        List<LineIdDto> lineIdDtos = accessLineResourceInventoryClient.getClient().lineIdController().searchLineIds()
                .body(new SearchLineIdDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        long freeLineIdCount = lineIdDtos.stream().filter(lineIdDto -> lineIdDto.getStatus().equals(LineIdDto.StatusEnum.FREE)).count();
        long usedLineIdCount = lineIdDtos.stream().filter(lineIdDto -> lineIdDto.getStatus().equals(LineIdDto.StatusEnum.USED)).count();

        Assert.assertEquals(freeLineIdCount, portsCount * LINE_ID_POOL_PER_PORT / 2);
        Assert.assertEquals(usedLineIdCount, portsCount * LINE_ID_POOL_PER_PORT / 2);
    }

    @Step("Restore OSR Database state")
    public void restoreOsrDbState() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        accessLineResourceInventoryClient.getClient().fillDatabase().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
