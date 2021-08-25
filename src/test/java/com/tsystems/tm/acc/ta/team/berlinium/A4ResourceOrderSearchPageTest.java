package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementlink.A4NetworkElementLinkCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.a4networkserviceprofilea10nsp.A4NetworkServiceProfileA10NspCase;
import com.tsystems.tm.acc.data.osr.models.a4terminationpoint.A4TerminationPointCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderDetailPageRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceOrderSearchPageRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.ResourceOrder;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.VUEP_PUBLIC_REFERENZ_NR;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.sleepForSeconds;
import static org.testng.Assert.assertEquals;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class A4ResourceOrderSearchPageTest extends GigabitTest {

    private final A4ResourceOrderSearchPageRobot a4ResourceOrderSearchPageRobot = new A4ResourceOrderSearchPageRobot();
    private final A4ResourceOrderDetailPageRobot a4ResourceOrderDetailPageRobot = new A4ResourceOrderDetailPageRobot();
    private final A4ResourceOrderRobot a4ResourceOrderRobot = new A4ResourceOrderRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();
    private final String DEFAULT_ORDER_ITEM_ID = "orderItemId" + getRandomDigits(4);
    private final String vuep = "A1000851";

    private A4NetworkElementGroup negData;
    private A4NetworkElement neData1;
    private A4NetworkElement neData2;
    private A4NetworkElement neData3;
    private A4NetworkElementPort nepData1;
    private A4NetworkElementPort nepData2;
    private A4NetworkElementPort nepData3;
    private A4NetworkElementLink nelData1;
    private A4NetworkElementLink nelData2;
    private A4NetworkServiceProfileA10Nsp nspA10Data1;
    private A4TerminationPoint tpData1;
    private UewegData uewegData1;
    private UewegData uewegData2;

    private ResourceOrder ro;


    @BeforeClass()
    public void init() {
        //Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        //setCredentials(loginData.getLogin(), loginData.getPassword());

        negData = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.NetworkElementGroupL2Bsa);

        neData1 = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementA10NspSwitch01);
        nepData1 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_100G_001);

        neData2 = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        nepData2 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_002);

        neData3 = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementB);
        nepData3 = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.networkElementPort_logicalLabel_10G_001);

        nelData1 = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.networkElementLinkLcsInstalling);
        nelData2 = osrTestContext.getData().getA4NetworkElementLinkDataProvider()
                .get(A4NetworkElementLinkCase.defaultNetworkElementLink);

        nspA10Data1 = osrTestContext.getData().getA4NetworkServiceProfileA10NspDataProvider()
                .get(A4NetworkServiceProfileA10NspCase.defaultNetworkServiceProfileA10Nsp);
        tpData1 = osrTestContext.getData().getA4TerminationPointDataProvider()
                .get(A4TerminationPointCase.defaultTerminationPointA10Nsp);

        uewegData1 = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.uewegA);
        uewegData2 = osrTestContext.getData().getUewegDataDataProvider()
                .get(UewegDataCase.uewegB);

        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());


        a4ResourceInventory.createNetworkElementGroup(negData);
        a4ResourceInventory.createNetworkElement(neData1, negData);
        a4ResourceInventory.createNetworkElement(neData2, negData);
        a4ResourceInventory.createNetworkElement(neData3, negData);
        a4ResourceInventory.createNetworkElementPort(nepData1, neData1);
        a4ResourceInventory.createNetworkElementPort(nepData2, neData2);
        a4ResourceInventory.createNetworkElementPort(nepData3, neData3);
        a4ResourceInventory.createNetworkElementLink(nelData1, nepData1, nepData2, neData1, neData2, uewegData1);
        a4ResourceInventory.createNetworkElementLink(nelData2, nepData1, nepData3, neData1, neData3, uewegData2);
        a4ResourceInventory.createTerminationPoint(tpData1, nepData1);
        a4ResourceInventory.createNetworkServiceProfileA10Nsp(nspA10Data1, tpData1);

        ro = a4ResourceOrderRobot.buildResourceOrder();

        a4ResourceOrderRobot.addOrderItemAdd(DEFAULT_ORDER_ITEM_ID, nelData1, ro);
        a4ResourceOrderRobot.setCharacteristicValue(VUEP_PUBLIC_REFERENZ_NR, vuep, DEFAULT_ORDER_ITEM_ID, ro);

        // WHEN
        a4ResourceOrderRobot.sendPostResourceOrder(ro);
        sleepForSeconds(10);
    }

    @AfterClass
    public void cleanUp() {
        a4ResourceInventory.deleteA4TestDataRecursively(negData);
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-116462")
    @Description("test RO search page of A4 browser")
    public void testRoSearchByVuep() throws InterruptedException {
        a4ResourceOrderSearchPageRobot.openRoSearchPage();
        a4ResourceOrderSearchPageRobot.enterRoVuep(vuep); // A1000851 560 Stück, A1000852 4 Stück,

        a4ResourceOrderSearchPageRobot.selectCompleted();
        a4ResourceOrderSearchPageRobot.selectInProgress();
        a4ResourceOrderSearchPageRobot.selectRejected();

        a4ResourceOrderSearchPageRobot.clickRoSearchButton();

        TimeUnit.SECONDS.sleep(15);  // wait for result

        // read ui
        ElementsCollection elementsCollection = a4ResourceOrderSearchPageRobot.getRoElementsCollection();
        System.out.println("+++ number of ROs in UI : "+elementsCollection.size()/6);     // 6 Felder pro Eintrag
        //System.out.println("+++ Tabelle in UI : "+elementsCollection);

        // get ROs from DB
        List<ResourceOrderDto> allRoList = a4ResourceOrderRobot.getResourceOrderListByVuepFromDb(vuep);
        System.out.println("+++ number of ROs in DB : "+allRoList.size());

       // assertEquals(elementsCollection.size()/6,allRoList.size());

        a4ResourceOrderSearchPageRobot.clickFirstRowInSearchResultTable();
        TimeUnit.SECONDS.sleep(5);  // wait for looking

        System.out.println("+++ VUEP in Detail-Page : "+a4ResourceOrderDetailPageRobot.readVuep());

    }

}
