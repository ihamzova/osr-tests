package com.tsystems.tm.acc.ta.data.osr.models;

import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_8_0.client.model.AbstractDeviceRelatedParty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

public class ExpectedAbstractDevice {
  private Integer id;
  private String productionPlatform;
  private String endSz;
  private String fiberOnLocationId;
  private Integer klsId;
  private String serialNumber;
  private String emsNbiName;
  private String materialNumber;
  private String accessTransmissionMedium;
  private String lifeCycleState;
  private String vpSz;
  private String fSz;
  private AbstractDeviceRelatedParty relatedParty;
}

