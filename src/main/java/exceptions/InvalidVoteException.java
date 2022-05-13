package exceptions;

import java.util.Set;

public class InvalidVoteException extends RuntimeException {

    public InvalidVoteException(Set<String> countryDuplicates, String juryName) {
        super("Vote: jury " + juryName + " voted some countries more than once: " +
                String.join(", ", countryDuplicates));
    }
}
