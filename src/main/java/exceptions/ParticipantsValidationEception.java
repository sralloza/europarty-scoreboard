package exceptions;

import java.util.List;

public class ParticipantsValidationEception extends RuntimeException {
    public ParticipantsValidationEception(List<String> requestedParticipants, List<String> existingParticipants) {
        super("Requested participants are not the same as in the database: "
                + requestedParticipants + " vs " + existingParticipants);
    }
}
