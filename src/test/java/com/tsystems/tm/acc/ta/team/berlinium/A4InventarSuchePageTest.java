package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.equipmentdata.EquipmentDataCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.saveEventsToDefaultDir;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_UI_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_BFF_PROXY_MS)
@ServiceLog(A4_INVENTORY_IMPORTER_MS)
@Slf4j
public class A4InventarSuchePageTest extends BaseTest {

    private final A4InventarSucheRobot a4InventarSucheRobot = new A4InventarSucheRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    A4InventarSuchePage a4InventarSuchePage = new A4InventarSuchePage();

    private A4NetworkElementGroup a4NetworkElementGroup;


    //helper methods
    public void waitForTableToFullyLoad(int numberOfElements){
        //add 1 to number of elements because of table header
        //numberOfElements++;

        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);
    }


    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);


        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);

    }

    @AfterClass
    public void cleanUp() {

        a4ResourceInventoryRobot.deleteNetworkElementGroups(a4NetworkElementGroup);
    }



    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchWorking() throws InterruptedException {
        // 2 NEG
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxWorking();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchOpInstalling() throws InterruptedException {

        // 1 NEG
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxOpInstalling();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchNotWorking() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxNotWorking();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchNotManageable() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxNotManageable();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchFailed() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxFailed();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchActivating() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxActivating();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchDeActivating() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxDeactivating();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchPlanning() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxPlanning();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchLifecycleInstalling() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxLifeInstalling();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchOperating() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxOperating();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchRetiring() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxRetiring();
        a4InventarSucheRobot.clickSearchButton();

        Thread.sleep(3000);
    }



    @Test
    @Owner("Anita.Junge@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchByName() throws InterruptedException {

        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.enterNegName(a4NetworkElementGroup.getName());
       // a4InventarSucheRobot.readNegName();  // hat keinen Sinn
        a4InventarSucheRobot.clickSearchButton();

        waitForTableToFullyLoad(1);

        Thread.sleep(3000);

        ElementsCollection elementsCollection = $(a4InventarSuchePage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        assertEquals(elementsCollection.get(0).getText(),a4NetworkElementGroup.getUuid());
        assertEquals(elementsCollection.get(1).getText(),a4NetworkElementGroup.getName());
        assertEquals(elementsCollection.get(2).getText(),a4NetworkElementGroup.getOperationalState());
        assertEquals(elementsCollection.get(3).getText(),a4NetworkElementGroup.getLifecycleState());
        //assertEquals(elementsCollection.get(4).getText(),a4NetworkElementGroup.getCreationTime());
        //assertEquals(elementsCollection.get(5).getText(),a4NetworkElementGroup.getLastUpdateTime());
        Thread.sleep(2000);
    }

}
