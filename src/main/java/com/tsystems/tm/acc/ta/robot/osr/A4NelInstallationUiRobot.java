package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.WebDriverRunner;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4NelInstallationPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class A4NelInstallationUiRobot {

    public void doNelInstallation() {
        checkCheckbox("1");
        clickButtonAndConfirm();
    }

    @Step("Check radioButton")
    public void checkCheckbox(String index) {
        $(A4NelInstallationPage.getCHECKBOX_LOCATOR()).click();
    }

    @Step("Click button")
    public void clickButtonAndConfirm() {
        $(A4NelInstallationPage.getSTART_INSTALL_BTN()).click();

        try {
            WebDriver driver = WebDriverRunner.getWebDriver();// new ChromeDriver(capabilities);
            WebDriverWait wait = new WebDriverWait(driver, 5000);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException e) {
            System.out.println("EXCEPTION " + e.getCause());
        }
    }

    @Step("Check error message not found")
    public String notFoundMsg() {
        return $(A4NelInstallationPage.getERROR_LOCATOR()).getText();
    }

    @Step("Conten not found msg")
    public void checkNotFound() {
        Assert.assertTrue(notFoundMsg().contains("Keine NetworkElementLinks zu diesem NetworkElement gefunden!"));
    }

    @Step("click planning filter")
    public void checkPlanningFilter() {
        $(A4NelInstallationPage.getPLANNING_FILTER_LOCATOR()).click();
    }

}
