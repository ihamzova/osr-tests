package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNegDetailsPage;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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

        System.out.println("Now for checkNegDetailsAndTableContents starting validate NegDetailPage");
        // now we have the detail-list
        a4ResourceInventoryNegDetailsPage.validate();

        // check neg-block / hauptdaten
        assertEquals(readNegUuid(), negData.getUuid());
        assertEquals(readNegName(), negData.getName());
        assertEquals(readNegDescription(), negData.getDescription());
        assertEquals(readNegLcs(), negData.getLifecycleState());


        // check ne list  data in table

/*
        NetworkElementDetails neDetailsExpectedListOf1Row =  neDetailsExpectedList.get(0);
        assertEquals(neDetailsExpectedListOf1Row.getLogicalLabel(), getTextOfElementInTable("tdLogicalLabel0"));
        assertEquals(neDetailsExpectedListOf1Row.getPhysicalLabel(), getTextOfElementInTable("tdPhysicalLabel0"));
        assertEquals(neDetailsExpectedListOf1Row.getLsz(), getTextOfElementInTable("tdLsz0"));
        assertEquals(neDetailsExpectedListOf1Row.getUewegeId(), getTextOfElementInTable("tdUewegeId0"));
        assertEquals(neDetailsExpectedListOf1Row.getLbz(), getTextOfElementInTable("tdLbz0"));
        assertEquals(neDetailsExpectedListOf1Row.getGegenstelleCategory(),getTextOfElementInTable("tdCategory0"));
        assertEquals(neDetailsExpectedListOf1Row.getGegenstelleVpsz(), getTextOfElementInTable("tdVpsz0"));

*/

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
            neList.get(i).setUuid(elementsCollection.get(i * numberOfColumnsNeList + 1).getText());
            neList.get(i).setVpsz(elementsCollection.get(i * numberOfColumnsNeList + 2).getText());
            neList.get(i).setFsz(elementsCollection.get(i * numberOfColumnsNeList + 3).getText());
            neList.get(i).setCategory(elementsCollection.get(i * numberOfColumnsNeList + 4).getText());
            neList.get(i).setOperationalState(elementsCollection.get(i * numberOfColumnsNeList + 5).getText());
            neList.get(i).setLifecycleState(elementsCollection.get(i * numberOfColumnsNeList + 6).getText());
        }

        // Sort
        neList = neList
                .stream().sorted(Comparator.comparing(A4NetworkElement::getUuid))
                .collect(Collectors.toList());
        return neList;
    }



    public void clickNeIcon() {

        //getNeElementsCollection().get(0).click();

        sleepForSeconds(2);
        WebElement element = $(A4InventarSuchePage.getNEG_NE_LIST_TABLE_LOCATOR())
                .toWebElement().findElement(A4InventarSuchePage.NE_DETAIL_LINK_LOCATOR_1);
        sleepForSeconds(2);
        element.click();


    }



    public String getUUiOfFirstNeOnUi() {
       return getNeElementsCollection().get(1).innerHtml();
    }

}


