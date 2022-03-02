package com.tsystems.tm.acc.ta.robot.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.testng.FileAssert.fail;

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
            fail("Unexpected exception: " + e.getMessage());
            Thread.currentThread().interrupt(); // make sonar happy
        }
    }

    public static String getRandomDigits(int numberOfDigits) {
        return RandomStringUtils.randomNumeric(numberOfDigits);
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

        // Don't map null values
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // date-time comes in unix milliseconds. Need to be mapped to OffsetDateTime
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

}
