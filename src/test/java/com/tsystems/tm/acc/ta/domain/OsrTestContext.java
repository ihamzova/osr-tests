package com.tsystems.tm.acc.ta.domain;

import com.tsystems.tm.acc.data.osr.models.DataBundle;
import lombok.Getter;

@Getter
public class OsrTestContext {
    private static OsrTestContext ourInstance = new OsrTestContext();
    private DataBundle data;

    private OsrTestContext() {
        data = new DataBundle();
    }

    public static OsrTestContext get() {
        return ourInstance;
    }
}
