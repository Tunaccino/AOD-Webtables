package leveretconey.dependencyDiscover.Parallel.distributed;

import leveretconey.dependencyDiscover.Parallel.RunParallel;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AODServiceImplementation extends UnicastRemoteObject implements AODService {
    protected AODServiceImplementation() throws RemoteException{
        super();
    }


    @Override
    public String processWebTable(String input) throws RemoteException {
        RunParallel runner = new RunParallel(input,"data/exp8 solutions");
        runner.runParallel();

        return ("Processed: " + input);
    }

    public static void main(String[]args){
        try {
            AODService server = new AODServiceImplementation();
            InetAddress ip = InetAddress.getLocalHost();
            java.rmi.Naming.rebind("rmi://"+ ip.getHostAddress() + ":1099/AODService",server);
            System.out.println("RMI-Server started waiting for connection ...");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
