package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.equipmentdata.EquipmentDataCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/*@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_UI_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_BFF_PROXY_MS)
@ServiceLog(A4_INVENTORY_IMPORTER_MS)*/
@Slf4j
public class A4MobileNeSearchPageTest extends BaseTest {

    private final A4MobileUiRobot a4MobileUiRobot = new A4MobileUiRobot();
    private final A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private A4NetworkElementGroup a4NetworkElementGroup;
    private A4NetworkElementPort a4NetworkElementPortA;
    private A4NetworkElementPort a4NetworkElementPortB;

    private final Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();

    final String A4_NE_INSTALLING_OLT_01 = "a4NetworkElementInstallingOlt01";
    final String A4_NE_INSTALLING_SPINE_01 = "a4NetworkElementInstallingSpine01";
    final String A4_NE_OPERATING_BOR_01 = "a4NetworkElementOperatingBor01";
    final String A4_NE_PLANNING_LEAFSWITCH_01 = "a4NetworkElementPlanningLeafSwitch01";
    final String A4_NE_RETIRING_PODSERVER_01 = "a4NetworkElementRetiringPodServer01";

    private UewegData uewegData;
    private EquipmentData equipmentDataA;

    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();


    //helper methods
    public void waitForTableToFullyLoad(int numberOfElements){
        //add 1 to number of elements because of table header
        numberOfElements++;

        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);
    }

    public void checkTableAccordingToSearchCriteria(Map<String, A4NetworkElement> a4NeFilteredList) {
        //check if rows of tables are there, before proceeding
        waitForTableToFullyLoad(a4NeFilteredList.size());

        ElementsCollection elementsCollection = $(A4MobileNeSearchPage.getSEARCH_RESULT_TABLE_LOCATOR())
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

    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

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

        a4NetworkElementPortA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_1G_002);
        a4NetworkElementPortB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);

        uewegData = osrTestContext.getData().getUewegDataDataProvider().get(UewegDataCase.defaultUeweg);
        equipmentDataA = osrTestContext.getData().getEquipmentDataDataProvider()
                .get(EquipmentDataCase.equipment_MatNr_40318601);

        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);

        a4NetworkElements.forEach((k, networkElement)->
               a4ResourceInventoryRobot.createNetworkElement(networkElement, a4NetworkElementGroup));

        a4ResourceInventoryRobot.createNetworkElementPort(a4NetworkElementPortA, a4NetworkElements.get(A4_NE_OPERATING_BOR_01));
        a4ResourceInventoryRobot.createNetworkElementPort(a4NetworkElementPortB, a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01));

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "MonitoringInstallingTest"))
                .addRebellMock(uewegData, a4NetworkElements.get(A4_NE_OPERATING_BOR_01), a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01))
                .addPslMock(equipmentDataA, a4NetworkElements.get(A4_NE_OPERATING_BOR_01))
                .addNemoMock()
                .build();

        mappingsContext.publish();
    }

    @AfterClass
    public void cleanUp() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        a4ResourceInventoryRobot.deleteA4TestDataRecursively(a4NetworkElementGroup);
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
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page with VPSZ and Category search criteria, perform installation process by entering ZTPIdent")
    public void testNeInstallation() {
        // GIVEN
        final String ztpi = "test-ztpi" + getRandomDigits(4);

        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        // !! Check if on search page (build into robot openNetworkElementMobileSearchPage)

        //we assume it's always the same VPSZ so it doesn't matter which element the VPSZ was taken from
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz());
        a4MobileUiRobot.enterFsz(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getFsz());
        a4MobileUiRobot.enterCategory(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getCategory());
        a4MobileUiRobot.checkPlanning();
        a4MobileUiRobot.checkOperating();
        a4MobileUiRobot.clickSearchButton();

        // WHEN
        a4MobileUiRobot.checkRadioButton("1");
        a4MobileUiRobot.clickInbetriebnahmeButton();

        // !! Check if on Inbetriebnahme page
        a4MobileUiRobot.enterZtpIdent(ztpi);
        a4MobileUiRobot.clickFinishButton();

        // THEN
        // !! Check if back on search page
        a4MobileUiRobot.checkInstalling();
        assertEquals(a4MobileUiRobot.readVpsz(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz());
        assertEquals(a4MobileUiRobot.readAkz(), stringSplit(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz(), "/").get(0));
        assertEquals(a4MobileUiRobot.readOnkz(), stringSplit(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz(), "/").get(1));
        assertEquals(a4MobileUiRobot.readVkz(), stringSplit(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz(), "/").get(2));
        assertEquals(a4MobileUiRobot.readFsz(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getFsz());
        assertEquals(a4MobileUiRobot.readCategory(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getCategory());
        assertTrue(a4MobileUiRobot.checkIsPlanningChecked());
        assertTrue(a4MobileUiRobot.checkIsOperatingChecked());

        // Give logic some time to do requests to PSL, REBELL and A4 resource inventory
        sleepForSeconds(5);

        // Check ZTPI value in search result table
        assertEquals(a4MobileUiRobot.readZtpIdent(), ztpi);

        a4ResourceInventoryRobot.checkNetworkElementIsUpdatedWithPslData(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getUuid(), equipmentDataA);
        a4NemoUpdaterRobot.checkLogicalResourceRequestToNemoWiremock(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getUuid(), "PUT",
                2);
        a4ResourceInventoryRobot.checkNetworkElementLinkConnectedToNePortExists(uewegData, a4NetworkElementPortA.getUuid(),
                a4NetworkElementPortB.getUuid());
        a4NemoUpdaterRobot.checkNetworkElementLinkPutRequestToNemoWiremock(a4NetworkElementPortA.getUuid());
    }

}
