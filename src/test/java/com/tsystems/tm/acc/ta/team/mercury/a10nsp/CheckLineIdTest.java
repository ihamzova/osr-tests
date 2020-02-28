package com.tsystems.tm.acc.ta.team.mercury.a10nsp;

import com.tsystems.tm.acc.ta.api.osr.A10nspInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

@Slf4j
@Epic("OS&R")
@Feature("Description olt auto-commissioning incl. LC-Commissioning Testcase on Mercury Team-environment")
@TmsLink("DIGIHUB-52132") // This is the Jira id of TestSet
public class CheckLineIdTest extends BaseTest {

    private static final Integer HTTP_CODE_OK_200 = 200;

    private A10nspInventoryClient a10nspInventoryClient;

    @BeforeClass
    public void init() {
        a10nspInventoryClient = new A10nspInventoryClient();
    }

    @Test(description = "DIGIHUB-12345")
    public void CheckLineIdTestOk() throws Exception {
        a10nspInventoryClient.getClient().a10nspInternalController().checkLineId().body("DEU.DTAG.3WVVC000000")
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
