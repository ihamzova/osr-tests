package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNegDetailsPage;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;

@Slf4j
public class A4ResourceInventoryNegDetailsRobot {
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();

    @Step("Read uuid neg")
    public String readNegUuid() {
        return $(A4ResourceInventoryNegDetailsPage.NEG_UUID_FIELD_LOCATOR).innerHtml();
    }

    @Step("Read name neg")
    public String readNegName() {
        return $(A4ResourceInventoryNegDetailsPage.NEG_NAME_FIELD_LOCATOR).innerHtml();
    }

    @Step("Read description neg")
    public String readNegDescription() {
        return $(A4ResourceInventoryNegDetailsPage.NEG_DESCRIPTION_FIELD_LOCATOR).innerHtml();
    }

    @Step("Read lcs ne")
    public String readNegLcs() {
        return $(A4ResourceInventoryNegDetailsPage.NEG_LCS_FIELD_LOCATOR).innerHtml();
    }

    @Step("Read creation time ne")
    public String readNegCreationTime() {
        return $(A4ResourceInventoryNegDetailsPage.NEG_CreationTime_FIELD_LOCATOR).innerHtml();
    }

    @Step("Read last update time ne")
    public String readNegLastUpdateTime() {
        return $(A4ResourceInventoryNegDetailsPage.NEG_LastUpdateTime_FIELD_LOCATOR).innerHtml();
    }


    @Step("Read last sync time ne")
    public String readNegLastSuccessfulSyncTime() {
        return $(A4ResourceInventoryNegDetailsPage.NEG_LastSuccessfulSync_FIELD_LOCATOR).innerHtml();
    }

    @Step("Open UI, log in")
    public void openRiNegDetailPage() {
        A4ResourceInventoryNegDetailsPage.login();     //TODO: warum wird hier die Klasse verwendet?
    }

    public ElementsCollection getNeElementsCollection() {
        return $(A4ResourceInventoryNegDetailsPage.NELIST_SEARCH_RESULT_TABLE_LOCATOR)
                .findAll(By.xpath("tr/td"));
    }

    @Step("Check details and table for network element group")
    public void checkNegDetailsAndTableContents(A4NetworkElementGroup negData) {
        final A4InventarSucheRobot a4InventarSucheRobot = new A4InventarSucheRobot();
        final A4ResourceInventoryNegDetailsPage a4ResourceInventoryNegDetailsPage = new A4ResourceInventoryNegDetailsPage();

        List<NetworkElementDto> allNeOfNegList = a4ResourceInventoryRobot.getNetworkElementsByNegUuid(negData.getUuid());

        // now we have the detail-list
        a4ResourceInventoryNegDetailsPage.validate();

        // check neg-block / hauptdaten
        assertEquals(readNegUuid(), negData.getUuid());
        assertEquals(readNegName(), negData.getName());
        assertEquals(readNegDescription(), negData.getDescription());
        assertEquals(readNegLcs(), negData.getLifecycleState());


        // check ne list  data in table
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNeList4NEGCollection(); // UI result
        sleepForSeconds(4);  // wait for result
        List<A4NetworkElement> neResultListFromUi = createNeList4NEGFromUI(elementsCollection); // list of NetworkElements displayed on UI
        sleepForSeconds(4);  // wait for result
        compareExpectedResultWithActualResultNeList4NEG(allNeOfNegList, neResultListFromUi, elementsCollection.size());
    }




    public void compareExpectedResultWithActualResultNeList4NEG(List<NetworkElementDto> neListInDB,
                                                             List<A4NetworkElement> neListOnUI,
                                                             int elementsCollectionSize) {
        int numberOfColumnsNeList = 10;

        neListInDB = neListInDB
                .stream().sorted(Comparator.comparing(NetworkElementDto::getUuid))
                .collect(Collectors.toList());
        neListOnUI = neListOnUI
                .stream().sorted(Comparator.comparing(A4NetworkElement::getUuid))
                .collect(Collectors.toList());
        for (int i = 0; i < elementsCollectionSize / numberOfColumnsNeList; i++) {
            assertEquals(neListInDB.get(i).getUuid(), neListOnUI.get(i).getUuid());
            assertEquals(neListInDB.get(i).getCategory(), neListOnUI.get(i).getCategory());
            assertEquals(neListInDB.get(i).getLifecycleState(), neListOnUI.get(i).getLifecycleState());
            assertEquals(neListInDB.get(i).getOperationalState(), neListOnUI.get(i).getOperationalState());
            assertEquals(neListInDB.get(i).getCreationTime(), OffsetDateTime.parse(neListOnUI.get(i).getCreationTime()) );
            assertEquals(neListInDB.get(i).getLastUpdateTime(), OffsetDateTime.parse(neListOnUI.get(i).getLastUpdateTime()) );
            assertEquals(neListInDB.get(i).getLastSuccessfulSyncTime(), OffsetDateTime.parse(neListOnUI.get(i).getLastSuccessfulSyncTime()) );
        }
    }

    private List<A4NetworkElement> createNeList4NEGFromUI(ElementsCollection elementsCollection) {
        final int numberOfColumnsNeList = 10;

        // Create empty list
        List<A4NetworkElement> neList = new ArrayList<>();
        for (int i = 0; i < elementsCollection.size() / numberOfColumnsNeList; i++) {
            A4NetworkElement neActualGeneric = new A4NetworkElement();
            neList.add(neActualGeneric);
        }

        // Read table from ui and fill list (actual result)
        for (int i = 0; i < elementsCollection.size() / numberOfColumnsNeList; i++) {
            /*
            UUID	Category	VPSZ	FSZ	LifecycleState	OperationalState	Creation Time	Last Update Time	Last Successful Sync Time
             */
            neList.get(i).setUuid(elementsCollection.get(i * numberOfColumnsNeList + 1).getText());
            neList.get(i).setVpsz(elementsCollection.get(i * numberOfColumnsNeList + 2).getText());
            neList.get(i).setFsz(elementsCollection.get(i * numberOfColumnsNeList + 3).getText());
            neList.get(i).setCategory(elementsCollection.get(i * numberOfColumnsNeList + 4).getText());
            neList.get(i).setOperationalState(elementsCollection.get(i * numberOfColumnsNeList + 5).getText());
            neList.get(i).setLifecycleState(elementsCollection.get(i * numberOfColumnsNeList + 6).getText());
            neList.get(i).setCreationTime(elementsCollection.get(i * numberOfColumnsNeList + 7).getText());
            neList.get(i).setLastUpdateTime(elementsCollection.get(i * numberOfColumnsNeList + 8).getText());
            neList.get(i).setLastSuccessfulSyncTime(elementsCollection.get(i * numberOfColumnsNeList + 9).getText());
        }

        // Sort
        neList = neList
                .stream().sorted(Comparator.comparing(A4NetworkElement::getUuid))
                .collect(Collectors.toList());
        return neList;
    }



    public void clickNeIcon() {
        getNeElementsCollection().get(0).click();
    }





    public void checkLandedOnCorrectNegDetailsPage(A4NetworkElementGroup negData) {
        final A4ResourceInventoryNegDetailsPage a4ResourceInventoryNegDetailsPage = new A4ResourceInventoryNegDetailsPage();
        a4ResourceInventoryNegDetailsPage.validate(negData.getUuid());
    }



}


