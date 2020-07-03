package com.tsystems.tm.acc.ta.data.osr.models;

import com.tsystems.tm.acc.ta.data.osr.enums.CustomerBusinessType;
import com.tsystems.tm.acc.ta.data.osr.enums.CustomerClassificationType;
import com.tsystems.tm.acc.ta.data.osr.enums.CustomerType;
import com.tsystems.tm.acc.ta.data.osr.enums.Salutation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String mobilePhoneNumber;
    private String email;
    private String iban;
    private Property property;
    private Credentials credentials;
    private Address billingAddress;
    private Boolean isNewCustomer;
    private Boolean isTbsMockOn = true;
    private CustomerClassificationType customerClassificationType;
    private CustomerBusinessType customerBusinessType;
    private CustomerType customerType;
    private Salutation salutation;
}
