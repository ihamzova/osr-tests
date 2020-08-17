package com.tsystems.tm.acc.ta.data.upiter;

import java.util.ArrayList;
import java.util.List;

public class CommonTestData {

    public static final Integer HTTP_CODE_OK_200 = 200;
    public static final Integer HTTP_CODE_CREATED_201 = 201;
    public static final Integer HTTP_CODE_ACCEPTED_202 = 202;
    public static final Integer HTTP_CODE_BAD_REQUEST_400 = 400;
    public static final Integer HTTP_CODE_NOT_FOUND_404 = 404;
    public static final Integer HTTP_CODE_INTERNAL_SERVER_ERROR_500 = 500;
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_WALLED_GARDEN = "WALLED_GARDEN";

    public static List<Integer> calculateExpectedOnuAccessIds(int numberOfAccessLinesForProvisioning) {
        List<Integer> expectedOnuIdsList = new ArrayList<>();
        for (int i = 1; i <= numberOfAccessLinesForProvisioning; i++) {
            expectedOnuIdsList.add(i);
        }
        return expectedOnuIdsList;
    }
}
