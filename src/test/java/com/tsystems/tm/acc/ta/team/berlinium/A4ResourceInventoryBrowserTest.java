package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ImportPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryBrowserRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Slf4j
@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_RESOURCE_INVENTORY_UI_MS,A4_RESOURCE_INVENTORY_BFF_PROXY_MS,A4_INVENTORY_IMPORTER_MS})
@Epic("OS&R")

public class A4ResourceInventoryBrowserTest extends GigabitTest {

    private final A4ResourceInventoryBrowserRobot a4ResourceInventoryBrowserRobot = new A4ResourceInventoryBrowserRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4InventarSuchePage a4InventarSuchePage = new A4InventarSuchePage();
    private final A4ImportPage a4ImportPage = new A4ImportPage();
    private final A4MobileNeSearchPage a4MobileNeSearchPage = new A4MobileNeSearchPage();

    @BeforeMethod()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserInventoryImportButton() {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        a4ResourceInventoryBrowserRobot.clickInventoryImportButton();
        a4ImportPage.validate();
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserInventorySearchButton() {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        a4ResourceInventoryBrowserRobot.clickInventorySearchButton();
        a4InventarSuchePage.validate();
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test RI Browser")
    public void testRiBrowserMobilUiButton() {
        a4ResourceInventoryBrowserRobot.openRiBrowserPage();
        a4ResourceInventoryBrowserRobot.clickMobilUiButton();
        a4MobileNeSearchPage.validate();
    }

}
