package exceptions;

import models.Participant;
import models.Televote;
import models.Vote;

public class ExcludedCountryException extends RuntimeException {
    public ExcludedCountryException(Participant participant, Televote televote) {
        super("Participant " + participant + " is excluded from voting (received in total "
                + televote.getVotes() + " votes in televote)");
    }

    public ExcludedCountryException(Participant participant, Vote vote) {
        super("Participant " + participant + " is excluded from voting (jury "
                + vote.getJuryName() + " voted for it)");
    }
}
