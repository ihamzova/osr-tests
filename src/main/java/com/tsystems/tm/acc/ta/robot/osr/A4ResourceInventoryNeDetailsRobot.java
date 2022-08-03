package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNeDetailsPage;
import io.qameta.allure.Step;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;

@Slf4j
public class A4ResourceInventoryNeDetailsRobot {

    @Step("Read uuid ne")
    public String readNeUuid() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());
        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_UUID_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read vpsz ne")
    public String readNeVpsz() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_VPSZ_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read fsz ne")
    public String readNeFsz() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_FSZ_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read category ne")
    public String readNeCategory() {

        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_CATEGORY_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read type ne")
    public String readNeType() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_TYPE_FIELD_LOCATOR)).innerHtml();
    }


    @Step("Read kls-id ne")
    public String readNeKlsId() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_KLSID_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read ztp ident ne")
    public String readNeZtpid() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_ZTPID_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read ops ne")
    public String readNeOps() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_OPS_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read lcs ne")
    public String readNeLcs() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_LCS_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read creation time ne")
    public String readNeCreationTime() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_CreationTime_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Read last update time ne")
    public String readNeLastUpdateTime() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_LastUpdateTime_FIELD_LOCATOR)).innerHtml();
    }


    @Step("Read last sync time ne")
    public String readNeLastSuccessfulSyncTime() {
        SelenideElement container = $(A4ResourceInventoryNeDetailsPage.getCONTAINER_MAIN_DATA_NETWORKELEMENT());

        return $(container.findElement(A4ResourceInventoryNeDetailsPage.NE_LastSuccessfulSync_FIELD_LOCATOR)).innerHtml();
    }

    @Step("Open UI, log in")
    public void openRiNeDetailPage() {
        A4ResourceInventoryNeDetailsPage.login();     //TODO: warum wird hier die Klasse verwendet?
    }

    public ElementsCollection getNelElementsCollection() {
        return $(A4ResourceInventoryNeDetailsPage.NEL_SEARCH_RESULT_TABLE_LOCATOR)
                .findAll(By.xpath("tr/td"));
    }

    @Step("Check details and table for network element")
    public void checkNeDetailsAndTableContents(A4NetworkElement neDataA, A4NetworkElementPort nepDataA, A4NetworkElementLink nelData, A4NetworkElement neDataB) {
        final A4InventarSucheRobot a4InventarSucheRobot = new A4InventarSucheRobot();
        final A4ResourceInventoryNeDetailsPage a4ResourceInventoryNeDetailsPage = new A4ResourceInventoryNeDetailsPage();
        List<NetworkElementDetails> neDetailsExpectedList = generateExpectedData(nepDataA, nelData, neDataB);


        System.out.println("Now for checkNeDetailsAndTableContents starting validate NeDetailPage");
        // now we have the detail-list with NE-Port, NE-Link and opposite NE
        a4ResourceInventoryNeDetailsPage.validate();

        // check ne-block
        assertEquals(readNeUuid(), neDataA.getUuid());
        assertEquals(readNeVpsz(), neDataA.getVpsz());
        assertEquals(readNeFsz(), neDataA.getFsz());
        assertEquals(readNeCategory(), neDataA.getCategory());
        assertEquals(readNeType(), neDataA.getType());
        assertEquals(readNeKlsId(), neDataA.getKlsId());
        assertEquals(readNeOps(), neDataA.getOperationalState());
        assertEquals(readNeLcs(), neDataA.getLifecycleState());

        // check port, link and gegenstelle data in table of 1 row

        NetworkElementDetails neDetailsExpectedListOf1Row =  neDetailsExpectedList.get(0);
        assertEquals(neDetailsExpectedListOf1Row.getLogicalLabel(), getTextOfElementInTable("tdLogicalLabel0"));
        assertEquals(neDetailsExpectedListOf1Row.getPhysicalLabel(), getTextOfElementInTable("tdPhysicalLabel0"));
        assertEquals(neDetailsExpectedListOf1Row.getLsz(), getTextOfElementInTable("tdLsz0"));
        assertEquals(neDetailsExpectedListOf1Row.getUewegeId(), getTextOfElementInTable("tdUewegeId0"));
        assertEquals(neDetailsExpectedListOf1Row.getLbz(), getTextOfElementInTable("tdLbz0"));
        assertEquals(neDetailsExpectedListOf1Row.getGegenstelleCategory(),getTextOfElementInTable("tdCategory0"));
        assertEquals(neDetailsExpectedListOf1Row.getGegenstelleVpsz(), getTextOfElementInTable("tdVpsz0"));


    }

    private String getTextOfElementInTable(String id) {
        SelenideElement tableComponent = $(A4InventarSuchePage.getNE_DETAILS_TABLE_LOCATOR());
        return tableComponent.findElement(By.id(id)).getText();
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
            neDetailtList.get(i).setLogicalLabel(elementsCollection.get(i * numberOfColumnsNeDetailList + 1).getText());
            neDetailtList.get(i).setPhysicalLabel(elementsCollection.get(i * numberOfColumnsNeDetailList + 2).getText());
            neDetailtList.get(i).setLsz(elementsCollection.get(i * numberOfColumnsNeDetailList + 4).getText());
            neDetailtList.get(i).setUewegeId(elementsCollection.get(i * numberOfColumnsNeDetailList + 5).getText());
            neDetailtList.get(i).setLbz(elementsCollection.get(i * numberOfColumnsNeDetailList + 6).getText());
            neDetailtList.get(i).setGegenstelleCategory(elementsCollection.get(i * numberOfColumnsNeDetailList + 8).getText());
            neDetailtList.get(i).setGegenstelleVpsz(elementsCollection.get(i * numberOfColumnsNeDetailList + 9).getText());
        }

        // Sort
        neDetailtList = neDetailtList
                .stream().sorted(Comparator.comparing(NetworkElementDetails::getNeUuid))
                .collect(Collectors.toList());
        return neDetailtList;
    }

    public void clickGegenStelleIcon() {
        SelenideElement tableComponent = $(A4InventarSuchePage.getNE_DETAILS_TABLE_LOCATOR());
        tableComponent.findElement(By.id("tdNetworkElementGegenstelle0")).click();
    }

    public void clickNepIcon() {
        SelenideElement tableComponent = $(A4InventarSuchePage.getNE_DETAILS_TABLE_LOCATOR());
        tableComponent.findElement(By.id("tdNetworkElementPort0")).click();
    }

    public void clickNelIcon() {
        SelenideElement tableComponent = $(A4InventarSuchePage.getNE_DETAILS_TABLE_LOCATOR());
        tableComponent.findElement(By.id("tdNetworkElementLink0")).click();
    }

    public void checkLandedOnCorrectNeDetailsPage(A4NetworkElement neDataB) {
        final A4ResourceInventoryNeDetailsPage a4ResourceInventoryNeDetailsPage = new A4ResourceInventoryNeDetailsPage();
        a4ResourceInventoryNeDetailsPage.validate(neDataB.getUuid());
    }

    // helper
    private List<NetworkElementDetails> generateExpectedData(A4NetworkElementPort nepDataA, A4NetworkElementLink nelData, A4NetworkElement neDataB) {
        NetworkElementDetails neDetailsLine1 = new NetworkElementDetails();
        neDetailsLine1.setLogicalLabel(nepDataA.getFunctionalPortLabel());
        neDetailsLine1.setPhysicalLabel("10ge 0/1");
        neDetailsLine1.setLsz("4N4"); // old: LSZ
        neDetailsLine1.setUewegeId(nelData.getUeWegId());
        neDetailsLine1.setLbz(nelData.getLbz());
        neDetailsLine1.setGegenstelleCategory(neDataB.getCategory());
        neDetailsLine1.setGegenstelleVpsz(neDataB.getVpsz());

        List<NetworkElementDetails> neDetailsExpectedList = new ArrayList<>();
        neDetailsExpectedList.add(neDetailsLine1);

        return neDetailsExpectedList;
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
