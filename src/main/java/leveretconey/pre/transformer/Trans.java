package leveretconey.pre.transformer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Interface used in the different Transformer-classes (Transformer,TransHTML,TransJSON and TransXML).
 * Forces the existence of a transform function which is necessary for the transformation steps.
 * As well as two formatting functions.
 */
public interface Trans {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * Transforms the input File into a csv-File.
     * @param data Path of the file that gets transformed.
     * @throws IOException
     */
    public void transform(String data)throws IOException;

    /**
     * As Strings in the csv have the format " "String" " and BigDecimals or Dates " 1 ", " 2000.04.23 " its necessary
     * to remove the quotation marks for BigDecimals and Dates.
     * @param text String input that gets parsed if possible
     * @return A BigDecimal, date or String depending on the format of the input String.
     */
    public default Object parseValue(String text) {
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            try {
                return DATE_FORMAT.parse(text);
            } catch (ParseException pe) {
                return text;
            }
        }
    }

    /**
     * As Strings in the csv have the format " "String" " and BigDecimals or Dates " 1 ", " 2000.04.23 " its necessary
     * to remove the quotation marks for BigDecimals and Dates.
     * @param text input text to be formatted.
     * @return A formatted String
     */
    public default String format(String text){
        StringBuilder sb = new StringBuilder();
        Object value = parseValue(text);
        if (value instanceof BigDecimal) {
            sb.append(((BigDecimal) value).toPlainString()).append(',');
        } else if (value instanceof Date) {
            sb.append(DATE_FORMAT.format((Date) value)).append(',');
        } else {
            sb.append('"').append(text).append('"').append(',');
        }
        return sb.toString();
    }
}
