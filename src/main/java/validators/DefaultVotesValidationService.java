package validators;

import com.beust.jcommander.Strings;
import exceptions.JuryNotFoundException;
import lombok.AllArgsConstructor;
import models.Vote;
import repositories.jury.JuryRepository;
import validators.helpers.ValidationResult;
import validators.helpers.ValidationStep;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultVotesValidationService implements VotesValidationService {
    private final JuryRepository juryRepository;

    @Inject
    public DefaultVotesValidationService(JuryRepository juryRepository) {
        this.juryRepository = juryRepository;
    }

    @Override
    public ValidationResult validate(List<Vote> votes) {
        return new EachJuryMustVoteOnlyOnceValidationStep(juryRepository).run(votes);
    }

    @AllArgsConstructor
    private static class EachJuryMustVoteOnlyOnceValidationStep extends ValidationStep<List<Vote>> {
        private final JuryRepository juryRepository;

        @Override
        public ValidationResult verify(List<Vote> toValidate) {

            List<String> duplicateVotes = toValidate.stream()
                    .collect(Collectors.groupingBy(vote -> {
                        try {
                            return juryRepository.getByLocalName(vote.getJuryName()).getName();
                        } catch (JuryNotFoundException e) {
                            return "INVALID_JURY_NAME";
                        }
                    }))
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() > 1)
                    .map(Map.Entry::getKey)
                    .filter(key-> !"INVALID_JURY_NAME".equals(key))
                    .collect(Collectors.toList());

            if (!duplicateVotes.isEmpty()) {
                return ValidationResult.invalid("Detected duplicated votes by the following juries: " +
                        Strings.join(", ", duplicateVotes));
            }
            return checkNext(toValidate);
        }
    }
}
