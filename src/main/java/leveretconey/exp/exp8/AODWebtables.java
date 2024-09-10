package leveretconey.exp.exp8;

import leveretconey.dependencyDiscover.Data.DataFormatConverter;
import leveretconey.dependencyDiscover.Parallel.RunParallel;
import leveretconey.dependencyDiscover.Parallel.distributed.AODClient;
import leveretconey.pre.transformer.Transformer;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.nio.file.Path;

public class AODWebtables {

      public static void main(String[]args) {
        RunParallel run = new RunParallel("data/exp10", "data/exp8 solutions");
        AODClient client = new AODClient();

        client.run();

        //Warm up
        for (int i = 0; i < 5; i++) {
            run.run();
            client.run();
        }

        StopWatch watch = new StopWatch();

        watch.start();
        for(int i = 0; i < 10; i++){
            run.run();
        }
        watch.stop();

        Long time = watch.getTime();
        watch.reset();

        watch.start();
        for (int i = 0; i < 10; i++){
            client.run();
        }
        watch.stop();

        Long timeP = watch.getTime();

        System.out.println("Time single : " + (time/10) + "ms\n" + "Time parallel : " + (timeP/10)+"ms");
    }



/*    public static void main(String[]args) throws IOException {
        RunParallel run = new RunParallel("data/unzipped","exp10");

        for (Path path : run.paths) {
            Transformer transformer = new Transformer();
            transformer.transform(path.toString());
        }

        RunParallel run2 = new RunParallel("src/main/resources/csvs","exp10");

        for(Path path : run2.paths) {
            DataFormatConverter converter = new DataFormatConverter();
            DataFormatConverter.DataFormatConverterConfig config = new DataFormatConverter.DataFormatConverterConfig(path.toString());

            converter.convert(config);
        }
    }*/




}
