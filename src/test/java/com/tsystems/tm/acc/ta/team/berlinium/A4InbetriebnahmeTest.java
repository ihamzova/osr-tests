package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.equipmentdata.EquipmentDataCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.saveEventsToDefaultDir;


@ServiceLog({A4_RESOURCE_INVENTORY_MS, A4_RESOURCE_INVENTORY_UI_MS, A4_RESOURCE_INVENTORY_BFF_PROXY_MS, A4_INVENTORY_IMPORTER_MS})
@Epic("OS&R")
@Feature("Test Inbetriebnahme for NEs and NELs, including monitoring")
@TmsLink("DIGIHUB-xxxxx")
public class A4InbetriebnahmeTest extends GigabitTest {

    private final String wiremockScenarioName = "A4InbetriebnahmeTest";
    private final A4MobileUiRobot a4MobileUi = new A4MobileUiRobot();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4ResourceInventoryRobot robotRI = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(
            new WireMockMappingsContext(WireMockFactory.get(), wiremockScenarioName)).build();
    private A4NetworkElementGroup neg;
    private A4NetworkElementPort nepA;
    private A4NetworkElementPort nepB;
    private UewegData uewegData;
    private EquipmentData equipmentData;
    private final Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();
    private final Map<String, A4NetworkElementLink> a4NetworkElementLinks = new HashMap<>();
    private final String A4_NE_OPERATING_BOR_02 = "a4NetworkElementOperatingBor02";
    private final String A4_NE_OPERATING_BOR_02_LINK1 = "a4NetworkElementOperatingBor02Link";
    private final String A4_NE_RETIRING_PODSERVER_01 = "a4NetworkElementRetiringPodServer01";
    private final String A4_NE_B = "a4NetworkElementB";

    @BeforeClass()
    public void init() {
        neg = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        a4NetworkElements.put(A4_NE_OPERATING_BOR_02, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementOperatingBor02));
        a4NetworkElements.put(A4_NE_RETIRING_PODSERVER_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01));
        a4NetworkElements.put(A4_NE_B, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementB));
        nepA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_1G_002);
        nepB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
        uewegData = osrTestContext.getData().getUewegDataDataProvider().get(UewegDataCase.defaultUeweg);
        equipmentData = osrTestContext.getData().getEquipmentDataDataProvider()
                .get(EquipmentDataCase.equipment_MatNr_40318601);

        A4NetworkElementLink a4Link = osrTestContext.getData().getA4NetworkElementLinkDataProvider().get(A4NetworkElementLinkCase.defaultNetworkElementLink);
        a4NetworkElementLinks.put(A4_NE_OPERATING_BOR_02_LINK1, a4Link);

        cleanUp(); // Make sure no old test data is in the way
    }

    @BeforeMethod
    public void setup() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider()
                .get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        robotRI.createNetworkElementGroup(neg);
        a4NetworkElements.forEach((k, networkElement) -> robotRI.createNetworkElement(networkElement, neg));
        robotRI.createNetworkElementPort(nepA, a4NetworkElements.get(A4_NE_OPERATING_BOR_02));
        robotRI.createNetworkElementPort(nepB, a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01));

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                wiremockScenarioName))
                .addPslMock(equipmentData, a4NetworkElements.get(A4_NE_OPERATING_BOR_02))
                .addRebellMock(uewegData, a4NetworkElements.get(A4_NE_OPERATING_BOR_02),
                        a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01))
                .addNemoMock()
                .build().publish();
    }

    @AfterMethod
    public void cleanUp() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        // Delete all A4 data which might provoke problems because of unique constraints
        robotRI.deleteA4NetworkElementGroupsRecursively(neg);
        a4NetworkElements.forEach((k, ne) -> robotRI.deleteA4NetworkElementsRecursively(ne));
        robotRI.deleteA4NetworkElementPortsRecursively(nepA, a4NetworkElements.get(A4_NE_OPERATING_BOR_02));
        robotRI.deleteA4NetworkElementPortsRecursively(nepB, a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01));
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de, bela.kovac@t-systems.com, Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test NE Inbetriebnahme process (by entering ZTPIdent)")
    public void testNeInstallation() {
        // GIVEN
        final String ztpi = "test-ztpi" + getRandomDigits(4);
        a4MobileUi.searchForNetworkElement(a4NetworkElements.get(A4_NE_OPERATING_BOR_02));
        // WHEN
        a4MobileUi.doNeInbetriebnahme(ztpi);
        // THEN
        a4MobileUi.checkSearchResultPageAfterNeInbetriebnahme(a4NetworkElements.get(A4_NE_OPERATING_BOR_02), ztpi);
        sleepForSeconds(5); // Give logic some time to do requests to PSL, REBELL and A4 resource inventory
        robotRI.checkNetworkElementIsUpdatedWithPslData(a4NetworkElements.get(A4_NE_OPERATING_BOR_02).getUuid(), equipmentData);
        robotRI.checkNetworkElementLinkConnectedToNePortExists(uewegData, nepA.getUuid(), nepB.getUuid());
        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(a4NetworkElements.get(A4_NE_OPERATING_BOR_02).getUuid(), "PUT", 2);
        a4NemoUpdater.checkNetworkElementLinkPutRequestToNemoWiremock(nepA.getUuid());
    }

    @Test
    @Owner("Thea.John@telekom.de, heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile Monitoring page of NE for which Inbetriebnahme was done")
    public void testNeMonitoring() {
        // GIVEN
        Map<String, A4NetworkElement> a4NeFilteredMap = new HashMap<>();
        a4NeFilteredMap.put(A4_NE_OPERATING_BOR_02, a4NetworkElements.get(A4_NE_OPERATING_BOR_02));
        a4MobileUi.searchForNetworkElement(a4NetworkElements.get(A4_NE_OPERATING_BOR_02));
        a4MobileUi.doNeInbetriebnahme("ztp");

        // WHEN
        a4MobileUi.clickMonitoringButton();

        // THEN
        a4MobileUi.checkNEMonitoringList(a4NeFilteredMap, equipmentData);
        a4MobileUi.removeNetworkElementFromNEMonitoringList(a4NeFilteredMap, A4_NE_OPERATING_BOR_02,
                a4NetworkElements.get(A4_NE_OPERATING_BOR_02));
        a4MobileUi.checkEmptyNeMonitoringList(a4NeFilteredMap);
    }

    @Test
    @Owner("juergen.mayer@t-systems.com, anita.junge@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile Monitoring page of NEL for which Inbetriebnahme was done")
    public void testNelMonitoring() {
        // GIVEN
        Map<String, A4NetworkElement> a4NeFilteredApointMap = new HashMap<>();
        a4NeFilteredApointMap.put(A4_NE_OPERATING_BOR_02, a4NetworkElements.get(A4_NE_OPERATING_BOR_02));

        Map<String, A4NetworkElementLink> a4NelFilteredMap = new HashMap<>();
        a4NelFilteredMap.put(A4_NE_OPERATING_BOR_02_LINK1, a4NetworkElementLinks.get(A4_NE_OPERATING_BOR_02_LINK1));

        // WHEN
        a4MobileUi.searchForNetworkElement(a4NetworkElements.get(A4_NE_OPERATING_BOR_02));
        a4MobileUi.checkOperating();
        a4MobileUi.checkRadioButton("1");
        a4MobileUi.clickZeigeNelZuNeButton();
        a4MobileUi.checkPlanningFilter();
        a4MobileUi.checkCheckbox();
        a4MobileUi.clickButtonAndConfirm();
        a4MobileUi.clickMonitoringButton();


        // THEN
        a4MobileUi.checkNELMonitoringList(a4NeFilteredApointMap,
                a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01).getVpsz(),
                a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01).getFsz());

        a4MobileUi.removeNetworkElementFromNELMonitoringList(a4NelFilteredMap, A4_NE_OPERATING_BOR_02_LINK1,
                a4NetworkElementLinks.get(A4_NE_OPERATING_BOR_02_LINK1));

        a4MobileUi.checkEmptyNelMonitoringList(a4NelFilteredMap);

    }

    @Test
    @Owner("Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test NEL Inbetriebnahme process")
    public void testNelInstallation() {
        // GIVEN
        a4MobileUi.searchForNetworkElement(a4NetworkElements.get(A4_NE_OPERATING_BOR_02));

        // WHEN
        a4MobileUi.doNelInbetriebnahme();

        // THEN
        robotRI.checkNetworkElementLinkInStateInstalling(nepB.getUuid());
        // TODO: Fix me! How to do correct check? (How to reset wiremock counter between tests?)
//        a4NemoUpdater.checkNetworkElementLinkPutRequestToNemoWiremock(nepB.getUuid());
//        a4NemoUpdater.checkLogicalResourceRequestToNemoWiremock(nepB.getUuid(), "PUT", 4);
        cleanUp();
    }

    @Test
    @Owner("Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test NEL Monitoring page when no suitable NEL available")
    public void testNelInstallationNoNel() {
        // GIVEN
        a4MobileUi.searchForNetworkElement(a4NetworkElements.get(A4_NE_B));
        // WHEN
        a4MobileUi.startNelInstallation();
        sleepForSeconds(3);
        // THEN
        a4MobileUi.checkNotFound();
    }

}
