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

    private static final String CREATE_DPU_DEMAND_ONLY_MANDATORY = "/team/morpheus/dpuPlanning/createDpuDemandOnlyMandatory.json";
    private static final String CREATE_DPU_DEMAND_ALL_PARAMS = "/team/morpheus/dpuPlanning/createDpuDemandAllparameters.json";
    private static final String CREATE_DPU_DEMAND_KLS_ID_MISSING = "/team/morpheus/dpuPlanning/createDpuDemandKlsidMissing.json";
    private static final String CREATE_DPU_DEMAND_INVALID_FORMAT = "/team/morpheus/dpuPlanning/createDpuDemandInvalidFormat.json";
    private static final String CREATE_DPU_DEMAND_WO_NOT_UNIQUE = "/team/morpheus/dpuPlanning/createDpuDemandMandatoryAndWorkorderId.json";
    private static final String CREATE_DPU_DEMAND_ENDSZ_NOT_UNIQUE = "/team/morpheus/dpuPlanning/createDpuDemandMandatoryAndEndSz.json";

    private com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandAllParametersAfterProcess;
    private com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandResponseToValidate;

    private DpuDemand dpuDemandAllParametersExpectedData;
    private DpuDemand dpuDemandOnlyMandatoryParametersExpectedData;
    private DpuDemand dpuDemandStateAndEndSzExpectedData;
    private DpuDemand dpuDemandWoExpectedData;

    private DpuDemandCreate createDpuDemandAllParametersRequestData;
    private DpuDemandCreate createDpuDemandOnlyMandatoryParametersRequestData;
    private DpuDemandCreate createDpuDemandInvalidFormatRequestData;
    private DpuDemandCreate createDpuDemandKlsIdMissingRequestData;
    private DpuDemandCreate createDpuDemandMandatoryAndEndSzRequestData;
    private DpuDemandCreate createDpuDemandMandatoryAndWoIdRequestData;

    @BeforeTest
    public void init() {
        //Expected Data
        DataBundle dataBundle = new DataBundle();
        dpuDemandAllParametersExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.CreateDpuDemandAllParameters);
        dpuDemandOnlyMandatoryParametersExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.CreateDpuDemandOnlyMandatory);
        dpuDemandStateAndEndSzExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.ModifyDpuDemandStateAndEndSz);
        dpuDemandWoExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.ModifyDpuDemandWo);

        //Request Data
        createDpuDemandAllParametersRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        createDpuDemandOnlyMandatoryParametersRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ONLY_MANDATORY);
        createDpuDemandInvalidFormatRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_INVALID_FORMAT);
        createDpuDemandKlsIdMissingRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_KLS_ID_MISSING);
        createDpuDemandMandatoryAndEndSzRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ENDSZ_NOT_UNIQUE);
        createDpuDemandMandatoryAndWoIdRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_WO_NOT_UNIQUE);
    }

    @Test(priority = 1)
    @TmsLink("DIGIHUB-117098")
    @Description("[Positive] Create DPU Demand: only mandatory body parameters are filled")
    public void createDpuDemandOnlyMandatory() {
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandOnlyMandatoryAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);
        dpuPlanningRobot.validateDpuDemand(dpuDemandOnlyMandatoryAfterProcess, dpuDemandOnlyMandatoryParametersExpectedData);
        dpuPlanningRobot.deleteDpuDemand(dpuDemandOnlyMandatoryAfterProcess);
    }

    @Test(priority = 2)
    @TmsLink("DIGIHUB-117103")
    @Description("[Positive] Create DPU Demand: all body parameters are filled, mandatory and optional")
    public void createDpuDemandAllParameters() {
        dpuDemandAllParametersAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandAllParametersRequestData);
        dpuPlanningRobot.validateDpuDemand(dpuDemandAllParametersAfterProcess, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 3)
    @TmsLink("DIGIHUB-117105")
    @Description("[Negative] Create DPU Demand: Mandatory parameter is missing")
    public void createDpuDemandMandatoryParameterMissing() {
        dpuPlanningRobot.createDpuDemand400(createDpuDemandKlsIdMissingRequestData);
    }

    @Test(priority = 4)
    @TmsLink("DIGIHUB-117106")
    @Description("[Negative] Create DPU Demand: Invalid format of request parameter")
    public void createDpuDemandInvalidFormat() {
        dpuPlanningRobot.createDpuDemand400(createDpuDemandInvalidFormatRequestData);
    }

    @Test(priority = 5, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-121879")
    @Description("[Negative] Create DPU Demand: workorderId is not unique")
    public void createDpuDemandWoNotUnique() {
        dpuPlanningRobot.createDpuDemand409(createDpuDemandMandatoryAndWoIdRequestData);
    }

    @Test(priority = 6, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-121881")
    @Description("[Negative] Create DPU Demand: dpuEndSz is not unique")
    public void createDpuDemandDpuEndSzNotUnique() {
        dpuPlanningRobot.createDpuDemand409(createDpuDemandMandatoryAndEndSzRequestData);
    }

    @Test(priority = 7, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-117814")
    @Description("[Positive] Read DPU Demand by fiberOnLocationId")
    public void readDpuDemandByFolId() {
        dpuDemandResponseToValidate = dpuPlanningRobot.readDpuDemandByFolId(dpuDemandAllParametersAfterProcess);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 8, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-117821")
    @Description("[Positive] Read DPU Demand by dpuEndSz")
    public void readDpuDemandByEndsz() {
        dpuDemandResponseToValidate = dpuPlanningRobot.readDpuDemandByEndsz(dpuDemandAllParametersAfterProcess);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 9, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-117830")
    @Description("[Positive] Read DPU Demand by dpuAccessTechnology")
    public void readDpuDemandByDpuAccessTechnology() {
        dpuDemandResponseToValidate = dpuPlanningRobot.readDpuDemandByDpuAccessTechnology(dpuDemandAllParametersAfterProcess);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 10, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-117831")
    @Description("[Positive] Read DPU Demand by klsId")
    public void readDpuDemandByKlsId() {
        dpuDemandResponseToValidate = dpuPlanningRobot.readDpuDemandByKlsId(dpuDemandAllParametersAfterProcess);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 11, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-117832")
    @Description("[Positive] Read DPU Demand by numberOfNeededDpuPorts")
    public void readDpuDemandByNumberOfNeededDpuPorts() {
        dpuDemandResponseToValidate = dpuPlanningRobot.readDpuDemandByNumberOfNeededDpuPorts(dpuDemandAllParametersAfterProcess);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 12, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-117833")
    @Description("[Positive] Read DPU Demand by state")
    public void readDpuDemandByState() {
        dpuDemandResponseToValidate = dpuPlanningRobot.readDpuDemandByState(dpuDemandAllParametersAfterProcess);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 13, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-117834")
    @Description("[Positive] Read DPU Demand by workorderId")
    public void readDpuDemandByWorkorderId() {
        dpuDemandResponseToValidate = dpuPlanningRobot.readDpuDemandByWorkorderId(dpuDemandAllParametersAfterProcess);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 14, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-117828")
    @Description("[Positive] Read DPU Demand by id")
    public void readDpuDemandById() {
        dpuDemandResponseToValidate = dpuPlanningRobot.readDpuDemandById(dpuDemandAllParametersAfterProcess);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandAllParametersExpectedData);
    }

    @Test(priority = 15, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-119030")
    @Description("[Positive] Delete DPU Demand")
    public void deleteDpuDemand() {
        dpuPlanningRobot.deleteDpuDemand(dpuDemandAllParametersAfterProcess);
    }

    @Test(priority = 16, dependsOnMethods = {"createDpuDemandAllParameters"})
    @TmsLink("DIGIHUB-119031")
    @Description("[Negative] Delete DPU Demand: demand not found")
    public void deleteDpuDemandNotFound() {
        dpuPlanningRobot.deleteDpuDemand404(dpuDemandAllParametersAfterProcess);
    }

    @Test(priority = 17, dependsOnMethods = {"deleteDpuDemand"}, enabled = false)
    @TmsLink("DIGIHUB-117822")
    @Description("[Negative] Read DPU Demand by filter criterium: demand not found")
    public void readDpuDemandByWorkorderIdDemandNotFound() {
        dpuPlanningRobot.readDpuDemandByWorkorderId404(dpuDemandAllParametersAfterProcess.getWorkorderId());
    }

    @Test(priority = 18, dependsOnMethods = {"deleteDpuDemand"}, enabled = false)
    @TmsLink("DIGIHUB-117829")
    @Description("[Negative] Read DPU Demand by id: demand not found")
    public void readDpuDemandByIdDemandNotFound() {
        dpuPlanningRobot.readDpuDemandById404(dpuDemandAllParametersAfterProcess);
    }

    @Test(priority = 19)
    @TmsLink("DIGIHUB-118638")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified separately by replace operation, [Positive] DpuPlanningCompletedEvent is published if FOL has other dpu-demands and all of them are completed")
    public void modifyEndszAndStateSeparatelyByReplace() {
        //Precondition: Register for Notification and create two demands, first completed, second open
        dpuPlanningRobot.registerForNotifications();
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand firstDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandAllParametersRequestData);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand secondDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);

        //Modify state and dpuEndsz of second demand
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(secondDpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, REPLACE_OPERATION);
        dpuDemandResponseToValidate = dpuPlanningRobot.patchDpuDemandModifyOneParameter(secondDpuDemandAfterProcess, DPU_STATE_PATH, DPU_STATE_VALUE, REPLACE_OPERATION);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandStateAndEndSzExpectedData);

        //Check published notifications after second demand is completed
        dpuPlanningRobot.validateDpuPlanningCompletedEvent(secondDpuDemandAfterProcess, 1);

        dpuPlanningRobot.deleteDpuDemand(firstDpuDemandAfterProcess);
        dpuPlanningRobot.deleteDpuDemand(secondDpuDemandAfterProcess);
    }

    @Test(priority = 20)
    @TmsLink("DIGIHUB-118641")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified separately by add operation, [Negative] DpuPlanningCompletedEvent is not published if FOL has other dpu-demands but not all of them are completed")
    public void modifyEndszAndStateSeparatelyByAdd() {
        //Precondition: Register for Notifications and create two demands, both not completed
        dpuPlanningRobot.registerForNotifications();
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand firstDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand secondDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);

        //Modify state and dpuEndsz of second demand
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(secondDpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, ADD_OPERATION);
        dpuDemandResponseToValidate = dpuPlanningRobot.patchDpuDemandModifyOneParameter(secondDpuDemandAfterProcess, DPU_STATE_PATH, DPU_STATE_VALUE, ADD_OPERATION);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandStateAndEndSzExpectedData);

        //Check published notifications after second demand is completed
        dpuPlanningRobot.validateDpuPlanningCompletedEvent(secondDpuDemandAfterProcess, 0);

        dpuPlanningRobot.deleteDpuDemand(firstDpuDemandAfterProcess);
        dpuPlanningRobot.deleteDpuDemand(secondDpuDemandAfterProcess);
    }

    @Test(priority = 21)
    @TmsLink("DIGIHUB-118643")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified together by replace operation, [Positive] DpuPlanningCompletedEvent is published if new DPU Demand is created after all other are completed")
    public void modifyEndszAndStateTogetherByReplace() {
        //Precondition: Register for Notifications and create one open demand
        dpuPlanningRobot.registerForNotifications();
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand firstDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);

        //Modify state and dpuEndsz of created demand
        dpuDemandResponseToValidate = dpuPlanningRobot.patchDpuDemandModifyTwoParameters(firstDpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, DPU_STATE_PATH, DPU_STATE_VALUE, ADD_OPERATION);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandStateAndEndSzExpectedData);

        //Create second open demand and modify it
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand secondDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);
        dpuPlanningRobot.patchDpuDemandModifyTwoParameters(secondDpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_NOT_UNIQUE_VALUE, DPU_STATE_PATH, DPU_STATE_VALUE, ADD_OPERATION);

        //Check published notifications after second demand is completed
        dpuPlanningRobot.validateDpuPlanningCompletedEvent(secondDpuDemandAfterProcess, 2);

        dpuPlanningRobot.deleteDpuDemand(firstDpuDemandAfterProcess);
        dpuPlanningRobot.deleteDpuDemand(secondDpuDemandAfterProcess);
    }

    @Test(priority = 22)
    @TmsLink("DIGIHUB-118642")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified together by add operation, [Positive] DpuPlanningCompletedEvent is published if FOL has one dpu-demand and it is completed")
    public void modifyEndszAndStateTogetherByAdd() {
        //Precondition: Register for Notifications and create one open demand
        dpuPlanningRobot.registerForNotifications();
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand firstDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);

        //Modify state and dpuEndsz of created demand
        dpuDemandResponseToValidate = dpuPlanningRobot.patchDpuDemandModifyTwoParameters(firstDpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, DPU_STATE_PATH, DPU_STATE_VALUE, ADD_OPERATION);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandStateAndEndSzExpectedData);

        //Check published notifications after second demand is completed
        dpuPlanningRobot.validateDpuPlanningCompletedEvent(firstDpuDemandAfterProcess, 1);

        dpuPlanningRobot.deleteDpuDemand(firstDpuDemandAfterProcess);
    }

    @Test(priority = 23)
    @TmsLink("DIGIHUB-118650")
    @Description("[Positive] Modify DPU Demand: workorderId is modified by replace operation, [Negative] DpuPlanningCompletedEvent isn't published if other parameters are modified")
    public void modifyWoIdByReplace() {
        //Precondition: Register for Notifications and create one completed demand
        dpuPlanningRobot.registerForNotifications();
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand firstDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);

        //Modify workorderId
        dpuDemandResponseToValidate = dpuPlanningRobot.patchDpuDemandModifyOneParameter(firstDpuDemandAfterProcess, DPU_WO_ID_PATH, DPU_WO_ID_VALUE, REPLACE_OPERATION);
        dpuPlanningRobot.validateDpuDemand(dpuDemandResponseToValidate, dpuDemandWoExpectedData);

        //Check published notifications after second demand is completed
        dpuPlanningRobot.validateDpuPlanningCompletedEvent(firstDpuDemandAfterProcess, 0);

        dpuPlanningRobot.deleteDpuDemand(firstDpuDemandAfterProcess);
    }

    @Test(priority = 24)
    @TmsLink("DIGIHUB-121882")
    @Description("[Negative] Modify DPU Demand: workorderId is not unique")
    public void modifyWoIdNotUnique() {
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand firstDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandAllParametersRequestData);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand secondDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);
        dpuPlanningRobot.patchDpuDemand409(secondDpuDemandAfterProcess, DPU_WO_ID_PATH, DPU_WO_ID_NOT_UNIQUE_VALUE, REPLACE_OPERATION);
        dpuPlanningRobot.deleteDpuDemand(firstDpuDemandAfterProcess);
        dpuPlanningRobot.deleteDpuDemand(secondDpuDemandAfterProcess);
    }

    @Test(priority = 25)
    @TmsLink("DIGIHUB-121883")
    @Description("[Negative] Modify DPU Demand: dpuEndSz is not unique")
    public void modifyDpuEndSzIdNotUnique() {
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand firstDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandAllParametersRequestData);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand secondDpuDemandAfterProcess = dpuPlanningRobot.createDpuDemand(createDpuDemandOnlyMandatoryParametersRequestData);
        dpuPlanningRobot.patchDpuDemand409(secondDpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_NOT_UNIQUE_VALUE, REPLACE_OPERATION);
        dpuPlanningRobot.deleteDpuDemand(firstDpuDemandAfterProcess);
        dpuPlanningRobot.deleteDpuDemand(secondDpuDemandAfterProcess);
    }

}
