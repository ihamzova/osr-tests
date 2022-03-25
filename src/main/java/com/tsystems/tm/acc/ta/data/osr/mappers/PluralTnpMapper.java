package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4ImportCsvData;
import com.tsystems.tm.acc.tests.osr.plural.tnp.client.model.JsonIRsrv1KnotenLesenD3Response;
import com.tsystems.tm.acc.tests.osr.plural.tnp.client.model.JsonIRsrv1KnotenLesenD3ResponseErep1Knoten;
import com.tsystems.tm.acc.tests.osr.plural.tnp.client.model.JsonIRsrv1KnotenLesenD3ResponseExp1Knoten;
import com.tsystems.tm.acc.tests.osr.plural.tnp.client.model.JsonIRsrv1KnotenLesenD3ResponseExp1Vpsz;

import java.util.ArrayList;
import java.util.List;

public class PluralTnpMapper {

    public JsonIRsrv1KnotenLesenD3Response getJsonIRsrv1KnotenLesenD3Response(A4ImportCsvData csvData){
        List<JsonIRsrv1KnotenLesenD3ResponseErep1Knoten> erep1KnotenList = new ArrayList<>();
        csvData.getCsvLines().forEach(csvLine -> {
            JsonIRsrv1KnotenLesenD3ResponseExp1Vpsz exp1Vpsz = new JsonIRsrv1KnotenLesenD3ResponseExp1Vpsz()
                    .vpsz(csvLine.getNeVpsz());
            JsonIRsrv1KnotenLesenD3ResponseExp1Knoten exp1Knoten = new JsonIRsrv1KnotenLesenD3ResponseExp1Knoten()
                    .fachsz(csvLine.getNeFsz());
            JsonIRsrv1KnotenLesenD3ResponseErep1Knoten erep1Knoten = new JsonIRsrv1KnotenLesenD3ResponseErep1Knoten()
                    .exp1Vpsz(exp1Vpsz)
                    .exp1Knoten(exp1Knoten);
            erep1KnotenList.add(erep1Knoten);
        });

        return new JsonIRsrv1KnotenLesenD3Response()
                .command("ANZEIGEN")
                .erep1Knoten(erep1KnotenList);
    }

}
