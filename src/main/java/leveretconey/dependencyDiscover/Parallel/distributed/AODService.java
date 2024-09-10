package leveretconey.dependencyDiscover.Parallel.distributed;

import javafx.util.Pair;
import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

public interface AODService extends Remote {
    ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>> processWebTable(String[] input, String output) throws RemoteException;
}
