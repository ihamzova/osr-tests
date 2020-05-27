package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.csv.CsvStream;
import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    public ArrayList<A4ResourceInventoryEntry> generateCsvData() {
        Random random = new Random();
        String negName = UUID.randomUUID().toString().substring(0, 6);
        String neVpsz1 = String.format("%d/6151/%s", random.ints(1, 50).findFirst().getAsInt(), random.ints(0, 50).findFirst().getAsInt());

        ArrayList<A4ResourceInventoryEntry> list = new ArrayList<>();
        A4ResourceInventoryEntry entry1 = new A4ResourceInventoryEntry()
                .negCno("operator")
                .negName(negName)
                .negDescription("test csv upload via ui group")
                .neDescription("first NE added via ui")
                .neFsz("7KDC")
                .neLocAddress("Address")
                .neLocKlsId("123456")
                .neLocRackId("RackId")
                .neLocRackPosition("RackPosition")
                .nePlanningDeviceName("dmst.spine.1")
                .neVpsz(neVpsz1)
                .neVsp("DT");

        list.add(entry1);

        String neVpsz2 = String.format("%d/6151/%s", random.ints(1, 50).findFirst().getAsInt(), random.ints(0, 50).findFirst().getAsInt());

        A4ResourceInventoryEntry entry2 = new A4ResourceInventoryEntry()
                .negCno("operator")
                .negName(negName)
                .negDescription("test csv upload via ui group")
                .neDescription("second NE added via ui")
                .neFsz("7KDC")
                .neLocAddress("Address")
                .neLocKlsId("123456")
                .neLocRackId("RackId")
                .neLocRackPosition("RackPosition")
                .nePlanningDeviceName("dmst.spine.1")
                .neVpsz(neVpsz2)
                .neVsp("DT");

        list.add(entry2);

        return list;
    }
}
