package transformer;

import leveretconey.pre.transformer.Transformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TransformerTest {

    private String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    @Test
    void transformHTML() throws IOException {
        Transformer transformer = new Transformer();
        transformer.transform("src/test/test.data/test.html/test.basic.html");

        String expected = readFile(Paths.get("src/test/solution.csv's/solution.html/solution.basic.csv"));
        String actual = readFile(Paths.get("src/main/resources/csv's/test.basic.csv"));

        assertEquals(expected.trim(),actual.trim());
    }

    @Disabled
    @Test
    void transformJSON() throws IOException {
        Transformer transformer = new Transformer();
        transformer.transform("src/test/test.data/test.json/test.basic.json");

        String expected = readFile(Paths.get("src/test/solution.csv's/solution.json/solution.basic.csv"));
        String actual = readFile(Paths.get("src/main/resources/csv's/test.basic.csv"));

        assertEquals(expected.trim(),actual.trim());
    }

    @Test
    void transformXML() throws IOException {
        Transformer transformer = new Transformer();
        transformer.transform("src/test/test.data/test.xml/test.basic.xml");

        String expected = readFile(Paths.get("src/test/solution.csv's/solution.xml/solution.basic.csv"));
        String actual = readFile(Paths.get("src/main/resources/csv's/test.basic.csv"));

        assertEquals(expected.trim(),actual.trim());
    }

    @Test
    void transformNull() throws IOException {
        Transformer transformer = new Transformer();

        Assertions.assertThrows(ClassCastException.class, () -> {
            transformer.transform("");
        });
    }

    @Test
    void transformPDF() throws IOException {
        Transformer transformer = new Transformer();

        Assertions.assertThrows(ClassCastException.class, () -> {
            transformer.transform(".pdf");
        });
    }
}