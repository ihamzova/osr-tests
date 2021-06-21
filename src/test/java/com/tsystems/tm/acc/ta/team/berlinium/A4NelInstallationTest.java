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
import com.tsystems.tm.acc.ta.robot.osr.A4NelInstallationUiRobot;
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

import java.util.HashMap;
import java.util.Map;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.saveEventsToDefaultDir;

@Slf4j
public class A4NelInstallationTest extends GigabitTest {

        private final String wiremockScenarioName = "A4NelInstallationTest";

        private final A4MobileUiRobot a4MobileUiRobot = new A4MobileUiRobot();
        private final A4NelInstallationUiRobot a4NelInstallationUiRobot = new A4NelInstallationUiRobot();
        private final A4NemoUpdaterRobot a4NemoUpdaterRobot = new A4NemoUpdaterRobot();
        private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
        private final OsrTestContext osrTestContext = OsrTestContext.get();
        private WireMockMappingsContext mappingsContext = new OsrWireMockMappingsContextBuilder(
                new WireMockMappingsContext(WireMockFactory.get(), wiremockScenarioName)).build();

        private A4NetworkElementGroup neg;
        private A4NetworkElementPort nepA;
        private A4NetworkElementPort nepB;
        private UewegData uewegData;
        private EquipmentData equipmentData;
        private final Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();

        private final String A4_NE_OPERATING_BOR_01 = "a4NetworkElementOperatingBor02";
        private final String A4_NE_RETIRING_PODSERVER_01 = "a4NetworkElementRetiringPodServer01";
        private final String A4_NE_B = "a4NetworkElementB";

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
            a4NetworkElements.put(A4_NE_B, osrTestContext.getData().getA4NetworkElementDataProvider()
                    .get(A4NetworkElementCase.networkElementB));
            nepA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                    .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_1G_002);
            nepB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                    .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);
            uewegData = osrTestContext.getData().getUewegDataDataProvider().get(UewegDataCase.defaultUeweg);
            equipmentData = osrTestContext.getData().getEquipmentDataDataProvider()
                    .get(EquipmentDataCase.equipment_MatNr_40318601);

            cleanUp(); // Make sure no old test data is in the way
        }

        @BeforeMethod
        public void setup() {
            a4ResourceInventoryRobot.createNetworkElementGroup(neg);
            a4NetworkElements.forEach((k, networkElement) ->
                    a4ResourceInventoryRobot.createNetworkElement(networkElement, neg));
            a4ResourceInventoryRobot.createNetworkElementPort(nepA, a4NetworkElements.get(A4_NE_OPERATING_BOR_01));
            a4ResourceInventoryRobot.createNetworkElementPort(nepB, a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01));

            mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(),
                    wiremockScenarioName))
                    .addPslMock(equipmentData, a4NetworkElements.get(A4_NE_OPERATING_BOR_01))
                    .addRebellMock(uewegData, a4NetworkElements.get(A4_NE_OPERATING_BOR_01),
                            a4NetworkElements.get(A4_NE_RETIRING_PODSERVER_01))
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
        @Owner("Thea.John@telekom.de")
        @TmsLink("DIGIHUB-xxxxx")
        @Description("Test NEL Inbetriebnahme process")
        public void testNelInstallation() {
            // GIVEN
            a4MobileUiRobot.searchForNetworkElement(a4NetworkElements.get(A4_NE_OPERATING_BOR_01));

            // WHEN
            a4MobileUiRobot.startNelInstallation();
            a4NelInstallationUiRobot.doNelInstallation();

            // THEN
            sleepForSeconds(5); // Give logic some time to do requests to PSL, REBELL and A4 resource inventory
            // a4NemoUpdaterRobot.checkNetworkElementLinkPutRequestToNemoWiremock(nepB.getUuid());
            a4ResourceInventoryRobot.checkNetworkElementLinkInStateInstalling(nepB.getUuid());
        }

        @Test
        @Owner("Thea.John@telekom.de")
        @TmsLink("DIGIHUB-xxxxx")
        @Description("Test NEL Inbetriebnahme process")
        public void testNelInstallationNoNel() {
            // GIVEN
            a4MobileUiRobot.searchForNetworkElement(a4NetworkElements.get(A4_NE_B));

            // WHEN
            a4MobileUiRobot.startNelInstallation();
            sleepForSeconds(3);
            a4NelInstallationUiRobot.checkNotFound();

        }


    }

