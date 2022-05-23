package cucumber.stepdefinitions.domain;

import cucumber.stepdefinitions.team.berlinium.a4.ResInvServiceSteps;
import cucumber.stepdefinitions.team.berlinium.a4.resourceinventory.NetworkElementPortSteps;
import cucumber.stepdefinitions.team.berlinium.a4.resourceinventory.NetworkServiceProfileFtthAccessSteps;
import io.cucumber.java.en.Given;

public class DpuCommissioningSteps {

    private final NetworkElementPortSteps a4NepStes;
    private final NetworkServiceProfileFtthAccessSteps a4NspFtthAccessSteps;
    private final ResInvServiceSteps a4ResInvServiceSteps;

    public DpuCommissioningSteps(NetworkElementPortSteps a4NepStes,
                                 NetworkServiceProfileFtthAccessSteps a4NspFtthAccessSteps,
                                 ResInvServiceSteps a4ResInvServiceSteps) {
        this.a4NepStes = a4NepStes;
        this.a4NspFtthAccessSteps = a4NspFtthAccessSteps;
        this.a4ResInvServiceSteps = a4ResInvServiceSteps;
    }

    // -----=====[ GIVENS ]=====-----

    @Given("a DPU preprovisioning was done earlier")
    public void givenDpuPreprovisioningWasDoneEarlier() {
        // ACTION
        a4NepStes.givenANEPIsExistingInA4ResourceInventory();
        a4ResInvServiceSteps.whenNemoSendsACreateTPRequestWithType("PON_TP");
        a4NspFtthAccessSteps.thenTheNspFtthConnectedToTpDoesExistInA4ResourceInventory();
    }

}
