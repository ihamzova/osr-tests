package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.csv.CsvStream;
import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import com.tsystems.tm.acc.ta.data.osr.generators.A4ImportCsvDataGenerator;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4StartPage;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@Slf4j
public class A4ResourceInventoryImportRobot {

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

    public void importCsvFileViaUi(A4ImportCsvData csvData) {
        File csvFile = Paths.get("target/", "a4Testcase" + UUID.randomUUID().toString().substring(1, 6)
                + ".csv").toFile();
        generateCsvFile(csvData, csvFile);

        A4StartPage.
                login().
                validate().
                goToInstallation().
                uploadCSV(csvFile);
    }

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
