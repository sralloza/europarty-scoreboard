package exceptions;

public class NoValidVotesFoundException extends RuntimeException {
    public NoValidVotesFoundException(String s) {
        super(s);
    }
}
