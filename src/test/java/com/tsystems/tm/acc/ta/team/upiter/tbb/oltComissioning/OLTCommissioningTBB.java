package com.tsystems.tm.acc.ta.team.upiter.tbb.oltComissioning;

import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.ta.ui.pages.oltmaintenance.DiscoveryStartenPage;
import com.tsystems.tm.acc.ta.ui.pages.oltmaintenance.OLTSuchePage;
import com.tsystems.tm.acc.ta.ui.pages.oltresourceinventory.OltRiCommissioningPage;
import com.tsystems.tm.acc.ta.ui.pages.oltresourceinventory.OltRiOltDetailPage;
import com.tsystems.tm.acc.ta.ui.pages.oltresourceinventory.OltRiSearchPage;
import com.tsystems.tm.acc.ta.util.SoftAsserter;
import io.qameta.allure.Step;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OLTCommissioningTBB {

    @Step("Automatic OLT Commissioning")
    public void oltCommissioning(OltDevice olt, Nvt nvt) {

        OLTSuchePage suchePage = OLTSuchePage.openPage();
        suchePage.validate();
        suchePage.typeEndSZ(String.format("%s/%s", olt.getVpsz(), olt.getFsz()));
        suchePage.pressSuchenButton();

        DiscoveryStartenPage discoveryStartenPage = suchePage.pressDiscoveryStarten();
        discoveryStartenPage.validate();

        discoveryStartenPage.typeKLSId(olt.getVst().getAddress().getKlsId());
        discoveryStartenPage.typeDownlinkBNGENDSZ(olt.getBngEndsz());
        discoveryStartenPage.typeDownlinkBNGPort(olt.getBngDownlinkPort());
        discoveryStartenPage.typeDownlinkBNGSlot(olt.getBngDownlinkSlot());

        discoveryStartenPage.typeUplinkOltPort(nvt.getOltPort());
        discoveryStartenPage.typeUplinkOltSlot(nvt.getOltSlot());

        discoveryStartenPage.typeOrdungNummer(olt.getOrderNumber());
        discoveryStartenPage.selectLSZ(olt.getLsz());

        discoveryStartenPage.clickDiscoveryStartButton();
        discoveryStartenPage.waitUntilComissioningIsDone();
    }

    public void newOltCommissioning(OltDevice oltData, Nvt nvtData) {
        OltRiSearchPage suchePage = OltRiSearchPage.openPage();
        suchePage.validate();
        suchePage.typeAkz(oltData.getVpsz().split("/")[0]);
        suchePage.typeOnkz(oltData.getVpsz().split("/")[1]);
        suchePage.typeVkz(oltData.getVpsz().split("/")[2]);
        suchePage.typeFsz(oltData.getFsz());
        suchePage.clickSearchButton();
        suchePage.checkOltWasNotFound();
        OltRiCommissioningPage commissioningPage = suchePage.clickStartAutomaticOltCommissioning();
        commissioningPage.validate();
        commissioningPage.typeKls(oltData.getVst().getAddress().getKlsId());
        commissioningPage.typeUplinkSlot(nvtData.getOltSlot());
        commissioningPage.typeUplinkPort(nvtData.getOltPort());

        commissioningPage.typeDownlinkBNGENDSZ(oltData.getBngEndsz());
        commissioningPage.typeDownlinkBNGSlot(oltData.getBngDownlinkSlot());
        commissioningPage.typeDownlinkBNGPort(oltData.getBngDownlinkPort());

        commissioningPage.selectLSZ(oltData.getLsz());
        commissioningPage.typeOrdungNummer(oltData.getOrderNumber());

        commissioningPage.clickStartAutoOltCommissioningButton();

        OltRiOltDetailPage oltDetailPage = commissioningPage.waitUntilComissioningIsDone();
        oltDetailPage.validate();

        SoftAsserter softAssert = new SoftAsserter();
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getEndsz().split("\n")[1], String.format("%s/%s", oltData.getVpsz(), oltData.getFsz()));
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getEndsz().split("\n")[1], String.format("%s/%s", oltData.getVpsz(), oltData.getFsz()));
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getBezeichnung().split("\n")[1], oltData.getBezeichnung());
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getKlsID().split("\n")[1], oltData.getVst().getAddress().getKlsId());

        //todo check access lines

        //todo Hersteller?
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getHersteller().split("\n")[1], oltData.getHersteller());

        //todo Seriennummer?
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getSeriennummer().split("\n")[1], oltData.getSeriennummer());

        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getFirmwareVersion().split("\n")[1], oltData.getFirmwareVersion());
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getIpAdresse().split("\n")[1], oltData.getIpAdresse());
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getStatus().split("\n")[1], "WORKING");
        softAssert.assertEqualsEmptyAndNull(oltDetailPage.getLatestDiscovery().split("\n")[1], new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime()));

        softAssert.assertAll();
    }
}
