package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String city;
    private String district;
    private String municipality;
    private String streetFull;
    private String streetShort;
    private String hausNummer;
    private String houseNumberSupplement;
    private String zipCode;
    private String asb;
    private String onkz;
    private String klsId;
}
