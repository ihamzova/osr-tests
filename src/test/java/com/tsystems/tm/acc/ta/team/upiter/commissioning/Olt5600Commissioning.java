package com.tsystems.tm.acc.ta.team.upiter.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioning;
import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioningCase;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.ui.UITest;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltCommssioningPage;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_OK_200;

public class Olt5600Commissioning extends UITest {

    private OsrTestContext context = OsrTestContext.get();
    private OltResourceInventoryClient oltResourceInventoryClient;
    private OltCommissioning oltCommissioning;

    @BeforeClass
    public void setLoginData() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @BeforeMethod
    public void prepareData() throws InterruptedException {
       // clearDataBase();
       // Thread.sleep(100);
    }

    @AfterMethod
    public void clearData() {
      //  clearDataBase();
    }

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        oltCommissioning = OsrTestContext.get().getData().getOltCommissioningDataProvider().get(OltCommissioningCase.MA5600);
    }

    @Test
    public void oltCommissioningTest() {
        oltCommissioning = context.getData().getOltCommissioningDataProvider().get(OltCommissioningCase.MA5600);
        OltSearchPage oltSearchPage = OltSearchPage.openPage();
        oltSearchPage.validate();
        oltSearchPage.searchOlt(oltCommissioning);
        OltCommssioningPage oltCommssioningPage = oltSearchPage.searchOlt();
        oltCommssioningPage.validate();
        oltCommssioningPage.startOltCommissioning(oltCommissioning);
    }

    private void clearDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
