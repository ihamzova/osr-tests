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
public class AccessLineManagementTableElement {
    private SelenideElement magnifyingGlass;
    private SelenideElement endSz;
    private SelenideElement slot;
    private SelenideElement port;
    private SelenideElement onuId;
    private SelenideElement lineId;
    private SelenideElement homeId;
    private SelenideElement accessPlatform;
    private SelenideElement ontSerialNumber;
    private SelenideElement sealConfigDefault;
    private SelenideElement sealConfigSubscriber;
    private SelenideElement sealConfigFttb;
    private SelenideElement rdqConfigDefault;
    private SelenideElement rdqConfigSubscriber;
    private SelenideElement a4ConfigNsp;
    private SelenideElement a4ConfigL2Bsa;
    private SelenideElement status;
    private SelenideElement businessStatus;
    private SelenideElement syncStatus;
}
