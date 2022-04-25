package exceptions;

public class InvalidPrivateKeyException extends RuntimeException {
    public InvalidPrivateKeyException() {
        super("Invalid Private Key");
    }
}
