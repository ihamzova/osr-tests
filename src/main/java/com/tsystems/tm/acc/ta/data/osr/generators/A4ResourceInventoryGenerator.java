package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.csv.CsvStream;
import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class A4ResourceInventoryGenerator {
    public File generate(Path path, List<A4ResourceInventoryEntry> data) throws IOException {
        File target;
        target = Paths.get(path.toString(), String.format("bedarfsliste_%s.csv", UUID.randomUUID().toString())).toFile();
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        new CsvStream(target)
                .withDelimeter(';')
                .write(A4ResourceInventoryEntry.class, data);

        return target;
    }

}
