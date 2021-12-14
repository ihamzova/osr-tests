package cucumber.stepdefinitions.team.upiter;

import com.tsystems.tm.acc.data.upiter.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.data.osr.models.PortProvisioning;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WgA4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.v1_9_0.client.model.TpRefDto;
import cucumber.BaseSteps;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndsz;
import static org.testng.Assert.assertEquals;

public class WgA4ProvisioningSteps extends BaseSteps {

    final String SLOT_NUMBER = "99";

    private final WgA4PreProvisioningRobot wgA4PreProvisioningRobot = new WgA4PreProvisioningRobot();
    private final AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private final UpiterTestContext context = UpiterTestContext.get();

    public WgA4ProvisioningSteps(TestContext testContext) {
        super(testContext);
    }

    @Before
    public void setup() {
        cleanup(); // Make sure no old test data is in the way
        accessLineRiRobot.fillDatabaseForOltCommissioningV2(1, 1);
    }

    @After
    public void cleanup() {
        accessLineRiRobot.clearDatabase();
    }

    @Given("an access line exists in U-Piter access line inventory for the TP")
    public void anAccessLineExistsInUPiterAccessLineInventoryForTheTp() {
        final int PARTY_ID = 10001;

        A4TerminationPoint tp = (A4TerminationPoint) getScenarioContext().getContext(Context.A4_TP);
        A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);
        A4NetworkElement ne = (A4NetworkElement) getScenarioContext().getContext(Context.A4_NE);

        // Data set in U-Piter WgA4Provisioning tests:

        // NE:
        //value.setVpsz("49/30/179");
        //value.setFsz("7KH0");
        //value.setKlsId("123456");

        // NEP:
        //value.setSlotNumber("1");
        //value.setPortNumber("0");

        // Data set in A4 Berlinium tests:
        //...

        TpRefDto tpRef = new TpRefDto()
                .endSz(getEndsz(ne))
                .slotNumber(SLOT_NUMBER)
                .portNumber(nep.getFunctionalPortLabel())
                .klsId(ne.getKlsId())
                .tpRef(tp.getUuid())
                .partyId((long) PARTY_ID);

//        getScenarioContext().setContext(Context.TP_REF, tpRef);
        wgA4PreProvisioningRobot.startPreProvisioning(tpRef);
    }

    @Then("the access line does not exist in U-Piter access line inventory anymore")
    public void theAccessLineDoesNotExistInUPiterAccessLineInventoryAnymore() {
        PortProvisioning a4PortDeprovisioning = context.getData().getPortProvisioningDataProvider().get(PortProvisioningCase.A4PortForDeprovisioning);

        A4NetworkElementPort nep = (A4NetworkElementPort) getScenarioContext().getContext(Context.A4_NEP);
        A4NetworkElement ne = (A4NetworkElement) getScenarioContext().getContext(Context.A4_NE);

        a4PortDeprovisioning.setEndSz(getEndsz(ne));
        a4PortDeprovisioning.setSlotNumber(SLOT_NUMBER);
        a4PortDeprovisioning.setPortNumber(nep.getFunctionalPortLabel());

//        assertEquals(accessLineRiRobot.getAccessLinesByPort(a4PortDeprovisioning).size(), 1, "There are > 1 AccessLines on the port");

//        wgA4PreProvisioningRobot.startAccessLineDeprovisioning(a4AccessLine.getLineId());

        accessLineRiRobot.checkPhysicalResourceRefCountA4(a4PortDeprovisioning, 0);
        assertEquals(accessLineRiRobot.getAccessLinesByPort(a4PortDeprovisioning).size(), 0, "There are AccessLines left on the port");
        accessLineRiRobot.checkHomeIdsCount(a4PortDeprovisioning);
        accessLineRiRobot.checkLineIdsCount(a4PortDeprovisioning);

        // check state of AL == ??
        // number of AL == 0
    }

}
