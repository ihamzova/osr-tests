package com.tsystems.tm.acc.ta;

import com.tsystems.tm.acc.ta.robot.osr.ETCDRobot;
import com.tsystems.tm.acc.ta.sql.Keys;
import de.telekom.it.t3a.kotlin.kubernetes.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.tsystems.tm.acc.ta.sftp.UtilsKt.getSftpDataDefault;
import static com.tsystems.tm.acc.ta.sql.UtilsKt.getJdbcDataDefaultSpring;

@Slf4j
public class Debug {
    @Test
    public void debug() {
        ETCDRobot etcdRobot = new ETCDRobot();

        etcdRobot.checkEtcdValues("dpu-commissioning/f7a8296a-0756-4ff9-a716-cd5dd6e718ce", Arrays.asList(
                "EXECUTED Successfuly [Read DPU device data]",
                "EXECUTED Successfuly [update LifecycleStatus of DPU to INSTALLING]",
                "EXECUTED Successfuly [update LifecycleStatus of DPU.uplinkPort to INSTALLING]",
                "EXECUTED Successfuly [Read OltPonPort Data]",
                "EXECUTED Successfuly [Read OltUpLinkPortData]",
                "EXECUTED Successfuly [Get Unique OnuId for DPU]",
                "EXECUTED Successfuly [Read BackhaulId]",
                "EXECUTED Successfuly [Read BackhaulId]",
                "EXECUTED Successfuly [Deprovision FTTH on PonPort][call]",
                "EXECUTED Successfuly [Deprovision FTTH on PonPort][callback]",
                "EXECUTED Successfuly [Configure ANCP on BNG][call]",
                "EXECUTED Successfuly [Configure ANCP on BNG][callback]",
                "EXECUTED Successfuly [Read ANCP Info]",
                "EXECUTED Successfuly [Create DpuAtOltConfiguration If Missing]",
                "EXECUTED Successfuly [Configure DPU at OLT][call]",
                "EXECUTED Successfuly [Configure DPU at OLT][callback]",
                "EXECUTED Successfuly [Set DpuAtOltConfiguration.configurationState to active]",
                "EXECUTED Successfuly [Create DpuEmsConfiguration If Missing]",
                "EXECUTED Successfuly [Configure DPU Ems][call]",
                "EXECUTED Successfuly [Configure DPU Ems][callback]",
                "EXECUTED Successfuly [Set DpuEmsConfiguration.configurationState to active]",
                "EXECUTED Successfuly [Provision FTTB access provisioning on DPU][call]",
                "EXECUTED Successfuly [Provision FTTB access provisioning on DPU][callback]"));
    }

    @Test
    public void debug2() {
        Map<Keys, String> data = getJdbcDataDefaultSpring("pon-inventory");
        data.forEach((k, v) -> {
            log.info(k.name() + ": " + v);
        });

        EnumMap<com.tsystems.tm.acc.ta.sftp.Keys, String> data2 = getSftpDataDefault("address-loader");
        data2.forEach((k, v) -> {
            log.info(k.name() + ": " + v);
        });
    }
}
