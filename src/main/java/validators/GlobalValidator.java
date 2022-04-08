package validators;

import com.google.inject.Inject;
import models.Jury;
import models.Vote;

import java.util.List;

public class GlobalValidator {
    private final JuriesValidator juriesValidator;
    private final VotesValidator votesValidator;

    @Inject
    public GlobalValidator(JuriesValidator juriesValidator, VotesValidator votesValidator) {
        this.juriesValidator = juriesValidator;
        this.votesValidator = votesValidator;
    }

    public void validateJuries(List<String> savedParticipants, List<Jury> juries) {
        juriesValidator.validate(savedParticipants, juries);
    }

    public void validateVotes(List<String> requestedParticipants, List<Vote> juryVotes) {
        votesValidator.validate(requestedParticipants, juryVotes);
    }
}