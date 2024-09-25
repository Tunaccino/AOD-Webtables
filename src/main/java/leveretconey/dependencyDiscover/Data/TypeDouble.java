package leveretconey.dependencyDiscover.Data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TypeDouble extends AbstractType{
    private List<String> nullRep = Arrays.asList("","-","null","NAN","NA","N/A","NULL","_","?","??","Unknown","unknown","--","n/a",".","(n/a)","---");

    public TypeDouble() {
        super("[\\+-]?(([1-9]\\d*|0)(\\.\\d+)?|(\\.\\d+))([eE][\\+-]?(([1-9]\\d*)|0))?");

    }

    @Override
    public Double parse(String s) {
        if(nullRep.contains(s))
            return null;
        return Double.parseDouble(s);
    }

    @Override
    public Comparator<Double> getComparator() {
        return Double::compareTo;
    }
}
