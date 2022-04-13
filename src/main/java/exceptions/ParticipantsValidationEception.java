package exceptions;

import models.Participant;

import java.util.List;

public class ParticipantsValidationEception extends RuntimeException {
    public ParticipantsValidationEception(List<Participant> requestedParticipants,
                                          List<Participant> existingParticipants) {
        super("Requested participants are not the same as in the database: "
                + requestedParticipants + " vs " + existingParticipants);
    }
}
