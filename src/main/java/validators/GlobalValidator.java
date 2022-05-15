package validators;

import com.google.inject.Inject;
import exceptions.ValidationException;
import models.Jury;
import models.Televote;
import models.Vote;
import validators.helpers.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalValidator {
    private final JuryValidationService juriesValidator;
    private final VotesValidationService votesValidator;
    private final VoteValidationService voteValidator;
    private final TelevotesValidationService televotesValidator;
    private final ScoreboardValidationService scoreboardNameValidator;

    @Inject
    public GlobalValidator(JuryValidationService juriesValidator,
                           VotesValidationService votesValidator,
                           VoteValidationService voteValidator,
                           TelevotesValidationService televotesValidator,
                           ScoreboardValidationService scoreboardNameValidator) {
        this.juriesValidator = juriesValidator;
        this.votesValidator = votesValidator;
        this.voteValidator = voteValidator;
        this.televotesValidator = televotesValidator;
        this.scoreboardNameValidator = scoreboardNameValidator;
    }

    public void validateData(List<Jury> juries,
                             List<Vote> juryVotes,
                             List<Televote> televotes,
                             String scoreboardName) {
        var resultJuries = validateJuries(juries);
        var resultVotes = validateVotes(juryVotes);
        var resultTelevotes = validateTelevotes(televotes);
        var resultScoreboardName = validateScoreboardName(scoreboardName);

        List<ValidationResult> results = new ArrayList<>();
        results.addAll(resultJuries);
        results.addAll(resultVotes);
        results.addAll(resultTelevotes);
        results.addAll(resultScoreboardName);

        var filtered = results.stream()
                .filter(ValidationResult::notValid)
                .collect(Collectors.toList());

        if (!filtered.isEmpty()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
            throw new ValidationException(filtered);
        }
    }

    private List<ValidationResult> validateJuries(List<Jury> juries) {
        return juries.stream()
                .map(juriesValidator::validate)
                .collect(Collectors.toList());
    }

    private List<ValidationResult> validateVotes(List<Vote> juryVotes) {
        var result1 = votesValidator.validate(juryVotes);
        var results2 = juryVotes.stream()
                .map(voteValidator::validate);

        return Stream.concat(results2, Stream.of(result1))
                .collect(Collectors.toList());
    }

    private List<ValidationResult> validateTelevotes(List<Televote> televotes) {
        return televotes.stream()
                .map(televotesValidator::validate)
                .collect(Collectors.toList());
    }

    private List<ValidationResult> validateScoreboardName(String name) {
        return List.of(scoreboardNameValidator.validate(name));
    }
}
