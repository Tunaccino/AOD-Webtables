package leveretconey.pre.transformer;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Transformation process for XML > CSV.
 */
public class TransXML implements Trans {

    public TransXML() {

    }


    /**
     * Implementation of super function for XML-Files.
     * @param data Path of the file that gets transformed.
     */
    @Override
    public void transform(String data) {
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(data);

            Element root = document.getDocumentElement();
            NodeList rows = root.getChildNodes();

            Set<String> allColumnNames = new LinkedHashSet<>();
            List<Map<String, String>> rowData = new ArrayList<>();

            for (int i = 0; i < rows.getLength(); i++) {
                Node row = rows.item(i);

                if (row.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) row;
                    Map<String, String> currentRowData = new LinkedHashMap<>();

                    NamedNodeMap attributes = element.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attribute = attributes.item(j);
                        String attributeName = attribute.getNodeName();
                        String attributeValue = attribute.getNodeValue();
                        allColumnNames.add(attributeName);
                        currentRowData.put(attributeName, attributeValue);
                    }

                    NodeList columns = element.getChildNodes();
                    for (int j = 0; j < columns.getLength(); j++) {
                        Node column = columns.item(j);
                        if (column.getNodeType() == Node.ELEMENT_NODE) {
                            String columnName = column.getNodeName();
                            String columnValue = column.getTextContent().trim();
                            allColumnNames.add(columnName);
                            currentRowData.put(columnName, columnValue);
                        }
                    }
                    rowData.add(currentRowData);
                }
            }

            try (FileWriter writer = new FileWriter(Path.of("data/Stage 1" + data.substring(data.lastIndexOf(File.separator), data.lastIndexOf(".")) + ".csv").toString())) {
                StringBuilder headerBuilder = new StringBuilder();
                for (String columnName : allColumnNames) {
                    headerBuilder.append("\"").append(columnName).append("\",");
                }
                headerBuilder.setLength(headerBuilder.length() - 1);
                writer.append(headerBuilder.toString()).append("\n");

                for (Map<String, String> row : rowData) {
                    StringBuilder rowBuilder = new StringBuilder();
                    for (String columnName : allColumnNames) {
                        rowBuilder.append(format(row.getOrDefault(columnName,"")));
                    }
                    rowBuilder.setLength(rowBuilder.length() - 1);
                    writer.append(rowBuilder.toString()).append("\n");
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}

