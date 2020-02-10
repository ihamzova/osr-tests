package com.tsystems.tm.acc.ta.team.upiter.lineid;

import com.tsystems.tm.acc.data.osr.models.lineidbatch.LineIdBatch;
import com.tsystems.tm.acc.data.osr.models.lineidbatch.LineIdBatchCase;
import com.tsystems.tm.acc.tests.osr.line.id.generator.internal.client.model.PoolLineId;
import com.tsystems.tm.acc.tests.osr.line.id.generator.internal.client.model.SingleLineId;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.internal.client.model.Port;
import com.tsystems.tm.acc.ta.api.LineIdGeneratorClient;
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

public class LineIdTest extends ApiTest {

    private LineIdGeneratorClient lineidGeneratorClient;
    private LineIdBatch lineIdBatch;

    private String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    @BeforeClass
    public void init() {
        lineidGeneratorClient = new LineIdGeneratorClient();
        lineIdBatch = OsrTestContext.get().getData()
                .getLineIdBatchDataProvider()
                .get(LineIdBatchCase.lineIdBatch);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 1 Line Id")
    public void createSingleLineId() {
        SingleLineId response = lineidGeneratorClient.getClient().lineIdGeneratorInternal().generateLineId()
                .endSzQuery(lineIdBatch.getEndSz())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
        assertNotNull(response);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 32 Line Ids")
    public void createPoolLineIds() {
        PoolLineId response = lineidGeneratorClient.getClient().lineIdGeneratorInternal().generateLineIdsBatch()
                .endSzQuery(lineIdBatch.getEndSz())
                .numberLineIdsQuery(lineIdBatch.getNumberLineIds())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
        assertEquals(response.getLineIds().size(), lineIdBatch.getNumberLineIds().intValue());
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 1 Line Id TOPAS")
    public void createSingleLineIdTopas() {
        SingleLineId response = lineidGeneratorClient.getClient().lineIdGeneratorInternal().generateLineId()
                .endSzQuery(lineIdBatch.getTopasEndSz())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
        assertNotNull(response);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 10 Line Ids TOPAS")
    public void createPoolOfTenLineIdsTopas() {
        PoolLineId response = lineidGeneratorClient.getClient().lineIdGeneratorInternal().generateLineIdsBatch()
                .endSzQuery(lineIdBatch.getTopasEndSz())
                .numberLineIdsQuery(lineIdBatch.getTopasNumberLineIds())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
        assertEquals(response.getLineIds().size(), lineIdBatch.getTopasNumberLineIds().intValue());
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Invalid Number for Creation Pool Line Ids")
    public void failCreatePoolLineIdsOver() {
        lineidGeneratorClient.getClient().lineIdGeneratorInternal().generateLineIdsBatch()
                .endSzQuery(lineIdBatch.getEndSz())
                .numberLineIdsQuery(33)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Invalid number for Creation Pool of Line Ids")
    public void failCreatePoolLineIdsMinus() {
        lineidGeneratorClient.getClient().lineIdGeneratorInternal().generateLineIdsBatch()
                .endSzQuery(lineIdBatch.getEndSz())
                .numberLineIdsQuery(-1)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Invalid EndSZ for Creation Pool Line Ids")
    public void failCreatePoolLineIdsEndSZ() {
        lineidGeneratorClient.getClient().lineIdGeneratorInternal().generateLineIdsBatch()
                .endSzQuery("49/911/1100/H176")
                .numberLineIdsQuery(5)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create Necessary Line Ids in PostProvisioning case")
    public void createNecessaryLineIdsPostProvisioning() throws IOException {
        File template = new File(getClass().getResource("/team/upiter/lineid/portForLineIdPool.json").getFile());
        Port port = new JSON().deserialize(readFile(template.toPath(), Charset.defaultCharset()), Port.class);
        PoolLineId poolLineId = lineidGeneratorClient.getClient().lineIdGeneratorInternal().generateLineIdsBatch()
                .endSzQuery(lineIdBatch.getEndSz())
                .numberLineIdsQuery(lineIdBatch.getNumberLineIds() - port.getLineIdPools().size())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
        assertEquals(poolLineId.getLineIds().size(), lineIdBatch.getNumberLineIds() - port.getLineIdPools().size());
    }
}
