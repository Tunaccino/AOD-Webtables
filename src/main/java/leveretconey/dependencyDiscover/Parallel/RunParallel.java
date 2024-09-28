package leveretconey.dependencyDiscover.Parallel;

import javafx.beans.binding.BooleanExpression;
import javafx.util.Pair;
import leveretconey.cocoa.multipleStandard.DFSDiscovererWithMultipleStandard;
import leveretconey.dependencyDiscover.Data.DataFormatConverter;
import leveretconey.dependencyDiscover.Data.DataFrame;
import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;
import leveretconey.pre.transformer.Transformer;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.nio.file.StandardOpenOption;


import static leveretconey.cocoa.multipleStandard.DFSDiscovererWithMultipleStandard.ValidatorType.G1;

/**
 * Adds the possibility to discover multiple Order Dependencies on a directory of Webtables in paralllel.
 */
public class RunParallel {


    private Path directory;
    private Path output;
    public ArrayList<Path> paths;
    public ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>> collections;
    private final Lock lock = new ReentrantLock();
    private ThreadLocal<DFSDiscovererWithMultipleStandard> discovererThreadLocal = ThreadLocal.withInitial(() -> new DFSDiscovererWithMultipleStandard(G1, 0.01));
    public Collection<Collection<LexicographicalOrderDependency>> testing = new ArrayList<>();
    /**
     * @param directory Directory consisting of Webtables to be checked for OD's.
     * @param output Directory where the found OD's are written to.
     */
    public RunParallel(String directory, String output){
        this.directory = Paths.get(directory);
        this.output = Paths.get(output);
        setPaths();
        collections = new ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>>();
    }

    public RunParallel(File[] files,String output){
        this.output = Paths.get(output);

        this.paths = new ArrayList<>();

        for (File file : files){
            this.paths.add(file.toPath());
        }

        directory = Paths.get("");
        collections = new ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>>();
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
    public void runWithConvert(Boolean filter, Boolean dontUseNull) throws IOException {
        for (Path path : paths){
            String stPath = path.toString();
            String raw = stPath.substring(stPath.lastIndexOf("/"), stPath.lastIndexOf(".")) + ".csv";
            DataFormatConverter converter = new DataFormatConverter();
            converter.filter = filter;
            DataFormatConverter.DataFormatConverterConfig config = new DataFormatConverter.DataFormatConverterConfig(path.toString());
            config.outputPath = "data/Stage 2" + raw;
            converter.convert(config);
            DataFrame data = DataFrame.fromCsv(config.outputPath);
            DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
            discoverer.dontUseNull = dontUseNull;
            writeSolution(discoverer.discover(data, 0.01),output + raw);
        }
    }

    public void runWithFullConvert(Boolean filter,Boolean dontUseNull) throws IOException {
        calculateStage1();
        calculateStage2(filter);
        calculateStage3(dontUseNull);
    }

    private void calculateStage1() throws IOException {
        for (Path path : paths) {
            String stPath = path.toString();
            Transformer transformer = new Transformer();
            transformer.transform(stPath);
        }
    }
    private void calculateStage2(Boolean filter){
        for (Path path : paths){
            String stPath = path.toString();
            String raw = stPath.substring(stPath.lastIndexOf("/"), stPath.lastIndexOf(".")) + ".csv";
            String updatedPath = "data/Stage 1" + raw;
            DataFormatConverter converter = new DataFormatConverter();
            converter.filter = filter;
            DataFormatConverter.DataFormatConverterConfig config = new DataFormatConverter.DataFormatConverterConfig(updatedPath);
            config.outputPath = "data/Stage 2" + raw;
            converter.convert(config);
        }
    }

    private void calculateStage3(Boolean dontUseNull){
        for (Path path : paths){
            String stPath = path.toString();
            String raw = stPath.substring(stPath.lastIndexOf("/"), stPath.lastIndexOf(".")) + ".csv";
            String outputPath = "data/Stage 2" + raw;
            DataFrame data = DataFrame.fromCsv(outputPath);
            DFSDiscovererWithMultipleStandard discoverer = new DFSDiscovererWithMultipleStandard(G1, 0.01);
            discoverer.dontUseNull = dontUseNull;
            var x = discoverer.discover(data, 0.01);
            writeSolution(x, output + raw);
        }
    }

    private void calculateStage3Remote(Boolean dontUseNull){
        for (Path path : paths){
            String stPath = path.toString();
            String raw = stPath.substring(stPath.lastIndexOf("/"), stPath.lastIndexOf(".")) + ".csv";
            String outputPath = "data/Stage 2" + raw;
            DataFrame data = DataFrame.fromCsv(outputPath);
            DFSDiscovererWithMultipleStandard discoverer = new DFSDiscovererWithMultipleStandard(G1, 0.01);
            discoverer.dontUseNull = dontUseNull;
            collections.add(new Pair<>(discoverer.discover(data,0.01),outputPath));
        }
    }

    /**
     * Runs the algorithm without data conversion and without parallelization.
     */
    public void run(Boolean dontUseNull){
        int index = 0;
        for (Path path : paths){
            String stPath = path.toString();
            System.out.println("TABLE: " + stPath);
            DataFrame data = DataFrame.fromCsv(stPath);
            DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
            discoverer.dontUseNull = dontUseNull;
            String raw = stPath.substring(stPath.lastIndexOf("/"), stPath.lastIndexOf(".")) + ".csv";
            writeSolution(discoverer.discover(data, 0.01),output + raw);
        }
    }

    public void runParallelWithFullConvert(Boolean filter, Boolean dontUseNull) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try{
        Future<?> stage1 = executor.submit(() ->{
            try {
                calculateStage1();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        stage1.get();

        Future<?> stage2 = executor.submit(()->{
            calculateStage2(filter);
        });
        stage2.get();

        Future<?> stage3 = executor.submit(() -> {
           calculateStage3(dontUseNull);
        });
        stage3.get();

    } finally {
        executor.shutdown();
    }
    }

    /**
     * Runs the algorithm with data conversion and with parallelization.
     */
    public void runParallelWithConvert(Boolean filter, Boolean dontUseNull) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (Path path : paths) {
                String stPath = path.toString();
                Future<Void> future = executor.submit(() -> {
                    String raw = stPath.substring(stPath.lastIndexOf("/"), stPath.lastIndexOf(".")) + ".csv";
                    DataFormatConverter converter = new DataFormatConverter();
                    converter.filter = filter;
                    DataFormatConverter.DataFormatConverterConfig config = new DataFormatConverter.DataFormatConverterConfig(path.toString());
                    config.outputPath = "data/Stage 2" + raw;
                    converter.convert(config);
                    DataFrame data = DataFrame.fromCsv(config.outputPath);
                    DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
                    discoverer.dontUseNull = dontUseNull;
                    writeSolution(discoverer.discover(data, 0.01),output + raw);

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
    public void runParallelRemote(Boolean dontUseNull) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (Path path : paths) {
                String stPath = path.toString();
                Future<Void> future = executor.submit(() -> {
                    DataFrame data = DataFrame.fromCsv(stPath);
                    DFSDiscovererWithMultipleStandard discoverer = discovererThreadLocal.get();
                    discoverer.dontUseNull = dontUseNull;
                    collections.add(new Pair<>(discoverer.discover(data,0.01),path.toString()));
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

    public void runParallelRemoteWithConvert(Boolean filter, Boolean dontUseNull) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (Path path : paths) {
                String stPath = path.toString();
                Future<Void> future = executor.submit(() -> {
                    String raw = stPath.substring(stPath.lastIndexOf("/"), stPath.lastIndexOf(".")) + ".csv";
                    DataFormatConverter converter = new DataFormatConverter();
                    converter.filter = filter;
                    DataFormatConverter.DataFormatConverterConfig config = new DataFormatConverter.DataFormatConverterConfig(path.toString());
                    config.outputPath = "data/Stage 2" + raw;
                    converter.convert(config);
                    DataFrame data = DataFrame.fromCsv(config.outputPath);
                    DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
                    discoverer.dontUseNull = dontUseNull;
                    collections.add(new Pair<>(discoverer.discover(data,0.01), config.outputPath));

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

    public void runParallelRemoteWithFullConvert(Boolean filter, Boolean dontUseNull) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try{
            Future<?> stage1 = executor.submit(() ->{
                try {
                    calculateStage1();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            stage1.get();

            Future<?> stage2 = executor.submit(()->{
                calculateStage2(filter);
            });
            stage2.get();

            Future<?> stage3 = executor.submit(() -> {
                calculateStage3Remote(dontUseNull);
            });
            stage3.get();

        } finally {
            executor.shutdown();
        }
    }


    /**
     *  Runs the algorithm without data conversion but with parallelization.
     */
    public void runParallel(Boolean dontUseNull) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (Path path : paths) {
                String stPath = path.toString();
                Future<Void> future = executor.submit(() -> {
                    DataFrame data = DataFrame.fromCsv(stPath);
                    DFSDiscovererWithMultipleStandard discoverer = discovererThreadLocal.get();
                    discoverer.dontUseNull = dontUseNull;
                    var x = discoverer.discover(data, 0.01);
                    String raw = stPath.substring(stPath.lastIndexOf("/"), stPath.lastIndexOf(".")) + ".csv";
                    writeSolution(x, output + raw);
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
            discovererThreadLocal.remove();
        }
    }

    public void runParallelTesting() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (Path path : paths) {
                String stPath = path.toString();
                Future<Void> future = executor.submit(() -> {
                    DataFrame data = DataFrame.fromCsv(stPath);
                    DFSDiscovererWithMultipleStandard discoverer = new DFSDiscovererWithMultipleStandard(G1,0.01);
                    testing.add(discoverer.discover(data, 0.01));

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
            discovererThreadLocal.remove();
        }
    }
    public void runTesting(){
        int index = 0;
        for (Path path : paths){
            String stPath = path.toString();
            System.out.println("TABLE: " + stPath);
            DataFrame data = DataFrame.fromCsv(stPath);
            DFSDiscovererWithMultipleStandard discoverer =new DFSDiscovererWithMultipleStandard(G1,0.01);
            testing.add(discoverer.discover(data, 0.01));
        }
    }


/*
    *//**
     * writes the found OD's to the location specified.
     * @param discoveredLods List of all found OD's for a certain Webtable.
     * @param location Location where the solution is written to.
     *//*
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
    }*/

    public void writeSolution(Collection<LexicographicalOrderDependency> discoveredLods, String directory) {
        String tableName = directory.substring(directory.lastIndexOf("/"));
        directory = directory.substring(0,directory.lastIndexOf("/"));

        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = "lod_report.txt";
        File file = new File(dir, fileName);
        boolean fileExists = file.exists();

        StringBuilder builder = new StringBuilder();

        if (!fileExists) {
            builder.append("==== Lexicographical Order Dependencies Report ====\n");
            builder.append("Generated on: ").append(java.time.LocalDateTime.now()).append("\n");
            builder.append("===================================================\n");
        }


        builder.append("\n---- Webtable: ").append(tableName).append(" ----\n");


        for (LexicographicalOrderDependency lod : discoveredLods) {
            builder.append(lod.toString()).append("\n");
        }

        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(builder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
