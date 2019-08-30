package com.tsystems.tm.acc.ta.ui.pages.oltmaintenance.SelenideConditions;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import com.tsystems.tm.acc.ta.ui.pages.oltmaintenance.SelenideExceptions.ErrorMessageFound;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class VisibleOkOrErrorCondition extends CollectionCondition {
    private final List<String> expectedTexts;
    private final List<String> errorTexts;

    public VisibleOkOrErrorCondition(List<String> expectedTexts, List<String> errorTexts) {
        this.expectedTexts = expectedTexts;
        this.errorTexts = errorTexts;
    }

    @Override
    public boolean apply(List<WebElement> elements) {
        List<String> messages = elements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        if (messages.stream().anyMatch(errorTexts::contains)) {
            throw new ErrorMessageFound(messages.stream()
                    .filter(errorTexts::contains)
                    .reduce("", (partialString, element) -> String.join(", ", partialString, element)));
        }
        log.info("==================");
        messages.forEach(log::info);

        return messages.containsAll(expectedTexts);
    }

    @Override
    public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
        if (elements != null && !elements.isEmpty()) {
            throw new TextsMismatch(collection, ElementsCollection.texts(elements), this.expectedTexts, this.explanation, timeoutMs);
        } else {
            ElementNotFound elementNotFound = new ElementNotFound(collection, this.expectedTexts, lastError);
            elementNotFound.timeoutMs = timeoutMs;
            throw elementNotFound;
        }
    }

    @Override
    public boolean applyNull() {
        return false;
    }
}