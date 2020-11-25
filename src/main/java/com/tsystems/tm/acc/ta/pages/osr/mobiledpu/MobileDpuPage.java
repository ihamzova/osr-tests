package com.tsystems.tm.acc.ta.pages.osr.mobiledpu;

import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

import static com.codeborne.selenide.Selenide.open;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;

@Slf4j

public class MobileDpuPage {

    private static final String APP = "mobile-dpu";
    private static final String ENDPOINT = "/workorder";
    private static final long TIMEOUT = 30000;

    @Step("Open MobileDpuPage")
    public static MobileDpuPage openPage() {
        URL url = new OCUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, MobileDpuPage.class);
    }

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

}
