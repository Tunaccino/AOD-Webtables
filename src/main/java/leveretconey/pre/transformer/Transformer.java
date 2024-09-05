package leveretconey.pre.transformer;

import java.io.IOException;

/**
 * Scans the file extension to decide which type of transformation process is necessary.
 */
public class Transformer implements Trans{
    TransHTML transHTML;
    TransXML transXML;
    TransJSON transJSON;

    /**
     * Automatically creates all supported transformers.
     * @throws IOException
     */
    public Transformer() throws IOException {
        transHTML = new TransHTML();
        transXML = new TransXML();
        transJSON = new TransJSON();
    }

    /**
     * Implementation of super function.
     * @param data Path of the file that gets transformed.
     * @throws IOException
     */
    @Override
    public void transform(String data) throws IOException {
        if(data.length() < 4){
            throw new ClassCastException("The data format " + data + " is currently not supported.");
        }

        String extension = data.substring(data.lastIndexOf("."));
        switch (extension){
            case ".xml":
                transXML.transform(data);
                break;

            case ".html":
                transHTML.transform(data);
                break;

            case ".json":
                transJSON.transform(data);
                break;

            case ".csv":

                break;

            default:
                throw new ClassCastException("The data format " + extension + " is currently not supported.");
        }
    }
}
