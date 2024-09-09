package leveretconey.dependencyDiscover.Parallel.distributed;

import leveretconey.dependencyDiscover.Parallel.RunParallel;

import java.io.File;
import java.rmi.Naming;
import java.util.ArrayList;

public class AODClient {
    public static void main(String[]args){
        try{
            AODService service = (AODService) Naming.lookup("rmi://192.168.178.25:1099/AODService");

            File directory = new File("data/exp11");
            File[] files = directory.listFiles();
            int half = files.length/2;

            Thread localProcessingThread = new Thread(() ->{
                RunParallel runner = new RunParallel("data/exp10", "data/exp8 solutions");
                runner.runParallel();

            });

            Thread remoteProcessingThread = new Thread(() -> {
               for (int i = half; i < files.length; i++){
                   try{
                       service.processWebTable("data/exp10", "data/exp8 solutions");
                   }catch (Exception e){
                       e.printStackTrace();
                   }
               }
            });

            localProcessingThread.start();
            remoteProcessingThread.start();

            localProcessingThread.join();
            remoteProcessingThread.join();

            System.out.println("Done calculating OD's!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
