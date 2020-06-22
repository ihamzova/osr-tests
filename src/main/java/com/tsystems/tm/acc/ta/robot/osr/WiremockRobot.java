package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.data.models.stable.OltDevice;
import com.tsystems.tm.acc.ta.api.RequestSpecBuilders;
import com.tsystems.tm.acc.ta.data.osr.generators.PslEquipmentGeneratorMapper;
import com.tsystems.tm.acc.ta.data.osr.generators.PslGetEquipmentStubGeneratorMapper;
import com.tsystems.tm.acc.ta.data.osr.generators.RebellUewegGeneratorMapper;
import com.tsystems.tm.acc.ta.data.osr.generators.SealAccessNodeConfigurationGeneratorMapper;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.ta.generators.WiremockMappingGenerator;
import com.tsystems.tm.acc.ta.helpers.WiremockHelper;
import com.tsystems.tm.acc.ta.helpers.WiremockMappingsPublisher;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.wiremock.client.api.WireMockApi;
import com.tsystems.tm.acc.tests.wiremock.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.wiremock.client.invoker.GsonObjectMapper;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import io.qameta.allure.Step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class WiremockRobot {
    private WireMockApi wiremockApi = ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(() -> {
        return RequestSpecBuilders.getDefault(GsonObjectMapper.gson(), (new OCUrlBuilder("wiremock-acc"))
                .withEndpoint("/__admin").buildUri());
    })).wireMock();

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

    @Step("Set up REBELL wiremock")
    public void setUpRebellWiremock(UewegData uewegData) {
        RebellUewegGeneratorMapper mapper = new RebellUewegGeneratorMapper();
        StubMapping result = wiremockApi
                .mappingsPost()
                .body(mapper.getData(uewegData))
                .executeAs(validatedWith(shouldBeCode(201)));
        uewegData.setRebellWiremockUuid(result.getId());
    }

    @Step("Set up PSL wiremock")
    public void setUpPslWiremock() {
        PslEquipmentGeneratorMapper mapper = new PslEquipmentGeneratorMapper();
        StubMapping result = wiremockApi
                .mappingsPost()
                .body(mapper.getData())
                .executeAs(validatedWith(shouldBeCode(201)));
    }

    @Step("Tear down wiremock")
    public void tearDownWiremock(String wiremockEntryUuid) {
        wiremockApi.mappingsStubMappingIdDelete().stubMappingIdPath(wiremockEntryUuid).execute(validatedWith(shouldBeCode(200)));
    }
}
