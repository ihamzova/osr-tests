package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import java.io.File;
import java.net.URL;

import static io.restassured.RestAssured.given;

public class A4ResourceInventoryRobot {

    public void createNegWithA4ResourceInventoryApi(String uuid) {

        String endPoint = "/networkElementGroups/" + uuid;
        String app = "a4-resource-inventory";
        URL url = new OCUrlBuilder(app).withEndpoint(endPoint).build();
        File networkElementGroupBody = new File(System.getProperty("user.dir") +
                "/src/test/resources/team.berlinium/networkElementGroup.json");

        Response response = given()
//                .auth()
//                .basic("giga", "bit")
                .header("Content-Type", "application/json")
                .body(networkElementGroupBody)
                .when()
                .put(url);

        response.then().assertThat().statusCode(HttpStatus.SC_OK);

    }

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

}
