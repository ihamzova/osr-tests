package com.tsystems.tm.acc.ta.data.osr.models;

import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.network.line.profile.management.v1_5_0.client.model.ResourceOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class NetworkLineProfileData {

    private ResourceOrder resourceOrder;
    private List<ResourceOrderItem> resourceOrderItems;
    private List<ResourceCharacteristic> resourceCharacteristics;
}
