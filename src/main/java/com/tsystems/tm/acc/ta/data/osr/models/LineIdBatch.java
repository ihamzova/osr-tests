package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LineIdBatch {
  private String partyID;
  private String topasPartyID;
  private String negativPartyId;
  private Integer numberLineIds;
  private Integer topasNumberLineIds;
  private Integer negativNumberLineIds;
  private Integer invalidNumberLineIds;
}
