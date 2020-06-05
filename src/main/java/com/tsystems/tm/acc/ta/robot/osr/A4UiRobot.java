package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvLine;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4StartPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.InstallationPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class A4UiRobot {

    @Step("Open UI and search for existing network element")
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

    @Step("Open monitoring page and check network element values")
    public void checkMonitoringPage(A4NetworkElement neData, String ztpIdent) {
        InstallationPage installationPage = new InstallationPage();
        installationPage
                .openMonitoringPage()
                .checkNeData(neData, ztpIdent);
    }

    @Step("validate network elements")
    public void checkNetworkElementsViaUi(A4ImportCsvData csvData) {
        List<A4ImportCsvLine> list = csvData.getCsvLines();
        //@TODO: maybe this method needs to be more flexible - here we expect the correct page to be already open
        InstallationPage installationPage = new InstallationPage();
        list.stream().findFirst().ifPresent(installationPage::checkNetworkElementExists);
        list.stream().skip(1).forEach(a4ImportCsvLine -> {
            installationPage.resetSearch();
            installationPage.checkNetworkElementExists(a4ImportCsvLine);
        });
    }

}
