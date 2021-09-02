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

    @Test (priority = 1)
    @TmsLink("DIGIHUB-117098")
    @Description("[Positive] Create DPU Demand: only mandatory body parameters are filled")
    public void createDpuDemandOnlyMandatory() {
        dpuDemandExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.CreateDpuDemandOnlyMandatory);
        DpuDemandCreate dpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ONLY_MANDATORY);
        dpuPlanningRobot.createDpuDemandAndValidateSuccessResponse(dpuDemandRequestData, dpuDemandExpectedData);
    }

    @Test (priority = 2)
    @TmsLink("DIGIHUB-117103")
    @Description("[Positive] Create DPU Demand: all body parameters are filled, mandatory and optional")
    public void createDpuDemandAllParameters() {
        dpuDemandExpectedData = dataBundle.getDpuDemandDataProvider().get(DpuDemandCase.CreateDpuDemandAllParameters);
        DpuDemandCreate dpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        dpuPlanningRobot.createDpuDemandAndValidateSuccessResponse(dpuDemandRequestData, dpuDemandExpectedData);
    }

    @Test (priority = 3)
    @TmsLink("DIGIHUB-117105")
    @Description("[Negative] Create DPU Demand: Mandatory parameter is missing")
    public void createDpuDemandMandatoryParameterMissing() {
        DpuDemandCreate dpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_KLS_ID_MISSING);
        dpuPlanningRobot.createDpuDemandAndValidateErrorResponse(dpuDemandRequestData);
    }

    @Test (priority = 4)
    @TmsLink("DIGIHUB-117106")
    @Description("[Negative] Create DPU Demand: Invalid format of request parameter")
    public void createDpuDemandInvalidFormat() {
        DpuDemandCreate dpuDemandRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_INVALID_FORMAT);
        dpuPlanningRobot.createDpuDemandAndValidateErrorResponse(dpuDemandRequestData);
    }

    @Test (priority = 5)
    @TmsLink("DIGIHUB-118638")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified separately by replace operation")
    public void modifyEndszAndStateSeparatelyByReplace() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, REPLACE_OPERATION);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_STATE_PATH, DPU_STATE_VALUE, REPLACE_OPERATION);
    }

    @Test (priority = 6)
    @TmsLink("DIGIHUB-11864")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified separately by add operation")
    public void modifyEndszAndStateSeparatelyByAdd() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, ADD_OPERATION);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_STATE_PATH, DPU_STATE_VALUE, ADD_OPERATION);
    }

    @Test (priority = 7)
    @TmsLink("DIGIHUB-118643")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified together by replace operation")
    public void modifyEndszAndStateTogetherByReplace() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyTwoParameters(dpuDemandToModify, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, DPU_STATE_PATH, DPU_STATE_VALUE, REPLACE_OPERATION);
    }

    @Test (priority = 8)
    @TmsLink("DIGIHUB-118642")
    @Description("[Positive] Modify DPU Demand: dpuEndSz and state are modified together by add operation")
    public void modifyEndszAndStateTogetherByAdd() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyTwoParameters(dpuDemandToModify, DPU_ENDSZ_PATH, DPU_ENDSZ_VALUE, DPU_STATE_PATH, DPU_STATE_VALUE, ADD_OPERATION);
    }

    @Test (priority = 9)
    @TmsLink("DIGIHUB-118650")
    @Description("[Positive] Modify DPU Demand: workorderId is modified by replace operation")
    public void modifyWoIdByReplace() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_WO_ID_PATH, DPU_WO_ID_VALUE, REPLACE_OPERATION);
    }

    @Test (priority = 10)
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: dpuAccessTechnology")
    public void modifyDpuAccessTechnology() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_TECHNOLOGY_PATH, DPU_TECHNOLOGY_VALUE, ADD_OPERATION);
    }

    @Test (priority = 11)
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: dpuInstallationInstruction")
    public void modifyDpuInstallationInstruction() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_INSTALLATION_INSTRUCTION_PATH, DPU_NSTALLATION_INSTRUCTION_VALUE, REPLACE_OPERATION);
    }

    @Test (priority = 12)
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: dpuLocation")
    public void modifyDpuLocation() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_LOCATION_PATH, DPU_LOCATION_VALUE, ADD_OPERATION);
    }

    @Test (priority = 13)
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: fiberOnLocationId")
    public void modifyFiberOnLocationId() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_FOL_ID_PATH, DPU_FOL_ID_VALUE, REPLACE_OPERATION);
    }

    @Test (priority = 14)
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: klsId")
    public void modifyKlsId() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_KLS_ID_PATH, DPU_KLS_ID_VALUE, ADD_OPERATION);
    }

    @Test (priority = 15)
    @TmsLink("DIGIHUB-118645")
    @Description("[Positive] Modify DPU Demand: numberOfNeededDpuPorts")
    public void modifyNumberOfNeededDpuPorts() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_PORT_NUMBER_PATH, DPU_PORT_NUMBER_VALUE, REPLACE_OPERATION);
    }

    @Test (priority = 16)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete id")
    public void modifyId() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_ID_PATH, null, REMOVE_OPERATION);
    }

    @Test (priority = 17)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete href")
    public void modifyHref() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandModifyOneParameter(dpuDemandToModify, DPU_HREF_PATH, null, REMOVE_OPERATION);
    }

    @Test (priority = 18)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: creationDate")
    public void modifyCreationDate() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandToModify, DPU_CREATION_DATE_PATH, REMOVE_OPERATION);
    }

    @Test (priority = 19)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: modificationDate")
    public void modifyModificationDate() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandToModify, DPU_MODIFICATION_DATE_PATH, REMOVE_OPERATION);
    }

    @Test (priority = 20)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete dpuAccessTechnology")
    public void deleteDpuAccessTechnology() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandToModify, DPU_TECHNOLOGY_PATH, REMOVE_OPERATION);
    }

    @Test (priority = 21)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete fiberOnLocationId")
    public void deleteFiberOnLocationId() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandToModify, DPU_FOL_ID_PATH, REMOVE_OPERATION);
    }

    @Test (priority = 22)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete klsId")
    public void deleteKlsId() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandToModify, DPU_KLS_ID_PATH, REMOVE_OPERATION);
    }

    @Test (priority = 23)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete numberOfNeededDpuPorts")
    public void deleteNumberOfNeededDpuPorts() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandToModify, DPU_PORT_NUMBER_PATH, REMOVE_OPERATION);
    }

    @Test (priority = 24)
    @TmsLink("DIGIHUB-118645")
    @Description("[Negative] Modify DPU Demand: delete state")
    public void deleteState() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToModify = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.patchDpuDemandBadRequest(dpuDemandToModify, DPU_STATE_PATH, REMOVE_OPERATION);
    }

    @Test (priority = 25)
    @TmsLink("DIGIHUB-117814")
    @Description("[Positive] Read DPU Demand by fiberOnLocationId")
    public void readDpuDemandByFolId() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToRead = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.readDpuDemandByFolIdAndValidateResponse(dpuDemandToRead);
    }

    @Test (priority = 26)
    @TmsLink("DIGIHUB-117821")
    @Description("[Positive] Read DPU Demand by dpuEndSz")
    public void readDpuDemandByEndsz() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToRead = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.readDpuDemandByEndszAndValidateResponse(dpuDemandToRead);
    }

    @Test (priority = 27)
    @TmsLink("DIGIHUB-117830")
    @Description("[Positive] Read DPU Demand by dpuAccessTechnology")
    public void readDpuDemandByDpuAccessTechnology() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToRead = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.readDpuDemandByDpuAccessTechnologyAndValidateResponse(dpuDemandToRead);
    }

    @Test (priority = 28)
    @TmsLink("DIGIHUB-117831")
    @Description("[Positive] Read DPU Demand by klsId")
    public void readDpuDemandByKlsId() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToRead = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.readDpuDemandByKlsIdAndValidateResponse(dpuDemandToRead);
    }

    @Test (priority = 29)
    @TmsLink("DIGIHUB-117832")
    @Description("[Positive] Read DPU Demand by numberOfNeededDpuPorts")
    public void readDpuDemandByNumberOfNeededDpuPorts() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToRead = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.readDpuDemandByNumberOfNeededDpuPortsAndValidateResponse(dpuDemandToRead);
    }

    @Test (priority = 30)
    @TmsLink("DIGIHUB-117833")
    @Description("[Positive] Read DPU Demand by state")
    public void readDpuDemandByState() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToRead = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.readDpuDemandByStateAndValidateResponse(dpuDemandToRead);
    }

    @Test (priority = 31)
    @TmsLink("DIGIHUB-117834")
    @Description("[Positive] Read DPU Demand by workorderId")
    public void readDpuDemandByWorkorderId() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToRead = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.readDpuDemandByWorkorderIdAndValidateResponse(dpuDemandToRead);
    }

    @Test (priority = 32)
    @TmsLink("DIGIHUB-117828")
    @Description("[Positive] Read DPU Demand by id")
    public void readDpuDemandById() {
        DpuDemandCreate dpuDemandCreateRequestData = dpuPlanningRobot.getDpuDemandCreateFromJson(CREATE_DPU_DEMAND_ALL_PARAMS);
        com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand dpuDemandToRead = dpuPlanningRobot.createDpuDemandForModification(dpuDemandCreateRequestData);
        dpuPlanningRobot.readDpuDemandByIdAndValidateResponse(dpuDemandToRead);
    }
}
