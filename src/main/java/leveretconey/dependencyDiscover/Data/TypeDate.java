package leveretconey.dependencyDiscover.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TypeDate extends AbstractType{

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM.dd.yyyy");

    public TypeDate(){
        super("^(0[1-9]|1[0-2])\\.(0[1-9]|[12][0-9]|3[01])\\.\\d{4}$");
    }

    @Override
    public Object parse(String s) {
        try {
            if (s.equals("")) {
                return DATE_FORMAT.parse("01.01.1970");
            }
            return DATE_FORMAT.parse(s);
        }catch (ParseException e){
            System.out.println("Can not parse: \"" + s + "\" into Simple Date Format MM.dd.yyyy.");
            return null;
        }
    }

    @Override
    public Comparator<Date> getComparator() {
        return Date::compareTo;
    }
}
