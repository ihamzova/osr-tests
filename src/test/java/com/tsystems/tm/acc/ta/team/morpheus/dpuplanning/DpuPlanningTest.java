package com.tsystems.tm.acc.ta.team.morpheus.dpuplanning;

import com.tsystems.tm.acc.data.osr.models.DataBundle;
import com.tsystems.tm.acc.data.osr.models.dpudemand.DpuDemandCase;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDemand;
import com.tsystems.tm.acc.ta.robot.osr.DpuPlanningRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemandCreate;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.morpheus.CommonTestData.*;

@Epic("Morpheus")
@Feature("DPU Planning")
public class DpuPlanningTest extends GigabitTest {

    private DpuPlanningRobot dpuPlanningRobot = new DpuPlanningRobot();

    private static final String CREATE_DPU_DEMAND = "/team/morpheus/dpuPlanning/createDpuDemand.json";
    private static final String CREATE_DPU_DEMAND_KLS_ID_MISSING = "/team/morpheus/dpuPlanning/createDpuDemandKlsidMissing.json";
    private static final String CREATE_DPU_DEMAND_INVALID_FORMAT = "/team/morpheus/dpuPlanning/createDpuDemandInvalidFormat.json";

    private DpuDemand dpuDemandAllParametersExpectedData;
    private com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand createdDpuDemand;

    private DpuDemandCreate createDpuDemandAllParametersRequestData;
    private DpuDemandCreate createDpuDemandInvalidFormatRequestData;
    private DpuDemandCreate createDpuDemandKlsIdMissingRequestData;

    @BeforeTest
    public void init() {
        DataBundle dataBundle = new DataBundle();
        dpuDemandAllParametersExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.CreateDpuDemandAllParameters);
        createDpuDemandAllParametersRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND);
        createDpuDemandInvalidFormatRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_INVALID_FORMAT);
        createDpuDemandKlsIdMissingRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_KLS_ID_MISSING);
    }

    @Test(priority = 1)
    @TmsLink("DIGIHUB-117103")
    @Description("Create DPU Demand: happy case")
    public void createDpuDemand() {
        createdDpuDemand = dpuPlanningRobot.createDpuDemand(createDpuDemandAllParametersRequestData);
    }

    @Test(priority = 2)
    @TmsLink("DIGIHUB-117105")
    @Description("Create DPU Demand: Mandatory parameter is missing")
    public void createDpuDemandMandatoryParameterMissing() {
        dpuPlanningRobot.createDpuDemand400(createDpuDemandKlsIdMissingRequestData);
    }

    @Test(priority = 3)
    @TmsLink("DIGIHUB-117106")
    @Description("Create DPU Demand: Invalid format of request parameter")
    public void createDpuDemandInvalidFormat() {
        dpuPlanningRobot.createDpuDemand400(createDpuDemandInvalidFormatRequestData);
    }

    @Test(priority = 4, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-117828")
    @Description("Read DPU Demand by id")
    public void readDpuDemandById() {
        dpuPlanningRobot.readDpuDemandById(createdDpuDemand.getId());
    }

    @Test(priority = 5, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-117814")
    @Description("Read DPU Demand by fiberOnLocationId")
    public void readDpuDemandByFolId() {
        dpuPlanningRobot.readDpuDemandByFolId(createdDpuDemand.getFiberOnLocationId());
    }

    @Test(priority = 6, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-117830")
    @Description("Read DPU Demand by dpuAccessTechnology")
    public void readDpuDemandByDpuAccessTechnology() {
        dpuPlanningRobot.readDpuDemandByAccessTechnology(String.valueOf(createdDpuDemand.getDpuAccessTechnology()));
    }

    @Test(priority = 7, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-117831")
    @Description("Read DPU Demand by klsId")
    public void readDpuDemandByKlsId() {
        dpuPlanningRobot.readDpuDemandByKlsId(createdDpuDemand.getKlsId());
    }

    @Test(priority = 8, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-117832")
    @Description("Read DPU Demand by numberOfNeededDpuPorts")
    public void readDpuDemandByNumberOfNeededDpuPorts() {
        dpuPlanningRobot.readDpuDemandByNumberOfNeededDpuPorts(String.valueOf(createdDpuDemand.getNumberOfNeededDpuPorts()));
    }

    @Test(priority = 9, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-117833")
    @Description("Read DPU Demand by state")
    public void readDpuDemandByState() {
        dpuPlanningRobot.readDpuDemandByState(String.valueOf(createdDpuDemand.getState()));
    }

    @Test(priority = 10, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-118638")
    @Description("Fulfill DPU Demand")
    public void fulfillDpuDemand() {
        dpuPlanningRobot.fulfillDpuDemand(createdDpuDemand, DPU_ENDSZ_BNG);
    }

    @Test(priority = 11, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-118650")
    @Description("Add workorderId to DPU Demand")
    public void addWorkorderId() {
        dpuPlanningRobot.addWorkorderId(createdDpuDemand, dpuDemandAllParametersExpectedData.getWorkorderId());
    }

    @Test(priority = 12, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-121882")
    @Description("Modify DPU Demand: workorderId is not unique")
    public void modifyWoIdNotUnique() {
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand secondDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandAllParametersRequestData);
        dpuPlanningRobot.patchDpuDemand409(secondDpuDemandAfterProcess, DPU_WO_ID_PATH, dpuDemandAllParametersExpectedData.getWorkorderId());
        dpuPlanningRobot.deleteDpuDemand(secondDpuDemandAfterProcess);
    }

    @Test(priority = 13, dependsOnMethods = {"createDpuDemand"})
    @TmsLink("DIGIHUB-121883")
    @Description("Modify DPU Demand: dpuEndSz is not unique")
    public void modifyDpuEndSzIdNotUnique() {

        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand secondDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandAllParametersRequestData);
        dpuPlanningRobot.patchDpuDemand409(secondDpuDemandAfterProcess, DPU_ENDSZ_PATH, dpuDemandAllParametersExpectedData.getDpuEndSz());
        dpuPlanningRobot.deleteDpuDemand(secondDpuDemandAfterProcess);
    }

    @Test(priority = 14, dependsOnMethods = {"fulfillDpuDemand"})
    @TmsLink("DIGIHUB-117821")
    @Description("Read DPU Demand by dpuEndSz")
    public void readDpuDemandByEndsz() {
        dpuPlanningRobot.readDpuDemandByEndsz(dpuDemandAllParametersExpectedData.getDpuEndSz());
    }

    @Test(priority = 15, dependsOnMethods = {"addWorkorderId"})
    @TmsLink("DIGIHUB-117834")
    @Description("Read DPU Demand by workorderId")
    public void readDpuDemandByWorkorderId() {
        dpuPlanningRobot.readDpuDemandByWorkorderId(dpuDemandAllParametersExpectedData.getWorkorderId());
    }

    @Test(priority = 16, dependsOnMethods = {"createDpuDemand"}, alwaysRun = true)
    @TmsLink("DIGIHUB-119030")
    @Description("Delete DPU Demand")
    public void deleteDpuDemand() {
        dpuPlanningRobot.deleteDpuDemand(createdDpuDemand);
    }

    @Test(priority = 17, dependsOnMethods = {"deleteDpuDemand"})
    @TmsLink("DIGIHUB-119031")
    @Description("[Delete DPU Demand: demand not found")
    public void deleteDpuDemandNotFound() {
        dpuPlanningRobot.deleteDpuDemand404(createdDpuDemand);
    }

}
