package com.floorestimatepro.export;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import com.floorestimatepro.model.EstimateResult;

public class EstimateExporterTest{
    @Test
    void toCsv_contains_header_and_values(){
        EstimateResult result = new EstimateResult(42.0, 0.42, 0.462, 2.31);
        String csv = EstimateExporter.toCsv(result);

        assertTrue(csv.contains("netPixelArea,netRealArea,materialSqFt,estimatedCost"));
        assertTrue(csv.contains("42.0"));
        assertTrue(csv.contains("0.42"));
        assertTrue(csv.contains("0.462"));
        assertTrue(csv.contains("2.31"));
    }

    @Test
    void exportCsv_writes_file() throws Exception{
        EstimateResult result = new EstimateResult(42.0, 0.42, 0.462, 2.31);
        String path = "target/test-estimate.csv";

        EstimateExporter.exportCsv(result, path);

        String contents = Files.readString(Path.of(path));
        assertEquals(EstimateExporter.toCsv(result), contents);
    }
}
