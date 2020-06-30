package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.db.etcd.ETCDClient;
import com.tsystems.tm.acc.ta.db.etcd.Node;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import org.testng.Assert;

import java.util.List;

public class ETCDRobot {
    public void checkIfEtcdKeyContainsValue(String key, String value) {
        ETCDClient client = new ETCDClient(new OCUrlBuilder("ont-etcd").withoutSuffix().buildUri());

        Assert.assertTrue(client.get(key).getValue().contains(value));
    }

    public void checkEtcdValue(String key, String value) {
        ETCDClient client = new ETCDClient(new OCUrlBuilder("ont-etcd").withoutSuffix().buildUri());

        List<Node> nodes = client.get(key).getNodes();

        Boolean result = false;
        for (int n = 0; n < nodes.size();  n++) {
            if (!client.get(key).getNodes().get(n).getValue().contains(value)) {
                result = true;
            }
        }
        Assert.assertTrue(result);
    }

    public void checkIfEtcdDirContainsKey(String dir, String key) {
        ETCDClient client = new ETCDClient(new OCUrlBuilder("ont-etcd").withoutSuffix().buildUri());

        Assert.assertTrue(client.get(dir).getNodes().stream().anyMatch(n -> n.getKey().equals(dir + key)));
    }
}
