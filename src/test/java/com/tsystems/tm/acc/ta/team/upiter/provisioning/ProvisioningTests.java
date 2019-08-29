package com.tsystems.tm.acc.ta.team.upiter.provisioning;

import com.tsystems.tm.acc.area.data.management.client.invoker.JSON;
import com.tsystems.tm.acc.area.data.management.client.model.VVMAreaImportDTO;
import com.tsystems.tm.acc.ta.api.ProvisioningClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

@Epic("Domain Areas&Location")
@Feature("Area Data Management")
@Story("VVM Area")
public class ProvisioningTests extends ApiTest {
    private ProvisioningClient api;

    private String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    @BeforeClass
    public void init() {
        api = new ProvisioningClient();
    }


    @Test
    @TmsLink("DIGIHUB-13850")
    @Description("Create VVM Area. Success")
    public void vvmAreaCreated() throws IOException {
        File template = new File(getClass().getResource("/team/moon/am/ValidVvmAreaImport.json").getFile());
        VVMAreaImportDTO area = new JSON().deserialize(readFile(template.toPath(), Charset.defaultCharset()), VVMAreaImportDTO.class);

        Response response = api.getClient().ontOltOrchestrator().createOntResource().body(null)
                .execute(validatedWith(shouldBeCode(201)));
    }
}
