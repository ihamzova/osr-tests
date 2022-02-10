package com.tsystems.tm.acc.ta.helpers.osr;

import com.tsystems.tm.acc.ta.helpers.osr.logs.TimeoutBlock;
import lombok.Getter;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.testng.Assert.fail;

@Getter
public class RetryLoop {

    private long runtimeSeconds = 10;
    private long steptimeSeconds = 1;
    private Supplier<Boolean> condition;
    private String assertMessage;
    private TimeoutBlock tob;

    public RetryLoop steptime(long seconds) {
        this.steptimeSeconds = seconds;
        return this;
    }

    public RetryLoop runtime(long seconds) {
        this.runtimeSeconds = seconds;
        return this;
    }

    public RetryLoop assertMessage(String assertMessage) {
        this.assertMessage = assertMessage;
        return this;
    }

    public RetryLoop withCondition(Supplier<Boolean> condition) {
        this.condition = condition;
        return this;
    }

    public void run() {
        assert condition != null;
        tob = new TimeoutBlock(TimeUnit.MILLISECONDS.convert(runtimeSeconds, TimeUnit.SECONDS));
        tob.setTimeoutInterval(TimeUnit.MILLISECONDS.convert(steptimeSeconds, TimeUnit.SECONDS));
        try {
            tob.addBlock(condition);
        } catch (Exception e) {
            fail(assertMessage == null ? String.format("retry loop [%s times] failed", runtimeSeconds) : assertMessage);
        }
    }

}
