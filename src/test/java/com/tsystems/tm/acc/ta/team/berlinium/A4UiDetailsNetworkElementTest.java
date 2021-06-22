package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryNeDetailRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import io.qameta.allure.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("OS&R")
@Feature("Tests for A4 UI Inventory Browser UI")
@TmsLink("DIGIHUB-xxxxx")
public class A4UiDetailsNetworkElementTest extends GigabitTest {

    private final A4InventarSucheRobot a4InventarSucheRobot = new A4InventarSucheRobot();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryNeDetailRobot a4ResourceInventoryNeDetailRobot = new A4ResourceInventoryNeDetailRobot();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neDataA;
    private A4NetworkElement neDataB;
    private A4NetworkElementPort nepDataA;
    private A4NetworkElementPort nepDataB;
    private A4NetworkElementLink nelData;

    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

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

        // Ensure that no old test data is in the way
        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neDataA, negData);
        a4ResourceInventory.createNetworkElementPort(nepDataA, neDataA);
        a4ResourceInventory.createNetworkElement(neDataB, negData);
        a4ResourceInventory.createNetworkElementPort(nepDataB, neDataB);
        a4ResourceInventory.createNetworkElementLink(nelData, nepDataA, nepDataB);
    }

    @AfterClass
    public void cleanUp() {
        a4ResourceInventory.deleteA4TestDataRecursively(negData);
    }

    @Test
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test for Network Element Detail page")
    public void testA4NeDetailPage() {
        // WHEN
        a4InventarSucheRobot.searchForNetworkElement(neDataA);
        a4InventarSucheRobot.clickFirstRowInSearchResultTable();

        // THEN
        a4ResourceInventoryNeDetailRobot.checkNeDetailsAndTableContents(neDataA, nepDataA, nelData, neDataB);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test for Network Element Detail page")
    public void testA4NeDetailPageAndClickOppositeNe() {
        // WHEN
        a4InventarSucheRobot.searchForNetworkElement(neDataA);
        a4InventarSucheRobot.clickFirstRowInSearchResultTable();
        a4ResourceInventoryNeDetailRobot.clickGegenStelleIcon();

        // THEN
        a4ResourceInventoryNeDetailRobot.checkLandedOnCorrectNeDetailsPage(neDataB);
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test for Network Element Detail page")
    public void testA4NeDetailPageAndClickNepButton() {
        // WHEN
        a4InventarSucheRobot.searchForNetworkElement(neDataA);
        a4InventarSucheRobot.clickFirstRowInSearchResultTable();
        a4ResourceInventoryNeDetailRobot.clickNepIcon();

        // THEN
        a4ResourceInventoryNeDetailRobot.checkLandedOnCorrectNepDetailsPage();
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test for Network Element Detail page")
    public void testA4NeDetailPageAndClickNelButton() {
        // WHEN
        a4InventarSucheRobot.searchForNetworkElement(neDataA);
        a4InventarSucheRobot.clickFirstRowInSearchResultTable();
        a4ResourceInventoryNeDetailRobot.clickNelIcon();

        // THEN
        a4ResourceInventoryNeDetailRobot.checkLandedOnCorrectNelDetailsPage();
    }

}
