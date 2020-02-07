package com.tsystems.tm.acc.ta.team.mercury.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_OK_200;

@Slf4j
@Feature("Description olt auto-commissioning incl. LC-Commissioning Testcase on Mercury Team-environment")
@TmsLink("DIGIHUB-XXXXX") // This is the Jira id of a TestSet if applicable
public class OltAutoCommissioning extends BaseTest {

    private OltResourceInventoryClient oltResourceInventoryClient;

    @BeforeClass
    public void init() throws InterruptedException {

        oltResourceInventoryClient = new OltResourceInventoryClient();

        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        log.debug("loginData user= {}, password = {}", loginData.getLogin(), loginData.getPassword() );
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
        RHSSOAuthListener.startListening();
        // clearResourceInventoryDataBase();
    }

    @Test(description = "DIGIHUB-12345 Test description")
    public void OltAutoCommissioningTest() {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
    }

    /**
     * clears complete olt-resource-invemtory database
     */
    private void clearResourceInventoryDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
