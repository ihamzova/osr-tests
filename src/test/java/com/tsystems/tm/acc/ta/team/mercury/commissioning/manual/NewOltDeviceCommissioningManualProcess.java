package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.UplinkConfigurationPage;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
public class NewOltDeviceCommissioningManualProcess extends BaseTest {

    @BeforeClass
    public void init() throws InterruptedException {
        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
        RHSSOAuthListener.startListening();
    }

    @Test(description = "DIGIHUB-1 New device commissioning")
    @TmsLink("DIGIHUB-1") // Jira Id for this test in Xray
    @Description("Looking for new Olt. Perform commissioning")
    public void SearchAndDiscoverOlt() throws InterruptedException {

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchNotDiscoveredByParameters(getDevice());
        oltSearchPage.pressManualCommissionigButton();
        OltDiscoveryPage oltDiscoveryPage = new OltDiscoveryPage();
        oltDiscoveryPage.makeOltDiscovery();
        oltDiscoveryPage.saveDiscoveryResults();
        oltDiscoveryPage.openOltSearchPage();
        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(getDevice());
        UplinkConfigurationPage uplinkConfigurationPage = oltDetailsPage.startUplinkConfiguration();
        Nvt nvt = new Nvt();
        nvt.setOltPort("1");
        nvt.setOltSlot("8");
        nvt.setOltDevice(getDevice());
        uplinkConfigurationPage.inputUplinkParameters(nvt);
        uplinkConfigurationPage.saveUplinkConfiguration();
        oltDetailsPage.configureAncpSession();
        oltDetailsPage.updateAncpSessionStatus();
        oltDetailsPage.deconfigureAncpSession();
        UplinkConfigurationPage uplinkConfigurationPage1 = oltDetailsPage.startUplinkDeConfiguration();
        uplinkConfigurationPage1.deleteUplinkConfiguration();
        Thread.sleep(10000);

    }

    private OltDevice getDevice() {
        OltDevice device = new OltDevice();
        device.setVpsz("49/911/1100/");
        device.setFsz("76H1");
        device.setLsz("4C1");
        device.setBngEndsz("49/30/179/43G1");
        device.setBngDownlinkPort("ge-1/2/3");
        device.setBngDownlinkSlot("7");
        device.setOrderNumber("0123456789");
        return device;
    }
}


