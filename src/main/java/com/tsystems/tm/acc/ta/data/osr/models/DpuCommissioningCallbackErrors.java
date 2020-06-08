package com.tsystems.tm.acc.ta.data.osr.models;

public class DpuCommissioningCallbackErrors {
    /**
     * Error messages for callback
     */
    public static final String DEPROVISIONING_OLT_ERROR = "{\n" +
            "      \t\"error\": {\n" +
            "    \"message\": \"string\",\n" +
            "    \"status\": 0,\n" +
            "    \"code\": \"string\"\n" +
            "      \t},\n" +
            "  \"response\": {},\n" +
            "  \"success\": false\n" +
            "}";

    public static final String CONFIGURE_ANCP_ERROR = "{\n" +
            "  \"error\": {\n" +
            "    \"errorCode\": \"1111\",\n" +
            "    \"message\": \"ErrorMessage\"\n" +
            "  },\n" +
            "  \"response\": {},\n" +
            "  \"success\": false\n" +
            "}";
}
