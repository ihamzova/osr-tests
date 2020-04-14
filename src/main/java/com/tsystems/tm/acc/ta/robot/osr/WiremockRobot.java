package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.generators.PslGetEquipmentStubGeneratorMapper;
import com.tsystems.tm.acc.ta.data.osr.generators.SealAccessNodeConfigurationGeneratorMapper;
import com.tsystems.tm.acc.ta.generators.WiremockMappingGenerator;
import com.tsystems.tm.acc.ta.helpers.WiremockHelper;
import com.tsystems.tm.acc.ta.helpers.WiremockMappingsPublisher;
import io.qameta.allure.Step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class WiremockRobot {
    @Step("Upload mock data to wiremock")
    public void initializeWiremock(File pathToWiremockData) {
        WiremockMappingsPublisher publisher = new WiremockMappingsPublisher();
        try {
            publisher.publish(pathToWiremockData);
        } catch (IOException e) {
            throw new RuntimeException("Wiremock stubs not found. They cannot be uploaded!");
        }
        WiremockHelper.requestsReset();
    }

    @Step("Create mock data for PSL")
    public void createMocksForPSL(File storeToFolder, List<OltDevice> devices) {
        WiremockMappingGenerator generator = new WiremockMappingGenerator();
        PslGetEquipmentStubGeneratorMapper mapper = new PslGetEquipmentStubGeneratorMapper();
        generator.generate(devices.stream()
                .map(mapper::getData)
                .collect(Collectors.toList()), Paths.get(storeToFolder.toURI()));
    }

    @Step("Create mock data for PSL")
    public void createMocksForSEAL(File storeToFolder, List<OltDevice> devices) {
        WiremockMappingGenerator generator = new WiremockMappingGenerator();
        SealAccessNodeConfigurationGeneratorMapper mapper = new SealAccessNodeConfigurationGeneratorMapper();
        generator.generate(devices.stream()
                .map(mapper::getData)
                .collect(Collectors.toList()), Paths.get(storeToFolder.toURI()));
    }
}
