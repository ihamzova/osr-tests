package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4StartPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.InstallationPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
public class A4ResourceInventoryImporterUiRobot {
    private A4ImportCsvRobot a4ImportCsvRobot = new A4ImportCsvRobot();

    @Step("Open UI, log in, and search for existing Network Element")
    public void openNetworkElement(A4NetworkElement neData) {
        A4StartPage
                .login()
                .validate()
                .goToInstallation()
                .checkNetworkElementExists(neData);
    }

    @Step("Enter ZTP Ident")
    public void enterZtpIdent(String value) {
        InstallationPage installationPage = new InstallationPage();
        installationPage.enterZtpIdent(value);
    }

    @Step("Open monitoring page and check Network Element values")
    public void checkMonitoringPage(A4NetworkElement neData, String ztpIdent) {
        InstallationPage installationPage = new InstallationPage();
        installationPage
                .openMonitoringPage()
                .checkNeData(neData, ztpIdent);
    }

    @Step("Open UI, log in, and upload CSV file, then submit")
    public void importCsvFileViaUi(A4ImportCsvData csvData) {
        File csvFile = Paths.get("target/", "a4Testcase" + UUID.randomUUID().toString().substring(1, 6)
                + ".csv").toFile();
        a4ImportCsvRobot.generateCsvFile(csvData, csvFile);

        A4StartPage.
                login().
                validate().
                goToInstallation().
                uploadCSV(csvFile);
    }

    @Step("validate Network Elements")
    public void checkNetworkElementsViaUi(A4ImportCsvData csvData) {
        List<A4ImportCsvLine> list = csvData.getCsvLines();
        //TODO: maybe this method needs to be more flexible - here we expect the correct page to be already open
        InstallationPage installationPage = new InstallationPage();
        list.stream().findFirst().ifPresent(installationPage::checkNetworkElementExists);
        list.stream().skip(1).forEach(a4ImportCsvLine -> {
            installationPage.resetSearch();
            installationPage.checkNetworkElementExists(a4ImportCsvLine);
        });
    }

}
