package exceptions;

public class JarResourceNotFoundException extends RuntimeException {
    public JarResourceNotFoundException(String path) {
        super("Resource not found inside jar: " + path);
    }
}
