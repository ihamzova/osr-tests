package cucumber.stepdefinitions.domain;

import cucumber.stepdefinitions.team.berlinium.A4ResInvServiceSteps;
import cucumber.stepdefinitions.team.berlinium.A4ResInvSteps;
import io.cucumber.java.en.Given;

public class DpuCommissioningSteps {

    private final A4ResInvSteps a4ResInvSteps;
    private final A4ResInvServiceSteps a4ResInvServiceSteps;

    public DpuCommissioningSteps(A4ResInvSteps a4ResInvSteps, A4ResInvServiceSteps a4ResInvServiceSteps) {
        this.a4ResInvSteps = a4ResInvSteps;
        this.a4ResInvServiceSteps = a4ResInvServiceSteps;
    }

    // -----=====[ GIVENS ]=====-----

    @Given("a DPU preprovisioning was done earlier")
    public void givenDpuPreprovisioningWasDoneEarlier() {
        // ACTION
        a4ResInvSteps.givenANEPIsExistingInA4ResourceInventory();
        a4ResInvServiceSteps.whenNemoSendsACreateTPRequestWithType("PON_TP");
        a4ResInvSteps.thenTheNspFtthConnectedToTpDoesExistInA4ResourceInventory();
    }

}
