package leveretconey.dependencyDiscover.Data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TypeLong extends AbstractType{
    private List<String> nullRep = Arrays.asList("","-","null","NAN","NA","N/A","NULL","_","?","??","Unknown","unknown","--","n/a",".","(n/a)","---");

    public TypeLong() {
        super("[\\+-]?([1-9]\\d*|0)");
    }

    @Override
    public Long parse(String s) {
        if(nullRep.contains(s))
            return null;
        try {
            return Long.parseLong(s);
        }catch (Exception e){
            if(s.charAt(0)=='-')
                return Long.MIN_VALUE;
            else
                return Long.MAX_VALUE;
        }

    }

    @Override
    public Comparator<Long> getComparator() {
        return Long::compareTo;
    }
}
