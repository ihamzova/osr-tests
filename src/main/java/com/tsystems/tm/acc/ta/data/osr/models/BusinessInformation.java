package com.tsystems.tm.acc.ta.data.osr.models;


import lombok.*;

import java.util.Objects;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BusinessInformation {
    private String fehlerbildnummer;
    private String description;
    private String eventId;
    private String endSz;
    private String slotNumber;
    private String portNumber;
    private String internal_state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessInformation that = (BusinessInformation) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(endSz, that.endSz) &&
                Objects.equals(slotNumber, that.slotNumber) &&
                Objects.equals(portNumber, that.portNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, endSz, slotNumber, portNumber);
    }
}
