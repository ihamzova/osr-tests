package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.db.etcd.ETCDClient;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import org.testng.Assert;

public class ETCDRobot {
    public void checkIfEtcdKeyContainsValue(String key, String value) {
        ETCDClient client = new ETCDClient(new OCUrlBuilder("ont-etcd").withoutSuffix().buildUri());

        Assert.assertTrue(client.get(key).getValue().contains(value));
    }

    public void checkIfEtcdDirContainsKey(String dir, String key) {
        ETCDClient client = new ETCDClient(new OCUrlBuilder("ont-etcd").withoutSuffix().buildUri());

        Assert.assertTrue(client.get(dir).getNodes().stream().anyMatch(n -> n.getKey().equals(dir + key)));
    }
}
