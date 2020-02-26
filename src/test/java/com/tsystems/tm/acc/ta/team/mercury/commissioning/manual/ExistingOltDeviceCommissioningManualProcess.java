package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;


import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.UplinkConfigurationPage;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Feature("Description: Manual process of commissioning and decommissioning. Without LC commissioning (domain relevant)")
@TmsLink("DIGIHUB-12345") // This is the Jira id of a TestSet if applicable
public class ExistingOltDeviceCommissioningManualProcess extends BaseTest {


    @BeforeClass
    public void init() throws InterruptedException {
        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
        RHSSOAuthListener.startListening();
    }

    @Test(description = "DIGIHUB-12345 MA5800-X7. Manual process of commissioning and decommissioning. Without LC commissioning (domain relevant)")
    @TmsLink("DIGIHUB-1") // Jira Id for this test in Xray
    @Description("Manual process of commissioning and decommissioning. Without LC commissioning (domain relevant)")
    public void SearchAndDiscoverOlt() throws InterruptedException {

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
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
        oltDetailsPage.startUplinkDeConfiguration();
        uplinkConfigurationPage.deleteUplinkConfiguration();
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
