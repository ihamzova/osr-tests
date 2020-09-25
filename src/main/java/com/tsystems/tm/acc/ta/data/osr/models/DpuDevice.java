package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DpuDevice {
    private String vpsz;
    private String fsz;
    private String oltEndsz;
    private String oltGponSlot;
    private String oltGponPort;
    private String bngEndsz;
    private String bezeichnung;
    private String typ;
    private String hersteller;
    private String klsId;
    private String seriennummer;
    private String ipAdresse;
    private String ponConnectionWe;
    private String ponConnectionGe;
    private String dpuDistributionPointId;

    @Override
    public String toString() {
        return getEndsz();
    }

    public String getEndsz() {
        return String.format("%s/%s", getVpsz(), getFsz());
    }
}