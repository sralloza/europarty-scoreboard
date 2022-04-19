package exceptions;

import java.util.List;

public class DuplicateVoteException extends RuntimeException {
    public DuplicateVoteException(List<String> duplicates) {
        super("Found juries voting more than one time: " + String.join(", ", duplicates));
    }
}
