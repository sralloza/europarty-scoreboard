package validators;

import com.google.inject.Inject;
import exceptions.ValidationException;
import models.Jury;
import models.Participant;
import models.Televote;
import models.Vote;
import validators.helpers.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalValidator {
    private final JuryValidationService juriesValidator;
    private final VotesValidator votesValidator;
    private final TelevotesValidationService televotesValidator;
    private final ScoreboardValidationService scoreboardNameValidator;

    @Inject
    public GlobalValidator(JuryValidationService juriesValidator,
                           VotesValidator votesValidator,
                           TelevotesValidationService televotesValidator,
                           ScoreboardValidationService scoreboardNameValidator) {
        this.juriesValidator = juriesValidator;
        this.votesValidator = votesValidator;
        this.televotesValidator = televotesValidator;
        this.scoreboardNameValidator = scoreboardNameValidator;
    }

    public void validateData(List<Jury> juries,
                             List<Participant> requestedParticipants,
                             List<Vote> juryVotes,
                             List<Televote> televotes) {
    }

    public void validateJuries(List<Jury> juries) {
        List<ValidationResult> results = juries.stream()
                .map(juriesValidator::validate)
                .filter(ValidationResult::notValid)
                .collect(Collectors.toList());

        if (!results.isEmpty()) {
            throw new ValidationException(results);
        }
    }

    public void validateVotes(List<Participant> requestedParticipants, List<Jury> juries, List<Vote> juryVotes) {
        votesValidator.validate(requestedParticipants, juries, juryVotes);
    }

    public void validateTelevotes(List<Televote> televotes) {
        List<ValidationResult> results = televotes.stream()
                        .map(televotesValidator::validate)
                .filter(ValidationResult::notValid)
                                .collect(Collectors.toList());
        if (!results.isEmpty()) {
            throw new ValidationException(results);
        }
    }

    public void validateScoreboardName(String name) {
        var result = scoreboardNameValidator.validate(name);
        if (result.notValid()) {
            throw new ValidationException(List.of(result));
        }
    }
}
