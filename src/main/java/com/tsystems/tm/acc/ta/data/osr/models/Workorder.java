package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Workorder {
    private String groupName;
    private String startDate;
    private String endDate;
    private List<String> dateList;
    private String id;
    private String typ;
}
