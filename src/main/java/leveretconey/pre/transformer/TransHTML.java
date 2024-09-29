package leveretconey.pre.transformer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Transformation process for HTML > CSV.
 */
public class TransHTML implements Trans{

    public TransHTML() throws IOException {

    }

    /**
     * Implementation of super function for HTML-Files.
     * @param data Path of the file that gets transformed.
     * @throws IOException
     */
    @Override
    public void transform(String data) throws IOException {
        String html = new String(Files.readAllBytes(Paths.get(data)));

        Document doc = Jsoup.parse(html);


        Element table = doc.select("table").first();
        String name = Path.of(data.substring(data.lastIndexOf("/"), data.lastIndexOf("."))).toString();

        if (table == null) {
            System.out.println("HTML-File" + name + " is empty.");
            return;
        }

        FileWriter writer = new FileWriter(Path.of("data/Stage 1" + name + ".csv").toString());

        List<Integer> columnCounts = new ArrayList<>();
        Elements rows = table.select("tr");

        for (Element row : rows) {
            Elements cols = row.select("td, th");
            columnCounts.add(cols.size());
        }

        int maxColumns = columnCounts.stream().mapToInt(Integer::intValue).max().orElse(0);

        for (Element row : rows) {
            Elements cols = row.select("td, th");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < maxColumns; i++) {
                if(i < cols.size()) {

                    String text = cols.get(i).text().replace("\"", "\"\"");

                    sb.append(format(text));
                }else{sb.append("\"").append("\"").append(",");}
            }

            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            sb.append('\n');
            writer.write(sb.toString());
        }

        writer.flush();
        writer.close();
    }

}
