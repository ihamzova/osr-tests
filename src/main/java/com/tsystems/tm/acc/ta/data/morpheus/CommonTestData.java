package com.tsystems.tm.acc.ta.data.morpheus;

import com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation;

import static com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation.OpEnum.REPLACE;
import static com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation.OpEnum.ADD;
import static com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation.OpEnum.REMOVE;

public class CommonTestData {
    public static final String DPU_PLANNING = "dpu-planning";
    public static final String DPU_PLANNING_PUBSUB_TOPIC = "resource-order-resource-inventory/v1/dpuPlanningEvents";
    public static final String DPU_ENDSZ_PATH = "/dpuEndSz";
    public static final String DPU_WO_ID_PATH = "/workorderId";
    public static final String DPU_ENDSZ_BNG = "49/311/32657/71GA";
    public static final String DPU_ENDSZ_A4 = "49/411/32657/71GA";
    public static final String DPU_STATE_VALUE = "FULFILLED";
    public static final String DPU_EMS_NBI_NAME = "SDX2221-08-TP";
    public static final String DPU_MAT_NAME = "SDX2221-08 TP-AC-M-FTTB ETSI";
    public static final String DPU_MAT_NO = "40898328";
    public static final String DPU_PORT_COUNT = "8";
    public static final String DPU_WO_ID = "651799";
    public static final JsonPatchOperation.OpEnum REPLACE_OPERATION = REPLACE;
}
