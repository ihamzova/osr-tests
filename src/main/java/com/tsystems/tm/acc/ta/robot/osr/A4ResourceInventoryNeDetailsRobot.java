package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNeDetailsPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNepDetailsPage;
import io.qameta.allure.Step;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.assertEquals;

@Slf4j
public class A4ResourceInventoryNeDetailsRobot {

    @Step("Read uuid ne")
    public String readNeUuid() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_UUID_FIELD_LOCATOR()).val();
    }

    @Step("Read vpsz ne")
    public String readNeVpsz() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_VPSZ_FIELD_LOCATOR()).val();
    }

    @Step("Read fsz ne")
    public String readNeFsz() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_FSZ_FIELD_LOCATOR()).val();
    }

    @Step("Read category ne")
    public String readNeCategory() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_CATEGORY_FIELD_LOCATOR()).val();
    }

    @Step("Read type ne")
    public String readNeType() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_TYPE_FIELD_LOCATOR()).val();
    }

    @Step("Read PD Name ne")
    public String readNePlanningDeviceName() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_PlanningDeviceName_FIELD_LOCATOR()).val();
    }

    @Step("Read kls-id ne")
    public String readNeKlsId() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_KLSID_FIELD_LOCATOR()).val();
    }

    @Step("Read ztp ident ne")
    public String readNeZtpid() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_ZTPID_FIELD_LOCATOR()).val();
    }

    @Step("Read ops ne")
    public String readNeOps() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_OPS_FIELD_LOCATOR()).val();
    }

    @Step("Read lcs ne")
    public String readNeLcs() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_LCS_FIELD_LOCATOR()).val();
    }

    @Step("Read creation time ne")
    public String readNeCreationTime() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_CreationTime_FIELD_LOCATOR()).val();
    }

    @Step("Read last update time ne")
    public String readNeLastUpdateTime() {
        return $(A4ResourceInventoryNeDetailsPage.getNE_LastUpdateTime_FIELD_LOCATOR()).val();
    }

    @Step("Open UI, log in")
    public void openRiNeDetailPage(){
        A4ResourceInventoryNeDetailsPage.login();     // warum wird hier die Klasse verwendet?
    }

    public ElementsCollection getNelElementsCollection() {
        // waitForTableToFullyLoad(elementsCollection.size());
        return $(A4ResourceInventoryNeDetailsPage.getNEL_SEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));
    }

    @Step("Check details and table for network element")
    public void checkNeDetailsAndTableContents(A4NetworkElement neDataA, A4NetworkElementPort nepDataA, A4NetworkElementLink nelData, A4NetworkElement neDataB) {
        final A4InventarSucheRobot a4InventarSucheRobot = new A4InventarSucheRobot();
        final A4ResourceInventoryNeDetailsPage a4ResourceInventoryNeDetailsPage = new A4ResourceInventoryNeDetailsPage();
        List<NetworkElementDetails> neDetailsExpectedList = generateExpectedData(nepDataA, nelData, neDataB);

//        sleepForSeconds(10);

        // now we have the detail-list with NE-Port, NE-Link and opposite NE
        a4ResourceInventoryNeDetailsPage.validate();

        // check ne-block
        assertEquals(readNeUuid(), neDataA.getUuid());
        assertEquals(readNeVpsz(), neDataA.getVpsz());
        assertEquals(readNeFsz(), neDataA.getFsz());
        assertEquals(readNeCategory(), neDataA.getCategory());
        assertEquals(readNeType(), neDataA.getType());
        assertEquals(readNePlanningDeviceName(), neDataA.getPlanningDeviceName());
        assertEquals(readNeKlsId(), neDataA.getKlsId());
        assertEquals(readNeOps(), neDataA.getOperationalState());
        assertEquals(readNeLcs(), neDataA.getLifecycleState());

        // check port, link and gegenstelle data in table
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNeElementsCollection();
        List<NetworkElementDetails> neDetailsResultList = createNeDetailList(elementsCollection);
        assertEquals(neDetailsResultList.toString(), neDetailsExpectedList.toString());
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
        getNelElementsCollection().get(7).click();
    }

    public void clickNepIcon() {
        getNelElementsCollection().get(0).click();
    }

    public void clickNelIcon() {
        getNelElementsCollection().get(3).click();
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