package leveretconey.dependencyDiscover.Parallel.distributed.Service;

import javafx.util.Pair;
import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public interface AODService extends Remote {
    ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>> processWebTable(String[] input, String output, Boolean dontUseNull) throws RemoteException;
    ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>> processWebTableWithConvert(String[] input, String output,Boolean filtering, Boolean dontUseNull) throws RemoteException;
    ArrayList<Pair<Collection<LexicographicalOrderDependency>,String>> processWebTableWithFullConvert(String[] input, String output,Boolean filtering, Boolean donUseNull) throws RemoteException, ExecutionException, InterruptedException;

}
