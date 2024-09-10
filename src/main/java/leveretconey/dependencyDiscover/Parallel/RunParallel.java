package leveretconey.dependencyDiscover.Parallel;

import javafx.util.Pair;
import leveretconey.cocoa.multipleStandard.DFSDiscovererWithMultipleStandard;
import leveretconey.dependencyDiscover.Data.DataFormatConverter;
import leveretconey.dependencyDiscover.Data.DataFrame;
import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;
import leveretconey.dependencyDiscover.Parallel.distributed.FileArrayWrapper;
import leveretconey.dependencyDiscover.Parallel.distributed.Wrapper;
import leveretconey.pre.transformer.Transformer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static leveretconey.cocoa.multipleStandard.DFSDiscovererWithMultipleStandard.ValidatorType.G1;

/**
 * Adds the possibility to discover multiple Order Dependencies on a directory of Webtables in paralllel.
 */
public class RunParallel {


    private Path directory;
    private Path output;
    public ArrayList<Path> paths;
    public ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>> collections;

    /**
     * @param directory Directory consisting of Webtables to be checked for OD's.
     * @param output Directory where the found OD's are written to.
     */
    public RunParallel(String directory, String output){
        this.directory = Paths.get(directory);
        this.output = Paths.get(output);
        setPaths();
    }

    public RunParallel(File[] files,String output){
        this.output = Paths.get(output);

        this.paths = new ArrayList<>();

        for (File file : files){
            this.paths.add(file.toPath());
        }

        directory = Paths.get("");
    }

    public RunParallel(FileArrayWrapper files, String output){
        this.output = Paths.get(output);

        this.paths = new ArrayList<>();

        for (File file : files.getFiles()){
            this.paths.add(file.toPath());
        }

        directory = Paths.get("");
    }

    public RunParallel(String[] files, String output){
        this.output = Paths.get(output);

        this.paths = new ArrayList<>();

        for (String file : files){
            this.paths.add(Path.of(file));
        }

        directory = Paths.get("");

        collections = new ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>>();

    }

    /**
     * Finds all the paths to the Files contained in the directory.
     */
    private void setPaths() {
        ArrayList<Path> paths = new ArrayList<>();
        try {
            Files.walk(directory)
                    .filter(path -> !path.equals(directory))
                    .forEach(file -> paths.add(file));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.paths = paths;
    }

    /**
     * Runs the algorithm with data conversion but without parallelization.
     */
    public void runWithConvert() throws IOException {
        for (Path path : paths){
            String stPath = path.toString();
            DataFormatConverter converter = new DataFormatConverter();
            DataFormatConverter.DataFormatConverterConfig config = new DataFormatConverter.DataFormatConverterConfig(path.toString());
            converter.convert(config);
            DataFrame data = DataFrame.fromCsv(stPath.substring(0,stPath.lastIndexOf(".")) + " converted.csv");
            DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
            writeSolution(discoverer.discover(data, 0.01),stPath.substring(stPath.lastIndexOf("/")));
        }
    }

    /**
     * Runs the algorithm without data conversion and without parallelization.
     */
    public void run(){
        int index = 0;
        for (Path path : paths){
            String stPath = path.toString();
            System.out.println("TABLE: " + stPath);
            DataFrame data = DataFrame.fromCsv(stPath);
            DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
            collections.add(new Pair(discoverer.discover(data,0.01),path.toString()));
            //writeSolution(discoverer.discover(data, 0.01),output.toString());
        }
    }

    /**
     * Runs the algorithm with data conversion and with parallelization.
     */
    public void runParallelWithConvert() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (Path path : paths) {
                String stPath = path.toString();
                Future<Void> future = executor.submit(() -> {
                    DataFormatConverter converter = new DataFormatConverter();
                    DataFormatConverter.DataFormatConverterConfig config = new DataFormatConverter.DataFormatConverterConfig(path.toString());
                    converter.convert(config);
                    DataFrame data = DataFrame.fromCsv(stPath);
                    DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
                    collections.add(new Pair<>(discoverer.discover(data,0.01),path.toString()));
                    //writeSolution(discoverer.discover(data, 0.01),stPath.substring(stPath.lastIndexOf("/")));
                    return null;
                });

                futures.add(future);
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            executor.shutdown();
        }
    }

    /**
     *  Runs the algorithm without data conversion but with parallelization.
     */
    public void runParallel() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (Path path : paths) {
                String stPath = path.toString();
                Future<Void> future = executor.submit(() -> {
                    DataFrame data = DataFrame.fromCsv(stPath);
                    DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
                    collections.add(new Pair<>(discoverer.discover(data,0.01),path.toString()));
                    //writeSolution(discoverer.discover(data, 0.01),output.toString()+stPath.substring(stPath.lastIndexOf("/")));
                    return null;
                });

                futures.add(future);
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            executor.shutdown();
        }
    }


    /**
     * writes the found OD's to the location specified.
     * @param discoveredLods List of all found OD's for a certain Webtable.
     * @param location Location where the solution is written to.
     */
    private void writeSolution(Collection<LexicographicalOrderDependency> discoveredLods, String  location){
        ArrayList<String> lines = new ArrayList<>();
        for(LexicographicalOrderDependency lod : discoveredLods){
            lines.add(lod.toString());
        }

        try {
            Files.write(Paths.get(location),lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
