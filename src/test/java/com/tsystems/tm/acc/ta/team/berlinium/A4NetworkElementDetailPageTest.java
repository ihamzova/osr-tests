package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.WebDriverRunner;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.*;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_RESOURCE_INVENTORY_UI_MS,A4_RESOURCE_INVENTORY_BFF_PROXY_MS})
@Epic("OS&R")
@Feature("Test detail-view for found NEs in UI")
@TmsLink("DIGIHUB-xxxxx")
public class A4NetworkElementDetailPageTest extends GigabitTest {

    private final A4InventarSucheRobot a4InventarSuche = new A4InventarSucheRobot();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryNeDetailsRobot a4ResourceInventoryNeDetails = new A4ResourceInventoryNeDetailsRobot();
    private final A4ResourceInventoryNepDetailsRobot a4ResourceInventoryNepDetails = new A4ResourceInventoryNepDetailsRobot();
    private final A4ResourceInventoryNelDetailsRobot a4ResourceInventoryNelDetails = new A4ResourceInventoryNelDetailsRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neDataA;
    private A4NetworkElement neDataB;
    private A4NetworkElementPort nepDataA;
    private A4NetworkElementPort nepDataB;
    private A4NetworkElementLink nelData;
    private UewegData uewegData;

    @BeforeClass()
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neDataA = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingOlt01);
        neDataB = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementPlanningLeafSwitch01);
        nepDataA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        nepDataB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_100G_001);
        nelData = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);

        // Ensure that no old test data is in the way
        cleanUp();

        // Test cases only do read requests, therefore it's ok to crete them only once at beginning
        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neDataA, negData);
        a4ResourceInventory.createNetworkElementPort(nepDataA, neDataA);
        a4ResourceInventory.createNetworkElement(neDataB, negData);
        a4ResourceInventory.createNetworkElementPort(nepDataB, neDataB);
        nelData.setUeWegId(uewegData.getUewegId());
        a4ResourceInventory.createNetworkElementLink(nelData, nepDataA, nepDataB, neDataA, neDataB, uewegData);
    }

    @BeforeMethod
    void setup() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @AfterClass
    public void cleanUp() {
        // Delete all A4 data which might provoke problems because of unique constraints
        a4ResourceInventory.deleteA4NetworkElementGroupsRecursively(negData);
        a4ResourceInventory.deleteA4NetworkElementsRecursively(neDataA);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepDataA, neDataA);
        a4ResourceInventory.deleteA4NetworkElementPortsRecursively(nepDataB, neDataB);
    }

    @Test
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test for Network Element Detail page")
    public void testA4NeDetailPage() {
        // WHEN
//        WebDriverWait wait = new WebDriverWait(driver, 5000);
        a4InventarSuche.searchForNetworkElement(neDataA);

        WebDriver driver = WebDriverRunner.getWebDriver();
        driver.manage().window().maximize();
        sleepForSeconds(4);  // wait for result


        a4InventarSuche.clickDetailLinkForFirstNEInSearchResultTable();

        // THEN
        a4ResourceInventoryNeDetails.checkNeDetailsAndTableContents(neDataA, nepDataA, nelData, neDataB);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test if link for NE Gegenstelle works")
    public void testA4NeDetailPageAndClickOppositeNe() {
        // GIVEN
        a4InventarSuche.searchForNetworkElement(neDataA);
        WebDriver driver = WebDriverRunner.getWebDriver();
        driver.manage().window().maximize();
        sleepForSeconds(4);  // wait for result
        a4InventarSuche.clickDetailLinkForFirstNEInSearchResultTable();

        // WHEN
        a4ResourceInventoryNeDetails.clickGegenStelleIcon();

        // THEN
        a4ResourceInventoryNeDetails.checkLandedOnCorrectNeDetailsPage(neDataB);

    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test if link for NEP details works")
    public void testA4NeDetailPageAndClickNepButton() {
        // GIVEN
        a4InventarSuche.searchForNetworkElement(neDataA);
        WebDriver driver = WebDriverRunner.getWebDriver();
        driver.manage().window().maximize();
        sleepForSeconds(4);  // wait for result
        a4InventarSuche.clickDetailLinkForFirstNEInSearchResultTable();

        // WHEN
        a4ResourceInventoryNeDetails.clickNepIcon();

        // THEN
        a4ResourceInventoryNepDetails.checkLandedOnCorrectNepDetailsPage(nepDataA);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test if link for NEL details works")
    public void testA4NeDetailPageAndClickNelButton() {
        // GIVEN
        a4InventarSuche.searchForNetworkElement(neDataA);
        WebDriver driver = WebDriverRunner.getWebDriver();
        driver.manage().window().maximize();
        sleepForSeconds(4);  // wait for result
        a4InventarSuche.clickDetailLinkForFirstNEInSearchResultTable();

        // WHEN
        a4ResourceInventoryNeDetails.clickNelIcon();

        // THEN
        a4ResourceInventoryNelDetails.checkLandedOnCorrectNelDetailsPage(nelData);
    }

}
