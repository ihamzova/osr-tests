package com.tsystems.tm.acc.ta.data.morpheus;

import com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation;
import static com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation.OpEnum.REPLACE;
import static com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation.OpEnum.ADD;
import static com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation.OpEnum.REMOVE;

public class CommonTestData {
    public static final String DPU_PLANNING = "dpu-planning";
    public static final String DPU_PLANNING_PUBSUB_TOPIC = "resource-order-resource-inventory/v1/dpuPlanningEvents";

    public static final String DPU_ENDSZ_PATH = "/dpuEndSz";
    public static final String DPU_STATE_PATH = "/state";
    public static final String DPU_WO_ID_PATH = "/workorderId";
    public static final String DPU_TECHNOLOGY_PATH = "/dpuAccessTechnology";
    public static final String DPU_INSTALLATION_INSTRUCTION_PATH = "/dpuInstallationInstruction";
    public static final String DPU_LOCATION_PATH = "/dpuLocation";
    public static final String DPU_FOL_ID_PATH = "/fiberOnLocationId";
    public static final String DPU_KLS_ID_PATH = "/klsId";
    public static final String DPU_PORT_NUMBER_PATH = "/numberOfNeededDpuPorts";
    public static final String DPU_ID_PATH = "/id";
    public static final String DPU_HREF_PATH = "/href";
    public static final String DPU_CREATION_DATE_PATH = "/creationDate";
    public static final String DPU_MODIFICATION_DATE_PATH = "/modificationDate";

    public static final String DPU_ENDSZ_VALUE = "49/670/4/88F9";
    public static final String DPU_STATE_VALUE = "FULFILLED";
    public static final String DPU_WO_ID_VALUE = "11636";
    public static final String DPU_TECHNOLOGY_VALUE = "FTTB_CUDA";
    public static final String DPU_NSTALLATION_INSTRUCTION_VALUE = "Modified";
    public static final String DPU_LOCATION_VALUE = "Modified";
    public static final String DPU_FOL_ID_VALUE = "1000000929999";
    public static final String DPU_KLS_ID_VALUE = "584749999";
    public static final String DPU_PORT_NUMBER_VALUE = "50";

    public static final JsonPatchOperation.OpEnum REPLACE_OPERATION = REPLACE;
    public static final JsonPatchOperation.OpEnum ADD_OPERATION = ADD;
    public static final JsonPatchOperation.OpEnum REMOVE_OPERATION = REMOVE;

}
