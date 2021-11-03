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

    public static final String DPU_ENDSZ_VALUE = "49/670/4/88F9";
    public static final String DPU_ENDSZ_NOT_UNIQUE_VALUE = "49/520/4/78G2";
    public static final String DPU_STATE_VALUE = "FULFILLED";
    public static final String DPU_WO_ID_VALUE = "11636";
    public static final String DPU_WO_ID_NOT_UNIQUE_VALUE = "10835";

    public static final JsonPatchOperation.OpEnum REPLACE_OPERATION = REPLACE;
    public static final JsonPatchOperation.OpEnum ADD_OPERATION = ADD;
}
