package exceptions;

public class NormalResourceNotFoundException extends RuntimeException {
    public NormalResourceNotFoundException(String path) {
        super("Resource not found outside jar: " + path);
    }
}
