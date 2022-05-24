package com.tsystems.tm.acc.ta.team.morpheus.mobiledpu;

import com.tsystems.tm.acc.ta.data.morpheus.wiremock.MorpeusWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.pages.osr.mobiledpu.MobileDpuPage;
import com.tsystems.tm.acc.ta.robot.osr.DpuPlanningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemandCreate;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.saveEventsToDefaultDir;


public class MobileDpuTest extends GigabitTest {
    private WireMockMappingsContext mappingsContext;
    private DpuPlanningRobot dpuPlanningRobot = new DpuPlanningRobot();
    private static final String CREATE_DPU_DEMAND = "/team/morpheus/dpuPlanning/createDpuDemandOnlyMandatory.json";
    private static final String ONKZ_BNG = "311";
    private static final String ONKZ_A4 = "411";
    MobileDpuPage mobileDpuPage;

    @BeforeMethod
    public void init() {
        OsrTestContext context = OsrTestContext.get();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase.RHSSOMobileDpu);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        mappingsContext.close();
        mappingsContext.eventsHook(saveEventsToDefaultDir()).eventsHook(attachEventsToAllureReport());
    }

    @Test(description = "DPU Commissioning V2 at BNG Platform: happy case")
    @TmsLink("DIGIHUB-125297")
    public void dpuCommissioningBngPlatform() throws IOException {
        String folId = new SimpleDateFormat("ddHHmm").format(new java.util.Date());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJsonForMobileDpu(CREATE_DPU_DEMAND, folId);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_BNG);

        mobileDpuPage = MobileDpuPage.openPage(folId);
        mobileDpuPage.selectDpuDemand();
        mobileDpuPage.goToNextPage();
        mobileDpuPage.inputSerialNumber();
        mobileDpuPage.goToNextPage();

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addMocksDpuCommissioningBngHappyCase");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksDpuCommissioningBngHappyCase()
                .build()
                .publish();

        mobileDpuPage.startCommissioning();
        mobileDpuPage.goToNextPage();
        mobileDpuPage.finishCommissioning();

        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_BNG + "/" + folId + "/71GA"));
    }

    @Test(description = "DPU Commissioning V2 at A4 Platform: happy case")
    @TmsLink("DIGIHUB-152228")
    public void dpuCommissioningA4Platform() throws IOException {
        String folId = new SimpleDateFormat("ddHHmm").format(new java.util.Date());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJsonForMobileDpu(CREATE_DPU_DEMAND, folId);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_A4);

        mobileDpuPage = MobileDpuPage.openPage(folId);
        mobileDpuPage.selectDpuDemand();
        mobileDpuPage.goToNextPage();
        mobileDpuPage.inputSerialNumber();
        mobileDpuPage.goToNextPage();

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addMocksDpuCommissioningA4HappyCase");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksDpuCommissioningA4HappyCase()
                .build()
                .publish();

        mobileDpuPage.startCommissioning();
        mobileDpuPage.goToNextPage();
        mobileDpuPage.finishCommissioning();

        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_A4 + "/" + folId + "/71GA"));
    }

    @Test(description = "DPU Demand Tab: multiple demands on folId")
    @TmsLink("DIGIHUB-125301")
    public void dpuDemandTabMultipleDemands() throws IOException {
        String folId = new SimpleDateFormat("ddHHmm").format(new java.util.Date());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJsonForMobileDpu(CREATE_DPU_DEMAND, folId);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_A4);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_BNG);

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addMocksDpuCommissioningA4HappyCase");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksDpuCommissioningA4HappyCase()
                .build()
                .publish();

        mobileDpuPage = MobileDpuPage.openPage(folId);
        mobileDpuPage.selectMultipleDpuDemands();

        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_A4 + "/" + folId + "/71GA"));
        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_BNG + "/" + folId + "/71GA"));
    }

    @Test(description = "DPU Demand Tab: demand with current workorderId")
    @TmsLink("DIGIHUB-125303")
    public void dpuDemandTabDemandWithCurrentWoId() throws IOException {
        String folId = new SimpleDateFormat("ddHHmm").format(new java.util.Date());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJsonForMobileDpu(CREATE_DPU_DEMAND, folId);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_A4);
        dpuPlanningRobot.modifyWorkorderId(dpuPlanningRobot.findDpuDemandByFolId(folId));
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_BNG);

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addMocksDpuCommissioningA4HappyCase");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksDpuCommissioningA4HappyCase()
                .build()
                .publish();

        mobileDpuPage = MobileDpuPage.openPage(folId);
        mobileDpuPage.selectMultipleDpuDemandsDisabled();

        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_A4 + "/" + folId + "/71GA"));
        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_BNG + "/" + folId + "/71GA"));
    }

    @Test(description = "DPU Demand Tab: demands are not found")
    @TmsLink("DIGIHUB-125308")
    public void dpuDemandTabDemandsNotFound() throws IOException {

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addMocksDpuCommissioningA4HappyCase");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksDpuCommissioningA4HappyCase()
                .build()
                .publish();

        mobileDpuPage = MobileDpuPage.openPage("9999");
        mobileDpuPage.errorNotificationDisplayed();

    }

    @Test(description = "DPU Tab: Serial number is not unique")
    @TmsLink("DIGIHUB-139096")
    public void dpuTabSerialNumberNotUnique() throws IOException {
        String folId = new SimpleDateFormat("ddHHmm").format(new java.util.Date());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJsonForMobileDpu(CREATE_DPU_DEMAND, folId);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_A4);

        mobileDpuPage = MobileDpuPage.openPage(folId);
        mobileDpuPage.selectDpuDemand();
        mobileDpuPage.goToNextPage();

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addMocksDpuCommissioningSerialNumberNotUnique");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksDpuCommissioningSerialNumberNotUnique()
                .build()
                .publish();

        mobileDpuPage.inputSerialNumber();
        mobileDpuPage.errorNotificationDisplayed();

        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_A4 + "/" + folId + "/71GA"));
    }

    @Test(description = "DPU Tab: Get DPU, technical error from RAL")
    @TmsLink("DIGIHUB-136898")
    public void dpuTabGetDeviceTechnicalError() throws IOException {
        String folId = new SimpleDateFormat("ddHHmm").format(new java.util.Date());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJsonForMobileDpu(CREATE_DPU_DEMAND, folId);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_A4);

        mobileDpuPage = MobileDpuPage.openPage(folId);
        mobileDpuPage.selectDpuDemand();

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addMocksDpuCommissioningGetDevice500");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksDpuCommissioningGetDevice500()
                .build()
                .publish();

        mobileDpuPage.goToNextPage();
        mobileDpuPage.errorNotificationDisplayed();

        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_A4 + "/" + folId + "/71GA"));
    }

    @Test(description = "DPU Tab: Set Serial number, technical error from RAL")
    @TmsLink("DIGIHUB-139097")
    public void dpuTabUpdateSerialNumberTechnicalError() throws IOException {
        String folId = new SimpleDateFormat("ddHHmm").format(new java.util.Date());
        DpuDemandCreate createDpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJsonForMobileDpu(CREATE_DPU_DEMAND, folId);
        dpuPlanningRobot.createDpuDemand(createDpuDemandRequestData);
        dpuPlanningRobot.fulfillDpuDemand(dpuPlanningRobot.findDpuDemandByFolIdAndState(folId), ONKZ_A4);

        mobileDpuPage = MobileDpuPage.openPage(folId);
        mobileDpuPage.selectDpuDemand();
        mobileDpuPage.goToNextPage();

        mappingsContext = new WireMockMappingsContext(WireMockFactory.get(), "addMocksDpuCommissioningUpdateSerialNumber500");
        new MorpeusWireMockMappingsContextBuilder(mappingsContext)
                .addMocksDpuCommissioningUpdateSerialNumber500()
                .build()
                .publish();

        mobileDpuPage.inputSerialNumber();
        mobileDpuPage.errorNotificationDisplayed();

        dpuPlanningRobot.deleteDpuDemand(dpuPlanningRobot.findDpuDemandByEndSz("49/" + ONKZ_A4 + "/" + folId + "/71GA"));
    }

}
