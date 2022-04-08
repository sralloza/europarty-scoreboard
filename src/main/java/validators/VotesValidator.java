package validators;

import exceptions.CountryNotFoundException;
import models.Vote;

import java.util.List;

public class VotesValidator {

    public void validate(List<String> savedParticipants, List<Vote> juryVotes) {
        juryVotes.forEach(vote -> vote.getAllPoints().forEach(s -> {
            if (!savedParticipants.contains(s)) {
                throw new CountryNotFoundException(s, vote.getJuryName());
            }
        }));
    }
}
