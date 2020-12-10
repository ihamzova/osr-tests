package com.tsystems.tm.acc.ta.data.osr.models;

import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_6_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_6_0.client.model.Port;
import lombok.Data;

@Data
public class Dpu {
    private String endSz;
    private Integer onuId;
    private String gfApFolId;
    private Device.LifeCycleStateEnum lifeCycleDpu = Device.LifeCycleStateEnum.NOT_OPERATING;
    private Port.LifeCycleStateEnum lifeCycleUplink = Port.LifeCycleStateEnum.NOT_OPERATING;
}
