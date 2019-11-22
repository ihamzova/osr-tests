package com.tsystems.tm.acc.ta.team.upiter.homeid;

import com.tsystems.tm.acc.data.osr.models.homeidbatch.HomeIdBatch;
import com.tsystems.tm.acc.data.osr.models.homeidbatch.HomeIdBatchCase;
import com.tsystems.tm.acc.home.id.generator.client.model.PoolHomeId;
import com.tsystems.tm.acc.home.id.generator.client.model.SingleHomeId;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.invoker.JSON;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.Port;
import com.tsystems.tm.acc.ta.api.HomeIdGeneratorClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class HomeIdTest extends ApiTest {

    private HomeIdGeneratorClient homeIdGeneratorClient;
    private HomeIdBatch homeIdBatch;

    private String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    @BeforeClass
    public void init() {
        homeIdGeneratorClient = new HomeIdGeneratorClient();
        homeIdBatch = OsrTestContext.get().getData()
                .getHomeIdBatchDataProvider()
                .get(HomeIdBatchCase.homeIdBatch);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 1 Home Id")
    public void createSingleHomeId() {
        SingleHomeId response = homeIdGeneratorClient.getClient()
                .homeIdGeneratorController()
                .generate()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
        assertNotNull(response);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 32 Home Ids")
    public void createPoolHomeIds() {
        PoolHomeId response = homeIdGeneratorClient.getClient().homeIdGeneratorController().generateBatch()
                .numberHomeIdsQuery(homeIdBatch.getNumberLineIds())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
        assertEquals(response.getHomeIds().size(), homeIdBatch.getNumberLineIds().intValue());
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Invalid number for Creation Pool of Home Ids")
    public void failCreatePoolHomeIdOver() {
        homeIdGeneratorClient.getClient().homeIdGeneratorController().generateBatch()
                .numberHomeIdsQuery(33)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Invalid number for Creation Pool of Home Ids")
    public void failCreatePoolHomeIdMinus() {
        homeIdGeneratorClient.getClient().homeIdGeneratorController().generateBatch()
                .numberHomeIdsQuery(-1)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create Necessary Home Ids in PostProvisioning case")
    public void createNecessaryHomeIdsPostProvisioning() throws IOException {
        File template = new File(getClass().getResource("/team/upiter/homeid/portForHomeIdPool.json").getFile());
        Port port = new JSON().deserialize(readFile(template.toPath(), Charset.defaultCharset()), Port.class);
        PoolHomeId poolHomeId = homeIdGeneratorClient.getClient().homeIdGeneratorController().generateBatch()
                .numberHomeIdsQuery(homeIdBatch.getNumberLineIds() - port.getHomeIdPools().size())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
        assertEquals(poolHomeId.getHomeIds().size(), homeIdBatch.getNumberLineIds() - port.getHomeIdPools().size());
    }
}
