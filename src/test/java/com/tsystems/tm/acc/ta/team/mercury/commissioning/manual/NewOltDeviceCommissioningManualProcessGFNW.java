package com.tsystems.tm.acc.ta.team.mercury.commissioning.manual;

import com.tsystems.tm.acc.data.models.Credentials;
import com.tsystems.tm.acc.data.models.Nvt;
import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltDiscoveryPage;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.ANCPSession;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.UplinkDTO;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

@Slf4j
public class NewOltDeviceCommissioningManualProcessGFNW extends BaseTest {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final String EMS_NBI_NAME_MA5600 = "MA5600T";

    private OltResourceInventoryClient oltResourceInventoryClient;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
    }

    @Test(description = "DIGIHUB-53713 Manual commissioning for MA5600 with GFNW user on team environment")
    @TmsLink("DIGIHUB-53713") // Jira Id for this test in Xray
    @Description("Perform manual commissioning for not discovered MA5600 device as GFNW user")
    public void SearchAndDiscoverOlt() throws InterruptedException {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        String endSz = getDevice().getVpsz() + getDevice().getFsz();
        clearResourceInventoryDataBase(endSz);
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();

        oltSearchPage.searchNotDiscoveredByParameters(getDevice());
        oltSearchPage.pressManualCommissionigButton();
        OltDiscoveryPage oltDiscoveryPage = new OltDiscoveryPage();
        oltDiscoveryPage.makeOltDiscovery();
        oltDiscoveryPage.saveDiscoveryResults();
        oltDiscoveryPage.openOltSearchPage();

        Thread.sleep(2000);
        OltDetailsPage oltDetailsPage = oltSearchPage.searchDiscoveredOltByParameters(getDevice());
        oltDetailsPage.startUplinkConfiguration();
        oltDetailsPage.inputUplinkParameters(getDevice());
        oltDetailsPage.saveUplinkConfiguration();
        oltDetailsPage.modifyUplinkConfiguration();

        oltDetailsPage.configureAncpSession();
        oltDetailsPage.updateAncpSessionStatus();
        oltDetailsPage.checkAncpSessionStatus();

        checkDeviceMA5600(endSz);
        checkUplink(endSz);

        oltDetailsPage.deconfigureAncpSession();
        oltDetailsPage.deleteUplinkConfiguration();

        Thread.sleep(1000); // ensure that the resource inventory database is updated
        checkUplinkDeleted(endSz);
    }

    // private
    private OltDevice getDevice() {
        OltDevice device = new OltDevice();
        device.setVpsz("49/8571/0/");
        device.getVpsz();
        device.setFsz("76Z7");
        device.setLsz("4Z2");
        device.setOltPort("0");
        device.setOltSlot("19");
        device.setBngEndsz("49/911/84/7ZJE");
        device.setBngDownlinkPort("ge-2/1/4");
        device.setBngDownlinkSlot("2");
        device.setOrderNumber("0123456789");
        return device;
    }


    /**
     * check device MA5600 data from olt-ressource-inventory
     */
    private void checkDeviceMA5600(String endSz) {
        Device device = oltResourceInventoryClient.getClient().deviceInternalController().getOltByEndSZ().
                endSZQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));


        Assert.assertEquals(device.getEmsNbiName(), "MA5600T");
        Assert.assertEquals(device.getTkz1(), "02351082");
        Assert.assertEquals(device.getTkz2(), "02353310");
        Assert.assertEquals(device.getType(), Device.TypeEnum.OLT);

        OltDetailsPage oltDetailsPage = new OltDetailsPage();
        oltDetailsPage.validateUrl();
        Assert.assertEquals(oltDetailsPage.getEndsz(), endSz);
        Assert.assertEquals(oltDetailsPage.getBezeichnung(), EMS_NBI_NAME_MA5600);
        Assert.assertEquals(oltDetailsPage.getKlsID(), "17056514");
    }

    /**
     * check uplink and ancp-session data from olt-ressource-inventory
     */
    private void checkUplink(String endSz) {
        List<UplinkDTO> uplinkDTOList = oltResourceInventoryClient.getClient().ethernetController().findEthernetLinksByEndsz()
                .oltEndSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(uplinkDTOList.size(), 1L);
        Assert.assertEquals(uplinkDTOList.get(0).getAncpSessions().size(), 1L);
        Assert.assertEquals(uplinkDTOList.get(0).getAncpSessions().get(0).getSessionStatus(), ANCPSession.SessionStatusEnum.ACTIVE);
    }

    /**
     * check uplink is not exist in olt-resource-inventory
     */
    private void checkUplinkDeleted(String endSz) {
        List<UplinkDTO> uplinkDTOList = oltResourceInventoryClient.getClient().ethernetController().findEthernetLinksByEndsz()
                .oltEndSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertTrue(uplinkDTOList.isEmpty());
    }

    /**
     * clears complete olt-resource-invemtory database
     */
    private void clearResourceInventoryDataBase(String endSz) {
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }


}


