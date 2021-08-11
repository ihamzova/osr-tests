package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderSearchPageRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class A4ResourceOrderSearchPageTest extends GigabitTest {

    private final A4ResourceOrderSearchPageRobot a4ResourceOrderSearchPageRobot = new A4ResourceOrderSearchPageRobot();
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
        a4ResourceOrderSearchPageRobot.enterRoVuep("A1000851");
        a4ResourceOrderSearchPageRobot.clickRoSearchButton();

        TimeUnit.SECONDS.sleep(10);  // wait for result




/*
        // read ui
        //ElementsCollection elementsCollection = a4ResourceOrderSearchPageRobot.getNeElementsCollection();
        //log.info("+++ Anzahl NEs in UI : "+elementsCollection.size()/12);     // 12 Felder pro Eintrag

        // get all NEs from DB
        //List<NetworkElementDto> allNeList = a4ResourceInventoryRobot.getExistingNetworkElementAll();
        //log.info("+++ Anzahl NEs in DB : "+allNeList.size());  //


        // create expected result
        List<NetworkElementDto> neFilteredList;
        neFilteredList = allNeList
                .stream()
                .filter(group -> group.getVpsz().equals("49/9715/0") && group.getCategory().equals("LEAF_SWITCH") )
                .collect(Collectors.toList());
        //log.info("+++ Anzahl NEs in Filterliste : "+neFilteredList.size());

        // sort
        neFilteredList = neFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++neFilteredList : "+neFilteredList.size());

        // create actual result
        List<NetworkElementDto> neActualResultList = createNeListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNeList (neFilteredList, neActualResultList, elementsCollection.size());

         */
    }

}
