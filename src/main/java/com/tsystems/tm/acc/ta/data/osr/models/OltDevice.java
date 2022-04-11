package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OltDevice {
    private String deviceType;
    private String vpsz;
    private String fsz;
    private String bngEndsz;
    private String bngDownlinkPort;
    private String bngDownlinkSlot;
    private String lsz;
    private String orderNumber;
    private String reihe;
    private String platz;
    private String bgt;
    private Vst vst;
    private String tplnr;
    private String bezeichnung;
    private String hersteller;
    private String seriennummer;
    private String firmwareVersion;
    private String ipAdresse;
    private String cardType;
    private String oltSlot;
    private String oltPort;
    private String portType;
    private String uplinkSlot;
    private String uplinkPort;
    private Integer numberOfPonSlots;
    private Integer numberOfEthernetSlots;
    private Integer numberOfPonPorts;
    private Integer numberOfEthernetPorts;
    private Integer matNumber;
    private String klsId;
    private int compositePartyId;
    private String tkz;

    @Override
    public String toString() {
        return getEndsz();
    }

    public String getEndsz() {
        return String.format("%s/%s", getVpsz(), getFsz());
    }
}
