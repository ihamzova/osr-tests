package com.tsystems.tm.acc.ta.team.upiter.homeid;

import com.tsystems.tm.acc.home.id.generator.client.model.SingleHomeId;
import com.tsystems.tm.acc.ta.api.HomeIdGeneratorClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static org.testng.Assert.assertNotNull;

public class HomeIdTest extends ApiTest {

    private HomeIdGeneratorClient api;

    private String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    @BeforeClass
    public void init() {
        api = new HomeIdGeneratorClient();
    }

    @Test
    @TmsLink("")
    @Description("Create 1 Home Id")
    public void createSingleHomeId()  {
        SingleHomeId response = api.getClient().homeIdGeneratorController().generate().executeAs(validatedWith(shouldBeCode(201)));
        assertNotNull(response);
    }
}
