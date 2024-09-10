package leveretconey.dependencyDiscover.Parallel.distributed;

import leveretconey.dependencyDiscover.Parallel.RunParallel;

import java.io.File;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Arrays;

public class AODClient {
    public static void main(String[]args){
        try{
            AODService service = (AODService) Naming.lookup("rmi://192.168.178.25:1099/AODService");

            File directory = new File("data/exp10");
            File[] files = directory.listFiles();

            File[] filesLower = Arrays.copyOfRange(files,0,files.length/2);
            File[] filesUpper = Arrays.copyOfRange(files,files.length/2,files.length);

            FileArrayWrapper filesLowerWrapper = new FileArrayWrapper(filesLower);

            Thread localProcessingThread = new Thread(() ->{
                RunParallel runner = new RunParallel(filesUpper, "data/exp8 solutions");
                runner.runParallel();

            });

            Thread remoteProcessingThread = new Thread(() -> {
                try{
                    service.processWebTable(filesLowerWrapper, "data/exp8 solutions");
                }catch (Exception e){
                    e.printStackTrace();
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
