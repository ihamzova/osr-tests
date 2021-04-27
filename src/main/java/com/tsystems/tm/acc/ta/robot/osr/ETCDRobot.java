package com.tsystems.tm.acc.ta.robot.osr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.tsystems.tm.acc.ta.etcd.ETCDV3Client;
import com.tsystems.tm.acc.ta.kubernetes.ServicePortForwarder;
import io.etcd.jetcd.Watch;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
        try (ServicePortForwarder portForwarder = new ServicePortForwarder("ont-etcd-api", ETCDV3Client.DEFAULT_ETCD_API_PORT)) {
            ETCDV3Client client = new ETCDV3Client(portForwarder.getUri());
            client.getAllKeys(true).getKvs().forEach(kv -> log.info(kv.getKey().toString(Charset.defaultCharset())));
            ETCDV3Client.WatchListener listener = new ETCDV3Client.WatchListener();
            try (Watch.Watcher watcher = client.watch(key, listener)) {
                ObjectMapper mapper = new ObjectMapper();
                await().atMost(5, SECONDS).and().pollInterval(500, MILLISECONDS)
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

                            assertThat(values).allMatch(v -> events.stream().anyMatch(e -> e.contains(v)));
                        });
            }
        }

    }

    @Data
    static class DPUCommissioningEvent {
        String source;
        String type;
        String message;
    }
}
