package com.tsystems.tm.acc.ta.team.mercury.commissioning.auto;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.MercuryWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_DISCOVERY_MS, OLT_RESOURCE_INVENTORY_MS, OLT_UPLINK_MANAGEMENT_MS, OLT_UPLINK_MANAGEMENT_MS, PSL_ADAPTER_MS, PSL_TRANSFORMER_MS})
@Epic("OS&R")
@Feature("Description olt auto-commissioning incl. LC-Commissioning Testcase on Mercury Team-environment")
@TmsLink("DIGIHUB-52132") // This is the Jira id of TestSet
public class OltAutoCommissioning extends GigabitTest {

    final String STUB_GROUP_ID = "OltAutoCommissioning";
    private static final Integer TIMEOUT_FOR_OLT_COMMISSIONING = 2 * 60_000;

    OsrTestContext context = OsrTestContext.get();
    private final OltDevice oltDeviceDTAG = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76HG_SDX_6320_16);
    private final OltDevice oltDeviceGFNW = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_8571_0_76Z9_SDX_6320);
    private final OltDevice oltDeviceGFMM_MA5600 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_1100_76G1_MA5600);
    private final OltDevice oltDeviceGFMM_SDX_6320 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_1100_76G3_SDX_6320_16);

    private final OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private final WireMockMappingsContext mappingsContextOsr = new WireMockMappingsContext(WireMockFactory.get(), STUB_GROUP_ID);
    private final WireMockMappingsContext mappingsContextTeam = new WireMockMappingsContext(WireMockFactory.get(), STUB_GROUP_ID);

    @BeforeClass
    public void init() {

        List<OltDevice> olts = Arrays.asList(oltDeviceDTAG, oltDeviceGFNW, oltDeviceGFMM_MA5600, oltDeviceGFMM_SDX_6320);

        olts.forEach(oltCommissioningRobot::clearResourceInventoryDataBase);

        olts.forEach(olt ->
                new OsrWireMockMappingsContextBuilder(mappingsContextOsr)
                        .addSealMock(olt)
                        //.addPslMock(olt)
                        .addPslMockXML(olt)
                        .build()
                        .publish()
                        .publishedHook(savePublishedToDefaultDir())
                        .publishedHook(attachStubsToAllureReport())
        );
        olts.forEach(olt ->
                new MercuryWireMockMappingsContextBuilder(mappingsContextTeam)
                        .addPonInventoryMock(olt)
                        .addRebellUewegeMock(olt)
                        .build()
                        .publish()
                        .publishedHook(savePublishedToDefaultDir())
                        .publishedHook(attachStubsToAllureReport())
        );
    }

    @AfterClass
    public void cleanUp() {
        mappingsContextOsr.close();
        mappingsContextOsr
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());

        mappingsContextTeam.close();
        mappingsContextTeam
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
    }

    @Test(description = "DIGIHUB-52130 OLT RI UI. Auto Commissioning MA5600 for DTAG user.")
    public void OltAutoCommissioningDTAGTest() {

        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        // fix wiremock stub
        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_30_2000_76H1_MA5600);
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);
        oltCommissioningRobot.startAutomaticOltCommissioning(oltDevice, TIMEOUT_FOR_OLT_COMMISSIONING);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDevice, COMPOSITE_PARTY_ID_DTAG);
        oltCommissioningRobot.checkUplink(oltDevice);
    }

    @Test(description = "DIGIHUB-52133 OLT RI UI. Auto Commissioning MA5800 for GFNW user.")
    public void OltAutoCommissioningGFNWTest() {

        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        // fix wiremock stub
        OltDevice oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.EndSz_49_911_1100_76ZB_MA5800);
        oltCommissioningRobot.clearResourceInventoryDataBase(oltDevice);
        oltCommissioningRobot.startAutomaticOltCommissioning(oltDevice, TIMEOUT_FOR_OLT_COMMISSIONING);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDevice, COMPOSITE_PARTY_ID_GFNW);
        oltCommissioningRobot.checkUplink(oltDevice);
    }

    @Test(description = "DIGIHUB-104212 OLT RI UI. Auto Commissioning SDX 6320 16 for DTAG user.")
    public void OltAdtranAutoCommissioningDTAGTest() {

        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiDTAG);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        oltCommissioningRobot.startAutomaticOltCommissioning(oltDeviceDTAG, TIMEOUT_FOR_OLT_COMMISSIONING);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDeviceDTAG, COMPOSITE_PARTY_ID_DTAG);
        oltCommissioningRobot.checkUplink(oltDeviceDTAG);
    }

    @Test(description = "DIGIHUB-104213 OLT RI UI. Auto Commissioning SDX 6320 for GFNW user.")
    public void OltAdtranAutoCommissioningGFNWTest() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFNW);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        oltCommissioningRobot.startAutomaticOltCommissioning(oltDeviceGFNW, TIMEOUT_FOR_OLT_COMMISSIONING);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDeviceGFNW, COMPOSITE_PARTY_ID_GFNW);
        oltCommissioningRobot.checkUplink(oltDeviceGFNW);
    }

    @Test(description = "DIGIHUB-159699 OS&R UI. Auto Commissioning MA5600 for GFMM user.")
    public void OltAutoCommissioningGFMM_MA5600Test() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFMM);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        oltCommissioningRobot.startAutomaticOltCommissioning(oltDeviceGFMM_MA5600, TIMEOUT_FOR_OLT_COMMISSIONING);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDeviceGFMM_MA5600, COMPOSITE_PARTY_ID_GFMM);
        oltCommissioningRobot.checkUplink(oltDeviceGFMM_MA5600);
    }

    @Test(description = "DIGIHUB-159663 OS&R UI. Auto Commissioning SDX_6320 for GFMM user.")
    public void OltAutoCommissioningGFMM_SDX_6320Test() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUiGFMM);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        oltCommissioningRobot.startAutomaticOltCommissioning(oltDeviceGFMM_SDX_6320, TIMEOUT_FOR_OLT_COMMISSIONING);

        oltCommissioningRobot.checkOltCommissioningResultWithoutAccessLines(oltDeviceGFMM_SDX_6320, COMPOSITE_PARTY_ID_GFMM);
        oltCommissioningRobot.checkUplink(oltDeviceGFMM_SDX_6320);
    }

}


