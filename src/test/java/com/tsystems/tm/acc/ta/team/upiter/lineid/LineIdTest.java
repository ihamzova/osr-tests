package com.tsystems.tm.acc.ta.team.upiter.lineid;

import com.tsystems.tm.acc.data.upiter.models.lineidbatch.LineIdBatchCase;
import com.tsystems.tm.acc.tests.osr.line.id.generator.v2_1_0.client.model.PoolLineId;
import com.tsystems.tm.acc.tests.osr.line.id.generator.v2_1_0.client.model.SingleLineId;
import com.tsystems.tm.acc.ta.api.osr.LineIdGeneratorClient;
import com.tsystems.tm.acc.ta.data.osr.models.LineIdBatch;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
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
public class LineIdTest extends GigabitTest {

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
    SingleLineId response = lineidGeneratorClient.getClient().lineIdGenerator()
            .generateLineId()
            .partyIdQuery(lineIdBatch.getPartyID())
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    assertNotNull(response);
  }

  @Test
  @TmsLink("DIGIHUB-34654")
  @Description("Create 32 Line Ids")
  public void createPoolLineIds() {
    PoolLineId response = lineidGeneratorClient.getClient()
            .lineIdGenerator().generateLineIdsBatch()
            .partyIdQuery(lineIdBatch.getPartyID())
            .numberLineIdsQuery(lineIdBatch.getNumberLineIds())
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    assertNotNull(response);
    assertEquals(response.getLineIds().size(), lineIdBatch.getNumberLineIds().intValue());
  }

  @Test
  @TmsLink("DIGIHUB-34654")
  @Description("Create 1 Line Id TOPAS")
  public void createSingleLineIdTopas() {
    SingleLineId response = lineidGeneratorClient.getClient()
            .lineIdGenerator()
            .generateLineId()
            .partyIdQuery(lineIdBatch.getTopasPartyID())
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    assertNotNull(response);
  }

  @Test
  @TmsLink("DIGIHUB-34654")
  @Description("Create 10 Line Ids TOPAS")
  public void createPoolOfTenLineIdsTopas() {
    PoolLineId response = lineidGeneratorClient.getClient()
            .lineIdGenerator()
            .generateLineIdsBatch()
            .partyIdQuery(lineIdBatch.getTopasPartyID())
            .numberLineIdsQuery(lineIdBatch.getTopasNumberLineIds())
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    assertEquals(response.getLineIds().size(), lineIdBatch.getTopasNumberLineIds().intValue());
  }

  @Test
  @TmsLink("DIGIHUB-34654")
  @Description("Invalid Number for Creation Pool Line Ids")
  public void failCreatePoolLineIdsOver() {
    lineidGeneratorClient.getClient()
            .lineIdGenerator()
            .generateLineIdsBatch()
            .partyIdQuery(lineIdBatch.getPartyID())
            .numberLineIdsQuery(lineIdBatch.getInvalidNumberLineIds())
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
  }

  @Test
  @TmsLink("DIGIHUB-34654")
  @Description("Invalid number for Creation Pool of Line Ids")
  public void failCreatePoolLineIdsMinus() {
    lineidGeneratorClient.getClient()
            .lineIdGenerator()
            .generateLineIdsBatch()
            .partyIdQuery(lineIdBatch.getPartyID())
            .numberLineIdsQuery(lineIdBatch.getNegativNumberLineIds())
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
  }

  @Test
  @TmsLink("DIGIHUB-34654")
  @Description("Invalid PartyID for Creation Pool Line Ids")
  public void failCreatePoolLineIdsEndSZ() {
    lineidGeneratorClient.getClient().lineIdGenerator().generateLineIdsBatch()
            .partyIdQuery(lineIdBatch.getNegativPartyId())
            .numberLineIdsQuery(lineIdBatch.getNumberLineIds())
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
  }
}
