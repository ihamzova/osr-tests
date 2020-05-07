package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.csv.CsvStream;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4StartPage;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static io.restassured.RestAssured.given;

public class A4ResourceInventoryImportRobot {

    public Response importCsvFileViaRestInterface() {
        final String endPoint = "/uploadCsvFile/";
        final String app = "a4-inventory-importer";
        URL url = new OCUrlBuilder(app).withEndpoint(endPoint).build();
        File csvFile = new File(System.getProperty("user.dir") +
                "/src/test/resources/team.berlinium/a4ResourceInventoryImport.csv");

        Response response = given()
                .header("Content-Type", "multipart/form-data")
                .multiPart("file", csvFile)
                .when()
                .post(url);

        response
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);

        return response;
    }

    public void importCsvFileViaUi(File csvFile){
        A4StartPage.
                login().
                validate().
                goToInstallation().
                uploadCSV(csvFile);
    }

    public void generateCsv(List<A4ResourceInventoryEntry> data, File targetFile){
        try {
            new CsvStream(targetFile).withDelimeter(';')
                    .write(A4ResourceInventoryEntry.class, data);
        } catch (IOException e) {
            throw new RuntimeException("cant build csv",e);
        }
    }
}