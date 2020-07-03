package com.tsystems.tm.acc.ta.data.osr.models;

import com.tsystems.tm.acc.ta.data.osr.enums.PropertyOwnerClassificationType;
import com.tsystems.tm.acc.ta.data.osr.enums.Salutation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Landlord {
    private String firstName;
    private String lastName;
    private String mobilePhoneNumber;
    private String email;
    private Property property;
    private Credentials credentials;
    private Salutation salutation;
    private Address billingAddress;
    private PropertyOwnerClassificationType propertyOwnerClassificationType;
}
