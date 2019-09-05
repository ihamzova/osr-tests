package com.tsystems.tm.acc.ta.team.upiter.homeid;

import com.tsystems.tm.acc.home.id.generator.client.invoker.JSON;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.HomeIdPool;
import com.tsystems.tm.acc.ta.api.HomeIdGeneratorClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
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
    public void vvmAreaCreated() throws IOException {
        File template = new File(getClass().getResource("/team/upiter/homeid/singleHomeId.json").getFile());
        HomeIdPool homeid = new JSON().deserialize(readFile(template.toPath(), Charset.defaultCharset()), HomeIdPool.class);

        Response response = api.getClient().homeIdGeneratorController().generate()
                .execute(validatedWith(shouldBeCode(201)));
    }
}
