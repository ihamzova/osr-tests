package com.tsystems.tm.acc.ta.robot.osr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.ta.etcd.AggregatingKeyValuesWatchListenerRest;
import com.tsystems.tm.acc.ta.etcd.ETCDV3Client;
import com.tsystems.tm.acc.ta.etcd.ETCDV3RestClient;
import com.tsystems.tm.acc.ta.etcd.WatchListenerGrpcImpl;
import com.tsystems.tm.acc.ta.kubernetes.ServicePortForwarder;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import de.telekom.it.t3a.kotlin.kubernetes.KubernetesContext;
import io.etcd.jetcd.Watch;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
public class ETCDRobot {
    public void checkEtcdValues(String key, List<String> values) {
        //checkEtcdValuesWithRest(key, values);
        if (KubernetesContext.Companion.get().getUseOpenshift()) {
            checkEtcdValuesWithPortForwarding(key, values);
        } else {
            checkEtcdValuesWithRest(key, values);
        }
    }

    public void checkEtcdValuesWithPortForwarding(String key, List<String> values) {
        try (ServicePortForwarder portForwarder = new ServicePortForwarder("ont-etcd-api", ETCDV3Client.DEFAULT_ETCD_API_PORT)) {
            checkEtcdValues(portForwarder.getUri(), key, values);
        }
    }

    public void checkEtcdValuesWithIngress(String key, List<String> values) {
        checkEtcdValues(new GigabitUrlBuilder("ont-etcd").withoutSuffix().withPort(433).buildUri(), key, values);
    }

    public void checkEtcdValuesWithRest(String key, List<String> values) {
        ETCDV3RestClient client = new ETCDV3RestClient(new GigabitUrlBuilder("ont-etcd").withoutSuffix().buildUri());
        AggregatingKeyValuesWatchListenerRest listener = new AggregatingKeyValuesWatchListenerRest();
        client.watch(key, listener);

        ObjectMapper mapper = new ObjectMapper();
        await().atMost(30, SECONDS).and().pollInterval(500, MILLISECONDS) // It needs more time
                .untilAsserted(() -> {
                    List<String> events = listener.getKeyValues().stream()
                            .filter(kv -> kv.getValue() != null)
                            //.map(kv -> new String(Base64.getDecoder().decode(kv.getValue())))
                            .map(kv -> new String(kv.getValue()))// Rest api returns Base64 encoded
                            .map(value -> {
                                try {
                                    return mapper.readValue(value, DPUCommissioningEvent.class);
                                } catch (JsonProcessingException e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .map(event -> event.message)
                            .collect(Collectors.toList());
                    values.forEach(value -> assertThat(value)
                            .matches(v -> events.stream().anyMatch(e -> e.contains(v)),
                                    "Should be one of: " + String.join(",", events)));
                    //assertThat(values).allMatch(v -> events.stream().anyMatch(e -> e.contains(v)));
                });
    }

    private void checkEtcdValues(URI endpoint, String key, List<String> values) {
        ETCDV3Client client = new ETCDV3Client(endpoint);
        WatchListenerGrpcImpl listener = new WatchListenerGrpcImpl();
        try (Watch.Watcher watcher = client.watch(key, listener)) {
            ObjectMapper mapper = new ObjectMapper();
            await().atMost(10, SECONDS).and().pollInterval(500, MILLISECONDS)
                    .untilAsserted(() -> {
                        List<String> events = listener.getKv().stream()
                                .map(kv -> kv.getValue().toString(Charset.defaultCharset()))
                                .map(value -> {
                                    try {
                                        return mapper.readValue(value, DPUCommissioningEvent.class);
                                    } catch (JsonProcessingException e) {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .map(event -> event.message)
                                .collect(Collectors.toList());
                        values.forEach(value -> assertThat(value)
                                .matches(v -> events.stream().anyMatch(e -> e.contains(v)),
                                        "Should be one of: " + String.join(",", events)));
                        //assertThat(values).allMatch(v -> events.stream().anyMatch(e -> e.contains(v)));
                    });
        }
    }

    @Data
    static class DPUCommissioningEvent {
        String source;
        String type;
        String message;
    }
}
