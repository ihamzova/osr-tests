package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.*;

@Slf4j
public class A4MobileNeSearchPageTest extends BaseTest {

    private final A4MobileUiRobot a4MobileUiRobot = new A4MobileUiRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    A4MobileNeSearchPage a4MobileNeSearchPage = new A4MobileNeSearchPage();

    private A4NetworkElementGroup a4NetworkElementGroup;

    private Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();

    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        a4NetworkElements.put("a4NetworkElementInstallingOlt01", osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingOlt01));

        a4NetworkElements.put("a4NetworkElementInstallingSpine01", osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingSpine01));

        a4NetworkElements.put("a4NetworkElementOperatingBor01",osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementOperatingBor01));

        a4NetworkElements.put("a4NetworkElementPlanningLeafSwitch01",osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementPlanningLeafSwitch01));

        a4NetworkElements.put("a4NetworkElementPodServer01",osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01));

        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);

        a4NetworkElements.forEach((k, networkElement)->
               a4ResourceInventoryRobot.createNetworkElement(networkElement, a4NetworkElementGroup));
    }

    @AfterMethod
    public void cleanUp() {

        a4NetworkElements.forEach((k,v)->
                a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(v));

        a4ResourceInventoryRobot.deleteNetworkElementGroups(a4NetworkElementGroup);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpsz() throws InterruptedException {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        //assumption is that all elements have the same VPSZ, so we chose first elements' VPSZ
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get("a4NetworkElementInstallingOlt01").getVpsz());
        a4MobileUiRobot.clickSearchButton();

        $(a4MobileNeSearchPage.getSEARCH_RESULT_TABLE_LOCATOR()).shouldBe(Condition.visible);

        ElementsCollection elementsCollection = $(a4MobileNeSearchPage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        Thread.sleep(3000);

        List<String> conn = new ArrayList<>();

        elementsCollection.forEach(k -> conn.add(k.getText()));

        a4NetworkElements.forEach((k,networkElement) -> {
            assertTrue(conn.contains(networkElement.getFsz()),networkElement.getFsz());
            assertTrue(conn.contains(networkElement.getCategory()),networkElement.getCategory());
            assertTrue(conn.contains(networkElement.getLifecycleState()),networkElement.getLifecycleState());
            assertTrue(conn.contains(networkElement.getVpsz()),networkElement.getVpsz());
            assertTrue(conn.contains(networkElement.getPlanningDeviceName()),networkElement.getPlanningDeviceName());
        });

    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpszAndFsz() throws InterruptedException {
        String olt = "a4NetworkElementInstallingOlt01";
        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(olt).getVpsz());
        a4MobileUiRobot.enterFsz(a4NetworkElements.get(olt).getFsz());
        a4MobileUiRobot.clickSearchButton();

        $(a4MobileNeSearchPage.getSEARCH_RESULT_TABLE_LOCATOR()).shouldBe(Condition.visible);

        ElementsCollection elementsCollection = $(a4MobileNeSearchPage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        Thread.sleep(3000);

        List<String> conn = new ArrayList<>();

        elementsCollection.forEach(k -> conn.add(k.getText()));

        assertTrue(conn.contains(a4NetworkElements.get(olt).getVpsz()));
        assertTrue(conn.contains(a4NetworkElements.get(olt).getFsz()));
        assertFalse(conn.contains(a4NetworkElements.get("a4NetworkElementInstallingSpine01").getFsz()));
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpszAndLifecyleState() throws InterruptedException {

    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process with VPSZ and Category search criteria")
    public void testNeSearchByVpszAndCategory() throws InterruptedException {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        //assumption is that all elements have the same VPSZ, so we chose first elements' VPSZ
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get("a4NetworkElementInstallingOlt01").getVpsz());
        a4MobileUiRobot.clickSearchButton();

        $(a4MobileNeSearchPage.getSEARCH_RESULT_TABLE_LOCATOR()).shouldBe(Condition.visible);

        ElementsCollection elementsCollection = $(a4MobileNeSearchPage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        Thread.sleep(3000);

        List<String> conn = new ArrayList<>();

        elementsCollection.forEach(k -> conn.add(k.getText()));

        a4NetworkElements.forEach((k,networkElement) -> {
            assertTrue(conn.contains(networkElement.getFsz()),networkElement.getFsz());
            assertTrue(conn.contains(networkElement.getCategory()),networkElement.getCategory());
            assertTrue(conn.contains(networkElement.getLifecycleState()),networkElement.getLifecycleState());
            assertTrue(conn.contains(networkElement.getVpsz()),networkElement.getVpsz());
            assertTrue(conn.contains(networkElement.getPlanningDeviceName()),networkElement.getPlanningDeviceName());
        });

    }

}
