package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNeDetailPage;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryBrowserRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryNeDetailRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class A4UiDetailsNetworkElementTest extends BaseTest {

    private final A4InventarSucheRobot a4InventarSucheRobot = new A4InventarSucheRobot();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4InventarSuchePage a4InventarSuchePage = new A4InventarSuchePage();
    private final A4ResourceInventoryNeDetailRobot a4ResourceInventoryNeDetailRobot = new A4ResourceInventoryNeDetailRobot();
    private final A4ResourceInventoryNeDetailPage a4ResourceInventoryNeDetailPage = new A4ResourceInventoryNeDetailPage();

    private A4NetworkElementGroup negData;
    private A4NetworkElement neDataA;
    private A4NetworkElement neDataB;
    private A4NetworkElementPort nepDataA;
    private A4NetworkElementPort nepDataB;
    private A4NetworkElementLink nelData;

    @Epic("OS&R")
    @Feature("Tests for A4 UI Inventory Browser UI")
    @TmsLink("DIGIHUB-xxxxx")
    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        //neDataA = osrTestContext.getData().getA4NetworkElementDataProvider()
          //      .get(A4NetworkElementCase.networkElementInstallingOlt01); // OLT01 hat Vpsz("49/1198/0")
        neDataA = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingOlt01); //
        neDataB = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementPlanningLeafSwitch01); // hat Vpsz("49/1198/0")
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
    public void testDetailPageForNetworkElementInA4ResourceInventoryBrowserUi() throws InterruptedException {
        // GIVEN
        List<NetworkElementDetails> neDetailsExpectedList = generateExpectedData();

        // Execute steps which should lead to the actual page under test
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElement();
        a4InventarSucheRobot.enterNeAkzByVpsz(neDataA.getVpsz());
        a4InventarSucheRobot.enterNeOnkzByVpsz(neDataA.getVpsz());
        a4InventarSucheRobot.enterNeVkzByVpsz(neDataA.getVpsz());
        a4InventarSucheRobot.clickNeSearchButton();

        // Click first row in search result table
        a4InventarSucheRobot.getNeElementsCollection().get(0).click();

        // Wait 10 seconds (UI slow currently)
        try {
            final long SLEEP_TIMER = 10;
            TimeUnit.SECONDS.sleep(SLEEP_TIMER);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // now we have the detail-list with NE-Port, NE-Link and opposite NE

        // sind wir auf der falschen Seite? ok bedeutet: /a4-inventory-browser/inventory-search
        // a4InventarSuchePage.validate(); // es kommt eine Fehler, also sind wir auf der richtigen Seite: a4-resource-inventory-ui/a4-inventory-browser/network-element/xyz --> ok
        // warum funktioniert dann a4InventarSucheRobot.getNeElementsCollection()? auf der Seite sind wir nicht mehr!

        // Expect table data to be correct, compare inventory-search-page with NetworkElement-Details-page ??
        // warum ist die elementsCollection die Liste von der Detail-Page? sollte doch die inventory-search-page sein?
        // funktioniert das, weil die Tabellen-class auf beiden Seiten gleich ist? w3-table-all
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNeElementsCollection();
        List<NetworkElementDetails> neDetailsResultList = createNeDetailList(elementsCollection);
        //System.out.println("+++ actual:   "+neDetailsResultList);
        assertEquals(neDetailsResultList.toString(), neDetailsExpectedList.toString());
        // neDetailsExpectedList doppelt ausführen, führt zu anderem Ergebnis und damit zu Fehler
        //System.out.println("+++ expected:   "+neDetailsExpectedList);  // wird weiter unten manuell zusammengebaut


        Thread.sleep(1000);

    }

    @Test
    @Owner("heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxx")
    @Description("Test for Network Element Detail page")
    public void testNELDetailPageForNetworkElementInA4ResourceInventoryBrowserUi() throws InterruptedException {

        List<NetworkElementDetails> neDetailsExpectedList = generateExpectedData();

        // Execute steps which should lead to the actual page under test
       // a4ResourceInventoryNeDetailRobot.openRiNeDetailPage();
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElement();
        a4InventarSucheRobot.enterNeAkzByVpsz(neDataA.getVpsz());
        a4InventarSucheRobot.enterNeOnkzByVpsz(neDataA.getVpsz());
        a4InventarSucheRobot.enterNeVkzByVpsz(neDataA.getVpsz());


        // value=<leer>, OLT, LEAF_SWITCH, SPINE_SWITCH, POD_SERVER, BOR
       // a4InventarSucheRobot.enterNeCategory("OLT");  // dropdown mit selectOptionByValue
       // a4InventarSucheRobot.checkboxNotWorking();
       // a4InventarSucheRobot.checkboxLifeInstalling();

        a4InventarSucheRobot.clickNeSearchButton();

        // get uuid, ztpid, time
        System.out.println("+++ uuid: "+a4InventarSucheRobot.getNeElementsCollection().get(0).innerText());
        neDataA.setUuid(a4InventarSucheRobot.getNeElementsCollection().get(0).innerText());
        neDataA.setZtpIdent(a4InventarSucheRobot.getNeElementsCollection().get(5).innerText());
        neDataA.setCreationTime(a4InventarSucheRobot.getNeElementsCollection().get(10).innerText());
        neDataA.setLastUpdateTime(a4InventarSucheRobot.getNeElementsCollection().get(11).innerText());

        Thread.sleep(8000);

        // Click first row in search result table
        a4InventarSucheRobot.getNeElementsCollection().get(0).click();






        // Wait 10 seconds (UI slow currently)
        try {
            final long SLEEP_TIMER = 10;
            TimeUnit.SECONDS.sleep(SLEEP_TIMER);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // now we have the detail-list with NE-Port, NE-Link and opposite NE

        a4ResourceInventoryNeDetailPage.validate();

        System.out.println("+++ u: "+a4ResourceInventoryNeDetailRobot.readNeUuid());
        System.out.println("+++ v: "+a4ResourceInventoryNeDetailRobot.readNeVpsz());
        System.out.println("+++ f: "+a4ResourceInventoryNeDetailRobot.readNeFsz());
        System.out.println("+++ c: "+a4ResourceInventoryNeDetailRobot.readNeCategory());
        System.out.println("+++ t: "+a4ResourceInventoryNeDetailRobot.readNeType());
        System.out.println("+++ p: "+a4ResourceInventoryNeDetailRobot.readNePlanningDeviceName());
        System.out.println("+++ k: "+a4ResourceInventoryNeDetailRobot.readNeKlsId());
        System.out.println("+++ z: "+a4ResourceInventoryNeDetailRobot.readNeZtpid());
        System.out.println("+++ o: "+a4ResourceInventoryNeDetailRobot.readNeOps());
        System.out.println("+++ l: "+a4ResourceInventoryNeDetailRobot.readNeLcs());
        System.out.println("+++ c: "+a4ResourceInventoryNeDetailRobot.readNeCreationTime());
        System.out.println("+++ l: "+a4ResourceInventoryNeDetailRobot.readNeLastUpdateTime());


        assertEquals(a4ResourceInventoryNeDetailRobot.readNeUuid(), neDataA.getUuid()); // nicht vorhanden in neData, oben gesetzt
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeVpsz(), neDataA.getVpsz());
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeFsz(), neDataA.getFsz());
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeCategory(), neDataA.getCategory());
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeType(), neDataA.getType());
        assertEquals(a4ResourceInventoryNeDetailRobot.readNePlanningDeviceName(), neDataA.getPlanningDeviceName());
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeKlsId(), neDataA.getKlsId());
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeZtpid(), neDataA.getZtpIdent()); // nicht vorhanden in neData, oben gesetzt
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeOps(), neDataA.getOperationalState());
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeLcs(), neDataA.getLifecycleState());
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeCreationTime(), neDataA.getCreationTime()); // nicht vorhanden in neData, oben gesetzt
        assertEquals(a4ResourceInventoryNeDetailRobot.readNeLastUpdateTime(), neDataA.getLastUpdateTime()); // nicht vorhanden in neData, oben gesetzt


       // Thread.sleep(1000);

        // Click on NEP/NEL-loupe in thirst row
       // a4ResourceInventoryNeDetailRobot.getNelElementsCollection().get(0).click(); // comming-soon-page Port, ok
       // a4ResourceInventoryNeDetailRobot.getNelElementsCollection().get(3).click(); // comming-soon-page Link, ok

/*

        // compare expected and actual result
        //ElementsCollection elementsCollection = a4InventarSucheRobot.getNeElementsCollection(); // falscher Robot auch ok
        ElementsCollection elementsCollection = a4ResourceInventoryNeDetailRobot.getNelElementsCollection(); // richtiger Robot,
        List<NetworkElementDetails> neDetailsResultList = createNeDetailList(elementsCollection);
       // System.out.println("+++ Result: "+neDetailsResultList);
        //System.out.println("+++ Expect: "+neDetailsExpectedList);
        assertEquals(neDetailsResultList.toString(), neDetailsExpectedList.toString());
*/


        Thread.sleep(2000);
    }












    private List<NetworkElementDetails> generateExpectedData() {
        NetworkElementDetails neDetailsLine1 = new NetworkElementDetails();
//        neDetailsLine1.setNeUuid(neDataA.getUuid());
        neDetailsLine1.setLogicalLabel(nepDataA.getFunctionalPortLabel());
        neDetailsLine1.setPhysicalLabel("10ge 0/1");
//        neDetailsLine1.setNelUuid(nelData.getUuid());
        neDetailsLine1.setLsz("123");
        neDetailsLine1.setUewegeId(nelData.getUeWegId());
        neDetailsLine1.setLbz(nelData.getLbz());
//        neDetailsLine1.setGegenstelleUuid(neDataB.getUuid());
        neDetailsLine1.setGegenstelleCategory(neDataB.getCategory());
        neDetailsLine1.setGegenstelleVpsz(neDataB.getVpsz());

//        NetworkElementDetails neDetailsLine2 = new NetworkElementDetails();
//        neDetailsLine2.setNeUuid(neDataB.getUuid());
//        neDetailsLine2.setLogicalLabel(nepDataB.getFunctionalPortLabel());
//        neDetailsLine2.setPhysicalLabel("0/0/49");
//        neDetailsLine2.setNelUuid(nelData.getUuid());
//        neDetailsLine2.setLsz("123");
//        neDetailsLine2.setUewegeId(nelData.getUeWegId());
//        neDetailsLine2.setLbz(nelData.getLbz());
//        neDetailsLine2.setGegenstelleUuid(neDataA.getUuid());
//        neDetailsLine2.setGegenstelleCategory(neDataA.getCategory());
//        neDetailsLine1.setGegenstelleVpsz(neDataA.getVpsz());

        List<NetworkElementDetails> neDetailsExpectedList = new ArrayList<>();
        neDetailsExpectedList.add(neDetailsLine1);
//        neDetailsExpectedList.add(neDetailsLine2);

        return neDetailsExpectedList;
    }

    private List<NetworkElementDetails> createNeDetailList(ElementsCollection elementsCollection) {
        final int numberOfColumnsNeDetailList = 10;

        // Create empty list
        List<NetworkElementDetails> neDetailtList = new ArrayList<>();
        for (int i = 0; i < elementsCollection.size() / numberOfColumnsNeDetailList; i++) {
            NetworkElementDetails neActualGeneric = new NetworkElementDetails();
            neDetailtList.add(neActualGeneric);
        }

        // Read table from ui and fill list (actual result)
        for (int i = 0; i < elementsCollection.size() / numberOfColumnsNeDetailList; i++) {
//            neDetailtList.get(i).setNeUuid(elementsCollection.get(i * numberOfColumnsNeDetailList).getText());
            neDetailtList.get(i).setLogicalLabel(elementsCollection.get(i * numberOfColumnsNeDetailList + 1).getText());
            neDetailtList.get(i).setPhysicalLabel(elementsCollection.get(i * numberOfColumnsNeDetailList + 2).getText());
//            neDetailtList.get(i).setNelUuid(elementsCollection.get(i * numberOfColumnsNeDetailList + 3).getText());
            neDetailtList.get(i).setLsz(elementsCollection.get(i * numberOfColumnsNeDetailList + 4).getText());
            neDetailtList.get(i).setUewegeId(elementsCollection.get(i * numberOfColumnsNeDetailList + 5).getText());
            neDetailtList.get(i).setLbz(elementsCollection.get(i * numberOfColumnsNeDetailList + 6).getText());
//            neDetailtList.get(i).setGegenstelleUuid(elementsCollection.get(i * numberOfColumnsNeDetailList + 7).getText());
            neDetailtList.get(i).setGegenstelleCategory(elementsCollection.get(i * numberOfColumnsNeDetailList + 8).getText());
            neDetailtList.get(i).setGegenstelleVpsz(elementsCollection.get(i * numberOfColumnsNeDetailList + 9).getText());
        }

        // Sort
        neDetailtList = neDetailtList
                .stream().sorted(Comparator.comparing(NetworkElementDetails::getNeUuid))
                .collect(Collectors.toList());
        return neDetailtList;
    }

}

@Getter
@Setter
@ToString
@EqualsAndHashCode
class NetworkElementDetails {
    private String neUuid;
    private String logicalLabel;
    private String physicalLabel;
    private String nelUuid;
    private String lsz;
    private String uewegeId;
    private String lbz;
    private String gegenstelleUuid;
    private String gegenstelleCategory;
    private String gegenstelleVpsz;
}
