package exceptions;

import java.util.List;

public class TooManyVotesByOneJuryException extends RuntimeException {
    public TooManyVotesByOneJuryException(List<String> duplicates) {
        super("Found juries voting more than one time: " + String.join(", ", duplicates));
    }
}
