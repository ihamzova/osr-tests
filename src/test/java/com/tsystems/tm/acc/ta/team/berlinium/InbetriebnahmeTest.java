package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
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
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.saveEventsToDefaultDir;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/*@ServiceLog(A4_RESOURCE_INVENTORY_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_UI_MS)
@ServiceLog(A4_RESOURCE_INVENTORY_BFF_PROXY_MS)
@ServiceLog(A4_INVENTORY_IMPORTER_MS)*/
@Slf4j
public class InbetriebnahmeTest extends GigabitTest {

    private final A4MobileUiRobot a4MobileUiRobot = new A4MobileUiRobot();
    private final A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private A4NetworkElementGroup neg;
    private A4NetworkElementPort nepA;
    private A4NetworkElementPort nepB;

    private final Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();

    final String A4_NE_OPERATING_BOR_01 = "a4NetworkElementOperatingBor02";
    final String A4_NE_RETIRING_PODSERVER_01 = "a4NetworkElementRetiringPodServer01";

    private UewegData uewegData;
    private EquipmentData equipmentData;

    private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "InbetriebnahmeTest")).build();

    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider()
                .get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        neg = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        a4NetworkElements.put(A4_NE_OPERATING_BOR_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementOperatingBor01));
        a4NetworkElements.put(A4_NE_RETIRING_PODSERVER_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01));

        nepA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_1G_002);
        nepB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);

        uewegData = osrTestContext.getData().getUewegDataDataProvider().get(UewegDataCase.defaultUeweg);
        equipmentData = osrTestContext.getData().getEquipmentDataDataProvider()
                .get(EquipmentDataCase.equipment_MatNr_40318601);

        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(neg);

        a4NetworkElements.forEach((k, networkElement) ->
                a4ResourceInventoryRobot.createNetworkElement(networkElement, neg));

        a4ResourceInventoryRobot.createNetworkElementPort(nepA, a4NetworkElements.get(A4_NE_OPERATING_BOR_01));
        a4ResourceInventoryRobot.createNetworkElementPort(nepB, a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01));

        mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "InbetriebnahmeTest"))
                .addPslMock(equipmentData, a4NetworkElements.get(A4_NE_OPERATING_BOR_01))
                .addRebellMock(uewegData, a4NetworkElements.get(A4_NE_OPERATING_BOR_01), a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01))
                .addNemoMock()
                .build().publish();
    }

    @AfterClass
    public void cleanUp() {
        mappingsContext.close();
        mappingsContext
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        a4ResourceInventoryRobot.deleteA4TestDataRecursively(neg);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de, bela.kovac@t-systems.com, Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test NE Inbetriebnahme process (by entering ZTPIdent)")
    public void testNeInstallation() {
        final String ztpi = "test-ztpi" + getRandomDigits(4);

        // GIVEN
        a4MobileUiRobot.searchForNetworkElement(a4NetworkElements.get(A4_NE_OPERATING_BOR_01));

        // WHEN
        a4MobileUiRobot.doInbetriebnahme(ztpi);

        // THEN

        // back on search page
        a4MobileUiRobot.checkInstalling();
        assertEquals(a4MobileUiRobot.readVpsz(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz());
        assertEquals(a4MobileUiRobot.readAkz(), stringSplit(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz(), "/").get(0));
        assertEquals(a4MobileUiRobot.readOnkz(), stringSplit(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz(), "/").get(1));
        assertEquals(a4MobileUiRobot.readVkz(), stringSplit(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz(), "/").get(2));
        assertEquals(a4MobileUiRobot.readFsz(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getFsz());
        assertEquals(a4MobileUiRobot.readCategory(), a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getCategory());

        // Give logic some time to do requests to PSL, REBELL and A4 resource inventory
        sleepForSeconds(5);

        // Check ZTP-ID value in search result table
        assertEquals(a4MobileUiRobot.readZtpIdent(), ztpi);


        // Fehlermeldung: expected [40318601] but found [], hat sich am psl-mapper etwas geändert?
        System.out.println("+++ checkNetworkElementIsUpdatedWithPslData: ");
        a4ResourceInventoryRobot.checkNetworkElementIsUpdatedWithPslData(a4NetworkElements.get(A4_NE_OPERATING_BOR_01)
                .getUuid(), equipmentData);


        // Expected exactly 2 requests matching the following pattern but received 1
        System.out.println("+++ checkLogicalResourceRequestToNemoWiremock: ");
        // a4NemoUpdaterRobot.checkLogicalResourceRequestToNemoWiremock(a4NetworkElements.get(A4_NE_OPERATING_BOR_02).getUuid(), "PUT", 2);


        // Problem bei Nachtlauf, expected [1] but found [0]
        System.out.println("+++ uewegData: "+uewegData);
        System.out.println("+++ NEP A: "+ nepA.getUuid());
        System.out.println("+++ NEP B: "+ nepB.getUuid());
        System.out.println("+++ checkNetworkElementLinkConnectedToNePortExists: ");
        a4ResourceInventoryRobot.checkNetworkElementLinkConnectedToNePortExists(uewegData, nepA.getUuid(), nepB.getUuid());


        // Problem bei Nachtlauf, Expected exactly 1 requests matching the following pattern but received 2
        // or: expected [1] but found [0]
        System.out.println("+++ checkNetworkElementLinkPutRequestToNemoWiremock: ");
        // a4NemoUpdaterRobot.checkNetworkElementLinkPutRequestToNemoWiremock(a4NetworkElementPortB.getUuid());
    }

    @Test
    @Owner("Thea.John@telekom.de, heiko.schwanke@t-systems.com, bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile Monitoring page of NE for which Inbetriebnahme was done")
    public void testNeMonitoring() throws InterruptedException {
        Map<String, A4NetworkElement> a4NeFilteredMap = new HashMap<>();

        // GIVEN
        a4MobileUiRobot.searchForNetworkElement(a4NetworkElements.get(A4_NE_OPERATING_BOR_01));
        a4MobileUiRobot.doInbetriebnahme("ztp");

        // WHEN
        a4NeFilteredMap.put(A4_NE_OPERATING_BOR_01, a4NetworkElements.get(A4_NE_OPERATING_BOR_01));
        a4MobileUiRobot.clickMonitoringButton();

        // THEN
        a4MobileUiRobot.checkMonitoring(a4NeFilteredMap, equipmentData);

        // remove all entries
        a4MobileUiRobot.removeNetworkElementFromMonitoringList(a4NeFilteredMap, A4_NE_OPERATING_BOR_01, a4NetworkElements.get(A4_NE_OPERATING_BOR_01));
        a4MobileUiRobot.checkEmptyMonitoringList(a4NeFilteredMap);
    }

}
