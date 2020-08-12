package com.tsystems.tm.acc.ta.helpers.osr.logs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tsystems.tm.acc.ta.data.osr.models.BusinessInformation;

import java.util.List;
import java.util.stream.Collectors;

public class LogConverter {
    private static Gson gson = new Gson();

    public static List<BusinessInformation> logsToBusinessInformationMessages(List<String> logs) {
        return logsToMessages(logs, "business_information", BusinessInformation.class);
    }

    public static List<String> logsToStringMessages(List<String> logs) {
        return logsToMessages(logs, "message", String.class);
    }

    private static <T> List<T> logsToMessages(List<String> logs, String member, Class<T> clazz) {
        return logs.stream()
                .map(log -> gson.fromJson(log, JsonObject.class))
                .filter(jsonPodLog -> jsonPodLog.has(member))
                .map(jsonPodLog -> gson.fromJson(
                        jsonPodLog.get(member).toString(), clazz
                        )
                )
                .collect(Collectors.toList());
    }
}
