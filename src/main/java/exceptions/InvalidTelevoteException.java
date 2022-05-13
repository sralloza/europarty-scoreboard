package exceptions;

import java.util.Set;

public class InvalidTelevoteException extends RuntimeException {

    public InvalidTelevoteException(Set<String> countryDuplicates, String juryName) {
        super("Televote: jury " + juryName + " voted some countries more than once: " +
                String.join(", ", countryDuplicates));
    }
}
