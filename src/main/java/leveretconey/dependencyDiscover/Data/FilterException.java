package leveretconey.dependencyDiscover.Data;

public class FilterException extends Exception{
    public FilterException(){
        super("The Entire Dataset was filtered, please try again with the filter deactivated.");
    }

    public FilterException(String message) {
        super("The entire Dataset \"" + message + "\" was filtered, please try again with the filter deactivated.");
    }
}
