package com.floorestimatepro.export;

import java.nio.file.Files;
import java.nio.file.Path;

import com.floorestimatepro.model.EstimateResult;

public class EstimateExporter{
    public static String toCsv(EstimateResult result){
        return "netPixelArea,netRealArea,materialSqFt,estimatedCost\n"
            + result.netPixelArea() + ","
            + result.netRealArea() + ","
            + result.materialSqFt() + ","
            + result.estimatedCost() + "\n";
    }

    public static void exportCsv(EstimateResult result, String filePath) throws Exception{
        String csv = toCsv(result);
        Files.writeString(Path.of(filePath), csv);
    }
}
