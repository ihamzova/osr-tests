package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.DpuPlanningClient;
import com.tsystems.tm.acc.ta.helpers.NotificationHelper;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.util.TestSettings;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemand;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.DpuDemandCreate;
import com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.morpheus.CommonTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

public class DpuPlanningRobot {

    DpuPlanningClient dpuPlanningClient = new DpuPlanningClient(new RhssoClientFlowAuthTokenProvider(DPU_PLANNING, RhssoHelper.getSecretOfGigabitHub(DPU_PLANNING)));

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
        DpuPlanningClient dpuPlanningClient = new DpuPlanningClient(new RhssoClientFlowAuthTokenProvider(DPU_PLANNING, RhssoHelper.getSecretOfGigabitHub(DPU_PLANNING)));
        return dpuPlanningClient.getClient().dpuDemand().createDpuDemand()
                .body(dpuDemandRequestData)
                .executeAs(validatedWith(shouldBeCode(201)));
    }

    @Step("Validate DPU Demand")
    public void validateDpuDemand(DpuDemand dpuDemandAfterProcess, com.tsystems.tm.acc.ta.data.osr.models.DpuDemand dpuDemandExpectedData) {

        assertEquals(String.valueOf(dpuDemandAfterProcess.getDpuAccessTechnology()), dpuDemandExpectedData.getDpuAccessTechnology());
        assertEquals(dpuDemandAfterProcess.getFiberOnLocationId(), dpuDemandExpectedData.getFiberOnLocationId());
        assertEquals(dpuDemandAfterProcess.getKlsId(), dpuDemandExpectedData.getKlsId());
        assertEquals(String.valueOf(dpuDemandAfterProcess.getNumberOfNeededDpuPorts()), dpuDemandExpectedData.getNumberOfNeededDpuPorts());
        assertEquals(String.valueOf(dpuDemandAfterProcess.getState()), dpuDemandExpectedData.getState());
        assertEquals(dpuDemandAfterProcess.getDpuEndSz(), dpuDemandExpectedData.getDpuEndSz());
        assertEquals(dpuDemandAfterProcess.getDpuInstallationInstruction(), dpuDemandExpectedData.getDpuInstallationInstruction());
        assertEquals(dpuDemandAfterProcess.getDpuLocation(), dpuDemandExpectedData.getDpuLocation());
        assertEquals(dpuDemandAfterProcess.getWorkorderId(), dpuDemandExpectedData.getWorkorderId());
        assertNotNull(dpuDemandAfterProcess.getId());
        assertNotNull(dpuDemandAfterProcess.getHref());
        assertNotNull(dpuDemandAfterProcess.getCreationDate());
    }

    @Step("Validate DPU Demand")
    public void checkDpuDemandDomain(DpuDemand dpuDemandToValidate) {

        assertNotNull(dpuDemandToValidate.getDpuEndSz());
        assertEquals(String.valueOf(dpuDemandToValidate.getState()), "FULFILLED");
    }

    @Step("Create DPU Demand: 400 error code")
    public void createDpuDemand400(DpuDemandCreate dpuDemandRequestData) {
        DpuPlanningClient dpuPlanningClient = new DpuPlanningClient(new RhssoClientFlowAuthTokenProvider(DPU_PLANNING, RhssoHelper.getSecretOfGigabitHub(DPU_PLANNING)));
        dpuPlanningClient.getClient().dpuDemand().createDpuDemand()
                .body(dpuDemandRequestData)
                .executeAs(validatedWith(shouldBeCode(400)));
    }

    @Step("Create DPU Demand: 409 error code")
    public void createDpuDemand409(DpuDemandCreate dpuDemandRequestData) {
        DpuPlanningClient dpuPlanningClient = new DpuPlanningClient(new RhssoClientFlowAuthTokenProvider(DPU_PLANNING, RhssoHelper.getSecretOfGigabitHub(DPU_PLANNING)));
        dpuPlanningClient.getClient().dpuDemand().createDpuDemand()
                .body(dpuDemandRequestData)
                .executeAs(validatedWith(shouldBeCode(409)));
    }

    @Step("Delete DPU Demand and check successful Response")
    public void deleteDpuDemand(DpuDemand dpuDemandToDelete) {
        dpuPlanningClient.getClient().dpuDemand().deleteDpuDemand()
                .idPath(dpuDemandToDelete.getId()).execute(validatedWith(shouldBeCode(204)));
    }

    @Step("Delete DPU Demand and check error Response")
    public void deleteDpuDemand404(DpuDemand dpuDemandToDelete) {
        dpuPlanningClient.getClient().dpuDemand().deleteDpuDemand()
                .idPath(dpuDemandToDelete.getId()).execute(validatedWith(shouldBeCode(404)));
    }

    @Step("Modify DPU Demand by replace: one parameter updated")
    public DpuDemand patchDpuDemandModifyOneParameter(DpuDemand dpuDemandToModify, String path, String value, JsonPatchOperation.OpEnum operation) {
        return dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Collections.singletonList(new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation().op(operation)
                        .path(path)
                        .value(value)))
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Modify DPU Demand: 409 error code")
    public void patchDpuDemand409(DpuDemand dpuDemandToModify, String path, String value, JsonPatchOperation.OpEnum operation) {
        dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Collections.singletonList(new JsonPatchOperation().op(operation)
                        .path(path)
                        .value(value)))
                .executeAs(validatedWith(shouldBeCode(409)));
    }

    @Step("Modify DPU Demand by replace: two parameters updated")
    public DpuDemand patchDpuDemandModifyTwoParameters(DpuDemand dpuDemandToModify, String path, String value, String path2, String value2, JsonPatchOperation.OpEnum operation) {
        return dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Arrays.asList(new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(operation)
                                .path(path)
                                .value(value),
                        new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation()
                                .op(operation)
                                .path(path2)
                                .value(value2)))
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by fiberOnLocationId and check Response")
    public DpuDemand readDpuDemandByFolId(DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .fiberOnLocationIdQuery(dpuDemandToRead.getFiberOnLocationId())
                .executeAs(validatedWith(shouldBeCode(200))).get(0);
    }

    @Step("Read DPU Demand by dpuEndSz and check Response")
    public DpuDemand readDpuDemandByEndsz(DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .dpuEndSzQuery(dpuDemandToRead.getDpuEndSz())
                .executeAs(validatedWith(shouldBeCode(200))).get(0);
    }

    @Step("Find DPU Demand by dpuEndSz and check Response")
    public DpuDemand findDpuDemandByFolIdDomain(com.tsystems.tm.acc.ta.data.osr.models.DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .fiberOnLocationIdQuery(dpuDemandToRead.getFiberOnLocationId())
                .executeAs(validatedWith(shouldBeCode(200))).get(0);
    }

    @Step("Read DPU Demand by dpuAccessTechnology and check Response")
    public DpuDemand readDpuDemandByDpuAccessTechnology(DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .dpuAccessTechnologyQuery(dpuDemandToRead.getDpuAccessTechnology())
                .executeAs(validatedWith(shouldBeCode(200))).get(0);
    }

    @Step("Read DPU Demand by klsId and check Response")
    public DpuDemand readDpuDemandByKlsId(DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .klsIdQuery(dpuDemandToRead.getKlsId())
                .executeAs(validatedWith(shouldBeCode(200))).get(0);
    }

    @Step("Read DPU Demand by numberOfNeededDpuPorts and check Response")
    public DpuDemand readDpuDemandByNumberOfNeededDpuPorts(DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .numberOfNeededDpuPortsQuery(dpuDemandToRead.getNumberOfNeededDpuPorts())
                .executeAs(validatedWith(shouldBeCode(200))).get(0);
    }

    @Step("Read DPU Demand by state and check Response")
    public DpuDemand readDpuDemandByState(DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .stateQuery(dpuDemandToRead.getState())
                .executeAs(validatedWith(shouldBeCode(200))).get(0);
    }

    @Step("Read DPU Demand by workorderId and check Response")
    public DpuDemand readDpuDemandByWorkorderId(DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .workorderIdQuery(dpuDemandToRead.getWorkorderId())
                .executeAs(validatedWith(shouldBeCode(200))).get(0);
    }

    @Step("Read DPU Demand by filter criterium: error code 404")
    public void readDpuDemandByWorkorderId404(String workorderId) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .workorderIdQuery(workorderId)
                .executeAs(validatedWith(shouldBeCode(404)));
    }

    @Step("Read DPU Demand by id and check Response")
    public DpuDemand readDpuDemandById(DpuDemand dpuDemandToRead) {
        return dpuPlanningClient.getClient().dpuDemand().retrieveDpuDemand()
                .idPath(dpuDemandToRead.getId())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by id: error code 404")
    public void readDpuDemandById404(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().retrieveDpuDemand()
                .idPath(dpuDemandToRead.getId())
                .executeAs(validatedWith(shouldBeCode(404)));
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
        helper.getTokenProvider().revokeToken();

        List<String> notifications = helper.getNotifications(DPU_PLANNING_PUBSUB_TOPIC);
        assertEquals(notifications.size(), expectedSize);
        if (expectedSize > 0) {
            notifications.forEach(notification -> assertThat(notification)
                    .contains(dpuDemandToRead.getFiberOnLocationId()));
        }
    }
}
