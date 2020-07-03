package com.tsystems.tm.acc.ta.data.osr.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    private String firstName;
    private String lastName;
    private String fixedLine;
    private String mobileNumber;
    private String email;
    private String id;
    private Supplier supplier;
    private ResourceTimeslot resourceTimeslot;
}
