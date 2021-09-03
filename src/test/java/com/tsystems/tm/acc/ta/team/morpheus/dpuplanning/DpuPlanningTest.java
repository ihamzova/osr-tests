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
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.morpheus.CommonTestData.*;

@Epic("Morpheus")
@Feature("DPU Planning")
public class DpuPlanningTest extends GigabitTest {
    private DpuPlanningRobot dpuPlanningRobot = new DpuPlanningRobot();

    private DpuDemand dpuDemandExpectedData;
    private static final String CREATE_DPU_DEMAND_ONLY_MANDATORY = "/team/morpheus/dpuPlanning/createDpuDemandOnlyMandatory.json";
    private static final String CREATE_DPU_DEMAND_ALL_PARAMS = "/team/morpheus/dpuPlanning/createDpuDemandMandatoryAndOptional.json";
    private static final String CREATE_DPU_DEMAND_KLS_ID_MISSING = "/team/morpheus/dpuPlanning/createDpuDemandKlsidMissing.json";
    private static final String CREATE_DPU_DEMAND_INVALID_FORMAT = "/team/morpheus/dpuPlanning/createDpuDemandInvalidFormat.json";

    DataBundle dataBundle = new DataBundle();
    private com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandAfterProcess;

    @BeforeGroups("dpu_demand_created")
    public void init() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        dpuDemandAfterProcess = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
    }

    @AfterGroups (value = "dpu_demand_deleted", enabled = false)
    public void cleanup() {
        dpuPlanningRobot.deleteDpuDemandSuccessResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 1, groups = "dpu_demand_deleted")
    @TmsLink("DIGIHUB-117098")
    @Description("[Positive] Create DPU Demand: only mandatory body parameters are filled")
    public void createDpuDemandOnlyMandatory() {
        dpuDemandExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.CreateDpuDemandOnlyMandatory);
        DpuDemandCreate dpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ONLY_MANDATORY);
        dpuPlanningRobot.createDpuDemandAndValidateSuccessResponse(dpuDemandRequestData, dpuDemandExpectedData);
    }

    @Test(priority = 2, groups = "dpu_demand_deleted")
    @TmsLink("DIGIHUB-117103")
    @Description("[Positive] Create DPU Demand: all body parameters are filled, mandatory and optional")
    public void createDpuDemandAllParameters() {
        dpuDemandExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.CreateDpuDemandAllParameters);
        DpuDemandCreate dpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        dpuPlanningRobot.createDpuDemandAndValidateSuccessResponse(dpuDemandRequestData, dpuDemandExpectedData);
    }

    @Test(priority = 3)
    @TmsLink("DIGIHUB-117105")
    @Description("[Negative] Create DPU Demand: Mandatory parameter is missing")
    public void createDpuDemandMandatoryParameterMissing() {
        DpuDemandCreate dpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_KLS_ID_MISSING);
        dpuPlanningRobot.createDpuDemandAndValidateErrorResponse(dpuDemandRequestData);
    }

    @Test(priority = 4)
    @TmsLink("DIGIHUB-117106")
    @Description("[Negative] Create DPU Demand: Invalid format of request parameter")
    public void createDpuDemandInvalidFormat() {
        DpuDemandCreate dpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_INVALID_FORMAT);
        dpuPlanningRobot.createDpuDemandAndValidateErrorResponse(dpuDemandRequestData);
    }

    @Test(priority = 5, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118638")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified separately by replace operation")
    public void modifyEndszAndStateSeparatelyByReplace() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, REPLACE_OPERATION);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_STATE_PATH, DPU_STATE_VALUE, REPLACE_OPERATION);
    }

    @Test(priority = 6, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-11864")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified separately by add operation")
    public void modifyEndszAndStateSeparatelyByAdd() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, ADD_OPERATION);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_STATE_PATH, DPU_STATE_VALUE, ADD_OPERATION);
    }

    @Test(priority = 7, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118643")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified together by replace operation")
    public void modifyEndszAndStateTogetherByReplace() {
        dpuPlanningRobot.patchDpuDemandModifyTwoParameters(dpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, DPU_STATE_PATH, DPU_STATE_VALUE, REPLACE_OPERATION);
    }

    @Test(priority = 8, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118642")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified together by add operation")
    public void modifyEndszAndStateTogetherByAdd() {
        dpuPlanningRobot.patchDpuDemandModifyTwoParameters(dpuDemandAfterProcess, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, DPU_STATE_PATH, DPU_STATE_VALUE, ADD_OPERATION);
    }

    @Test(priority = 9, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118650")
    @Description("[Positive] Modify DPU Demand: workorderId is modified by replace operation")
    public void modifyWoIdByReplace() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_WO_ID_PATH, DPU_WO_ID_VALUE, REPLACE_OPERATION);
    }

    @Test(priority = 10, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: dpuAccessTechnology")
    public void modifyDpuAccessTechnology() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_TECHNOLOGY_PATH, DPU_TECHNOLOGY_VALUE, ADD_OPERATION);
    }

    @Test(priority = 11, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: dpuInstallationInstruction")
    public void modifyDpuInstallationInstruction() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_INSTALLATION_INSTRUCTION_PATH, DPU_NSTALLATION_INSTRUCTION_VALUE, REPLACE_OPERATION);
    }

    @Test(priority = 12, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: dpuLocation")
    public void modifyDpuLocation() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_LOCATION_PATH, DPU_LOCATION_VALUE, ADD_OPERATION);
    }

    @Test(priority = 13, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: fiberOnLocationId")
    public void modifyFiberOnLocationId() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_FOL_ID_PATH, DPU_FOL_ID_VALUE, REPLACE_OPERATION);
    }

    @Test(priority = 14, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: klsId")
    public void modifyKlsId() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_KLS_ID_PATH, DPU_KLS_ID_VALUE, ADD_OPERATION);
    }

    @Test(priority = 15, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: numberOfNeededDpuPorts")
    public void modifyNumberOfNeededDpuPorts() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_PORT_NUMBER_PATH, DPU_PORT_NUMBER_VALUE, REPLACE_OPERATION);
    }

    @Test(priority = 16, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete id")
    public void modifyId() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_ID_PATH, null, REMOVE_OPERATION);
    }

    @Test(priority = 17, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete href")
    public void modifyHref() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_HREF_PATH, null, REMOVE_OPERATION);
    }

    @Test(priority = 18, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: creationDate")
    public void modifyCreationDate() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_CREATION_DATE_PATH, null, REMOVE_OPERATION);
    }

    @Test(priority = 19, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: modificationDate")
    public void modifyModificationDate() {
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandAfterProcess, DPU_MODIFICATION_DATE_PATH, null, REMOVE_OPERATION);
    }

    @Test(priority = 20, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete dpuAccessTechnology")
    public void deleteDpuAccessTechnology() {
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandAfterProcess, DPU_TECHNOLOGY_PATH, REMOVE_OPERATION);
    }

    @Test(priority = 21, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete fiberOnLocationId")
    public void deleteFiberOnLocationId() {
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandAfterProcess, DPU_FOL_ID_PATH, REMOVE_OPERATION);
    }

    @Test(priority = 22, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete klsId")
    public void deleteKlsId() {
       dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandAfterProcess, DPU_KLS_ID_PATH, REMOVE_OPERATION);
    }

    @Test(priority = 23, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete numberOfNeededDpuPorts")
    public void deleteNumberOfNeededDpuPorts() {
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandAfterProcess, DPU_PORT_NUMBER_PATH, REMOVE_OPERATION);
    }

    @Test(priority = 24, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete state")
    public void deleteState() {
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandAfterProcess, DPU_STATE_PATH, REMOVE_OPERATION);
    }

    @Test(priority = 25, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-117814")
    @Description("[Positive] Read DPU Demand by fiberOnLocationId")
    public void readDpuDemandByFolId() {
        dpuPlanningRobot.readDpuDemandByFolIdAndValidateResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 26, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-117821")
    @Description("[Positive] Read DPU Demand by dpuEndSz")
    public void readDpuDemandByEndsz() {
        dpuPlanningRobot.readDpuDemandByEndszAndValidateResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 27, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-117830")
    @Description("[Positive] Read DPU Demand by dpuAccessTechnology")
    public void readDpuDemandByDpuAccessTechnology() {
        dpuPlanningRobot.readDpuDemandByDpuAccessTechnologyAndValidateResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 28, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-117831")
    @Description("[Positive] Read DPU Demand by klsId")
    public void readDpuDemandByKlsId() {
        dpuPlanningRobot.readDpuDemandByKlsIdAndValidateResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 29, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-117832")
    @Description("[Positive] Read DPU Demand by numberOfNeededDpuPorts")
    public void readDpuDemandByNumberOfNeededDpuPorts() {
        dpuPlanningRobot.readDpuDemandByNumberOfNeededDpuPortsAndValidateResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 30, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-117833")
    @Description("[Positive] Read DPU Demand by state")
    public void readDpuDemandByState() {
        dpuPlanningRobot.readDpuDemandByStateAndValidateResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 31, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-117834")
    @Description("[Positive] Read DPU Demand by workorderId")
    public void readDpuDemandByWorkorderId() {
        dpuPlanningRobot.readDpuDemandByWorkorderIdAndValidateResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 32, groups = {"dpu_demand_created", "dpu_demand_deleted"})
    @TmsLink("DIGIHUB-117828")
    @Description("[Positive] Read DPU Demand by id")
    public void readDpuDemandById() {
       dpuPlanningRobot.readDpuDemandByIdAndValidateResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 33, groups = {"dpu_demand_created"}, enabled = false)
    @TmsLink("DIGIHUB-119030")
    @Description("[Positive] Delete DPU Demand")
    public void deleteDpuDemand() {
        dpuPlanningRobot.deleteDpuDemandSuccessResponse(dpuDemandAfterProcess);
        dpuPlanningRobot.readDpuDemandByIdErrorResponse(dpuDemandAfterProcess);
    }

    @Test(priority = 34, groups = {"dpu_demand_created"}, enabled = false)
    @TmsLink("DIGIHUB-119031")
    @Description("[Negative] Delete DPU Demand: demand not found")
    public void deleteDpuDemandNotFound() {
        dpuPlanningRobot.deleteDpuDemandSuccessResponse(dpuDemandAfterProcess);
        dpuPlanningRobot.deleteDpuDemandErrorResponse(dpuDemandAfterProcess);
    }
}
