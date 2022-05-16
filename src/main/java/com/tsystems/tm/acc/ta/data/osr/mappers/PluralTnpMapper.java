package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.tests.osr.plural.tnp.client.model.Rsrv1KnotenLesenD3Erep1KnotenItem;
import com.tsystems.tm.acc.tests.osr.plural.tnp.client.model.Rsrv1KnotenLesenD3Erep1KnotenItemExp1Knoten;
import com.tsystems.tm.acc.tests.osr.plural.tnp.client.model.Rsrv1KnotenLesenD3Erep1KnotenItemExp1Vpsz;
import com.tsystems.tm.acc.tests.osr.plural.tnp.client.model.Rsrv1KnotenLesenD3Response;

import java.util.ArrayList;
import java.util.List;

public class PluralTnpMapper {

    public Rsrv1KnotenLesenD3Response getJsonIRsrv1KnotenLesenD3Response(A4ImportCsvData csvData){
        List<Rsrv1KnotenLesenD3Erep1KnotenItem> erep1KnotenList = new ArrayList<>();
        csvData.getCsvLines().forEach(csvLine -> {
            Rsrv1KnotenLesenD3Erep1KnotenItemExp1Vpsz exp1Vpsz = new Rsrv1KnotenLesenD3Erep1KnotenItemExp1Vpsz()
                    .vpsz(csvLine.getNeVpsz());
            Rsrv1KnotenLesenD3Erep1KnotenItemExp1Knoten exp1Knoten = new Rsrv1KnotenLesenD3Erep1KnotenItemExp1Knoten()
                    .fachsz(csvLine.getNeFsz());
            Rsrv1KnotenLesenD3Erep1KnotenItem erep1Knoten = new Rsrv1KnotenLesenD3Erep1KnotenItem()
                    .exp1Vpsz(exp1Vpsz)
                    .exp1Knoten(exp1Knoten);
            erep1KnotenList.add(erep1Knoten);
        });

        return new Rsrv1KnotenLesenD3Response()
                .command("ANZEIGEN")
                .erep1Knoten(erep1KnotenList);
    }

}
