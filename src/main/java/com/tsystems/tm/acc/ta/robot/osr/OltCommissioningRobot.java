package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.data.osr.models.oltcommissioningresult.OltCommissioningResult;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Card;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.EquipmentHolder;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class OltCommissioningRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 30 * 60_000;
    private static final Integer TIMEOUT_FOR_CARD_PROVISIONING = 20 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();

    @Step("Starts automatic olt commissioning process")
    public void startAutomaticOltCommissioning(OltDevice oltDevice) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(oltDevice);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();

        oltCommissioningPage.validateUrl();
        oltCommissioningPage.startOltCommissioning(oltDevice, TIMEOUT_FOR_OLT_COMMISSIONING);
    }

    @Step("Starts manual olt commissioning process")
    public void startManualOltCommissioning(OltDevice oltDevice) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(oltDevice);

        OltDiscoveryPage oltDiscoveryPage = oltSearchPage.pressManualCommissionigButton();

        oltDiscoveryPage.validateUrl();
        int successfullyDiscoveriesBeforeStart = oltDiscoveryPage.getSuccessfullyDiscoveriesCount();
        oltDiscoveryPage = oltDiscoveryPage.makeOltDiscovery();
        Assert.assertEquals(oltDiscoveryPage.getSuccessfullyDiscoveriesCount(), successfullyDiscoveriesBeforeStart + 1);
        oltDiscoveryPage = oltDiscoveryPage.saveDiscoveryResults();

        oltSearchPage = oltDiscoveryPage.openOltSearchPage();

        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(oltDevice);
        oltDetailsPage.validateUrl();

        UplinkConfigurationPage uplinkConfigurationPage = oltDetailsPage.startUplinkConfiguration();
        uplinkConfigurationPage.validateUrl();
        uplinkConfigurationPage = uplinkConfigurationPage.inputUplinkParameters(oltDevice);
        oltDetailsPage = uplinkConfigurationPage.saveUplinkConfiguration();

        oltDetailsPage = oltDetailsPage.configureAncpSession();
        oltDetailsPage = oltDetailsPage.updateAncpSessionStatus();
        oltDetailsPage.startAccessLinesProvisioning(TIMEOUT_FOR_CARD_PROVISIONING);
    }

    @Step("Checks olt data in olt-ri after commissioning process")
    public void checkOltCommissioningResult(OltCommissioningResult oltCommissioningResult) {
        Device deviceAfterCommissioning = oltResourceInventoryClient.getClient().deviceInternalController()
                .getOltByEndSZ().endSZQuery(oltCommissioningResult.getOltEndSz()).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        List<Integer> countResults = new ArrayList<>();

        deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard).map(Card::getPorts)
                .forEach(ports -> ports.forEach(port -> countResults.add(port.getAccessLines().size())));
        Assert.assertEquals(countResults.stream().mapToInt(Integer::intValue).sum(), oltCommissioningResult.getExpectedAccessLinesCount().intValue());
        countResults.clear();

        deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard).map(Card::getPorts)
                .forEach(ports -> ports.forEach(port -> countResults.add(port.getLineIdPools().size())));
        Assert.assertEquals(countResults.stream().mapToInt(Integer::intValue).sum(), oltCommissioningResult.getExpectedLineIdPoolsSize().intValue());
        countResults.clear();

        deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard).map(Card::getPorts)
                .forEach(ports -> ports.forEach(port -> countResults.add(port.getHomeIdPools().size())));
        Assert.assertEquals(countResults.stream().mapToInt(Integer::intValue).sum(), oltCommissioningResult.getExpectedHomeIdPoolsSize().intValue());
    }

    @Step("Restore OSR Database state")
    public void restoreOsrDbState() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
