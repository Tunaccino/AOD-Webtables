package leveretconey.pre.transformer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Transformation process for JSON > CSV.
 */
public class TransJSON implements Trans{

    public TransJSON(){

    };

    /**
     * Implementation of super function for JSON-Files.
     * @param data Path of the file that gets transformed.
     * @throws IOException
     */
    public void transform(String data) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(data));
        StringBuilder jsonContent = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonContent.append(line.trim());
        }

        reader.close();

        String name = data.substring(data.lastIndexOf("/"), data.lastIndexOf("."));

        if (jsonContent.length() == 0)
            throw new InvalidPropertiesFormatException("Json-Datei " + name + " ist leer.");

        String formattedJsonData = "[" + jsonContent.toString().replaceAll("}\\s*\\{", "},{") + "]";

        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> jsonNodes;
        try {
            JsonNode arrayNode = mapper.readTree(formattedJsonData);
            jsonNodes = new ArrayList<>();
            if (arrayNode.isArray()) {
                for (JsonNode node : arrayNode) {
                    jsonNodes.add(node);
                }
            } else {
                throw new IOException("JSON-Daten sind kein Array");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Fehler beim Lesen der JSON-Daten. Überprüfen Sie die Formatierung.");
        }

        Set<String> headers = new LinkedHashSet<>();
        for (JsonNode node : jsonNodes) {
            node.fieldNames().forEachRemaining(headers::add);
        }

        StringBuilder sb = new StringBuilder();

        for (String header : headers) {
            sb.append('"').append(header).append('"').append(',');
        }
        sb.deleteCharAt(sb.length() - 1).append('\n');

        for (JsonNode obj : jsonNodes) {
            for (String header : headers) {
                String value = obj.has(header) ? obj.get(header).asText() : "";
                sb.append(format(value));
            }
            sb.deleteCharAt(sb.length() - 1).append('\n');
        }

        FileWriter writer = new FileWriter("data/Stage 1" + name + ".csv");
        writer.write(sb.toString());
        writer.flush();
        writer.close();
    }

    public String format(String value) {
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\",";
        }
        return value + ",";
    }


    /**
     * Implementation of super function for JSON-Files.
     * @param data Path of the file that gets transformed.
     * @throws IOException
     */
    @Deprecated
    public void transformOld(String data) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(data));
        StringBuilder jsonContent = new StringBuilder();
        String line;
        Boolean arr = false;

        while ((line = reader.readLine()) != null) {
            if(line.equals("  \"table\": [")) {
                arr = true;
                jsonContent.append("[");
                continue;
            }
            if(line.equals("]")) {
                jsonContent.append("]");
                break;
            }
            if(arr)
                jsonContent.append(line);
        }

        reader.close();

        String name = data.substring(data.lastIndexOf("/"), data.lastIndexOf("."));

        if(jsonContent.isEmpty())
            throw new InvalidPropertiesFormatException("Json-File\" + name + \" is empty.");

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonArray = (ArrayNode) mapper.readTree(jsonContent.toString());
        Set<String> headers = new LinkedHashSet<>();
        for (JsonNode node : jsonArray) {
            node.fieldNames().forEachRemaining(headers::add);
        }

        StringBuilder sb = new StringBuilder();

        for (String header : headers) {
            sb.append('"').append(header).append('"').append(',');
        }

        sb.deleteCharAt(sb.length() - 1).append('\n');

        for (JsonNode obj : jsonArray) {
            for (String header : headers) {
                String value = obj.has(header) ? obj.get(header).asText() : "";
                sb.append(Trans.super.format(value));
            }
            sb.deleteCharAt(sb.length() - 1).append('\n');
        }

        FileWriter writer = new FileWriter("src/main/resources/csv's" + name + ".csv");
        writer.write(sb.toString());

        writer.flush();
        writer.close();

    }
}
