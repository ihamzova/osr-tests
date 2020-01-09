package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Card;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.EquipmentHolder;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class OltCommissioningRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 30 * 60_000;
    private static final Integer TIMEOUT_FOR_CARD_PROVISIONING = 20 * 60_000;
    private static final Integer ACCESS_LINE_PER_PORT = 16;
    private static final Integer LINE_ID_POOL_PER_PORT = 32;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();

    @Step("Starts automatic olt commissioning process")
    public void startAutomaticOltCommissioning(Nvt nvt) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(nvt.getOltDevice());

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();

        oltCommissioningPage.validateUrl();
        oltCommissioningPage.startOltCommissioning(nvt, TIMEOUT_FOR_OLT_COMMISSIONING);
    }

    @Step("Starts manual olt commissioning process")
    public void startManualOltCommissioning(Nvt nvt) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(nvt.getOltDevice());

        OltDiscoveryPage oltDiscoveryPage = oltSearchPage.pressManualCommissionigButton();

        oltDiscoveryPage.validateUrl();
        int successfullyDiscoveriesBeforeStart = oltDiscoveryPage.getSuccessfullyDiscoveriesCount();
        oltDiscoveryPage = oltDiscoveryPage.makeOltDiscovery();
        Assert.assertEquals(oltDiscoveryPage.getSuccessfullyDiscoveriesCount(), successfullyDiscoveriesBeforeStart + 1);
        oltDiscoveryPage = oltDiscoveryPage.saveDiscoveryResults();

        oltSearchPage = oltDiscoveryPage.openOltSearchPage();

        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(nvt.getOltDevice());
        oltDetailsPage.validateUrl();

        UplinkConfigurationPage uplinkConfigurationPage = oltDetailsPage.startUplinkConfiguration();
        uplinkConfigurationPage.validateUrl();
        uplinkConfigurationPage = uplinkConfigurationPage.inputUplinkParameters(nvt);
        oltDetailsPage = uplinkConfigurationPage.saveUplinkConfiguration();

        oltDetailsPage = oltDetailsPage.configureAncpSession();
        oltDetailsPage = oltDetailsPage.updateAncpSessionStatus();
        oltDetailsPage.startAccessLinesProvisioning(TIMEOUT_FOR_CARD_PROVISIONING);
    }

    @Step("Checks olt data in olt-ri after commissioning process")
    public void checkOltCommissioningResult(Nvt nvt) {
        String oltEndSz = nvt.getOltDevice().getVpsz() + "/" + nvt.getOltDevice().getFsz();
        int portsCount;

        Device deviceAfterCommissioning = oltResourceInventoryClient.getClient().deviceInternalController()
                .getOltByEndSZ().endSZQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Optional<Integer> portsCountOptional = deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard)
                .filter(card -> card.getCardType().equals(Card.CardTypeEnum.GPON)).map(card -> card.getPorts().size()).reduce(Integer::sum);
        portsCount = portsCountOptional.orElse(0);

        List<Integer> countResults = new ArrayList<>();

        deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard).map(Card::getPorts)
                .forEach(ports -> ports.forEach(port -> countResults.add(port.getAccessLines().size())));
        Assert.assertEquals(countResults.stream().mapToInt(Integer::intValue).sum(), portsCount * ACCESS_LINE_PER_PORT);
        countResults.clear();

        deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard).map(Card::getPorts)
                .forEach(ports -> ports.forEach(port -> countResults.add(port.getLineIdPools().size())));
        Assert.assertEquals(countResults.stream().mapToInt(Integer::intValue).sum(), portsCount * LINE_ID_POOL_PER_PORT);
        countResults.clear();
    }

    @Step("Restore OSR Database state")
    public void restoreOsrDbState() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
