package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4InventoryImporterClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

public class A4PluralImporterRobot {
    private final A4InventoryImporterClient a4Importer = new A4InventoryImporterClient();

    @Step("PluralAligment")
    public void postPluralAlignment(String negName) {
        a4Importer.getClient().pluralAlignment().pluralAlignment().nameNEGQuery(negName).execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("import ne list of neg from Plural")
    // public CsvFileUploadResult doPluralImport(String negName) {
    // public void doPluralImport(String negName) {
    public Response doPluralImport(String negName) {

        System.out.println("+++ A4InventoryImporter: frage folgende NEG bei Plural an: " + negName);
        //  request an Importer
        // /pluralAlignment?nameNeg=49/6808/1/POD/01
        // https://a4-inventory-importer-app-berlinium-03.priv.cl01.tmagic-dev.telekom.de/pluralAlignment?nameNEG=49/30/111/POD/02
        // https://a4-inventory-importer-app-berlinium-03.priv.cl01.tmagic-dev.telekom.de/pluralAlignment?nameNEG=49%2F6808%2F1%2FPOD%2F01
/*
        a4Importer.getClient().pluralAlignment().pluralAlignment().nameNEGQuery(negName)
                .execute(checkStatus(HTTP_CODE_OK_200));

 */

        return a4Importer.getClient().pluralAlignment().pluralAlignment().nameNEGQuery(negName)
                .execute(checkStatus(HTTP_CODE_OK_200));


    }
}
