





package transformer;

import leveretconey.pre.transformer.TransJSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InvalidPropertiesFormatException;

import static org.junit.jupiter.api.Assertions.*;

class TransJSONTest {

    private String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    private String readCsvFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file));
    }


    private void testJsonToCsv(String jsonFileName, String expectedCsvFileName, String solution) throws IOException {
        Path jsonPath = Paths.get("src/test/test.data/test.json/" + jsonFileName);
        Path expectedCsvPath = Paths.get("src/main/resources/csv's/" + expectedCsvFileName);

        TransJSON transformer = new TransJSON();

        transformer.transformOld(jsonPath.toString());

        String expectedCsv = readFile(expectedCsvPath);
        String actualCsv = readCsvFile(Path.of("src/test/solution.csv's/" + solution));
        assertEquals(expectedCsv.trim(), actualCsv.trim(), "The CSV output does not match the expected result.");
    }

    @org.junit.jupiter.api.Test
    void transformBasic() throws IOException {
        testJsonToCsv("test.basic.json","test.basic.csv", "solution.json/solution.basic.csv");
    }

    @org.junit.jupiter.api.Test
    void transformDiffHeader() throws IOException {
        testJsonToCsv("test.diffHeader.json","test.diffHeader.csv", "solution.json/solution.diffHeader.csv");
    }

    @org.junit.jupiter.api.Test
    void transformEmpty() throws IOException {
        testJsonToCsv("test.empty.json","test.empty.csv", "solution.json/solution.empty.csv");
    }

    @org.junit.jupiter.api.Test
    void transformEmptyRow() throws IOException {
        testJsonToCsv("test.emptyRow.json","test.emptyRow.csv", "solution.json/solution.emptyRow.csv");
    }

    @org.junit.jupiter.api.Test
    void transformMissing() throws IOException {
        testJsonToCsv("test.missing.json","test.missing.csv", "solution.json/solution.missing.csv");
    }

    @Disabled
    @org.junit.jupiter.api.Test
    void transformMult() throws IOException {
        testJsonToCsv("test.mult.json","test.mult.csv", "solution.json/solution.mult.csv");
    }

    @org.junit.jupiter.api.Test
    void transformNo() throws IOException {
        TransJSON transJSON = new TransJSON();
        Assertions.assertThrows(InvalidPropertiesFormatException.class, () -> {
            transJSON.transformOld("src/test/test.data/test.json/test.no.json");
        });
    }

    @org.junit.jupiter.api.Test
    void transformNull() throws IOException {
        testJsonToCsv("test.null.json","test.null.csv", "solution.json/solution.null.csv");
    }

    @org.junit.jupiter.api.Test
    void transformSingle() throws IOException {
        testJsonToCsv("test.single.json","test.single.csv", "solution.json/solution.single.csv");
    }

    @Disabled
    @org.junit.jupiter.api.Test
    void transformSpecial() throws IOException {
        testJsonToCsv("test.special.json","test.special.csv", "solution.json/solution.special.csv");
    }

    //TODO add tests for Date and Big Decimals

}