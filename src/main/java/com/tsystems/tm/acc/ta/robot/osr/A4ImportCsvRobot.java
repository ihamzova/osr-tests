package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.csv.CsvStream;
import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import com.tsystems.tm.acc.ta.data.osr.generators.A4ImportCsvDataGenerator;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static io.restassured.RestAssured.given;

public class A4ImportCsvRobot {

    public void importCsvFileViaRestInterface(File csvFile) {
        final String endPoint = "/uploadCsvFile/";
        final String app = "a4-inventory-importer";
        URL url = new OCUrlBuilder(app).withEndpoint(endPoint).build();

        Response response = given()
                .header("Content-Type", "multipart/form-data")
                .multiPart("file", csvFile)
                .when()
                .post(url);

        response
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Step("Generate CSV and save as file")
    public void generateCsvFile(A4ImportCsvData csvData, File targetFile) {
        A4ImportCsvDataGenerator a4ImportCsvDataGenerator = new A4ImportCsvDataGenerator();
        List<A4ResourceInventoryEntry> data = a4ImportCsvDataGenerator.generateCsv(csvData);

        try {
            new CsvStream(targetFile).withDelimeter(';')
                    .write(A4ResourceInventoryEntry.class, data);
        } catch (IOException e) {
            throw new RuntimeException("cant build csv", e);
        }
    }

}
