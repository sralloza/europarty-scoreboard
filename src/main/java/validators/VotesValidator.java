package validators;

import exceptions.CountryNotFoundException;
import exceptions.JuryNotFoundException;
import models.Jury;
import models.Vote;

import java.util.List;

public class VotesValidator {

    public void validate(List<String> savedParticipants, List<Jury> juries, List<Vote> juryVotes) {
        juryVotes.forEach(vote -> vote.getAllPoints().forEach(s -> {
            if (!savedParticipants.contains(s)) {
                throw new CountryNotFoundException(s, vote.getJuryName());
            }
            if (juries.stream().noneMatch(j -> j.getName().equals(vote.getJuryName()))) {
                throw new JuryNotFoundException(vote.getJuryName(), "validating votes");
            }
        }));
    }
}
