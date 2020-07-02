package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.db.etcd.ETCDClient;
import com.tsystems.tm.acc.ta.db.etcd.Node;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;

public class ETCDRobot {
    public void checkIfEtcdKeyContainsValue(String key, String value) {
        ETCDClient client = new ETCDClient(new OCUrlBuilder("ont-etcd").withoutSuffix().buildUri());

        Assert.assertTrue(client.get(key).getValue().contains(value));
    }

    public void checkEtcdValues(String key, List<String> values){
        List<Node> nodes = getEtcdNodes(key);
        for(String value : values){
            Assert.assertTrue(checkEtcdNodesValue(nodes, value), "Value: "+ value + " not found in ETCD");
        }
    }

    public void checkIfEtcdDirContainsKey(String dir, String key) {
        ETCDClient client = new ETCDClient(new OCUrlBuilder("ont-etcd").withoutSuffix().buildUri());

        Assert.assertTrue(client.get(dir).getNodes().stream().anyMatch(n -> n.getKey().equals(dir + key)));
    }


    private boolean checkEtcdNodesValue(List<Node> nodes, String value) {
        Boolean result = false;
        for (Node node : nodes) {
            if (node.getValue().contains(value)) {
                result = true;
            }
        }
        return result;
    }

    private List<Node> getEtcdNodes(String key){
        ETCDClient client = new ETCDClient(new OCUrlBuilder("ont-etcd").withoutSuffix().buildUri());
        return  client.get(key).getNodes();
    }
}
