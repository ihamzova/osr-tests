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

    @Step("Create DPU Demand and check successful Response")
    public void createDpuDemandAndValidateSuccessResponse(DpuDemandCreate dpuDemandRequestData, com.tsystems.tm.acc.ta.data.osr.models.DpuDemand dpuDemandExpectedData) {
        DpuPlanningClient dpuPlanningClient = new DpuPlanningClient(new RhssoClientFlowAuthTokenProvider(DPU_PLANNING, RhssoHelper.getSecretOfGigabitHub(DPU_PLANNING)));
        DpuDemand dpuDemandCreateResponse = dpuPlanningClient.getClient().dpuDemand().createDpuDemand()
                .body(dpuDemandRequestData)
                .executeAs(validatedWith(shouldBeCode(201)));

        assertEquals(String.valueOf(dpuDemandCreateResponse.getDpuAccessTechnology()), dpuDemandExpectedData.getDpuAccessTechnology());
        assertEquals(dpuDemandCreateResponse.getFiberOnLocationId(), dpuDemandExpectedData.getFiberOnLocationId());
        assertEquals(dpuDemandCreateResponse.getKlsId(), dpuDemandExpectedData.getKlsId());
        assertEquals(String.valueOf(dpuDemandCreateResponse.getNumberOfNeededDpuPorts()), dpuDemandExpectedData.getNumberOfNeededDpuPorts());
        assertEquals(String.valueOf(dpuDemandCreateResponse.getState()), dpuDemandExpectedData.getState());
        assertEquals(dpuDemandCreateResponse.getDpuEndSz(), dpuDemandExpectedData.getDpuEndSz());
        assertEquals(dpuDemandCreateResponse.getDpuInstallationInstruction(), dpuDemandExpectedData.getDpuInstallationInstruction());
        assertEquals(dpuDemandCreateResponse.getDpuLocation(), dpuDemandExpectedData.getDpuLocation());
        assertEquals(dpuDemandCreateResponse.getWorkorderId(), dpuDemandExpectedData.getWorkorderId());
        assertNotNull(dpuDemandCreateResponse.getId());
        assertNotNull(dpuDemandCreateResponse.getHref());
        assertNotNull(dpuDemandCreateResponse.getCreationDate());
        assertNull(dpuDemandCreateResponse.getModificationDate());
    }

    @Step("Create DPU Demand and check error Response")
    public void createDpuDemandAndValidateErrorResponse(DpuDemandCreate dpuDemandRequestData) {
        DpuPlanningClient dpuPlanningClient = new DpuPlanningClient(new RhssoClientFlowAuthTokenProvider(DPU_PLANNING, RhssoHelper.getSecretOfGigabitHub(DPU_PLANNING)));
        dpuPlanningClient.getClient().dpuDemand().createDpuDemand()
                .body(dpuDemandRequestData)
                .executeAs(validatedWith(shouldBeCode(400)));
    }

    @Step("Create DPU Demand for modification")
    public DpuDemand createDpuDemandForModification(DpuDemandCreate dpuDemandCreateRequestData) {
        return dpuPlanningClient.getClient().dpuDemand().createDpuDemand().body(dpuDemandCreateRequestData).executeAs(validatedWith(shouldBeCode(201)));
    }

    @Step("Delete DPU Demand and check successful Response")
    public void deleteDpuDemandSuccessResponse(DpuDemand dpuDemandToDelete) {
        dpuPlanningClient.getClient().dpuDemand().deleteDpuDemand()
                .idPath(dpuDemandToDelete.getId()).execute(validatedWith(shouldBeCode(204)));
    }

    @Step("Delete DPU Demand and check error Response")
    public void deleteDpuDemandErrorResponse(DpuDemand dpuDemandToDelete) {
        dpuPlanningClient.getClient().dpuDemand().deleteDpuDemand()
                .idPath(dpuDemandToDelete.getId()).execute(validatedWith(shouldBeCode(404)));
    }

    @Step("Modify DPU Demand by replace: one parameter updated")
    public void patchDpuDemandModifyOneParameter(DpuDemand dpuDemandToModify, String path, String value, JsonPatchOperation.OpEnum operation) {
        dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Collections.singletonList(new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation().op(operation)
                        .path(path)
                        .value(value)))
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Modify DPU Demand by replace: Bad Request")
    public void patchDpuDemandBadRequest(DpuDemand dpuDemandToModify, String path, JsonPatchOperation.OpEnum operation) {
        dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Collections.singletonList(new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation().op(operation)
                        .path(path)))
                .executeAs(validatedWith(shouldBeCode(400)));
    }

    @Step("Modify DPU Demand by replace: two parameters updated")
    public void patchDpuDemandModifyTwoParameters(DpuDemand dpuDemandToModify, String path, String value, String path2, String value2, JsonPatchOperation.OpEnum operation) {
        dpuPlanningClient.getClient().dpuDemand().patchDpuDemand()
                .idPath(dpuDemandToModify.getId())
                .body(Collections.singletonList(new com.tsystems.tm.acc.tests.osr.dpu.planning.model.JsonPatchOperation().op(operation)
                        .path(path)
                        .value(value)
                        .op(operation)
                        .path(path2)
                        .value(value2))
                )
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by fiberOnLocationId and check Response")
    public void readDpuDemandByFolIdAndValidateResponse(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .fiberOnLocationIdQuery(dpuDemandToRead.getFiberOnLocationId())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by dpuEndSz and check Response")
    public void readDpuDemandByEndszAndValidateResponse(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .dpuEndSzQuery(dpuDemandToRead.getDpuEndSz())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by dpuAccessTechnology and check Response")
    public void readDpuDemandByDpuAccessTechnologyAndValidateResponse(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .dpuAccessTechnologyQuery(dpuDemandToRead.getDpuAccessTechnology())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by klsId and check Response")
    public void readDpuDemandByKlsIdAndValidateResponse(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .klsIdQuery(dpuDemandToRead.getKlsId())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by numberOfNeededDpuPorts and check Response")
    public void readDpuDemandByNumberOfNeededDpuPortsAndValidateResponse(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .numberOfNeededDpuPortsQuery(dpuDemandToRead.getNumberOfNeededDpuPorts())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by state and check Response")
    public void readDpuDemandByStateAndValidateResponse(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .stateQuery(dpuDemandToRead.getState())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by workorderId and check Response")
    public void readDpuDemandByWorkorderIdAndValidateResponse(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().findDpuDemand()
                .workorderIdQuery(dpuDemandToRead.getWorkorderId())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by id and check Response")
    public void readDpuDemandByIdAndValidateResponse(DpuDemand dpuDemandToRead) {
        dpuPlanningClient.getClient().dpuDemand().retrieveDpuDemand()
                .idPath(dpuDemandToRead.getId())
                .executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("Read DPU Demand by id: demand not found")
    public void readDpuDemandByIdErrorResponse(DpuDemand dpuDemandToRead) {
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
