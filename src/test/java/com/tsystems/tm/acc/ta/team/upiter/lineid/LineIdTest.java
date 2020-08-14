package com.tsystems.tm.acc.ta.team.upiter.lineid;

import com.tsystems.tm.acc.data.upiter.models.lineidbatch.LineIdBatchCase;
import com.tsystems.tm.acc.ta.api.osr.LineIdGeneratorClient;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.LineIdBatch;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.line.id.generator.internal.client.model.PoolLineId;
import com.tsystems.tm.acc.tests.osr.line.id.generator.internal.client.model.SingleLineId;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_BAD_REQUEST_400;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.LINE_ID_GENERATOR_MS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@ServiceLog(LINE_ID_GENERATOR_MS)
public class LineIdTest extends BaseTest {

    private LineIdGeneratorClient lineidGeneratorClient;
    private LineIdBatch lineIdBatch;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        lineidGeneratorClient = new LineIdGeneratorClient();
        lineIdBatch = context.getData().getLineIdBatchDataProvider().get(LineIdBatchCase.lineIdBatch);
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
        assertNotNull(response);
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
}
