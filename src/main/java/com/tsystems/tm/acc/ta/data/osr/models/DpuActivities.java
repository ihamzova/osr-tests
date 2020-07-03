package com.tsystems.tm.acc.ta.data.osr.models;

import java.util.Arrays;
import java.util.List;

public class DpuActivities {
    public static final String START_EVENT = "Event_Start";
    public static final String GET_DPU = "Activity_OLT-RI.GET.DeviceDPU";
    public static final String GET_LLC = "Activity_OLT-RI.GET.DpuPonConn";
    public static final String GET_ETHLINK = "Activity_OLT-RI.GET.EthernetLink";
    public static final String GET_ONUID = "Activity_AL-RI.GET.OnuId";
    public static final String GET_BACKHAUL = "Activity_AL-RI.POST.Backhaul-id";
    public static final String DEPROVISION_OLT = "Activity_DeprovisionOltPort";
    public static final String SET_ANCP = "Activity_Ancp-Conf.POST.AncpConf";
    public static final String GET_ANCP = "Activity_OLT-RI.GET.AncpSession";
    public static final String GET_DPUOLT = "Activity_OLT-RI.GET.DpuAtOltConf";
    public static final String CREATE_DPUOLT = "Activity_OLT-RI.POST.DpuAtOltConf";
    public static final String CONFIGURE_DPU_SEAL = "Activity_SEAL.POST.DpuAtOltConf";
    public static final String UPDATE_INV = "Activity_OLT-RI.PUT.DpuAtOltConf";
    public static final String CREATE_DPUEMS_CONF = "Activity_OLT-RI.POST.DpuEmsConf";
    public static final String CONFIGURE_DPUEMS_SEAL = "Activity_SEAL.POST.DpuConf";
    public static final String SET_DPUEMS_CONF = "Activity_OLT-RI.PUT.DpuEmsConf";
    public static final String PROVISIONING_DEVICE = "Activity_WG-FTTB-AP.POST.startDeviceProvisioning";

    //List with async tasks
    public static final List<String> STEPS_WITH_202_CODE = Arrays.asList(CONFIGURE_DPU_SEAL,CONFIGURE_DPUEMS_SEAL, PROVISIONING_DEVICE);
}
