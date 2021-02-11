package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
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

    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserLogin() {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        // a4ResourceInventoryBrowserRobot.clickInventorySearchButton();
    }


    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserInventoryImportButton() throws InterruptedException {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        //Thread.sleep(2000);
        a4ResourceInventoryBrowserRobot.clickInventoryImportButton();
        Thread.sleep(5000);
    }
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserInventorySearchButton() throws InterruptedException {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        //Thread.sleep(2000);
        a4ResourceInventoryBrowserRobot.clickInventorySearchButton(); // wie teste ich, ob sich die Seite geöffnet hat?
        Thread.sleep(10000);
    }
    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserMobilUiButton() throws InterruptedException {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        //Thread.sleep(2000);
        a4ResourceInventoryBrowserRobot.clickMobilUiButton();
        Thread.sleep(5000);
    }




}
