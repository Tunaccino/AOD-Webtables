package leveretconey.dependencyDiscover.Parallel;

import leveretconey.dependencyDiscover.Dependency.LexicographicalOrderDependency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RunParallelTest {
    RunParallel run = new RunParallel("data/test","data/test solutions");
    RunParallel parallel = new RunParallel("data/test","data/test solutions");

    @RepeatedTest(100)
    void runParallelTesting() {
        run.runTesting();
        parallel.runParallelTesting();

        Assertions.assertTrue(areCollectionsEqual(run.testing,parallel.testing));

    }

    public static boolean areCollectionsEqual(
            Collection<Collection<LexicographicalOrderDependency>> col1,
            Collection<Collection<LexicographicalOrderDependency>> col2) {

        if (col1.size() != col2.size()) return false;

        List<Set<String>> list1 = new ArrayList<>();
        for (Collection<LexicographicalOrderDependency> c : col1) {
            list1.add(new HashSet<>(c.stream().map(x -> x.toString()).collect(Collectors.toSet())));
        }

        List<Set<String>> list2 = new ArrayList<>();
        for (Collection<LexicographicalOrderDependency> c : col2) {
            list2.add(new HashSet<>(c.stream().map(x -> x.toString()).collect(Collectors.toSet())));
        }
        Set<Set<String>> s1 = new HashSet<>(list1);
        Set<Set<String>> s2 = new HashSet<>(list2);
        boolean tr = true;
        for (Set<String> st : s1){
            if(!s2.contains(st))
                tr = false;
        }

        return tr;
    }

    @Test
    void runTesting() {
    }
}