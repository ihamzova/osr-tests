package com.tsystems.tm.acc.ta.data.osr.models;

import com.tsystems.tm.acc.ta.data.osr.enums.OrderType;
import com.tsystems.tm.acc.ta.data.osr.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PresalesOrder {
    private String tariff;
    private String tariffOption;
    private String router;
    private String ont;
    private List<String> mediaReceivers;
    private String orderNumber;
    private String ibtOrderNumber;
    private String montageTerminLink;
    private SourceType sourceType;
    private OrderType orderType;
}
