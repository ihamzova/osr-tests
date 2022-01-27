package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementGroupDto;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot.numberOfColumnsNeList;
import static com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot.numberOfColumnsNegList;
import static org.testng.Assert.assertEquals;

@Slf4j
@ServiceLog({A4_RESOURCE_INVENTORY_MS, A4_RESOURCE_INVENTORY_UI_MS, A4_RESOURCE_INVENTORY_BFF_PROXY_MS, A4_INVENTORY_IMPORTER_MS})
@Epic("OS&R")

public class A4InventarSuchePageTest extends GigabitTest {
    private final A4InventarSucheRobot a4InventarSucheRobot = new A4InventarSucheRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private A4NetworkElementGroup a4NetworkElementGroup;

    // helper 'compare ne'
    public void compareExpectedResultWithActualResultNeList(List<NetworkElementDto> neFilteredList,
                                                            List<NetworkElementDto> neActualResultList,
                                                            int elementsCollectionSize) {
        for (int i = 0; i < elementsCollectionSize / numberOfColumnsNeList; i++) {
            assertEquals(neFilteredList.get(i).getUuid(), neActualResultList.get(i).getUuid());
            assertEquals(neFilteredList.get(i).getVpsz(), neActualResultList.get(i).getVpsz());
            assertEquals(neFilteredList.get(i).getFsz(), neActualResultList.get(i).getFsz());
            assertEquals(neFilteredList.get(i).getCategory(), neActualResultList.get(i).getCategory());
            assertEquals(neFilteredList.get(i).getType(), neActualResultList.get(i).getType());
            //assertEquals(neFilteredList.get(i).getZtpIdent(), neActualResultList.get(i).getZtpIdent()); // null
            assertEquals(neFilteredList.get(i).getKlsId(), neActualResultList.get(i).getKlsId());
            assertEquals(neFilteredList.get(i).getPlanningDeviceName(), neActualResultList.get(i).getPlanningDeviceName());
            assertEquals(neFilteredList.get(i).getLifecycleState(), neActualResultList.get(i).getLifecycleState());
            assertEquals(neFilteredList.get(i).getOperationalState(), neActualResultList.get(i).getOperationalState());
            assertEquals(neFilteredList.get(i).getCreationTime().toString(), neActualResultList.get(i).getCreationTime().toString());
            assertEquals(neFilteredList.get(i).getLastUpdateTime().toString(), neActualResultList.get(i).getLastUpdateTime().toString());
            // log.info("+++ uuid: "+neActualResultList.get(i).getUuid());
        }
    }

    // helper 'compare neg'
    public void compareExpectedResultWithActualResultNegList(List<NetworkElementGroupDto> negFilteredList,
                                                             List<NetworkElementGroupDto> negActualResultList,
                                                             int elementsCollectionSize) {
        for (int i = 0; i < elementsCollectionSize / numberOfColumnsNegList; i++) {
            assertEquals(negFilteredList.get(i).getUuid(), negActualResultList.get(i).getUuid());
            assertEquals(negFilteredList.get(i).getName(), negActualResultList.get(i).getName());
            assertEquals(negFilteredList.get(i).getLifecycleState(), negActualResultList.get(i).getLifecycleState());
            assertEquals(negFilteredList.get(i).getOperationalState(), negActualResultList.get(i).getOperationalState());
            assertEquals(negFilteredList.get(i).getCreationTime().toString(), negActualResultList.get(i).getCreationTime().toString());
            assertEquals(negFilteredList.get(i).getLastUpdateTime().toString(), negActualResultList.get(i).getLastUpdateTime().toString());
            // log.info("+++uuid: "+negActualResultList.get(i).getUuid());
        }
    }

    @BeforeClass()
    public void init() {
        //Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        //setCredentials(loginData.getLogin(), loginData.getPassword());

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());

        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);
    }

    @AfterClass
    public void cleanUp() {
        a4ResourceInventoryRobot.deleteA4TestDataRecursively(a4NetworkElementGroup);
    }

    // tests network element
    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-96766")
    @Description("test ne inventory search page of A4 browser")
    public void testNeSearchByVpszNotWorkingInstalling() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElement();
        // a4InventarSucheRobot.enterNeVpsz("49/40/104"); // nicht notwendig, wird aus den nächsten Zeilen befüllt,  49/40/104
        a4InventarSucheRobot.enterNeAkz("49");     // 49,
        a4InventarSucheRobot.enterNeOnkz("30");    // dev-01: 40,  dev-03: 30
        a4InventarSucheRobot.enterNeVkz("13");    // dev-01: 104,  dev-03: 13
        //a4InventarSucheRobot.enterNeFsz("7KDA");   // nicht unbedingt notwendig,  7KDA

        // value=<leer>, OLT, LEAF_SWITCH, SPINE_SWITCH, POD_SERVER, BOR
        a4InventarSucheRobot.enterNeCategory("OLT");  // dropdown mit selectOptionByValue
        a4InventarSucheRobot.checkboxNotWorking();
        a4InventarSucheRobot.checkboxLifeInstalling();

        a4InventarSucheRobot.clickNeSearchButton();

        //Thread.sleep(5000);

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNeElementsCollection();
        //log.info("+++ Anzahl NEs in UI : "+elementsCollection.size()/12);     // 12 Felder pro Eintrag

        // get all NEs from DB
        List<NetworkElementDto> allNeList = a4ResourceInventoryRobot.getExistingNetworkElementAll();
        //log.info("+++ Anzahl NEs in DB : "+allNeList.size());  //

        // create expected result
        List<NetworkElementDto> neFilteredList;
        neFilteredList = allNeList
                .stream()
                .filter(group -> group.getOperationalState().equals("NOT_WORKING") && group.getLifecycleState().equals("INSTALLING"))
                .collect(Collectors.toList());
        //log.info("+++ Anzahl NEs in Filterliste : "+neFilteredList.size());

        // sort
        neFilteredList = neFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++neFilteredList : "+neFilteredList.size());

        // create actual result
        List<NetworkElementDto> neActualResultList = a4InventarSucheRobot.createNeListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNeList(neFilteredList, neActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-96766")
    @Description("test ne inventory search page of A4 browser")
    public void testNeSearchFszLeafSwitch() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElement();
        // a4InventarSucheRobot.enterNeVpsz("49/40/104"); // nicht notwendig, wird aus den nächsten Zeilen befüllt,  49/40/104
        a4InventarSucheRobot.enterNeAkz("49");     // 49,
        a4InventarSucheRobot.enterNeOnkz("9715");    // dev-01: 40,  dev-03: 30
        a4InventarSucheRobot.enterNeVkz("0");    // dev-01: 104,  dev-03: 13
        a4InventarSucheRobot.enterNeFsz("7KE0");   // nicht unbedingt notwendig,  7KDA

        // value=<leer>, OLT, LEAF_SWITCH, SPINE_SWITCH, POD_SERVER, BOR
        a4InventarSucheRobot.enterNeCategory("LEAF_SWITCH");  // dropdown mit selectOptionByValue

        a4InventarSucheRobot.clickNeSearchButton();

        //Thread.sleep(5000);

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNeElementsCollection();
        //log.info("+++ Anzahl NEs in UI : "+elementsCollection.size()/12);     // 12 Felder pro Eintrag

        // get all NEs from DB
        List<NetworkElementDto> allNeList = a4ResourceInventoryRobot.getExistingNetworkElementAll();
        //log.info("+++ Anzahl NEs in DB : "+allNeList.size());  //

        // create expected result
        List<NetworkElementDto> neFilteredList;
        neFilteredList = allNeList
                .stream()
                .filter(group -> group.getVpsz().equals("49/9715/0") && group.getCategory().equals("LEAF_SWITCH"))
                .collect(Collectors.toList());
        //log.info("+++ Anzahl NEs in Filterliste : "+neFilteredList.size());

        // sort
        neFilteredList = neFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++neFilteredList : "+neFilteredList.size());

        // create actual result
        List<NetworkElementDto> neActualResultList = a4InventarSucheRobot.createNeListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNeList(neFilteredList, neActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-96766")
    @Description("test ne inventory search page of A4 browser")
    public void testNeSearchVpszAllCheckboxes() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElement();
        // a4InventarSucheRobot.enterNeVpsz("49/40/104"); // nicht notwendig, wird aus den nächsten Zeilen befüllt,  49/40/104
        a4InventarSucheRobot.enterNeAkz("49");     // 49,
        a4InventarSucheRobot.enterNeOnkz("9715");    // dev-01: 40,  dev-03: 30
        a4InventarSucheRobot.enterNeVkz("0");    // dev-01: 104,  dev-03: 13
        //a4InventarSucheRobot.enterNeFsz("7KE0");   // nicht unbedingt notwendig,  7KDA

        // value=<leer>, OLT, LEAF_SWITCH, SPINE_SWITCH, POD_SERVER, BOR
        //a4InventarSucheRobot.enterNeCategory("LEAF_SWITCH");  // dropdown mit selectOptionByValue

        a4InventarSucheRobot.checkboxOpInstalling();
        a4InventarSucheRobot.checkboxFailed();
        a4InventarSucheRobot.checkboxWorking();
        a4InventarSucheRobot.checkboxNotWorking();
        a4InventarSucheRobot.checkboxPlanning();
        a4InventarSucheRobot.checkboxLifeInstalling();
        a4InventarSucheRobot.checkboxOperating();
        a4InventarSucheRobot.checkboxRetiring();

        a4InventarSucheRobot.clickNeSearchButton();

        //Thread.sleep(5000);

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNeElementsCollection();
        //log.info("+++ Anzahl NEs in UI : "+elementsCollection.size()/12);     // 12 Felder pro Eintrag

        // get all NEs from DB
        List<NetworkElementDto> allNeList = a4ResourceInventoryRobot.getExistingNetworkElementAll();
        //log.info("+++ Anzahl NEs in DB : "+allNeList.size());  //

        // create expected result
        List<NetworkElementDto> neFilteredList;
        neFilteredList = allNeList
                .stream()
                .filter(group -> group.getVpsz().equals("49/9715/0"))
                .collect(Collectors.toList());
        //log.info("+++ Anzahl NEs in Filterliste : "+neFilteredList.size());

        // sort
        neFilteredList = neFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++neFilteredList : "+neFilteredList.size());

        // create actual result
        List<NetworkElementDto> neActualResultList = a4InventarSucheRobot.createNeListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNeList(neFilteredList, neActualResultList, elementsCollection.size());
    }

    // tests neg
    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchWorkingOpsInstalling() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxWorking();
        a4InventarSucheRobot.checkboxOpInstalling();
        a4InventarSucheRobot.clickNegSearchButton();

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNegElementsCollection();

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("WORKING") || group.getOperationalState().equals("INSTALLING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = a4InventarSucheRobot.createNegListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNegList(negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchWorkingOperating() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxWorking();
        a4InventarSucheRobot.checkboxOperating();
        a4InventarSucheRobot.clickNegSearchButton();

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNegElementsCollection();

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("WORKING") && group.getLifecycleState().equals("OPERATING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = a4InventarSucheRobot.createNegListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNegList(negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchWorkingLcsInstalling() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxWorking();
        a4InventarSucheRobot.checkboxLifeInstalling();
        a4InventarSucheRobot.clickNegSearchButton();

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNegElementsCollection();

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("WORKING") && group.getLifecycleState().equals("INSTALLING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = a4InventarSucheRobot.createNegListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNegList(negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchNotWorkingPlanning() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxNotWorking();
        a4InventarSucheRobot.checkboxPlanning();
        a4InventarSucheRobot.clickNegSearchButton();

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNegElementsCollection();

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("NOT_WORKING") && group.getLifecycleState().equals("PLANNING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = a4InventarSucheRobot.createNegListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNegList(negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchNotManageableRetiring() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.checkboxNotManageable();
        a4InventarSucheRobot.checkboxRetiring();
        a4InventarSucheRobot.clickNegSearchButton();

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNegElementsCollection();

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getOperationalState().equals("NOT_MANAGEABLE") && group.getLifecycleState().equals("RETIRING"))
                .collect(Collectors.toList());
        // sort
        negFilteredList = negFilteredList
                .stream().sorted(Comparator.comparing(NetworkElementGroupDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++negFilteredList : "+negFilteredList.size());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = a4InventarSucheRobot.createNegListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNegList(negFilteredList, negActualResultList, elementsCollection.size());
    }

    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-94403")
    @Description("test neg inventory search page of Access 4.0 browser")
    public void testNegSearchByName() {
        a4InventarSucheRobot.openInventarSuchePage();
        a4InventarSucheRobot.clickNetworkElementGroup();
        a4InventarSucheRobot.enterNegName(a4NetworkElementGroup.getName());  // default: dev-01: NEG-367326, dev-03: NEG-130568, NEG-656487
        //a4InventarSucheRobot.enterNegName("NEG130568");
        a4InventarSucheRobot.clickNegSearchButton();

        // read ui
        ElementsCollection elementsCollection = a4InventarSucheRobot.getNegElementsCollection();

        // get all NEGs from DB
        List<NetworkElementGroupDto> allNegList = a4ResourceInventoryRobot.getExistingNetworkElementGroupAll();

        // create expected result
        List<NetworkElementGroupDto> negFilteredList;
        negFilteredList = allNegList
                .stream()
                .filter(group -> group.getName().equals(a4NetworkElementGroup.getName()))
                .collect(Collectors.toList());

        // create actual result
        List<NetworkElementGroupDto> negActualResultList = a4InventarSucheRobot.createNegListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNegList(negFilteredList, negActualResultList, elementsCollection.size());
    }
}
