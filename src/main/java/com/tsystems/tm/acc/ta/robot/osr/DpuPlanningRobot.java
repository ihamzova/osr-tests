package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.UnleashClient;
import com.tsystems.tm.acc.ta.api.osr.DpuPlanningClient;
import com.tsystems.tm.acc.ta.helpers.NotificationHelper;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.util.TestSettings;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemandCreate;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation;
import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.morpheus.CommonTestData.*;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public class DpuPlanningRobot {

    private final DpuPlanningClient dpuPlanningClient = new DpuPlanningClient();
    private UnleashClient unleashClient = new UnleashClient();
    String FEATURE_TOGGLE_DPU_CONFIG_A4_SUPPORT = "business.rori.use-dpu-configuration-v2-with-a4-support";

    @Step("use-dpu-configuration-v2-with-a4-support - сhange feature toggle state")
    public void changeFeatureToggleDpuConfigurationWithA4Support(boolean toggleState) {
        if (toggleState) {
            unleashClient.enableToggle(FEATURE_TOGGLE_DPU_CONFIG_A4_SUPPORT);
        } else {
            unleashClient.disableToggle(FEATURE_TOGGLE_DPU_CONFIG_A4_SUPPORT);
        }
    }

    @Step("Deserialize DpuDemandCreate object from json")
    public DpuDemandCreate getDpuDemandCreateFromJson(String pathToFile) {
        File template = new File(getClass().getResource(pathToFile).getFile());
        DpuDemandCreate dpuDemandCreate;
        try {
            dpuDemandCreate = DpuPlanningClient.json()
                    .deserialize(FileUtils.readFileToString(template, Charset.defaultCharset()), DpuDemandCreate.class);
            return dpuDemandCreate;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Step("Create DPU Demand")
    public DpuDemand createDpuDemand(DpuDemandCreate dpuDemandRequestData) {
        return dpuPlanningClient.getClient().dpuDemand().createDpuDemand()
                .body(dpuDemandRequestData)
                .executeAs(checkStatus(201));
    }

    @Step("Validate DPU Demand")
    public void checkDpuDemandDomain(DpuDemand dpuDemandToValidate) {
        assertNotNull(dpuDemandToValidate.getDpuEndSz());
        assertEquals(String.valueOf(dpuDemandToValidate.getState()), "FULFILLED");
    }

    @Step("Validate DPU Demand after deletion")
    public void checkDpuDemandAfterDeletionDomain(DpuDemand dpuDemandToValidate) {
        assertNull(dpuDemandToValidate.getDpuEndSz());
    }

    @Step("Create DPU Demand: 400 error code")
    public void createDpuDemand400(DpuDemandCreate dpuDemandRequestData) {
        dpuPlanningClient.getClient().dpuDemand().createDpuDemand()
                .body(dpuDemandRequestData)
                .executeAs(checkStatus(400));
    }

    @Step("Delete DPU Demand and check successful Response")
    public void deleteDpuDemand(DpuDemand dpuDemandToDelete) {
        dpuPlanningClient.getClient().dpuDemand().deleteDpuDemand()
                .idPath(dpuDemandToDelete.getId()).execute(checkStatus(204));
    }

    @Step("Delete DPU Demand and check error Response")
    public void deleteDpuDemand404(DpuDemand dpuDemandToDelete) {
        dpuPlanningClient.getClient().dpuDemand().deleteDpuDemand()
                .idPath(dpuDemandToDelete.getId()).execute(checkStatus(404));
    }

    @Step("Modify DPU Demand: 409 error code")
    public void patchDpuDemand409(DpuDemand dpuDemandToModify, String path, String value) {
        dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Collections.singletonList(new JsonPatchOperation().op(REPLACE_OPERATION)
                        .path(path)
                        .value(value)))
                .executeAs(checkStatus(409));
    }

    @Step("Add workorderId to DPU Demand")
    public void addWorkorderId(DpuDemand dpuDemand, String workorderId) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemand.getId())
                .body(Arrays.asList(new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                        .op(JsonPatchOperation.OpEnum.ADD)
                        .path("/workorderId")
                        .value(workorderId)))
                .executeAs(checkStatus(200));
        assertEquals(response.getWorkorderId(), workorderId);
    }

    @Step("Fulfill DPU Demand")
    public void fulfillDpuDemand(DpuDemand dpuDemandToModify, String dpuEndSz) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Arrays.asList(new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/state")
                                .value(DPU_STATE_VALUE),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/dpuEndSz")
                                .value(dpuEndSz),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/emsNbiName")
                                .value(DPU_EMS_NBI_NAME),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/dpuMatName")
                                .value(DPU_MAT_NAME),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/dpuMatNo")
                                .value(DPU_MAT_NO),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/dpuPortCount")
                                .value(DPU_PORT_COUNT)
                ))
                .executeAs(checkStatus(200));
        assertEquals(response.getDpuEndSz(), dpuEndSz);
        assertEquals(String.valueOf(response.getState()), DPU_STATE_VALUE);
    }

    @Step("Fulfill DPU Demand")
    public DpuDemand fulfillDpuDemandDomain(DpuDemand dpuDemandToModify) {
        return dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Arrays.asList(new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/state")
                                .value("FULFILLED"),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/dpuEndSz")
                                .value("49/30/306/71G1"),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/emsNbiName")
                                .value("SDX2221-04-TP"),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/dpuMatName")
                                .value("SDX2221-04 TP-AC-M-FTTB ETSI"),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/dpuMatNo")
                                .value("40898328"),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(JsonPatchOperation.OpEnum.ADD)
                                .path("/dpuPortCount")
                                .value("4")
                ))
                .executeAs(checkStatus(200));
    }

    @Owner("TMI")
    @Step("Find all DPU Demand by klsId and check Response")
    public List<DpuDemand> findDpuDemandsByKlsId(String klsId) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .klsIdQuery(klsId)
                .executeAs(checkStatus(200));
    }

    @Step("Read DPU Demand by fiberOnLocationId and check Response")
    public void readDpuDemandByFolId(String folId) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .fiberOnLocationIdQuery(folId)
                .executeAs(checkStatus(200)).get(0);
        assertEquals(response.getFiberOnLocationId(), folId);
    }

    @Step("Read DPU Demand by dpuAccessTechnology and check Response")
    public void readDpuDemandByAccessTechnology(String accessTechnology) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .dpuAccessTechnologyQuery(accessTechnology)
                .executeAs(checkStatus(200)).get(0);
        assertEquals(String.valueOf(response.getDpuAccessTechnology()), accessTechnology);
    }

    @Step("Read DPU Demand by klsId and check Response")
    public void readDpuDemandByKlsId(String klsId) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .klsIdQuery(klsId)
                .executeAs(checkStatus(200)).get(0);
        assertEquals(response.getKlsId(), klsId);
    }

    @Step("Read DPU Demand by numberOfNeededDpuPorts and check Response")
    public void readDpuDemandByNumberOfNeededDpuPorts(String numberOfNeededDpuPorts) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .numberOfNeededDpuPortsQuery(numberOfNeededDpuPorts)
                .executeAs(checkStatus(200)).get(0);
        assertEquals(String.valueOf(response.getNumberOfNeededDpuPorts()), numberOfNeededDpuPorts);
    }

    @Step("Read DPU Demand by state and check Response")
    public void readDpuDemandByState(String state) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .stateQuery(state)
                .executeAs(checkStatus(200)).get(0);
        assertEquals(String.valueOf(response.getState()), state);
    }

    @Step("Read DPU Demand by workorderId and check Response")
    public void readDpuDemandByWorkorderId(String workorderId) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .workorderIdQuery(workorderId)
                .executeAs(checkStatus(200)).get(0);
        assertEquals(response.getWorkorderId(), workorderId);
    }

    @Step("Read DPU Demand by id and check Response")
    public void readDpuDemandById(String demandId) {
        DpuDemand response = dpuPlanningClient.getClient().dpuDemand().retrieveDpuDemand()
                .idPath(demandId)
                .executeAs(checkStatus(200));
        assertEquals(response.getId(), demandId);
    }

    @Step("Read DPU Demand by dpuEndSz and check Response")
    public void readDpuDemandByEndsz(String endSz) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .dpuEndSzQuery(endSz)
                .executeAs(checkStatus(200));
    }

    @Step("Find DPU Demand by dpuEndSz and check Response")
    public DpuDemand findDpuDemandByFolIdDomain(com.tsystems.tm.acc.ta.data.osr.models.DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .fiberOnLocationIdQuery(dpuDemandToRead.getFiberOnLocationId())
                .executeAs(checkStatus(200)).get(0);
    }

    @Step("Register for notifications")
    public void registerForNotifications() {
        NotificationHelper helper = NotificationHelper.forClient(DPU_PLANNING,
                RhssoHelper.getSecretOfGigabitHub(DPU_PLANNING),
                TestSettings.get().getApiGwApplicationName());
        helper.registerForNotifications(DPU_PLANNING_PUBSUB_TOPIC);
    }

    @Step("Check DpuPlanningCompletedEvent events")
    public void validateDpuPlanningCompletedEvent(DpuDemand dpuDemandToRead, int expectedSize) {
        NotificationHelper helper = NotificationHelper.forClient(DPU_PLANNING,
                RhssoHelper.getSecretOfGigabitHub(DPU_PLANNING),
                TestSettings.get().getApiGwApplicationName());

        List<String> notifications = helper.getNotifications(DPU_PLANNING_PUBSUB_TOPIC);
        assertEquals(notifications.size(), expectedSize);
        if (expectedSize > 0) {
            notifications.forEach(notification -> assertThat(notification)
                    .contains(dpuDemandToRead.getFiberOnLocationId()));
        }
    }
}
