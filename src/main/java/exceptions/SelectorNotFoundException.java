package exceptions;

public class SelectorNotFoundException extends RuntimeException {
    public SelectorNotFoundException(String participant) {
        super("No selector found for " + participant);
    }
}
