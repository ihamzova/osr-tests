package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

public class AncpSessionData {
    private Integer partitionId;
    private String rmkEndpointId;
    private String sealConfigurationId;
    private Integer sessionId;
    private String sessionType;
    private Integer vlan;
    private String configurationStatus;
    private String ipAddressAccessNode;
}

