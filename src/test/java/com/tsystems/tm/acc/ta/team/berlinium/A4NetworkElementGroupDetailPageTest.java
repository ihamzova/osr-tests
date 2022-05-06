package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.WebDriverRunner;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.*;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;

@ServiceLog({A4_RESOURCE_INVENTORY_MS,A4_RESOURCE_INVENTORY_UI_MS,A4_RESOURCE_INVENTORY_BFF_PROXY_MS})
@Epic("OS&R")
@Feature("Test detail-view for found NEGs in UI")
@TmsLink("DIGIHUB-xxxxx")
public class A4NetworkElementGroupDetailPageTest extends GigabitTest {
    private final A4InventarSucheRobot a4InventarSuche = new A4InventarSucheRobot();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryNegDetailsRobot a4ResourceInventoryNegDetails = new A4ResourceInventoryNegDetailsRobot();
    private final A4ResourceInventoryNeDetailsRobot a4ResourceInventoryNeDetails = new A4ResourceInventoryNeDetailsRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData;

    @BeforeClass()
    public void init() {
        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        neData = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingOlt01);

        // Ensure that no old test data is in the way
        cleanUp();

        // Test cases only do read requests, therefore it's ok to crete them only once at beginning
        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neData, negData);
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
        a4ResourceInventory.deleteA4NetworkElementsRecursively(neData);
    }

    @Test
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test for Network Element Group Detail page")
    public void testA4NetworkElementGroupDetailPage() {
        // WHEN
//        WebDriverWait wait = new WebDriverWait(driver, 5000);
        a4InventarSuche.searchForNetworkElementGroup(negData);


        WebDriver driver = WebDriverRunner.getWebDriver();
        driver.manage().window().maximize();
        sleepForSeconds(4);  // wait for result

        a4InventarSuche.clickDetailLinkForFirstNEGInSearchResultTable();

        // THEN
        a4ResourceInventoryNegDetails.checkNegDetailsAndTableContents(negData, neData);
    }


    @Test
    @Owner("juergen.mayer@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test if link for NE  works")
    public void testA4NegDetailPageAndClickNeg() {
        // GIVEN
        a4InventarSuche.searchForNetworkElementGroup(negData);
        WebDriver driver = WebDriverRunner.getWebDriver();
        driver.manage().window().maximize();
        sleepForSeconds(4);  // wait for result
        a4InventarSuche.clickDetailLinkForFirstNEGInSearchResultTable();

        // WHEN
        a4ResourceInventoryNegDetails.clickNeIcon();

        // THEN
        a4ResourceInventoryNeDetails.checkLandedOnCorrectNeDetailsPage(neData);

    }
}

