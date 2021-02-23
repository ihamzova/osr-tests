package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static org.testng.Assert.assertEquals;

@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_UI_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_BFF_PROXY_MS)
@ServiceLog(A4_INVENTORY_IMPORTER_MS)
@Slf4j
public class A4InventarSuchePageTest extends BaseTest {
    private final A4InventarSucheRobot a4InventarSucheRobot = new A4InventarSucheRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final int numberOfColumnsNegList = 6;

    A4InventarSuchePage a4InventarSuchePage = new A4InventarSuchePage();

    private A4NetworkElementGroup a4NetworkElementGroup;
    private Map<String, A4NetworkElementGroup> a4NetworkElementGroups = new HashMap<>();

    // helper method 'wait'
    public void waitForTableToFullyLoad(int numberOfElements){
        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);
    }

    // helper 'createActualResult'
    public List<NetworkElementGroupDto> createNegListActualResult ( ElementsCollection elementsCollection ){
        // create empty list
        List <NetworkElementGroupDto> negActualResultList = new ArrayList<>();
        for (int i = 0; i < elementsCollection.size() / numberOfColumnsNegList; i++) {
            NetworkElementGroupDto negActualGeneric = new NetworkElementGroupDto();
            negActualResultList.add(negActualGeneric);
        }
        log.info("+++negActualResultList: "+negActualResultList.size());

        // read table from ui and fill list (actual result)
        for (int i = 0; i < elementsCollection.size() / numberOfColumnsNegList; i++){
            negActualResultList.get(i).setUuid(elementsCollection.get(i * numberOfColumnsNegList +0).getText());
            negActualResultList.get(i).setName(elementsCollection.get(i * numberOfColumnsNegList +1).getText());
            negActualResultList.get(i).setOperationalState(elementsCollection.get(i * numberOfColumnsNegList +2).getText());
            negActualResultList.get(i).setLifecycleState(elementsCollection.get(i * numberOfColumnsNegList +3).getText());
            OffsetDateTime creationTime = OffsetDateTime.parse(elementsCollection.get(i*numberOfColumnsNegList+4).getText());
            OffsetDateTime lastUpdateTime = OffsetDateTime.parse(elementsCollection.get(i*numberOfColumnsNegList+5).getText());
           // log.info("+++time "+creationTime.toString());
            negActualResultList.get(i).setCreationTime(creationTime); // wegen Formatproblem String-OffsetDateTime
            negActualResultList.get(i).setLastUpdateTime(lastUpdateTime); // wegen Formatproblem String-OffsetDateTime
            //negActualResultList.get(i).setLastUpdateTime(elementsCollection.get(i*numberOfColumnsNegList+5).getText()); // Formatproblem
        }
        // sort
        negActualResultList = negActualResultList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
           return negActualResultList;
    }

    // helper 'compare'
    public void compareExpectedResultWithActualResultNegList (List <NetworkElementGroupDto>negFilteredList,
                                                              List <NetworkElementGroupDto>negActualResultList,
                                                              int elementsCollectionSize ){
        for (int i = 0; i < elementsCollectionSize / numberOfColumnsNegList; i++) {
            assertEquals(negFilteredList.get(i).getUuid(), negActualResultList.get(i).getUuid());
            assertEquals(negFilteredList.get(i).getName(), negActualResultList.get(i).getName());
            assertEquals(negFilteredList.get(i).getLifecycleState(), negActualResultList.get(i).getLifecycleState());
            assertEquals(negFilteredList.get(i).getOperationalState(), negActualResultList.get(i).getOperationalState());
            assertEquals(negFilteredList.get(i).getCreationTime().toString(), negActualResultList.get(i).getCreationTime().toString()); // wegen Formatproblem String-OffsetDateTime
            assertEquals(negFilteredList.get(i).getLastUpdateTime().toString(), negActualResultList.get(i).getLastUpdateTime().toString()); // wegen Formatproblem String-OffsetDateTime
            log.info("+++uuid: "+negActualResultList.get(i).getUuid());
        }
    }

    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);
    }

    @AfterClass
    public void cleanUp() {
        a4ResourceInventoryRobot.deleteNetworkElementGroups(a4NetworkElementGroup);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchWorkingOpsInstalling() throws InterruptedException {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxWorking();
        a4InventarSucheRobot.checkboxOpInstalling();
        a4InventarSucheRobot.clickSearchButton();

        ElementsCollection elementsCollection = $(a4InventarSuchePage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        waitForTableToFullyLoad(elementsCollection.size()/numberOfColumnsNegList);

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();
        log.info("+++Anzahl NEGs: "+allNegList.size());

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("WORKING") || group.getOperationalState().equals("INSTALLING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = createNegListActualResult(elementsCollection);

        // compare, actual and expected result
         compareExpectedResultWithActualResultNegList (negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchWorkingOperating() throws InterruptedException {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxWorking();
        a4InventarSucheRobot.checkboxOperating();
        a4InventarSucheRobot.clickSearchButton();

        ElementsCollection elementsCollection = $(a4InventarSuchePage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        waitForTableToFullyLoad(elementsCollection.size()/numberOfColumnsNegList);

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();
        log.info("+++Anzahl NEGs: "+allNegList.size());

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("WORKING") && group.getLifecycleState().equals("OPERATING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = createNegListActualResult(elementsCollection);

        // compare, actual and expected result
        compareExpectedResultWithActualResultNegList (negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchWorkingLcsInstalling() throws InterruptedException {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxWorking();
        a4InventarSucheRobot.checkboxLifeInstalling();
        a4InventarSucheRobot.clickSearchButton();

        ElementsCollection elementsCollection = $(a4InventarSuchePage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        waitForTableToFullyLoad(elementsCollection.size()/numberOfColumnsNegList);

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();
        log.info("+++Anzahl NEGs: "+allNegList.size());

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("WORKING") && group.getLifecycleState().equals("INSTALLING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = createNegListActualResult(elementsCollection);

        // compare, actual and expected result
        compareExpectedResultWithActualResultNegList (negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchNotWorkingPlanning() throws InterruptedException {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxNotWorking();
        a4InventarSucheRobot.checkboxPlanning();
        a4InventarSucheRobot.clickSearchButton();

        ElementsCollection elementsCollection = $(a4InventarSuchePage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        waitForTableToFullyLoad(elementsCollection.size()/numberOfColumnsNegList);

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();
        log.info("+++Anzahl NEGs: "+allNegList.size());

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("NOT_WORKING") && group.getLifecycleState().equals("PLANNING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = createNegListActualResult(elementsCollection);

        // compare, actual and expected result
        compareExpectedResultWithActualResultNegList (negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchNotManageableRetiring() throws InterruptedException {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxNotManageable();
        a4InventarSucheRobot.checkboxRetiring();
        a4InventarSucheRobot.clickSearchButton();

        ElementsCollection elementsCollection = $(a4InventarSuchePage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        waitForTableToFullyLoad(elementsCollection.size()/numberOfColumnsNegList);

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();
        log.info("+++Anzahl NEGs: "+allNegList.size());

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("NOT_MANAGEABLE") && group.getLifecycleState().equals("RETIRING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = createNegListActualResult(elementsCollection);

        // compare, actual and expected result
        compareExpectedResultWithActualResultNegList (negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchByName() throws InterruptedException {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.enterNegName(a4NetworkElementGroup.getName());  // default NEG: NEG-367326
        a4InventarSucheRobot.clickSearchButton();

        waitForTableToFullyLoad(1);

        // read ui
        ElementsCollection elementsCollection = $(a4InventarSuchePage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        // compare ui and default
        assertEquals(elementsCollection.get(0).getText(),a4NetworkElementGroup.getUuid());
        assertEquals(elementsCollection.get(1).getText(),a4NetworkElementGroup.getName());
        assertEquals(elementsCollection.get(2).getText(),a4NetworkElementGroup.getOperationalState());
        assertEquals(elementsCollection.get(3).getText(),a4NetworkElementGroup.getLifecycleState());
        //assertEquals(elementsCollection.get(4).getText(),a4NetworkElementGroup.getCreationTime()); // not set in default
        //assertEquals(elementsCollection.get(5).getText(),a4NetworkElementGroup.getLastUpdateTime()); // not set in default

        //log.info("+++xxxtime "+elementsCollection.get(4).getText());     // 2021-02-23T14:11:19.316+01:00
        //log.info("+++xxxtime "+elementsCollection.get(4).toString());    // <td>2021-02-23T14:11:19.316+01:00</td>
        //log.info("+++xxxtime "+a4NetworkElementGroup.getCreationTime()); // null
    }
}
