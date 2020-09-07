package com.tsystems.tm.acc.ta.helpers.osr.logs;

import java.util.function.Supplier;

public class TimeoutBlock {

    private final long timeoutMilliSeconds;
    private long timeoutInterval = 16000;

    public TimeoutBlock(long timeoutMilliSeconds) {
        this.timeoutMilliSeconds = timeoutMilliSeconds;
    }

    public void addBlock(Supplier<Boolean> condition) throws Throwable {
        long collectIntervals = 0;
        boolean result;
        do {
            result = condition.get();
            Thread.sleep(timeoutInterval);
            collectIntervals += timeoutInterval;
        } while (collectIntervals < this.timeoutMilliSeconds && !result);

        if (collectIntervals >= this.timeoutMilliSeconds && !result) {
            throw new Exception("<<<<<<<<<<****>>>>>>>>>>> Timeout Block Execution Time Exceeded In " + timeoutMilliSeconds + " Milli Seconds. Thread Block Terminated.");
        }
        System.out.println("<<<<<<<<<<####>>>>>>>>>>> Timeout Block Executed Within " + collectIntervals + " Milli Seconds.");
    }

    /**
     * @return the timeoutInterval
     */
    public long getTimeoutInterval() {
        return timeoutInterval;
    }

    /**
     * @param timeoutInterval the timeoutInterval to set
     */
    public void setTimeoutInterval(long timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }
}