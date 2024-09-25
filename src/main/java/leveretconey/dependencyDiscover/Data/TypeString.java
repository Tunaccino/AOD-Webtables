package leveretconey.dependencyDiscover.Data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TypeString extends AbstractType{
    private List<String> nullRep = Arrays.asList("","-","null","NAN","NA","N/A","NULL","_","?","??","Unknown","unknown","--","n/a",".","(n/a)","---");

    public TypeString() {
        super(".*");
    }

    @Override
    public boolean fitFormat(String s) {
        return true;
    }

    @Override
    public String parse(String s) {return (nullRep.contains(s)) ? null : s;}

    @Override
    public Comparator<String> getComparator() {return String::compareTo;}
}
