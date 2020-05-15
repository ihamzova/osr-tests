package com.tsystems.tm.acc.ta.team.morpheus.dpucommissioning;

import com.tsystems.tm.acc.ta.api.osr.DpuCommissioningClient;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DpuCommissioningNew extends BaseTest {
    private DpuCommissioningClient dpuCommissioningClient;

    @BeforeClass
    public void init(){dpuCommissioningClient = new DpuCommissioningClient();}

    @Test
    public void makeItWorks(){

    }
}
