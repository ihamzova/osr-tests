package com.tsystems.tm.acc.ta.robot.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MiscUtils {

    private MiscUtils() {

    }

    public static List<String> stringSplit(String string, String separationRegExp) {
        return Arrays.asList(string.split(separationRegExp).clone());
    }

    public static void sleepForSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getRandomDigits(int numberOfDigits) {
        return new Random().ints(0, 9)
                .limit(numberOfDigits)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
    }

    public static String getEndsz(A4NetworkElement neData) {
        return neData.getVpsz() + "/" + neData.getFsz();
    }

    // Get configured object mapper that supports TMF quirks
    public static ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        // action property in json is e.g. "add". Needs to be mapped to enum ADD("add")
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        // some @... properties like e.g. @BaseType cannot be mapped (to atBaseType). Don't fail, isn't tested here
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // date-time comes in unix milliseconds. Need to be mapped to OffsetDateTime
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

}
