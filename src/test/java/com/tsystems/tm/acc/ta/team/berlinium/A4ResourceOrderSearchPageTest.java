package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderSearchPageRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class A4ResourceOrderSearchPageTest extends GigabitTest {

    private final A4ResourceOrderSearchPageRobot a4ResourceOrderSearchPageRobot = new A4ResourceOrderSearchPageRobot();
    private final A4ResourceOrderRobot a4ResourceOrderRobot = new A4ResourceOrderRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();

    @BeforeClass()
    public void init() {
        //Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        //setCredentials(loginData.getLogin(), loginData.getPassword());

        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

    }

    @AfterClass
    public void cleanUp() {

    }


    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser")
    public void testRoSearchByVuep() throws InterruptedException {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.enterRoVuep("A1000852"); // A1000851 560 Stück, A1000852 4 Stück,
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();

        TimeUnit.SECONDS.sleep(10);  // wait for result

        // read ui
        ElementsCollection elementsCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : "+elementsCollection.size()/6);     // 6 Felder pro Eintrag

        // get ROs from DB
        List<ResourceOrderDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb("A1000852");
        System.out.println("+++ number of ROs in DB : "+allRoList.size());

        assertEquals(elementsCollection.size()/6,allRoList.size());

    }

}
