package leveretconey.dependencyDiscover.Parallel.distributed;

import leveretconey.dependencyDiscover.Parallel.RunParallel;

import java.io.File;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AODServiceImplementation extends UnicastRemoteObject implements AODService {
    protected AODServiceImplementation() throws RemoteException{
        super();
    }


    @Override
    public String processWebTable(String[] input, String output) throws RemoteException {
        if (input == null || input.length == 0) {
            System.out.println("Received an empty or null file path array");
            return "failed";
        }
        for (String path : input) {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File does not exist: " + path);
                continue;
            }
        }

        RunParallel runner = new RunParallel(input,output);
        runner.runParallel();

        System.out.println("test");
        return ("Processed: ");
    }

    public static void main(String[]args){
        try {
            LocateRegistry.createRegistry(1099); // Port 1099 ist der Standard-RMI-Port
            AODService server = new AODServiceImplementation();

            Naming.rebind("rmi://localhost:1099/AODService", server);
            System.out.println("RMI-Server started waiting for connection ...");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
