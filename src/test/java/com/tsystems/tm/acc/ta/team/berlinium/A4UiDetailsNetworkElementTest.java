package com.tsystems.tm.acc.ta.team.berlinium;

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
import io.qameta.allure.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@Epic("OS&R")
@Feature("Test detail-view for found NEs in UI")
@TmsLink("DIGIHUB-xxxxx")
public class A4UiDetailsNetworkElementTest extends GigabitTest {

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
        uewegData = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.defaultUeweg);

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

        // "924713, 289182"    - ist das ok? seit 6-2020 im Code (uewege.yaml), aber erst seit 7-2021 genutzt
        nelData.setUeWegId(uewegData.getUewegId());

        System.out.println("+++ negDataA: "+negData);
        System.out.println("+++ neDataA: "+neDataA);
        System.out.println("+++ neDataB: "+neDataB);
        System.out.println("+++ nepDataA: "+nepDataA);
        System.out.println("+++ nepDataB: "+nepDataB);
        System.out.println("+++ nelData: "+nelData);

        //a4ResourceInventory.createNetworkElementLink(nelData, nepDataA, nepDataB);
        a4ResourceInventory.createNetworkElementLink(nelData, nepDataA, nepDataB, neDataA, neDataB, uewegData);

    }

    @AfterClass
    public void cleanUp() {
        a4ResourceInventory.deleteA4TestDataRecursively(negData);  // nel wird hier nicht gelöscht?
    }

    @Test
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test for Network Element Detail page")
    public void testA4NeDetailPage() throws InterruptedException {
        // WHEN
        a4InventarSuche.searchForNetworkElement(neDataA);
        a4InventarSuche.clickFirstRowInSearchResultTable();

        // THEN
        a4ResourceInventoryNeDetails.checkNeDetailsAndTableContents(neDataA, nepDataA, nelData, neDataB);
        a4ResourceInventory.deleteNetworkElementLink(nelData.getUuid()); // nel wird in afterclass nicht gelöscht?
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test if link for NE Gegenstelle works")
    public void testA4NeDetailPageAndClickOppositeNe() throws InterruptedException {
        // GIVEN
        a4InventarSuche.searchForNetworkElement(neDataA);
        a4InventarSuche.clickFirstRowInSearchResultTable();

        // WHEN
        a4ResourceInventoryNeDetails.clickGegenStelleIcon();

        // THEN
        a4ResourceInventoryNeDetails.checkLandedOnCorrectNeDetailsPage(neDataB);
        a4ResourceInventory.deleteNetworkElementLink(nelData.getUuid()); // nel wird in afterclass nicht gelöscht?

    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test if link for NEP details works")
    public void testA4NeDetailPageAndClickNepButton() throws InterruptedException {
        // GIVEN
        a4InventarSuche.searchForNetworkElement(neDataA);
        a4InventarSuche.clickFirstRowInSearchResultTable();

        // WHEN
        a4ResourceInventoryNeDetails.clickNepIcon();

        // THEN
        a4ResourceInventoryNepDetails.checkLandedOnCorrectNepDetailsPage(nepDataA);
        a4ResourceInventory.deleteNetworkElementLink(nelData.getUuid()); // nel wird in afterclass nicht gelöscht?
    }

    @Test
    @Owner("heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test if link for NEL details works")
    public void testA4NeDetailPageAndClickNelButton() throws InterruptedException {
        // GIVEN
        a4InventarSuche.searchForNetworkElement(neDataA);
        a4InventarSuche.clickFirstRowInSearchResultTable();

        // WHEN
        a4ResourceInventoryNeDetails.clickNelIcon();

        // THEN
        a4ResourceInventoryNelDetails.checkLandedOnCorrectNelDetailsPage(nelData);
        a4ResourceInventory.deleteNetworkElementLink(nelData.getUuid()); // nel wird in afterclass nicht gelöscht?
    }

}
