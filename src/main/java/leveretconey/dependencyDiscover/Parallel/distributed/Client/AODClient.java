package leveretconey.dependencyDiscover.Parallel.distributed.Client;

import javafx.util.Pair;
import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;
import leveretconey.dependencyDiscover.Parallel.RunParallel;
import leveretconey.dependencyDiscover.Parallel.distributed.Service.AODService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class AODClient {

    public static void main(String[] args){
        AODClient client = new AODClient();
        client.run("192.168.178.25","data/exp10","data/exp8 solutions");
    }

    public void run(String ip, String input, String output){
        try{
            AODService service = (AODService) Naming.lookup("rmi://"+ ip +":1099/AODService");

            File directory = new File(input);
            File[] files = directory.listFiles();

            File[] filesLower = Arrays.copyOfRange(files,0,files.length/2);
            File[] filesUpper = Arrays.copyOfRange(files,files.length/2,files.length);
            AtomicReference<ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>>> collections = new AtomicReference<>(new ArrayList<>());

            Thread localProcessingThread = new Thread(() ->{
                RunParallel runner = new RunParallel(filesUpper, output);
                runner.runParallel();

            });

            Thread remoteProcessingThread = new Thread(() -> {
                try{
                    collections.set(service.processWebTable(Arrays.stream(filesLower)
                            .map(File::getPath)
                            .toArray(String[]::new), output));
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            localProcessingThread.start();
            remoteProcessingThread.start();

            localProcessingThread.join();
            remoteProcessingThread.join();

            for (Pair<Collection<LexicographicalOrderDependency>,String> pair : collections.get()){
                ArrayList<String> lines = new ArrayList<>();
                for (LexicographicalOrderDependency lod : pair.getKey()){
                    lines.add(lod.toString());
                }

                try {
                    String name = pair.getValue().substring(11);
                    Files.write(Paths.get(output +"/"+ name),lines);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("Done calculating OD's!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
