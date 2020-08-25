package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltDiscoveryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.*;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private OltDiscoveryClient oltDiscoveryClient = new OltDiscoveryClient();

    @Step("Starts automatic olt commissioning process")
    public void startAutomaticOltCommissioning(OltDevice olt) {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage = oltSearchPage.searchNotDiscoveredByParameters(olt);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();

        oltCommissioningPage.validateUrl();
        oltCommissioningPage.startOltCommissioning(olt, TIMEOUT_FOR_OLT_COMMISSIONING);

        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(olt);
        oltDetailsPage.validateUrl();
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        oltDetailsPage.openPortView(olt.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());
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
        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.NOTOPERATING.toString());
        oltDetailsPage.openPortView(olt.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.NOTOPERATING.toString());

        oltDetailsPage.startUplinkConfiguration();
        oltDetailsPage.inputUplinkParameters(olt);
        oltDetailsPage.saveUplinkConfiguration();

        oltDetailsPage = oltDetailsPage.configureAncpSessionStart();
        oltDetailsPage.startAccessLinesProvisioning(TIMEOUT_FOR_CARD_PROVISIONING);

        Assert.assertEquals(oltDetailsPage.getDeviceLifeCycleState(), DevicePortLifeCycleStateUI.OPERATING.toString());
        oltDetailsPage.openPortView(olt.getOltSlot());
        Assert.assertEquals(oltDetailsPage.getPortLifeCycleState(olt.getOltSlot(), olt.getOltPort()), DevicePortLifeCycleStateUI.OPERATING.toString());
    }


    @Step("Checks olt data in olt-ri after commissioning process")
    public void checkOltCommissioningResult(OltDevice olt) {
        String oltEndSz = olt.getVpsz() + "/" + olt.getFsz();
        long portsCount;

        List<Device> deviceList = oltResourceInventoryClient.getClient().deviceInternalController().findDeviceByCriteria()
                .endszQuery(oltEndSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 1L);
        Assert.assertEquals(deviceList.get(0).getType(), Device.TypeEnum.OLT);
        Assert.assertEquals(deviceList.get(0).getEndSz(), oltEndSz);
        Device deviceAfterCommissioning = deviceList.get(0);

        Optional<Integer> portsCountOptional = deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard)
                .filter(card -> card.getCardType().equals(Card.CardTypeEnum.GPON)).map(card -> card.getPorts().size()).reduce(Integer::sum);
        portsCount = portsCountOptional.orElse(0);

        // add check device and port state
        Assert.assertEquals(/*DevicePortLifeCycleStateUI.OPERATING.toString()*/ "OPERATING", deviceAfterCommissioning.getLifeCycleState().toString());
        //convert to stream
        Optional<Port> uplinkPort = deviceAfterCommissioning.getEquipmentHolders().stream()
                //use filter for search in streams
                .filter(equipmentHolder -> equipmentHolder.getSlotNumber().equals(olt.getOltSlot()))
                //convert Stream<EquipmentHolder> into Stream<List<Card>>
                .map(EquipmentHolder::getCard)
                .filter(card -> card.getCardType().equals(Card.CardTypeEnum.UPLINK_CARD) || card.getCardType().equals(Card.CardTypeEnum.PROCESSING_BOARD))
                //convert Stream<List<Card>> into Stream<Port>
                .flatMap(card -> card.getPorts().stream())
                //method findFirst () returns the first element in the right order from Stream, wrapped Optional
                .filter(port -> port.getPortNumber().equals(olt.getOltPort())).findFirst();

        Assert.assertTrue(uplinkPort.isPresent());
        Assert.assertEquals(/*DevicePortLifeCycleStateUI.OPERATING.toString()*/ "OPERATING",  uplinkPort.get().getLifeCycleState().toString());

        List<AccessLineDto> wgAccessLines = accessLineResourceInventoryClient.getClient().accessLineInternalController().searchAccessLines()
                .body(new SearchAccessLineDto().endSz(oltEndSz)).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                .stream().filter(accessLineDto -> accessLineDto.getStatus().equals(AccessLineDto.StatusEnum.WALLED_GARDEN)).collect(Collectors.toList());
        long wgLinesCount = wgAccessLines.size();

        Assert.assertEquals(wgLinesCount, portsCount * ACCESS_LINE_PER_PORT);

        boolean allPortsInOperatingState = deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard)
                .filter(card -> card.getCardType().equals(Card.CardTypeEnum.GPON)).map(Card::getPorts)
                .flatMap(List::stream).map(Port::getLifeCycleState).allMatch(Port.LifeCycleStateEnum.OPERATING::equals);

        Assert.assertTrue(allPortsInOperatingState, "Some port is in not OPERATING state");

        List<Integer> anpTagsList = wgAccessLines.stream().map(accessLineDto -> accessLineDto.getAnpTag().getAnpTag())
                .filter(anpTagValue -> anpTagValue >= 128).collect(Collectors.toList());

        Assert.assertEquals(anpTagsList.size(), portsCount * ACCESS_LINE_PER_PORT);

        Assert.assertTrue(anpTagsList.contains(128));

        List<UplinkDTO> uplinksList = oltResourceInventoryClient.getClient().ethernetController().findEthernetLinksByEndsz().oltEndSzQuery(oltEndSz)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(uplinksList.size(), 1);

        UplinkDTO uplink = uplinksList.get(0);

        Assert.assertEquals(uplink.getIpStatus(), UplinkDTO.IpStatusEnum.ACTIVE);

        Assert.assertEquals(uplink.getAncpSessions().size(), 1);

        Assert.assertEquals(uplink.getAncpSessions().get(0).getSessionStatus(), ANCPSession.SessionStatusEnum.ACTIVE);

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
        accessLineResourceInventoryClient.getClient().fillDatabase().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        oltDiscoveryClient.reset();
    }

    @Step("Clear {oltDevice} device in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(OltDevice oltDevice) {
        String endSz = oltDevice.getVpsz() + "/" + oltDevice.getFsz();
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
