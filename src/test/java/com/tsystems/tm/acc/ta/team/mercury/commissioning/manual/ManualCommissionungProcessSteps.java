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

public class ManualCommissionungProcessSteps extends BaseTest {


    @BeforeClass
    public void init() throws InterruptedException {
        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
        RHSSOAuthListener.startListening();
    }

    @Test(description = "DIGIHUB-12345 Olt discovery")
    @TmsLink("DIGIHUB-1") // Jira Id for this test in Xray
    @Description("Searching for new Olt. Start discovery process. Save Disco results.")
    public void SearchAndDiscoverOlt() throws InterruptedException {

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchNotDiscoveredByParameters(getDevice());
        oltSearchPage.pressManualCommissionigButton();
        OltDiscoveryPage oltDiscoveryPage = new OltDiscoveryPage();
        oltDiscoveryPage.makeOltDiscovery();
        oltDiscoveryPage.saveDiscoveryResults();
        oltDiscoveryPage.openOltSearchPage();
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

    @Test(description = "DIGIHUB-12345 Uplink creation")
    @TmsLink("DIGIHUB-2") // Jira Id for this test in Xray
    @Description("Test Description")
    public void UplinkConfiguration() throws InterruptedException {

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(getDevice());// создаем новый объект, если нужно перейти на др страницу
        UplinkConfigurationPage uplinkConfigurationPage = oltDetailsPage.startUplinkConfiguration();
        Nvt nvt = new Nvt();
        nvt.setOltPort("1");
        nvt.setOltSlot("8");
        nvt.setOltDevice(getDevice());
        uplinkConfigurationPage.inputUplinkParameters(nvt);
        oltDetailsPage = uplinkConfigurationPage.saveUplinkConfiguration();
        Thread.sleep(10000);
    }

    @Test(description = "DIGIHUB-12345 ANCP Configuration")
    @TmsLink("DIGIHUB-3") // Jira Id for this test in Xray
    @Description("Test Description")
    public void AncpConfiguration() throws InterruptedException {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(getDevice());
        oltDetailsPage.configureAncpSession();
        oltDetailsPage.updateAncpSessionStatus();
        Thread.sleep(10000);

    }

    @Test(description = "DIGIHUB-12345 ANCP Deconfiguration")
    @TmsLink("DIGIHUB-4") // Jira Id for this test in Xray
    @Description("Test Description")
    public void AncpDeConfiguration() throws InterruptedException {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(getDevice());
        //oltDetailsPage.updateAncpSessionStatus();
        oltDetailsPage.deconfigureAncpSession();
        Thread.sleep(10000);
    }

    @Test(description = "DIGIHUB-12345 Delete Uplink")
    @TmsLink("DIGIHUB-5") // Jira Id for this test in Xray
    @Description("Test Description")
    public void UplinkDeConfiguration() throws InterruptedException {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(getDevice());
        UplinkConfigurationPage uplinkConfigurationPage = oltDetailsPage.startUplinkDeConfiguration();
        uplinkConfigurationPage.deleteUplinkConfiguration();
        Thread.sleep(10000);
    }

}
