package leveretconey.dependencyDiscover.Parallel.distributed.Service;

import javafx.util.Pair;
import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;
import leveretconey.dependencyDiscover.Parallel.RunParallel;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class AODServiceImplementation extends UnicastRemoteObject implements AODService {
    protected AODServiceImplementation() throws RemoteException{
        super();
    }


    @Override
    public ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>> processWebTable(String[] input, String output, Boolean dontUseNull) throws RemoteException {
        if (input == null || input.length == 0) {
            System.out.println("Received an empty or null file path array");
            return null;
        }
        for (String path : input) {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File does not exist: " + path);
            }
        }

        RunParallel runner = new RunParallel(input,output);
        runner.runParallelRemote(dontUseNull);

        return runner.collections;
    }

    @Override
    public ArrayList<Pair<Collection<LexicographicalOrderDependency>, String>> processWebTableWithConvert(String[] input, String output, Boolean filtering,Boolean dontUseNull) throws RemoteException {
        if (input == null || input.length == 0) {
            System.out.println("Received an empty or null file path array");
            return null;
        }
        for (String path : input) {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File does not exist: " + path);
            }
        }

        RunParallel runner = new RunParallel(input,output);
        runner.runParallelRemoteWithConvert(filtering,dontUseNull);

        return runner.collections;
    }

    @Override
    public ArrayList<Pair<Collection<LexicographicalOrderDependency>, String>> processWebTableWithFullConvert(String[] input, String output,Boolean filtering, Boolean dontUseNull) throws RemoteException, ExecutionException, InterruptedException {
        if (input == null || input.length == 0) {
            System.out.println("Received an empty or null file path array");
            return null;
        }
        for (String path : input) {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File does not exist: " + path);
            }
        }

        RunParallel runner = new RunParallel(input,output);
        runner.runParallelRemoteWithFullConvert(filtering,dontUseNull);

        return runner.collections;
    }

    public static void main(String[]args){
        try {
            LocateRegistry.createRegistry(1099);
            AODService server = new AODServiceImplementation();

            Naming.rebind("rmi://localhost:1099/AODService", server);
            System.out.println("RMI-Server started waiting for connection ...");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
