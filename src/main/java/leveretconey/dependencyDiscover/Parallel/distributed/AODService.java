package leveretconey.dependencyDiscover.Parallel.distributed;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AODService extends Remote {
    String processWebTable(String[] input, String output) throws RemoteException;
}
