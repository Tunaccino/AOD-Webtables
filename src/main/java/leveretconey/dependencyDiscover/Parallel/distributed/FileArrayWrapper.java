package leveretconey.dependencyDiscover.Parallel.distributed;

import java.io.File;
import java.io.Serializable;

public class FileArrayWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    private File[] files;

    public FileArrayWrapper(File[] files) {
        this.files = files;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
}