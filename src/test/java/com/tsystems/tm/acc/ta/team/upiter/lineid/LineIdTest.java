package com.tsystems.tm.acc.ta.team.upiter.lineid;

import com.tsystems.tm.acc.ta.api.osr.LineIdGeneratorClient;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.line.id.generator.v2_1_0.client.model.SingleLineId;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.LINE_ID_GENERATOR_MS;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static org.testng.Assert.assertNotNull;

@ServiceLog(LINE_ID_GENERATOR_MS)

@Epic("LineId Generator")
public class LineIdTest extends GigabitTest {

    private LineIdGeneratorClient lineidGeneratorClient;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        lineidGeneratorClient = new LineIdGeneratorClient();
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 1 Line Id")
    public void createSingleLineId() {
        SingleLineId response = lineidGeneratorClient.getClient().lineIdGenerator()
                .generateLineId()
                .partyIdQuery("10001")
                .executeAs(checkStatus(HTTP_CODE_CREATED_201));
        assertNotNull(response);
    }

    @Test
    @TmsLink("DIGIHUB-34654")
    @Description("Create 1 Line Id TOPAS")
    public void createSingleLineIdTopas() {
        SingleLineId response = lineidGeneratorClient.getClient()
                .lineIdGenerator()
                .generateLineId()
                .partyIdQuery("10000")
                .executeAs(checkStatus(HTTP_CODE_CREATED_201));
        assertNotNull(response);
    }
}
