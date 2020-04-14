package com.tsystems.tm.acc.ta.data.generators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.data.AbstractGeneratorMapper;
import com.tsystems.tm.acc.data.exceptions.MapperError;
import com.tsystems.tm.acc.data.model.Artifact;
import com.tsystems.tm.acc.data.model.DataKey;
import com.tsystems.tm.acc.data.registry.RegistryRegistry;
import com.tsytems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class A4ResourceInventoryMapper extends AbstractGeneratorMapper<A4ResourceInventoryEntry> {
    @Override
    public List<A4ResourceInventoryEntry> getData(Artifact artifact, RegistryRegistry registry) throws MapperError {
        List<A4ResourceInventoryEntry> values = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        List<A4ResourceInventoryEntry> a4ResourceInventoryEntryList = new ArrayList<>();
        a4ResourceInventoryEntryList.add(createEntry());
        return a4ResourceInventoryEntryList;


//        if (artifact.isTakeAll()) {
//            for (DataKey key : registry.getProcessedDataRegistry().getRegistry().keySet()
//                    .stream()
//                    .filter(key -> key.getTemplate().equals(artifact.getType()))
//                    .collect(Collectors.toList())) {
//                Nvt nvt = mapper.convertValue(registry.getObjectForKey(key), Nvt.class);
//                values.add(createEntry(nvt));
//            }
//        } else {
//            for (String value : artifact.getParameters()) {
//
////                Nvt nvt = mapper.convertValue(registry.getObjectForKey(new DataKey(value, artifact.getType())), Nvt.class);
//                values.add(createEntry(nvt));
//            }
//        }
//        return values;
    }

//    private static A4ResourceInventoryEntry createEntry(Nvt nvt) {
    private static A4ResourceInventoryEntry createEntry() {

            return new A4ResourceInventoryEntry()
                    .negName("NEG-Name")
                    .negCno("NEG-CNO")
                    .negDescription("NEG-Desc")
                    .neVpsz("neVpsz")
                    .neFsz("neFsz")
                    .nePlanningDeviceName("nePlDevNane")
                    .neType("neType")
                    .neVpsz("neVsp")
                    .neLocKlsId("neKLS")
                    .neLocAddress("neAddress")
                    .neLocRackId("neRackId")
                    .neLocRackPosition("neRackPos")
                    .neDescription("neDesc");

//                .nvtOnkz(nvt.getAddress().getOnkz())
//                .nvtAsb(nvt.getAddress().getAsb())
//                .nvtName(nvt.getName())
//                .gebietstyp(nvt.getGebietstyp())
//                .numberOfHKFiber("72")
//                .vstOnkz(nvt.getOltDevice().getVst().getAddress().getOnkz())
//                .vstAsb(nvt.getOltDevice().getVst().getAddress().getAsb())
//                .vstName(nvt.getOltDevice().getVst().getName())
//                .vpszOlt(nvt.getOltDevice().getVpsz())
//                .vstKlsId(nvt.getOltDevice().getVst().getAddress().getKlsId());
    }

}
