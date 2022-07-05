package com.tsystems.tm.acc.ta;

import com.tsystems.tm.acc.ta.etcd.ETCDV3Client;
import com.tsystems.tm.acc.ta.pages.osr.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.robot.osr.ETCDRobot;
import com.tsystems.tm.acc.ta.sql.Keys;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import de.telekom.it.t3a.kotlin.kubernetes.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.tsystems.tm.acc.ta.sftp.UtilsKt.getSftpDataDefault;
import static com.tsystems.tm.acc.ta.sql.UtilsKt.getJdbcDataDefaultSpring;

@Slf4j
public class Debug extends GigabitTest {
    @Test
    public void debugUI() {
        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
    }

    @Test
    public void debug() {
        ETCDV3Client client = new ETCDV3Client(new GigabitUrlBuilder("ont-etcd").withoutSuffix().withPort(443).buildUri());
        client.getAllKeys(true).getKvs().forEach(kv -> log.info(kv.getKey().toString(Charset.defaultCharset())));
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
