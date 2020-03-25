package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.AdditionalAttributeDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.TerminationPointDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static io.restassured.RestAssured.given;

public class A4ResourceInventoryRobot {
    private static final Integer HTTP_CODE_OK_200 = 200;

    private ApiClient a4ResourceInventoryClient = new A4ResourceInventoryClient().getClient();

    public void deleteNegWithA4ResourceInventoryApi(String uuid) {
        String endPoint = "/networkElementGroups/" + uuid;
        String app = "a4-resource-inventory";
        URL url = new OCUrlBuilder(app).withEndpoint(endPoint).build();

        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .delete(url);

        response.then().assertThat().statusCode(HttpStatus.SC_NO_CONTENT);
    }

    public Response getNegAsLogicalResourceWithA4ResourceInventoryServiceApi(String uuid) {

        String endPoint = "/logicalResource/" + uuid;
        String app = "a4-resource-inventory-service";
        URL url = new OCUrlBuilder(app).withEndpoint(endPoint).build();

        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get(url);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

        return response;

    }

    @Step("Create network element group")
    public void createNetworkElementGroup(NetworkElementGroupDto networkElementGroup) {
        a4ResourceInventoryClient
                .networkElementGroups()
                .createOrUpdateNetworkElementGroup()
                .body(networkElementGroup)
                .uuidPath(networkElementGroup.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Remove network element group")
    public void deleteNetworkElementGroup(String uuid) {
        a4ResourceInventoryClient
                .networkElementGroups()
                .deleteNetworkElementGroup()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create termination point")
    public void createTerminationPoint(TerminationPointDto terminationPoint) {
        List<AdditionalAttributeDto> additionalAttributes = new ArrayList<>();
        terminationPoint.getAdditionalAttribute().forEach(attribute -> {
            additionalAttributes.add(new AdditionalAttributeDto().key(attribute.getKey()).value(attribute.getValue()));
        });

        a4ResourceInventoryClient
                .terminationPoints()
                .createOrUpdateTerminationPoint()
                .body(terminationPoint)
                .uuidPath(terminationPoint.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Remove termination point")
    public void deleteTerminationPoint(String uuid) {
        a4ResourceInventoryClient
                .terminationPoints()
                .deleteTerminationPoint()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

}
