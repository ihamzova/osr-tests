package com.tsystems.tm.acc.ta.team.upiter.homeid;

import com.tsystems.tm.acc.data.upiter.models.homeidbatch.HomeIdBatchCase;
import com.tsystems.tm.acc.ta.api.osr.HomeIdGeneratorClient;
import com.tsystems.tm.acc.ta.data.osr.models.HomeIdBatch;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.tests.osr.home.id.generator.internal.client.model.PoolHomeId;
import com.tsystems.tm.acc.tests.osr.home.id.generator.internal.client.model.SingleHomeId;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_BAD_REQUEST_400;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.HOME_ID_GENERATOR_MS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@ServiceLog(HOME_ID_GENERATOR_MS)
public class HomeIdTest extends BaseTest {

    private HomeIdGeneratorClient homeIdGeneratorClient;
    private HomeIdBatch homeIdBatch;
    private UpiterTestContext context = UpiterTestContext.get();

    @BeforeClass
    public void init() {
        homeIdGeneratorClient = new HomeIdGeneratorClient();
        homeIdBatch = context.getData().getHomeIdBatchDataProvider().get(HomeIdBatchCase.homeIdBatch);
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
}
