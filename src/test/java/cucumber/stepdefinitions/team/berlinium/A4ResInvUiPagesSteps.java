package cucumber.stepdefinitions.team.berlinium;

import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.robot.osr.A4InventarSucheRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkElementDto;
import cucumber.Context;
import cucumber.TestContext;
import cucumber.stepdefinitions.BaseSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class A4ResInvUiPagesSteps extends BaseSteps {

    private final A4InventarSucheRobot a4InventarSuche = new A4InventarSucheRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();

    final int numberOfColumnsNeList = 12;

    public A4ResInvUiPagesSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void init() {
    }

    @After
    public void cleanup() {
    }

    @Given("user {string} is logged in to a4-resource-inventory-ui with password {string}")
    public void userIsLoggedInToA4ResourceInventoryUiWithPassword(String user, String password) {
//        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
//        setCredentials(user, password);

//        System.setProperty("webdriver.chrome.driver", "E://Selenium//Selenium_Jars//geckodriver.exe");

        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    @When("the user opens NE search page")
    public void userOpensNESearchPage() {
        a4InventarSuche.openInventarSuchePage();
        a4InventarSuche.clickNetworkElement();
    }

    @When("enters VPSZ {string} and FSZ {string} into the input fields")
    public void entersVPSZAndFSZIntoFields(String vpsz, String fsz) {
        a4InventarSuche.enterNeAkzByVpsz(vpsz);
        a4InventarSuche.enterNeOnkzByVpsz(vpsz);
        a4InventarSuche.enterNeVkzByVpsz(vpsz);
        a4InventarSuche.enterNeFsz(fsz);
    }

    @When("clicks the submit button")
    public void clicksSubmitButton() {
        a4InventarSuche.clickNeSearchButton();
    }

    @Then("the wanted NE is shown in the search result table")
    public void userGetsSearchResultsInTable() {
        ElementsCollection elementsCollection = a4InventarSuche.getNeElementsCollection();
        //log.info("+++ Anzahl NEs in UI : "+elementsCollection.size()/12);     // 12 Felder pro Eintrag

        // get all NEs from DB
//        List<NetworkElementDto> allNeList = a4ResourceInventoryRobot.getExistingNetworkElementAll();

        A4NetworkElement ne = (A4NetworkElement) getScenarioContext().getContext(Context.A4_NE);
        String uuid = ne.getUuid();
        NetworkElementDto neDto = a4ResourceInventoryRobot.getExistingNetworkElement(uuid);
        List<NetworkElementDto> allNeList = new ArrayList<>();
        allNeList.add(neDto);

        //log.info("+++ Anzahl NEs in DB : "+allNeList.size());  //

        // create expected result
        List<NetworkElementDto> neFilteredList;
        neFilteredList = allNeList
                .stream()
//                .filter(group -> group.getOperationalState().equals("NOT_WORKING") && group.getLifecycleState().equals("INSTALLING"))
                .collect(Collectors.toList());
        //log.info("+++ Anzahl NEs in Filterliste : "+neFilteredList.size());

        // sort
        neFilteredList = neFilteredList
                .stream()
                .sorted(Comparator.comparing(NetworkElementDto::getUuid))
                .collect(Collectors.toList());
        //log.info("+++neFilteredList : "+neFilteredList.size());

        // create actual result
        List<NetworkElementDto> neActualResultList = createNeListActualResult(elementsCollection);

        // compare, expected and actual result
        compareExpectedResultWithActualResultNeList(neFilteredList, neActualResultList, elementsCollection.size());
//        compareExpectedResultWithActualResultNeList(neActualResultList, elementsCollection.size());
    }

    // helper 'createActualResult NE'
    public List<NetworkElementDto> createNeListActualResult(ElementsCollection elementsCollection) {

        // create empty list
        List<NetworkElementDto> neActualResultList = new ArrayList<>();
        for (int i = 0; i < elementsCollection.size() / numberOfColumnsNeList; i++) {
            NetworkElementDto neActualGeneric = new NetworkElementDto();
            neActualResultList.add(neActualGeneric);
        }
        //  log.info("+++ neActualResultList: "+neActualResultList.size());

        // read table from ui and fill list (actual result)
        for (int i = 0; i < elementsCollection.size() / numberOfColumnsNeList; i++) {
            neActualResultList.get(i).setUuid(elementsCollection.get(i * numberOfColumnsNeList).getText());
            neActualResultList.get(i).setVpsz(elementsCollection.get(i * numberOfColumnsNeList + 1).getText());
            neActualResultList.get(i).setFsz(elementsCollection.get(i * numberOfColumnsNeList + 2).getText());
            neActualResultList.get(i).setCategory(elementsCollection.get(i * numberOfColumnsNeList + 3).getText());
            neActualResultList.get(i).setType(elementsCollection.get(i * numberOfColumnsNeList + 4).getText());
            neActualResultList.get(i).setZtpIdent(elementsCollection.get(i * numberOfColumnsNeList + 5).getText());
            neActualResultList.get(i).setKlsId(elementsCollection.get(i * numberOfColumnsNeList + 6).getText());
            neActualResultList.get(i).setPlanningDeviceName(elementsCollection.get(i * numberOfColumnsNeList + 7).getText());
            neActualResultList.get(i).setOperationalState(elementsCollection.get(i * numberOfColumnsNeList + 8).getText());
            neActualResultList.get(i).setLifecycleState(elementsCollection.get(i * numberOfColumnsNeList + 9).getText());
            OffsetDateTime creationTime = OffsetDateTime.parse(elementsCollection.get(i * numberOfColumnsNeList + 10).getText());
            OffsetDateTime lastUpdateTime = OffsetDateTime.parse(elementsCollection.get(i * numberOfColumnsNeList + 11).getText());
            neActualResultList.get(i).setCreationTime(creationTime); // wegen Formatproblem String-OffsetDateTime
            neActualResultList.get(i).setLastUpdateTime(lastUpdateTime); // wegen Formatproblem String-OffsetDateTime
            // log.info("+++ uuid: "+neActualResultList.get(i).getUuid());
        }
        // sort
        neActualResultList = neActualResultList
                .stream().sorted(Comparator.comparing(NetworkElementDto::getUuid))
                .collect(Collectors.toList());
        return neActualResultList;
    }

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

}
