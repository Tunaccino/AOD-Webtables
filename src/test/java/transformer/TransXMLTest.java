




package transformer;

import leveretconey.pre.transformer.TransXML;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TransXMLTest {

    private String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    private String readCsvFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file));
    }


    private void testXMLToCsv(String xmlFileName, String expectedCsvFileName, String solution) throws IOException {
        Path xmlPath = Paths.get("src/test/test.data/test.xml/" + xmlFileName);
        Path expectedCsvPath = Paths.get("src/main/resources/csv's/" + expectedCsvFileName);

        TransXML transformer = new TransXML();

        transformer.transform(xmlPath.toString());

        String expectedCsv = readFile(expectedCsvPath);
        String actualCsv = readCsvFile(Path.of("src/test/solution.csv's/" + solution));
        assertEquals(expectedCsv.trim(), actualCsv.trim(), "The CSV output does not match the expected result.");
    }

    @org.junit.jupiter.api.Test
    void transformBasic() throws IOException {
        testXMLToCsv("test.basic.xml","test.basic.csv", "solution.xml/solution.basic.csv");
    }

    @org.junit.jupiter.api.Test
    void transformAttribute() throws IOException {
        testXMLToCsv("test.attribute.xml","test.attribute.csv", "solution.xml/solution.attribute.csv");
    }

    @org.junit.jupiter.api.Test
    void transformInconsistent() throws IOException {
        testXMLToCsv("test.inconsistent.xml","test.inconsistent.csv", "solution.xml/solution.inconsistent.csv");
    }

    @org.junit.jupiter.api.Test
    void transformMissing() throws IOException {
        testXMLToCsv("test.missing.xml","test.missing.csv", "solution.xml/solution.missing.csv");
    }

    @org.junit.jupiter.api.Test
    void transformMult() throws IOException {
        testXMLToCsv("test.mult.xml","test.mult.csv", "solution.xml/solution.mult.csv");
    }

    @org.junit.jupiter.api.Test
    void transformNull() throws IOException {
        testXMLToCsv("test.null.xml","test.null.csv", "solution.xml/solution.null.csv");
    }

    @Disabled
    @org.junit.jupiter.api.Test
    void transformStructure() throws IOException {
        testXMLToCsv("test.structure.xml","test.structure.csv", "solution.xml/solution.structure.csv");
    }

    //TODO write test for dates and BigDecimals
}
