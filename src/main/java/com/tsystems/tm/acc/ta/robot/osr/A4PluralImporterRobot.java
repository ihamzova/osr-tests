package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4InventoryImporterClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.inventory.importer.client.invoker.ApiClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_BFF_PROXY_MS;

public class A4PluralImporterRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_INVENTORY_BFF_PROXY_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_INVENTORY_BFF_PROXY_MS));

    private final ApiClient a4Importer = new A4InventoryImporterClient(authTokenProvider).getClient();

    @Step("PluralAligment")
    public void postPluralAlignment(String negName) {
        a4Importer.pluralAlignment().pluralAlignment().nameNEGQuery(negName).execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("import ne list of neg from Plural")
    // public CsvFileUploadResult doPluralImport(String negName) {
    // public void doPluralImport(String negName) {
    public Response doPluralImport(String negName) {

        System.out.println("+++ A4InventoryImporter: frage folgende NEG bei Plural an: "+negName);
        //  request an Importer
        // /pluralAlignment?nameNeg=49/6808/1/POD/01
        // https://a4-inventory-importer-app-berlinium-03.priv.cl01.tmagic-dev.telekom.de/pluralAlignment?nameNEG=49/30/111/POD/02
        // https://a4-inventory-importer-app-berlinium-03.priv.cl01.tmagic-dev.telekom.de/pluralAlignment?nameNEG=49%2F6808%2F1%2FPOD%2F01
/*
        a4Importer.pluralAlignment().pluralAlignment().nameNEGQuery(negName)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

 */

       return a4Importer.pluralAlignment().pluralAlignment().nameNEGQuery(negName)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));


    }
}
