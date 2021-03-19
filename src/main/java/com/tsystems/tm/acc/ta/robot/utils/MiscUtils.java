package com.tsystems.tm.acc.ta.robot.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MiscUtils {

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
}
