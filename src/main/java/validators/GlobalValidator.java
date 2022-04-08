package validators;

import com.google.inject.Inject;
import models.Jury;
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

    public void validateJuries(List<String> savedParticipants, List<Jury> juries) {
        juriesValidator.validate(savedParticipants, juries);
    }

    public void validateVotes(List<String> requestedParticipants, List<Vote> juryVotes) {
        votesValidator.validate(requestedParticipants, juryVotes);
    }

    public void validateTelevotes(List<String> requestedParticipants, List<Televote> televotes) {
        televotesValidator.validate(requestedParticipants, televotes);
    }
}