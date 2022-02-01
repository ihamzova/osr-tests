package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.testng.annotations.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.*;

@Slf4j
@ServiceLog({A4_RESOURCE_INVENTORY_MS, A4_RESOURCE_INVENTORY_UI_MS, A4_RESOURCE_INVENTORY_BFF_PROXY_MS, A4_INVENTORY_IMPORTER_MS})
@Epic("OS&R")
public class A4MobileNeSearchPageTest extends GigabitTest {

    public static final String STATE_PLANNING = "PLANNING";
    private final A4MobileUiRobot robotMobileUi = new A4MobileUiRobot();
    private final A4ResourceInventoryRobot robotRI = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private A4NetworkElementGroup a4NetworkElementGroup;
    private A4NetworkElementPort a4NetworkElementPortA;
    private A4NetworkElementPort a4NetworkElementPortB;
    private final Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();
    private final String A4_NE_INSTALLING_OLT_01 = "a4NetworkElementInstallingOlt01";
    private final String A4_NE_OPERATING_BOR_02 = "a4NetworkElementOperatingBor02";
    private final String A4_NE_PLANNING_LEAFSWITCH_01 = "a4NetworkElementPlanningLeafSwitch01";
    private final String A4_NE_RETIRING_PODSERVER_01 = "a4NetworkElementRetiringPodServer01";

    //helper methods
    public void waitForTableToFullyLoad(int numberOfElements) {
        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);
    }

    public void checkLastUpdateTimeHasChanged (OffsetDateTime lastUpdateTimeOld, String ne){
        OffsetDateTime lastUpdateTimeNew = robotRI.getExistingNetworkElement(a4NetworkElements.get(ne)
                .getUuid()).getLastUpdateTime();
        assertNotEquals(lastUpdateTimeOld, lastUpdateTimeNew);
    }

    public void checkTableAccordingToSearchCriteria(Map<String, A4NetworkElement> a4NeFilteredList) {
        //check if rows of tables are there, before proceeding
        waitForTableToFullyLoad(a4NeFilteredList.size());

        ElementsCollection elementsCollection = null;
        try {
            elementsCollection = robotMobileUi.getNeElementsCollection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(elementsCollection);
        List<String> concat = new ArrayList<>();
        elementsCollection.forEach(k -> concat.add(k.getText()));
        log.info("+++ Inhalt UI: " + concat);
        log.info("+++ Größe UI: " + concat.size() / 7);
        log.info("+++ Inhalt NeFilteredList: " + a4NeFilteredList);
        log.info("+++ Größe NeFilteredList: " + a4NeFilteredList.size());

        a4NeFilteredList.forEach((k, a4NetworkElement) -> {
            assertTrue(concat.contains(a4NetworkElement.getVpsz()), a4NetworkElement.getVpsz());
            assertTrue(concat.contains(a4NetworkElement.getFsz()), a4NetworkElement.getFsz());
            assertTrue(concat.contains(a4NetworkElement.getCategory()), a4NetworkElement.getCategory());
            assertTrue(concat.contains(a4NetworkElement.getPlanningDeviceName()), a4NetworkElement.getPlanningDeviceName());
            assertTrue(concat.contains(a4NetworkElement.getLifecycleState()), a4NetworkElement.getLifecycleState());
        });

        a4NeFilteredList.forEach((k, v) -> log.info("+++ Category: " + v.getCategory()));
    }

    @BeforeClass()
    public void init() {
        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        a4NetworkElements.put(A4_NE_INSTALLING_OLT_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingOlt01));

        String a4_NE_INSTALLING_SPINE_01 = "a4NetworkElementInstallingSpine01";
        a4NetworkElements.put(a4_NE_INSTALLING_SPINE_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingSpine01));

        a4NetworkElements.put(A4_NE_OPERATING_BOR_02, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementOperatingBor02));

        a4NetworkElements.put(A4_NE_PLANNING_LEAFSWITCH_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementPlanningLeafSwitch01));

        a4NetworkElements.put(A4_NE_RETIRING_PODSERVER_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01));

        a4NetworkElementPortA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_1G_002);
        a4NetworkElementPortB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);

        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider()
                .get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        robotRI.createNetworkElementGroup(a4NetworkElementGroup);
        a4NetworkElements.forEach((k, networkElement) -> robotRI.createNetworkElement(networkElement, a4NetworkElementGroup));
        robotRI.createNetworkElementPort(a4NetworkElementPortA, a4NetworkElements.get(A4_NE_OPERATING_BOR_02));
        robotRI.createNetworkElementPort(a4NetworkElementPortB, a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01));
    }

    @AfterClass
    public void cleanUp() {
        robotRI.deleteA4TestDataRecursively(a4NetworkElementGroup);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de, Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpsz() {
        robotMobileUi.openNetworkElementMobileSearchPage();
        robotMobileUi.enterVpsz(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getVpsz());
        robotMobileUi.clickSearchButton();

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

    @DataProvider(name = "networkElements")
    public static Object[][] networkElements() {
               return new Object[][] {
                       {"a4NetworkElementInstallingOlt01", "INSTALLING"},
                       {"a4NetworkElementOperatingBor02", "OPERATING"}};
    }

    @Test(dataProvider = "networkElements")
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-125689, DIGIHUB-126552")
    @Description("Test Mobile NE-search-page - reset ne to planning")
    public void testResetNeToPlanning(String ne, String lcs) throws InterruptedException {
        robotMobileUi.openNetworkElementMobileSearchPage();
        robotMobileUi.enterVpsz(a4NetworkElements.get(ne).getVpsz());
        robotMobileUi.clickSearchButton();
        robotMobileUi.checkRadioButton("1");

        OffsetDateTime lastUpdateTimeOld = robotRI
                .getExistingNetworkElement(a4NetworkElements.get(ne).getUuid()).getLastUpdateTime();

        assertEquals(robotRI.getExistingNetworkElement(a4NetworkElements.get(ne).getUuid()).getLifecycleState(), lcs);

        robotMobileUi.clickNeResetToPlanningButtonAndConfirm();
        sleepForSeconds(3); // process in db have to work

        // check db
        robotRI.checkNetworkElementIsResetToPlanning(a4NetworkElements.get(ne).getUuid());
        checkLastUpdateTimeHasChanged (lastUpdateTimeOld, ne);

        // check ui
        ElementsCollection elementsCollection = robotMobileUi.getNeElementsCollection();
        assertEquals(elementsCollection.get(6).getText(), STATE_PLANNING);

        // check NEMO Update
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(a4NetworkElements.get(ne)
                .getUuid(), "PUT", 1);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpszAndFsz() {
        robotMobileUi.openNetworkElementMobileSearchPage();
        robotMobileUi.enterVpsz(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getVpsz());
        robotMobileUi.enterFsz(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getFsz());
        robotMobileUi.clickSearchButton();

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
    public void testNeSearchByVpszAndLifecyleState() {
        robotMobileUi.openNetworkElementMobileSearchPage();
        robotMobileUi.enterVpsz(a4NetworkElements.get(A4_NE_PLANNING_LEAFSWITCH_01).getVpsz());
        robotMobileUi.checkPlanning();
        robotMobileUi.clickSearchButton();

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
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de, Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpszAnd2LifecyleStates() {
        robotMobileUi.openNetworkElementMobileSearchPage();
        robotMobileUi.enterVpsz(a4NetworkElements.get(A4_NE_PLANNING_LEAFSWITCH_01).getVpsz());
        robotMobileUi.checkPlanning();
        robotMobileUi.checkOperating();
        robotMobileUi.clickSearchButton();

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
    @Owner("DL-Berlinium@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process with VPSZ and Category search criteria")
    public void testNeSearchByVpszAndCategory() {
        robotMobileUi.openNetworkElementMobileSearchPage();
        robotMobileUi.enterVpsz(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getVpsz());
        robotMobileUi.clickSearchButton();

        Map<String, A4NetworkElement> a4NeFilteredList = a4NetworkElements
                .entrySet()
                .stream()
                .filter(map -> map.getValue().getCategory()
                        .equals(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getCategory()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        checkTableAccordingToSearchCriteria(a4NeFilteredList);
    }

}
