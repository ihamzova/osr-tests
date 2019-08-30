package com.tsystems.tm.acc.ta.data;

import com.tsystems.tm.acc.data.osr.models.DataBundle;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Paths;

@Getter
public class OsrTestContext {
    private static OsrTestContext ourInstance = new OsrTestContext();
    private DataBundle data;

    private OsrTestContext() {
        try {
             data = new DataBundle(Paths.get(System.getProperty("user.dir"), "target", "osr-data"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Unable to init data. " + e.toString());
        }
    }

    public static OsrTestContext get() {
        return ourInstance;
    }
}
