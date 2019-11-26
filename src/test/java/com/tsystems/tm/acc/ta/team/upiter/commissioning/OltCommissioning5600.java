package com.tsystems.tm.acc.ta.team.upiter.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioning;
import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioningCase;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.ui.UITest;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_OK_200;

public class OltCommissioning5600 extends UITest {

    private static final Integer LATENCY_FOR_DEVICE_PROVISIONING = 20 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient;

    private OltCommissioning oltCommissioning5600;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
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

        OltSearchPage oltSearchPage = OltSearchPage.openPage();
        oltSearchPage.validate();
        oltSearchPage.searchOltByParameters(oltCommissioning5600);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.searchOlt();
        oltCommissioningPage.validate();
        oltCommissioningPage.insertDataAndStartOltCommissioning(oltCommissioning5600);
        Thread.sleep(20000);
        checkingTestResult();
    }

    private void checkingTestResult(){

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
