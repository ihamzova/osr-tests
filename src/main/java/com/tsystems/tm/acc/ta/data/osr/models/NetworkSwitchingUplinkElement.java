package com.tsystems.tm.acc.ta.data.osr.models;

import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NetworkSwitchingUplinkElement {
    private SelenideElement radio;
    private String checked;
    private PortProvisioning uplink;
    private String state;
}
