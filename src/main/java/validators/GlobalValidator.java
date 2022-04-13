package validators;

import com.google.inject.Inject;
import models.Jury;
import models.Participant;
import models.Televote;
import models.Vote;

import java.util.List;

public class GlobalValidator {
    private final JuriesValidator juriesValidator;
    private final VotesValidator votesValidator;
    private final TelevotesValidator televotesValidator;

    @Inject
    public GlobalValidator(JuriesValidator juriesValidator,
                           VotesValidator votesValidator,
                           TelevotesValidator televotesValidator) {
        this.juriesValidator = juriesValidator;
        this.votesValidator = votesValidator;
        this.televotesValidator = televotesValidator;
    }

    public void validateJuries(List<Participant> savedParticipants, List<Jury> juries) {
        juriesValidator.validate(savedParticipants, juries);
    }

    public void validateVotes(List<Participant> requestedParticipants, List<Jury> juries, List<Vote> juryVotes) {
        votesValidator.validate(requestedParticipants, juries, juryVotes);
    }

    public void validateTelevotes(List<Participant> requestedParticipants, List<Televote> televotes) {
        televotesValidator.validate(requestedParticipants, televotes);
    }
}