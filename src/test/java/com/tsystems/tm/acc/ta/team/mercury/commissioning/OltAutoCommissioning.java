package com.tsystems.tm.acc.ta.team.mercury.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.models.nvt.Nvt;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.nvt.NvtCase;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.ANCPSession;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.UplinkDTO;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

@Slf4j
@Epic("OS&R")
@Feature("Description olt auto-commissioning incl. LC-Commissioning Testcase on Mercury Team-environment")
@TmsLink("DIGIHUB-52132") // This is the Jira id of TestSet
public class OltAutoCommissioning extends BaseTest {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 1 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        clearResourceInventoryDataBase();
    }

    @Test(description = "DIGIHUB-52130 OLT RI UI. Auto Commissioning MA5600 for DTAG user.")
    public void OltAutoCommissioningDTAGTest() throws Exception {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
        RHSSOAuthListener.startListening();

        Nvt nvt = context.getData().getNvtDataProvider().get(NvtCase.nvtForOltAutoCommissioningMA5600);
        OltDevice oltDevice = nvt.getOltDevice();
        String endSz = oltDevice.getVpsz() + "/" + oltDevice.getFsz();
        log.debug("OltAutoCommissioningDTAGTest EndSz = {}, LSZ = {}", endSz, oltDevice.getLsz());

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchNotDiscoveredByParameters(oltDevice);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();
        oltCommissioningPage.validateUrl();

        oltCommissioningPage.startOltCommissioning(nvt, TIMEOUT_FOR_OLT_COMMISSIONING);

        checkDeviceMA5600(endSz);
        checkUplink(endSz);
    }

    @Test(description = "DIGIHUB-52130 OLT RI UI. Auto Commissioning MA5800 for GFNW user.")
    public void OltAutoCommissioningGFNWTest() throws Exception {

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
        RHSSOAuthListener.startListening();

        Nvt nvt = context.getData().getNvtDataProvider().get(NvtCase.nvtForOltAutoCommissioningMA5800);
        OltDevice oltDevice = nvt.getOltDevice();
        String endSz = oltDevice.getVpsz() + "/" + oltDevice.getFsz();
        log.debug("OltAutoCommissioningDTAGTest EndSz = {}, LSZ = {}", endSz, oltDevice.getLsz());

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchNotDiscoveredByParameters(oltDevice);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.pressAutoCommissionigButton();
        oltCommissioningPage.validateUrl();

        oltCommissioningPage.startOltCommissioning(nvt, TIMEOUT_FOR_OLT_COMMISSIONING);

        checkDeviceMA5800(endSz);
        checkUplink(endSz);
    }


    /**
     * check device MA5600 data from olt-ressource-inventory
     */
    private void checkDeviceMA5600(String endsz) {
        Device device = oltResourceInventoryClient.getClient().deviceInternalController().getOltByEndSZ().
                endSZQuery(endsz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(device.getEmsNbiName(), "MA5600T");
        Assert.assertEquals(device.getTkz1(), "02351082");
        Assert.assertEquals(device.getTkz2(), "02353310");
        Assert.assertEquals(device.getType(), Device.TypeEnum.OLT);
    }

    /**
     * check device MA5800 data from olt-ressource-inventory
     */
    private void checkDeviceMA5800(String endsz) {
        Device device = oltResourceInventoryClient.getClient().deviceInternalController().getOltByEndSZ().
                endSZQuery(endsz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));


        Assert.assertEquals(device.getEmsNbiName(), "MA5800-X7");
        Assert.assertEquals(device.getTkz1(), "2352QCR");
        Assert.assertEquals(device.getTkz2(), "02353310");
        Assert.assertEquals(device.getType(), Device.TypeEnum.OLT);
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
     * clears complete olt-resource-invemtory database
     */
    private void clearResourceInventoryDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

}
