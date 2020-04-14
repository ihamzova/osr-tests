package com.tsystems.tm.acc.ta.data.generators;

import com.tsystems.tm.acc.csv.CsvStream;
import com.tsystems.tm.acc.data.AbstractGenerator;
import com.tsystems.tm.acc.data.AbstractGeneratorMapper;
import com.tsystems.tm.acc.data.exceptions.GeneratorError;
import com.tsystems.tm.acc.data.exceptions.MapperError;
import com.tsystems.tm.acc.data.model.Artifact;
import com.tsystems.tm.acc.data.registry.RegistryRegistry;
import com.tsytems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class A4ResourceInventoryGenerator extends AbstractGenerator<A4ResourceInventoryEntry> {

    @Override
    public List<String> generate(Artifact artifact, RegistryRegistry registry, Path path, AbstractGeneratorMapper<A4ResourceInventoryEntry> mapper) throws GeneratorError {
        File target;
        try {
            target = Paths.get(path.toString(), artifact.getName() + ".csv").toFile();
            List<A4ResourceInventoryEntry> data = mapper.getData(artifact, registry);
            new CsvStream(target).withDelimeter(';')
                    .write(A4ResourceInventoryEntry.class, data);
        } catch (IOException | MapperError e) {
            throw new GeneratorError(e);
        }
        return Stream.of(target).map(f -> path.relativize(f.toPath())).map(Path::toString).collect(Collectors.toList());
    }

}
