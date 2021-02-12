package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryBrowserRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Slf4j
public class A4ResourceInventoryBrowserTest extends BaseTest {

    private final A4ResourceInventoryBrowserRobot a4ResourceInventoryBrowserRobot = new A4ResourceInventoryBrowserRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4InventarSuchePage a4InventarSuchePage = new A4InventarSuchePage();
    private final A4ImportPage a4ImportPage = new A4ImportPage();
    private final A4MobileNeSearchPage a4MobileNeSearchPage = new A4MobileNeSearchPage();

    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserInventoryImportButton() throws InterruptedException {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        a4ResourceInventoryBrowserRobot.clickInventoryImportButton();
        a4ImportPage.validate();

    }
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserInventorySearchButton() throws InterruptedException {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        a4ResourceInventoryBrowserRobot.clickInventorySearchButton();
        a4InventarSuchePage.validate();
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserMobilUiButton() throws InterruptedException {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        a4ResourceInventoryBrowserRobot.clickMobilUiButton();
        a4MobileNeSearchPage.validate();
    }


}
