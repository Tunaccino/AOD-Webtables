package leveretconey.dependencyDiscover.Parallel.distributed;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AODService extends Remote {
    String processWebTable(String webTable) throws RemoteException;
}
