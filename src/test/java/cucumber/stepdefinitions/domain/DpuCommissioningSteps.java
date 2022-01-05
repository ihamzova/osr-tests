package cucumber.stepdefinitions.domain;

import cucumber.BaseSteps;
import cucumber.TestContext;
import cucumber.stepdefinitions.team.berlinium.A4ResInvServiceSteps;
import cucumber.stepdefinitions.team.berlinium.A4ResInvSteps;
import io.cucumber.java.en.Given;

public class DpuCommissioningSteps extends BaseSteps {

    private final A4ResInvSteps a4ResInvSteps;
    private final A4ResInvServiceSteps a4ResInvServiceSteps;

    public DpuCommissioningSteps(TestContext testContext) {
        super(testContext);

        a4ResInvSteps = new A4ResInvSteps(testContext);
        a4ResInvServiceSteps = new A4ResInvServiceSteps(testContext);
    }

    // -----=====[ GIVENS ]=====-----

    @Given("a DPU preprovisioning was done earlier")
    public void givenDpuPreprovisioningWasDoneEarlier() {
        // ACTION
        a4ResInvSteps.givenANEPIsExistingInA4ResourceInventory();
        a4ResInvServiceSteps.whenNemoSendsACreateTPRequestWithType("PON_TP");
        a4ResInvSteps.thenTheNspFtthConnectedToTpDoesExistInA4ResourceInventoryAnymore();
    }

}
