package leveretconey.exp.exp8;

import leveretconey.cocoa.multipleStandard.DFSDiscovererWithMultipleStandard;
import leveretconey.dependencyDiscover.Data.DataFormatConverter;
import leveretconey.dependencyDiscover.Data.DataFrame;
import leveretconey.dependencyDiscover.Parallel.RunParallel;
import leveretconey.dependencyDiscover.Parallel.distributed.Client.AODClient;

import org.apache.commons.lang3.time.StopWatch;

import static leveretconey.cocoa.multipleStandard.DFSDiscovererWithMultipleStandard.ValidatorType.G1;

public class AODWebtables {

     public static void main(String[]args) {
         RunParallel run = new RunParallel("data/Stage 2","data/Stage 3");

         int aodsFoundWithNull = 0;
         int aodsFoundWithoutNull = 0;

         long TnormalNoNull = 0;
         long TnormalNull = 0;
         long TparallelNoNull = 0;
         long TparallelNull = 0;
         long Tdistributed = 0;


         //Warm-Up
         for(int i = 0; i < 5; i++){
             run.run(false);
             run.runParallel(false);
         }

         //Normal without null
         StopWatch stopWatchNormalNoNull = new StopWatch();
         for(int i = 0; i < 10; i++){
             run =  new RunParallel("data/Stage 2","data/Stage 3");
             run.runTesting(true);
         }
         aodsFoundWithoutNull = run.testing.size();
         stopWatchNormalNoNull.stop();
         TnormalNoNull = stopWatchNormalNoNull.getTime();

         //Normal with null
         StopWatch stopWatchNormalNull = new StopWatch();
         for(int i = 0; i < 10; i++){
             run =  new RunParallel("data/Stage 2","data/Stage 3");
             run.runTesting(false);
         }
         aodsFoundWithNull = run.testing.size();
         stopWatchNormalNull.stop();
         TnormalNull = stopWatchNormalNull.getTime();

         //Parallel without null
         StopWatch stopWatchParallelNoNull = new StopWatch();
         for(int i = 0; i < 10; i++){
             run =  new RunParallel("data/Stage 2","data/Stage 3");
             run.runParallelTesting(true);
         }
         stopWatchParallelNoNull.stop();
         TparallelNoNull = stopWatchParallelNoNull.getTime();

         //Parallel with null
         StopWatch stopWatchParallelNull = new StopWatch();
         for(int i = 0; i < 10; i++){
             run =  new RunParallel("data/Stage 2","data/Stage 3");
             run.runParallelTesting(false);
         }
         stopWatchParallelNull.stop();
         TparallelNull = stopWatchParallelNull.getTime();

         //Distributed
         StopWatch stopWatchDistributed = new StopWatch();
         for(int i = 0; i < 10; i++){
             AODClient client = new AODClient();
             client.run("192.168.178.25","data/Stage 2","data/Stage 3",true);
         }
         stopWatchDistributed.stop();
         Tdistributed = stopWatchDistributed.getTime();

         System.out.println("\n===== Zusammenfassung =====");
         System.out.printf("%-30s %-10s %-10s%n", "Testart", "AODs gefunden", "Zeit (ms)");
         System.out.println("----------------------------------------------");
         System.out.printf("%-30s %-10d %-10d%n", "Normal ohne Null", aodsFoundWithoutNull, TnormalNoNull);
         System.out.printf("%-30s %-10d %-10d%n", "Normal mit Null", aodsFoundWithNull, TnormalNull);
         System.out.printf("%-30s %-10d %-10d%n", "Parallel ohne Null", 0, TparallelNoNull);
         System.out.printf("%-30s %-10d %-10d%n", "Parallel mit Null", 0, TparallelNull);
         System.out.printf("%-30s %-10d %-10d%n", "Distributed", 0, Tdistributed);
         System.out.println("----------------------------------------------");
    }

}
