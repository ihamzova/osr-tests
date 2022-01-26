package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.robot.utils.MiscUtils;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class A4InventarSucheRobot {

    public static final int numberOfColumnsNegList = 6;
    public static final int numberOfColumnsNeList = 12;

    // helper method 'wait'
    public void waitForTableToFullyLoad(int numberOfElements) {
        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);
    }

    public ElementsCollection getNegElementsCollection() {
        // waitForTableToFullyLoad(elementsCollection.size());
        try {
            Thread.sleep(2000);
            return $(A4InventarSuchePage.getNEG_SEARCH_RESULT_TABLE_LOCATOR())
                    .findAll(By.xpath("tr/td"));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ElementsCollection getNeElementsCollection() {
        // waitForTableToFullyLoad(elementsCollection.size());
        try {
            Thread.sleep(2000);
            return $(A4InventarSuchePage.getNE_SEARCH_RESULT_TABLE_LOCATOR())
                    .findAll(By.xpath("tr/td"));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    // network element
    @Step("Choose search by NetworkElement")
    public void clickNetworkElement() {
        $(A4InventarSuchePage.getNE_CHOOSE_BUTTON_LOCATOR()).click();
    }

    @Step("Enter vpsz")
    public void enterNeVpsz(String value) {
        $(A4InventarSuchePage.getNE_VPSZ_FIELD_LOCATOR()).val(value);
    }

    @Step("Enter akz")
    public void enterNeAkz(String value) {
        $(A4InventarSuchePage.getNE_AKZ_FIELD_LOCATOR()).val(value);
    }

    @Step("Enter akz")
    public void enterNeAkzByVpsz(String vpsz) {
        final String akz = MiscUtils.stringSplit(vpsz, "/").get(0);
        $(A4InventarSuchePage.getNE_AKZ_FIELD_LOCATOR()).val(akz);
    }

    @Step("Enter onkz")
    public void enterNeOnkz(String value) {
        $(A4InventarSuchePage.getNE_ONKZ_FIELD_LOCATOR()).val(value);
    }

    @Step("Enter onkz")
    public void enterNeOnkzByVpsz(String vpsz) {
        final String onkz = MiscUtils.stringSplit(vpsz, "/").get(1);
        $(A4InventarSuchePage.getNE_ONKZ_FIELD_LOCATOR()).val(onkz);
    }

    @Step("Enter vkz")
    public void enterNeVkz(String value) {
        $(A4InventarSuchePage.getNE_VKZ_FIELD_LOCATOR()).val(value);
    }

    @Step("Enter vkz")
    public void enterNeVkzByVpsz(String vpsz) {
        final String vkz = MiscUtils.stringSplit(vpsz, "/").get(2);
        $(A4InventarSuchePage.getNE_VKZ_FIELD_LOCATOR()).val(vkz);
    }

    @Step("Enter fsz")
    public void enterNeFsz(String value) {
        $(A4InventarSuchePage.getNE_FSZ_FIELD_LOCATOR()).val(value);
    }

    @Step("Enter category")
    public void enterNeCategory(String value) {
        $(A4InventarSuchePage.getNE_CATEGORY_FIELD_LOCATOR()).selectOptionByValue(value);
    }

    @Step("Click ne search button")
    public void clickNeSearchButton() {
        $(A4InventarSuchePage.getNE_SEARCH_BUTTON_LOCATOR()).click();
    }


    // checkboxes
    @Step("Checkbox WORKING")
    public void checkboxWorking() {
        $(A4InventarSuchePage.getWORKING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox Op INSTALLING")
    public void checkboxOpInstalling() {
        $(A4InventarSuchePage.getOPS_INSTALLING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox NOT WORKING")
    public void checkboxNotWorking() {
        $(A4InventarSuchePage.getNOT_WORKING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox NOT MANAGEABLE")
    public void checkboxNotManageable() {
        $(A4InventarSuchePage.getNOT_MANAGEABLE_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox FAILED")
    public void checkboxFailed() {
        $(A4InventarSuchePage.getFAILED_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox ACTIVATING")
    public void checkboxActivating() {
        $(A4InventarSuchePage.getACTIVATING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox DEACTIVATING")
    public void checkboxDeactivating() {
        $(A4InventarSuchePage.getDEACTIVATING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox PLANNING")
    public void checkboxPlanning() {
        $(A4InventarSuchePage.getPLANNING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox Life INSTALLING")
    public void checkboxLifeInstalling() {
        $(A4InventarSuchePage.getLIFECYCLE_INSTALLING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox OPERATING")
    public void checkboxOperating() {
        $(A4InventarSuchePage.getOPERATING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Checkbox RETIRING")
    public void checkboxRetiring() {
        $(A4InventarSuchePage.getRETIRING_CHECKBOX_LOCATOR()).click();
    }

    // neg
    @Step("Choose search by NetworkElementGroup")
    public void clickNetworkElementGroup() {
        $(A4InventarSuchePage.getNEG_CHOOSE_BUTTON_LOCATOR()).click();
    }

    @Step("Enter neg name")
    public void enterNegName(String value) {
        $(A4InventarSuchePage.getNEG_NAME_INPUT_FIELD_LOCATOR()).val(value);
    }

    @Step("Read neg name")
    public String readNegName() {
        return $(A4InventarSuchePage.getNEG_NAME_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Click search button")
    public void clickNegSearchButton() {
        $(A4InventarSuchePage.getNEG_SEARCH_BUTTON_LOCATOR()).click();
    }

    // common
    @Step("Open UI, log in, and goTo Inventar-Suche-page")
    public void openInventarSuchePage() {
        A4InventarSuchePage
                .login();
    }

    @Step("Search for network element")
    public void searchForNetworkElement(A4NetworkElement neData) {
        openInventarSuchePage();
        clickNetworkElement();
        enterNeAkzByVpsz(neData.getVpsz());
        enterNeOnkzByVpsz(neData.getVpsz());
        enterNeVkzByVpsz(neData.getVpsz());
        clickNeSearchButton();
    }

    public void clickFirstRowInSearchResultTable() {
        getNeElementsCollection().get(0).click();
    }

    public List<NetworkElementGroupDto> createNegListActualResult() {
        ElementsCollection elementsCollection = getNegElementsCollection();
        return createNegListActualResult(elementsCollection);
    }

    public List<NetworkElementGroupDto> createNegListActualResult(ElementsCollection elementsCollection) {
        List<NetworkElementGroupDto> negActualResultList = new ArrayList<>();

        // read table from ui and fill list (actual result)
        List<String> eList = elementsCollection.texts();
        for (int i = 0; i < eList.size() / numberOfColumnsNegList; i++) {
            NetworkElementGroupDto negActualGeneric = new NetworkElementGroupDto();
            negActualGeneric.setUuid(eList.get(i * numberOfColumnsNegList));
            negActualGeneric.setName(eList.get(i * numberOfColumnsNegList + 1));
            negActualGeneric.setOperationalState(eList.get(i * numberOfColumnsNegList + 2));
            negActualGeneric.setLifecycleState(eList.get(i * numberOfColumnsNegList + 3));
            OffsetDateTime creationTime = OffsetDateTime.parse(eList.get(i * numberOfColumnsNegList + 4));
            OffsetDateTime lastUpdateTime = OffsetDateTime.parse(eList.get(i * numberOfColumnsNegList + 5));
            negActualGeneric.setCreationTime(creationTime); // wegen Formatproblem String-OffsetDateTime
            negActualGeneric.setLastUpdateTime(lastUpdateTime); // wegen Formatproblem String-OffsetDateTime
            negActualResultList.add(negActualGeneric);
        }

        // sort
        negActualResultList = negActualResultList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());

        return negActualResultList;
    }

    public List<NetworkElementDto> createNeListActualResult() {
        ElementsCollection elementsCollection = getNeElementsCollection();
        return createNeListActualResult(elementsCollection);
    }

    public List<NetworkElementDto> createNeListActualResult(ElementsCollection elementsCollection) {
        List<NetworkElementDto> neActualResultList = new ArrayList<>();

        // read table from ui and fill list (actual result)
        List<String> eList = elementsCollection.texts();
        for (int i = 0; i < eList.size() / numberOfColumnsNeList; i++) {
            NetworkElementDto neActualGeneric = new NetworkElementDto();
            neActualGeneric.setUuid(eList.get(i * numberOfColumnsNeList));
            neActualGeneric.setVpsz(eList.get(i * numberOfColumnsNeList + 1));
            neActualGeneric.setFsz(eList.get(i * numberOfColumnsNeList + 2));
            neActualGeneric.setCategory(eList.get(i * numberOfColumnsNeList + 3));
            neActualGeneric.setType(eList.get(i * numberOfColumnsNeList + 4));
            neActualGeneric.setZtpIdent(eList.get(i * numberOfColumnsNeList + 5));
            neActualGeneric.setKlsId(eList.get(i * numberOfColumnsNeList + 6));
            neActualGeneric.setPlanningDeviceName(eList.get(i * numberOfColumnsNeList + 7));
            neActualGeneric.setOperationalState(eList.get(i * numberOfColumnsNeList + 8));
            neActualGeneric.setLifecycleState(eList.get(i * numberOfColumnsNeList + 9));
            OffsetDateTime creationTime = OffsetDateTime.parse(eList.get(i * numberOfColumnsNeList + 10));
            OffsetDateTime lastUpdateTime = OffsetDateTime.parse(eList.get(i * numberOfColumnsNeList + 11));
            neActualGeneric.setCreationTime(creationTime); // wegen Formatproblem String-OffsetDateTime
            neActualGeneric.setLastUpdateTime(lastUpdateTime); // wegen Formatproblem String-OffsetDateTime
            neActualResultList.add(neActualGeneric);
        }

        // sort
        neActualResultList = neActualResultList
                .stream().sorted(Comparator.comparing(NetworkElementDto::getUuid))
                .collect(Collectors.toList());

        return neActualResultList;
    }

}
