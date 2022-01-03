package com.tsystems.tm.acc.ta.data.osr.models;

import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_17_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_17_0.client.model.Port;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Dpu {
    private String endSz;
    private Integer onuId;
    private String gfApFolId;
    private Device.LifeCycleStateEnum lifeCycleDpu = Device.LifeCycleStateEnum.NOT_OPERATING;
    private Port.LifeCycleStateEnum lifeCycleUplink = Port.LifeCycleStateEnum.NOT_OPERATING;
}
