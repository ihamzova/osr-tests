package com.tsystems.tm.acc.ta.team.upiter.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioning;
import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioningCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.AccessLine;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Device;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.ui.UITest;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.*;

public class OltCommissioning5600 extends UITest {

    private static final Integer LATENCY_FOR_DEVICE_PROVISIONING = 20 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient;

    private OltCommissioning oltCommissioning5600;
    private PortProvisioning portEmpty;
    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        portEmpty = OsrTestContext.get().getData().getPortProvisioningDataProvider().get(PortProvisioningCase.portEmpty5600);
        oltCommissioning5600 = OsrTestContext.get().getData().getOltCommissioningDataProvider().get(OltCommissioningCase.MA5600);
        Credentials loginData = OsrTestContext.get().getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());

    }

//    @BeforeMethod
//    public void prepareData() throws InterruptedException {
//        clearDataBase();
//        Thread.sleep(1000);
//        fillDataBase();
//    }
//
//    @AfterMethod
//    public void clearData() {
//        clearDataBase();
//    }

    @Test
    public void automaticallyOltCommissioning() throws InterruptedException{

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchOltByParameters(oltCommissioning5600);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.openOltCommissioningAutomaticallyPage();
        oltCommissioningPage.validateUrl();
        oltCommissioningPage.startOltCommissioning(oltCommissioning5600);

        //checkingTestResult();
    }

    private void checkingTestResult(){

        Device deviceAfterCommissioning = getDevice();

        int portNumber = (int) (Math.random() * deviceAfterCommissioning.getEquipmentHolders().get(0).getCard().getPorts().size());

        long countDefaultNEProfileActive = deviceAfterCommissioning.getEquipmentHolders().get(0).getCard().getPorts().get(portNumber).getAccessLines().stream().map(AccessLine::getDefaultNeProfile)
                .filter(DefaultNeProfile -> DefaultNeProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countDefaultNetworkLineProfileActive = deviceAfterCommissioning.getEquipmentHolders().get(0).getCard().getPorts().get(portNumber).getAccessLines().stream().map(AccessLine::getDefaultNetworkLineProfile)
                .filter(DefaultNetworkLineProfile -> DefaultNetworkLineProfile.getState().getValue().equals(STATUS_ACTIVE)).count();

        long countAccessLinesWG = deviceAfterCommissioning.getEquipmentHolders().get(0).getCard().getPorts().get(portNumber).getAccessLines().stream()
                .filter(AccessLine -> AccessLine.getStatus().getValue().equals(STATUS_WALLED_GARDEN)).count();

        Assert.assertEquals(deviceAfterCommissioning.getEquipmentHolders().get(0).getCard().getPorts().get(portNumber).getLineIdPools().size(), portEmpty.getLineIdPool().intValue());
        Assert.assertEquals(deviceAfterCommissioning.getEquipmentHolders().get(0).getCard().getPorts().get(portNumber).getHomeIdPools().size(), portEmpty.getHomeIdPool().intValue());
        Assert.assertEquals(countDefaultNetworkLineProfileActive, portEmpty.getDefaultNetworkLineProfilesActive().intValue());
        Assert.assertEquals(countDefaultNEProfileActive, portEmpty.getDefaultNEProfilesActive().intValue());
        Assert.assertEquals(countAccessLinesWG, portEmpty.getAccessLinesWG().intValue());
    }

    private Device getDevice() {
        return oltResourceInventoryClient.getClient().deviceInternalController()
                .getOltByEndSZ().endSZQuery("49/8571/0/76HA").executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void fillDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().fillDatabaseForOltCommissioning()
                .END_SZQuery("49/30/179/76H1")
                .KLS_IDQuery("14653")
                .slOTNUMBER1Query("3")
                .slOTNUMBER2Query("4")
                .slOTNUMBER3Query("5")
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void clearDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
