package cucumber.stepdefinitions.team.berlinium.a4.resourceinventory;

import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.A10NspQosDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.NetworkServiceProfileA10NspDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.TerminationPointDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.model.VlanRangeDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.*;
import cucumber.Context;
import cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.testng.Assert;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.DEFAULT;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.isNullOrEmpty;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class NetworkServiceProfileA10NspSteps {

    private final A4ResourceInventoryRobot a4ResInv;
    private final A4ResourceInventoryMapper a4ResInvMapper;
    private final TerminationPointSteps a4TpSteps;
    private final TestContext testContext;

    public NetworkServiceProfileA10NspSteps(TestContext testContext,
                                            A4ResourceInventoryRobot a4ResInv,
                                            A4ResourceInventoryMapper a4ResInvMapper,
                                            TerminationPointSteps a4TpSteps) {
        this.testContext = testContext;
        this.a4ResInv = a4ResInv;
        this.a4ResInvMapper = a4ResInvMapper;
        this.a4TpSteps = a4TpSteps;
    }


    // -----=====[ GIVENS ]=====-----

    @Given("a NSP A10NSP with operationalState {string} and lifecycleState {string}( connected to the TP)( is existing)( in A4 resource inventory)")
    public void givenNspA10nspWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState) {
        createNspA10NspWithStates(DEFAULT, operationalState, lifecycleState, DEFAULT);
    }

    @Given("a/another NSP A10NSP with itAccountingKey {string} and NetworkElementLinkUuid {string}( is existing)( in A4 resource inventory)")
    public void givenNspA10nspWithItAccountingKeyAndNetworkElementLinkUuid(String itAccountingKey, String networkElementLinkUuid) {
        createNspA10NspWithItAccountingKeyAndNelUuid(DEFAULT, itAccountingKey, networkElementLinkUuid, DEFAULT);
    }

    @Given("a/another NSP A10NSP {string} with itAccountingKey {string} and NetworkElementLinkUuid {string}( is existing)( in A4 resource inventory)")
    public void givenNspA10nspWithItAccountingKeyAndNetworkElementLinkUuid(String nspAlias, String itAccountingKey, String networkElementLinkUuid) {
        createNspA10NspWithItAccountingKeyAndNelUuid(nspAlias, itAccountingKey, networkElementLinkUuid, DEFAULT);
    }

    @Given("a NSP A10NSP {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspA10nsp(String nspAlias, String tpAlias) {
        createNspA10Nsp(nspAlias, tpAlias);
    }

    @Given("a NSP A10NSP {string} with vlanRangeLower {string} and vlanRangeUpper {string} and lifeCycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspA10nsp(String nspAlias, String vlanRangeLower, String vlanRangeUpper, String lcState, String tpAlias) {
        createNspA10NspWithVlanRangeAndLcState(nspAlias, vlanRangeLower, vlanRangeUpper, lcState, tpAlias);
    }

    @Given("no NSP A10NSP( connected to the TP)( exists)( in A4 resource inventory)")
    public void givenNoNspA10NspExistsInA4ResourceInventoryForTheTP() {
        NetworkServiceProfileA10NspDto nspA10Nsp = new NetworkServiceProfileA10NspDto();
        nspA10Nsp.setUuid(UUID.randomUUID().toString());

        // Make sure the NSP really doesn't exist
        a4ResInv.deleteNetworkServiceProfileA10NspWithoutCheck(nspA10Nsp.getUuid());

        testContext.getScenarioContext().setContext(Context.A4_NSP_A10NSP, nspA10Nsp);
    }

    @Given("a/another NSP A10NSP with operationalState {string} and lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspA10nspWithLineIDIsExistingInA4ResourceInventoryForTheTP(String operationalState, String lifecycleState, String tpAlias) {
        createNspA10NspWithStates(DEFAULT, operationalState, lifecycleState, tpAlias);
    }

    @Given("a/another NSP A10NSP {string} with operationalState {string} and lifecycleState {string} connected to TP {string}( is existing)( in A4 resource inventory)")
    public void givenNspA10nspWithLineIDIsExistingInA4ResourceInventoryForTheTP(String nspAlias, String operationalState, String lifecycleState, String tpAlias) {
        createNspA10NspWithStates(nspAlias, operationalState, lifecycleState, tpAlias);
    }


    // -----=====[ THENS ]=====-----

    @Then("the (new )NSP A10NSP operationalState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspA10nspOperationalStateIsUpdatedInA4ResInv(String operationalState) {
        final NetworkServiceProfileA10NspDto nspA10nspData = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());

        assertEquals(operationalState, nspA10nsp.getOperationalState());
    }

    @Then("the (new )NSP A10NSP lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheNspA10nspLifecycleStateIsUpdatedInA4ResInv(String lifecycleState) {
        final NetworkServiceProfileA10NspDto nspA10nspData = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());

        assertEquals(lifecycleState, nspA10nsp.getLifecycleState());
    }

    @Then("the (new )A10NSP {string} lifecycleState is (now )(updated to )(still ){string}( in the A4 resource inventory)")
    public void thenTheA10nspLifecycleStateIsUpdatedInA4ResInv(String nspAlias, String lifecycleState) {
        final NetworkServiceProfileA10NspDto nspA10nspData = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP, nspAlias);
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());

        assertEquals(lifecycleState, nspA10nsp.getLifecycleState());
    }


    @Then("the NSP A10NSP lastUpdateTime is updated")
    public void thenTheNspA10nspLastUpdateTimeIsUpdated() {
        final NetworkServiceProfileA10NspDto nspA10nspData = (NetworkServiceProfileA10NspDto) testContext.getScenarioContext().getContext(Context.A4_NSP_A10NSP);
        final OffsetDateTime oldDateTime = (OffsetDateTime) testContext.getScenarioContext().getContext(Context.TIMESTAMP);
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv.getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());

        assertNotNull(nspA10nsp.getLastUpdateTime());
        assertTrue(nspA10nsp.getLastUpdateTime().isAfter(oldDateTime), "lastUpdateTime (" + nspA10nsp.getLastUpdateTime() + ") is older than " + oldDateTime + "!");
    }

    @Then("all attributes from ResourceOrder for A10NSP {string} are saved in A4 resource inventory")
    public void thenAllAttributesFromRoForNspA10nspAreSaved(String nspAlias) {
        final ResourceOrder ro = (ResourceOrder) testContext.getScenarioContext().getContext(Context.A4_RESOURCE_ORDER);
        final NetworkServiceProfileA10NspDto nspA10nspData = (NetworkServiceProfileA10NspDto) testContext
                .getScenarioContext().getContext(Context.A4_NSP_A10NSP, nspAlias);
        final NetworkServiceProfileA10NspDto nspA10nsp = a4ResInv
                .getExistingNetworkServiceProfileA10Nsp(nspA10nspData.getUuid());

        // ACTION

        assertEquals((String)getcharacteristicValue(ro,"mtuSize"), nspA10nsp.getMtuSize());
        assertEquals((String)getcharacteristicValue(ro,"carrierBsaReference"), nspA10nsp.getCarrierBsaReference());
        assertEquals((String)getcharacteristicValue(ro,"lacpActive"), Objects.requireNonNull(nspA10nsp.getLacpActive()).toString());
        assertEquals((String)getcharacteristicValue(ro,"publicReferenceId"), nspA10nsp.getItAccountingKey());
        VlanRange roVlanRange;
        roVlanRange = (VlanRange)  getcharacteristicValue(ro,"VLAN_Range");
        assertNotNull(roVlanRange);
        assertEquals(1, Objects.requireNonNull(nspA10nsp.getsVlanRange()).size());
        assertEquals(roVlanRange.getVlanRangeLower(),nspA10nsp.getsVlanRange().get(0).getVlanRangeLower());
        assertEquals(roVlanRange.getVlanRangeUpper(),nspA10nsp.getsVlanRange().get(0).getVlanRangeUpper());
        assertEquals(Objects.requireNonNull(ro.getOrderItem()).size(),Integer.parseInt(Objects.requireNonNull(nspA10nsp.getNumberOfAssociatedNsps())));

        QosList roQosList;
        roQosList = (QosList) getcharacteristicValue(ro,"QoS_List");
        assertNotNull(roQosList);
        assertEquals(Objects.requireNonNull(roQosList.getQosClasses()).size(), Objects.requireNonNull(nspA10nsp.getQosClasses()).size());

        roQosList.getQosClasses().forEach(roq -> Assert.assertTrue(checkQosClassValues(roQosList,nspA10nsp.getQosClasses(),roq.getQosBandwidth(),roq.getQospBit())));
        nspA10nsp.getQosClasses().forEach(nspQosDto -> Assert.assertTrue(checkQosClassValues(roQosList,nspA10nsp.getQosClasses(),nspQosDto.getQosBandwidthUp(),nspQosDto.getQosPriority())));

    }


    private Object getcharacteristicValue(ResourceOrder ro, String key) {

        Optional<Characteristic> characteristic = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(ro.getOrderItem()).get(0).getResource())
                        .getResourceCharacteristic())

                .stream().filter(c -> key.equalsIgnoreCase(c.getName())).findFirst();
        Object value = "";
        if (characteristic.isPresent()) {
            value = characteristic.get().getValue();
        }
        return value;
    }


    private boolean checkQosClassValues(QosList roQosList, List<A10NspQosDto> a10NspQosDtos, String qosBandwidth, String qosospBit) {

        boolean sameCounts = false;

        List<QosClass> qosClassStream = Objects.requireNonNull(roQosList.getQosClasses())
                .stream()
                .filter(c -> qosBandwidth.equalsIgnoreCase(c.getQosBandwidth()))
                .filter(c -> qosospBit.equalsIgnoreCase(c.getQospBit())).collect(Collectors.toList());

        List<A10NspQosDto> a10NspQosDtoStream = Objects.requireNonNull(a10NspQosDtos)
                .stream()
                .filter(c -> qosBandwidth.equalsIgnoreCase(c.getQosBandwidthDown()))
                .filter(c -> qosBandwidth.equalsIgnoreCase(c.getQosBandwidthUp()))
                .filter(c -> qosospBit.equalsIgnoreCase(c.getQosPriority())).collect(Collectors.toList());

        if (qosClassStream != null && a10NspQosDtoStream != null ) {
            if (qosClassStream.size() == a10NspQosDtoStream.size())
                sameCounts = true;
        } else if (qosClassStream == null && a10NspQosDtoStream == null ) {
                sameCounts = true;
        }
        return sameCounts;
    }

    // -----=====[ HELPERS ]=====-----

    private NetworkServiceProfileA10NspDto setupDefaultNspA10NspTestData(String tpAlias) {
        final boolean TP_PRESENT = testContext.getScenarioContext().isContains(Context.A4_TP, tpAlias);

        // NSP needs to be connected to a TP, so if no TP present, create one
        if (!TP_PRESENT)
            a4TpSteps.givenATPIsExistingInA4ResourceInventory(tpAlias);

        final TerminationPointDto tp = (TerminationPointDto) testContext.getScenarioContext().getContext(Context.A4_TP, tpAlias);
        return a4ResInvMapper.getNetworkServiceProfileA10NspDto(tp.getUuid());
    }

    private void persistNspA10Nsp(String nspAlias, NetworkServiceProfileA10NspDto nspA10Nsp) {
        a4ResInv.createNetworkServiceProfileA10Nsp(nspA10Nsp);

        testContext.getScenarioContext().setContext(Context.A4_NSP_A10NSP, nspAlias, nspA10Nsp);
    }

    private void createNspA10Nsp(String nspAlias, String tpAlias) {
        // ACTION
        NetworkServiceProfileA10NspDto nspA10nsp = setupDefaultNspA10NspTestData(tpAlias);

        persistNspA10Nsp(nspAlias, nspA10nsp);
    }

    private void createNspA10NspWithVlanRangeAndLcState(String nspAlias, String vlanRangeLower, String vlanRangeUpper, String lcState, String tpAlias) {
        // ACTION
        NetworkServiceProfileA10NspDto nspA10nsp = setupDefaultNspA10NspTestData(tpAlias);

        if (isNullOrEmpty(nspA10nsp.getsVlanRange())) {
            List<VlanRangeDto> vlanRanges = new ArrayList<>();
            vlanRanges.add(new VlanRangeDto()
                    .vlanRangeLower(vlanRangeLower)
                    .vlanRangeUpper(vlanRangeUpper));
            nspA10nsp.setsVlanRange(vlanRanges);
        }

        nspA10nsp.getsVlanRange().get(0).setVlanRangeLower(vlanRangeLower);
        nspA10nsp.getsVlanRange().get(0).setVlanRangeUpper(vlanRangeUpper);

        nspA10nsp.setLifecycleState(lcState);

        persistNspA10Nsp(nspAlias, nspA10nsp);
    }

    private void createNspA10NspWithStates(String nspAlias, String opState, String lcState, String tpAlias) {
        // ACTION
        NetworkServiceProfileA10NspDto nspA10nsp = setupDefaultNspA10NspTestData(tpAlias);
        nspA10nsp.setOperationalState(opState);
        nspA10nsp.setLifecycleState(lcState);

        persistNspA10Nsp(nspAlias, nspA10nsp);
    }

    private void createNspA10NspWithItAccountingKeyAndNelUuid(String nspAlias, String itAccountingKey, String nelUuid, String tpAlias) {
        // ACTION
        NetworkServiceProfileA10NspDto nspA10nsp = setupDefaultNspA10NspTestData(tpAlias);
        nspA10nsp.setItAccountingKey(itAccountingKey);
        nspA10nsp.setNetworkElementLinkUuid(nelUuid);

        persistNspA10Nsp(nspAlias, nspA10nsp);
    }

}
