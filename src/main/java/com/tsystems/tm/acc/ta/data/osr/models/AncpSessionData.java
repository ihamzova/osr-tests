package com.tsystems.tm.acc.ta.data.osr.models;

import com.sun.jna.platform.unix.solaris.LibKstat;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.EntityRef;
import com.tsystems.tm.acc.tests.osr.ancp.configuration.v3_0_0.client.model.EquipmentBusinessRef;
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

