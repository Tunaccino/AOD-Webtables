package transformer;

import leveretconey.pre.transformer.TransHTML;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TransHTMLTest {

    private String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    private String readCsvFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file));
    }


    private void testHtmlToCsv(String htmlFileName, String expectedCsvFileName, String solution) throws IOException {
        Path htmlPath = Paths.get("src/test/test.data/test.html/" + htmlFileName);
        Path expectedCsvPath = Paths.get("src/main/resources/csv's/" + expectedCsvFileName);

        TransHTML transformer = new TransHTML();

        transformer.transform(htmlPath.toString());

        String expectedCsv = readFile(expectedCsvPath);
        String actualCsv = readCsvFile(Path.of("src/test/solution.csv's/" + solution));
        assertEquals(expectedCsv.trim(), actualCsv.trim(), "The CSV output does not match the expected result.");
    }


    @org.junit.jupiter.api.Test
    void transformBasic() throws IOException {
        testHtmlToCsv("test.basic.html","test.basic.csv", "solution.html/solution.basic.csv");
    }

    @org.junit.jupiter.api.Test
    void transformDataTypes() throws IOException {
        testHtmlToCsv("test.dataTypes.html","test.dataTypes.csv", "solution.html/solution.dataTypes.csv");
    }

    @org.junit.jupiter.api.Test
    void transformMissing() throws IOException {
        testHtmlToCsv("test.missing.html","test.missing.csv", "solution.html/solution.missing.csv");
    }

    //TODO adapt to HTML-Special-Commands
    @Disabled
    @org.junit.jupiter.api.Test
    void transformMultHeader() throws IOException {
        testHtmlToCsv("test.multHeader.html","test.multHeader.csv", "solution.html/solution.multHeader.csv");
    }

    @org.junit.jupiter.api.Test
    void transformNull() throws IOException {
        testHtmlToCsv("test.null.html","test.null.csv", "solution.html/solution.null.csv");
    }

    @org.junit.jupiter.api.Test
    void transformSingle() throws IOException {
        testHtmlToCsv("test.single.html","test.single.csv", "solution.html/solution.single.csv");
    }

    @org.junit.jupiter.api.Test
    void transformSpecialChar() throws IOException {
        testHtmlToCsv("test.specialChar.html","test.specialChar.csv", "solution.html/solution.specialChar.csv");
    }

    //TODO adapt to deep HTML structure
    @Disabled
    @org.junit.jupiter.api.Test
    void transformStructure() throws IOException {
        testHtmlToCsv("test.structure.html","test.structure.csv", "solution.html/solution.structure.csv");
    }

    //TODO write date test case
}