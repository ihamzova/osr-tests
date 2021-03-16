package com.tsystems.tm.acc.ta.robot.utils;

import java.util.Arrays;
import java.util.List;

public class MiscUtils {

    public static List<String> stringSplit(String string, String separationRegExp) {
        return Arrays.asList(string.split(separationRegExp).clone());
    }
}
