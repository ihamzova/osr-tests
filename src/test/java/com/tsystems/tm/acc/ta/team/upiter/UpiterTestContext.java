package com.tsystems.tm.acc.ta.team.upiter;

import com.tsystems.tm.acc.data.upiter.models.DataBundle;
import lombok.Getter;

@Getter
public class UpiterTestContext {
    private static UpiterTestContext ourInstance = new UpiterTestContext();
    private DataBundle data;

    private UpiterTestContext() {
        data = new DataBundle();
    }

    public static UpiterTestContext get() {
        return ourInstance;
    }
}
