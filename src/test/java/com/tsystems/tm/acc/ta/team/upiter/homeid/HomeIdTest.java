package com.tsystems.tm.acc.ta.team.upiter.homeid;

import com.tsystems.tm.acc.home.id.generator.client.model.PoolHomeId;
import com.tsystems.tm.acc.home.id.generator.client.model.SingleHomeId;
import com.tsystems.tm.acc.ta.api.HomeIdGeneratorClient;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class HomeIdTest extends ApiTest {

    private HomeIdGeneratorClient api;

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

    @Test
    @TmsLink("")
    @Description("Create 7 Home Ids")
    public void createPoolOfSevenHomeIds()  {
        PoolHomeId response = api.getClient().homeIdGeneratorController().generateBatch().numberHomeIdsQuery(7).executeAs(validatedWith(shouldBeCode(201)));
        assertEquals(response.getHomeIds().size(),7);
    }
}
