package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.testng.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.*;

import static com.tsystems.tm.acc.ta.data.berlinium.BerliniumConstants.*;

@ServiceLog(A4_RESOURCE_INVENTORY)
@ServiceLog(A4_RESOURCE_INVENTORY_UI)
@ServiceLog(A4_RESOURCE_INVENTORY_BFF_PROXY)
@Slf4j
public class A4MobileNeSearchPageTest extends BaseTest {

    private final A4MobileUiRobot a4MobileUiRobot = new A4MobileUiRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    A4MobileNeSearchPage a4MobileNeSearchPage = new A4MobileNeSearchPage();

    private A4NetworkElementGroup a4NetworkElementGroup;

    private Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();

    final int WAITING_INTERVAL = 0;

    final String A4_NE_INSTALLING_OLT_01 = "a4NetworkElementInstallingOlt01";
    final String A4_NE_INSTALLING_SPINE_01 = "a4NetworkElementInstallingSpine01";
    final String A4_NE_OPERATING_BOR_01 = "a4NetworkElementOperatingBor01";
    final String A4_NE_PLANNING_LEAFSWITCH_01 = "a4NetworkElementPlanningLeafSwitch01";
    final String A4_NE_RETIRING_PODSERVER_01 = "a4NetworkElementRetiringPodServer01";

    //helper methods
    public void waitForTableToFullyLoad(int numberOfElements){

        //add 1 to number of elements because of table header
        numberOfElements++;

        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);

    }

    public void checkTableAccordingToSearchCriteria(Map<String, A4NetworkElement> a4NeFilteredList) {
        //check if rows of tables are there, before proceeding
        waitForTableToFullyLoad(a4NeFilteredList.size());

        ElementsCollection elementsCollection = $(a4MobileNeSearchPage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        List<String> concat = new ArrayList<>();

        elementsCollection.forEach(k -> concat.add(k.getText()));

        a4NeFilteredList.forEach((k, a4NetworkElement) -> {
            assertTrue(concat.contains(a4NetworkElement.getVpsz()),a4NetworkElement.getVpsz());
            assertTrue(concat.contains(a4NetworkElement.getFsz()),a4NetworkElement.getFsz());
            assertTrue(concat.contains(a4NetworkElement.getCategory()),a4NetworkElement.getCategory());
            assertTrue(concat.contains(a4NetworkElement.getPlanningDeviceName()),a4NetworkElement.getPlanningDeviceName());
            assertTrue(concat.contains(a4NetworkElement.getLifecycleState()),a4NetworkElement.getLifecycleState());
        });

        log.info("+++" + concat.toString());

        a4NeFilteredList.forEach((k,v) -> log.info("+++" + v.getCategory()));

        //check if table has only as many rows as expected by test data set
        //table has 6 columns and a4NeFilteredList contains cells, so we need to calculate a little bit
        assertEquals(concat.size()/6, a4NeFilteredList.size());
    }

    @BeforeMethod()
        public void doLogin() {
            Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
            SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        }

    @BeforeClass()
    public void init() {

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        a4NetworkElements.put(A4_NE_INSTALLING_OLT_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingOlt01));

        a4NetworkElements.put(A4_NE_INSTALLING_SPINE_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingSpine01));

        a4NetworkElements.put(A4_NE_OPERATING_BOR_01,osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementOperatingBor01));

        a4NetworkElements.put(A4_NE_PLANNING_LEAFSWITCH_01,osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementPlanningLeafSwitch01));

        a4NetworkElements.put(A4_NE_RETIRING_PODSERVER_01,osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01));

        cleanUp();
    }

    @BeforeClass
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);

        a4NetworkElements.forEach((k, networkElement)->
               a4ResourceInventoryRobot.createNetworkElement(networkElement, a4NetworkElementGroup));
    }

    @AfterClass
    public void cleanUp() {

        a4NetworkElements.forEach((k,v)->
                a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(v));

        a4ResourceInventoryRobot.deleteNetworkElementGroups(a4NetworkElementGroup);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpsz() {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        //assumption is that all elements have the same VPSZ, so we chose first elements' VPSZ
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getVpsz());
        a4MobileUiRobot.clickSearchButton();

        checkTableAccordingToSearchCriteria(a4NetworkElements);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpszAndFsz() {

        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getVpsz());
        a4MobileUiRobot.enterFsz(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getFsz());
        a4MobileUiRobot.clickSearchButton();

        Map<String, A4NetworkElement> a4NeFilteredList = a4NetworkElements
                .entrySet()
                .stream()
                .filter(map -> ((
                        map.getValue().getVpsz()
                                .equals(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getVpsz()))
                        && map.getValue().getFsz()
                                .equals(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getFsz())
                        )
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        checkTableAccordingToSearchCriteria(a4NeFilteredList);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpszAndLifecyleState(){
        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_PLANNING_LEAFSWITCH_01).getVpsz());
        a4MobileUiRobot.checkPlanning();
        a4MobileUiRobot.clickSearchButton();

        Map<String, A4NetworkElement> a4NeFilteredList = a4NetworkElements
                .entrySet()
                .stream()
                .filter(map -> (map.getValue().getLifecycleState()
                        .equals(a4NetworkElements.get(A4_NE_PLANNING_LEAFSWITCH_01).getLifecycleState()))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        checkTableAccordingToSearchCriteria(a4NeFilteredList);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpszAnd2LifecyleStates() {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_PLANNING_LEAFSWITCH_01).getVpsz());
        a4MobileUiRobot.checkPlanning();
        a4MobileUiRobot.checkOperating();
        a4MobileUiRobot.clickSearchButton();

        Map<String, A4NetworkElement> a4NeFilteredList = a4NetworkElements
                .entrySet()
                .stream()
                .filter(map -> (map.getValue().getLifecycleState()
                        .equals(a4NetworkElements.get(A4_NE_PLANNING_LEAFSWITCH_01).getLifecycleState()))
                        || (map.getValue().getLifecycleState()
                        .equals(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getLifecycleState()))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        checkTableAccordingToSearchCriteria(a4NeFilteredList);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process with VPSZ and Category search criteria")
    public void testNeSearchByVpszAndCategory() {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();

        //we assume it's always the same VPSZ so it doesn't matter which element the VPSZ was taken from
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getVpsz());
        //check for OLT
        a4MobileUiRobot.enterCategory(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getCategory());
        a4MobileUiRobot.clickSearchButton();

        Map<String, A4NetworkElement> a4NeFilteredList = a4NetworkElements
                .entrySet()
                .stream()
                .filter(map -> map.getValue().getCategory()
                        .equals(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getCategory()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        checkTableAccordingToSearchCriteria(a4NeFilteredList);

    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process with VPSZ and Category search criteria")
    public void testNeInstallation() {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();

        //we assume it's always the same VPSZ so it doesn't matter which element the VPSZ was taken from
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz());
        a4MobileUiRobot.enterFsz(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getFsz());
        a4MobileUiRobot.enterCategory(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getCategory());
        a4MobileUiRobot.checkPlanning();
        a4MobileUiRobot.checkOperating();
        a4MobileUiRobot.clickSearchButton();

        a4MobileUiRobot.checkRadioButton("1");
        a4MobileUiRobot.clickInbetriebnahmeButton();

        a4MobileUiRobot.clickFinishButton();
        assertEquals(a4MobileUiRobot.readVpsz(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz());
        assertTrue(a4MobileUiRobot.checkIsPlanningChecked());
        assertTrue(a4MobileUiRobot.checkIsOperatingChecked());
        assertEquals(a4MobileUiRobot.readFsz(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getFsz());
        assertEquals(a4MobileUiRobot.readCategory(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getCategory());
    }

    @Test
    @Owner("Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile Monitoring page")
    public void testMonitoring() {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();

        //we assume it's always the same VPSZ so it doesn't matter which element the VPSZ was taken from
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz());
        a4MobileUiRobot.enterCategory(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getCategory());
        a4MobileUiRobot.clickSearchButton();

        a4MobileUiRobot.checkRadioButton("1");
        a4MobileUiRobot.clickInbetriebnahmeButton();
        a4MobileUiRobot.enterZtpIdent("ztp");
        a4MobileUiRobot.clickFinishButton();
        a4MobileUiRobot.clickMonitoringButton();

        Map<String, A4NetworkElement> a4NeFilteredList = new HashMap<>();
        a4NeFilteredList.put(A4_NE_OPERATING_BOR_01, a4NetworkElements.get(A4_NE_OPERATING_BOR_01));
//                .entrySet()
//                .stream()
//                .filter(map -> map.getValue().getCategory()
//                        .equals(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getCategory()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        a4MobileUiRobot.checkMonitoring(a4NeFilteredList);

    }



}
