package exceptions;

import java.io.File;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException(File file) {
        super("Could not delete file: " + file.getAbsolutePath());
    }
}
