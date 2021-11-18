package com.tsystems.tm.acc.ta.robot.utils;

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

}
