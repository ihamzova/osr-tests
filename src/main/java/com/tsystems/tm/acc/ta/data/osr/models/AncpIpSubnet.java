package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

public class AncpIpSubnet {
    private String ipAddressBng;
    private String ipAddressBroadcast;
    private String ipAddressLoopback;
    private String rmkAccessId;
    private String subnetMask;
    private String atType;

}
