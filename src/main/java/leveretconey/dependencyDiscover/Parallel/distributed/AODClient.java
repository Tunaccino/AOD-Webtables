package leveretconey.dependencyDiscover.Parallel.distributed;

import javafx.util.Pair;
import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;
import leveretconey.dependencyDiscover.Parallel.RunParallel;

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
    public static void main(String[]args){
        try{
            AODService service = (AODService) Naming.lookup("rmi://192.168.178.25:1099/AODService");

            File directory = new File("data/exp10");
            File[] files = directory.listFiles();

            File[] filesLower = Arrays.copyOfRange(files,0,files.length/2);
            File[] filesUpper = Arrays.copyOfRange(files,files.length/2,files.length);
            AtomicReference<ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>>> collections = new AtomicReference<>(new ArrayList<>());

            Thread localProcessingThread = new Thread(() ->{
                RunParallel runner = new RunParallel(filesUpper, "data/exp8 solutions");
                runner.runParallel();

            });

            Thread remoteProcessingThread = new Thread(() -> {
                try{
                    collections.set(service.processWebTable(Arrays.stream(filesLower)
                            .map(File::getPath)
                            .toArray(String[]::new), "data/exp8 solutions"));
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
                    Files.write(Paths.get("data/exp8 solutions/"+ name),lines);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("Done calculating OD's!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        try{
            AODService service = (AODService) Naming.lookup("rmi://192.168.178.25:1099/AODService");

            File directory = new File("data/exp10");
            File[] files = directory.listFiles();

            File[] filesLower = Arrays.copyOfRange(files,0,files.length/2);
            File[] filesUpper = Arrays.copyOfRange(files,files.length/2,files.length);
            AtomicReference<ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>>> collections = new AtomicReference<>(new ArrayList<>());

            Thread localProcessingThread = new Thread(() ->{
                RunParallel runner = new RunParallel(filesUpper, "data/exp8 solutions");
                runner.runParallel();

            });

            Thread remoteProcessingThread = new Thread(() -> {
                try{
                    collections.set(service.processWebTable(Arrays.stream(filesLower)
                            .map(File::getPath)
                            .toArray(String[]::new), "data/exp8 solutions"));
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
                    Files.write(Paths.get("data/exp8 solutions/"+ name),lines);
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
