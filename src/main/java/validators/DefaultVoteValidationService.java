package validators;

import com.beust.jcommander.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import models.Jury;
import models.Participant;
import models.Vote;
import repositories.jury.JuryRepository;
import repositories.participant.ParticipantRepository;
import utils.SetUtils;
import validators.helpers.ValidationResult;
import validators.helpers.ValidationStep;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultVoteValidationService implements VoteValidationService {
    private final JuryRepository juryRepository;
    private final ParticipantRepository participantRepository;
    private final SetUtils setUtils;

    @Getter
    @Setter
    private boolean validatingTelevote = false;

    @Inject
    public DefaultVoteValidationService(JuryRepository juryRepository,
                                        ParticipantRepository participantRepository,
                                        SetUtils setUtils) {
        this.juryRepository = juryRepository;
        this.participantRepository = participantRepository;
        this.setUtils = setUtils;
    }

    @Override
    public ValidationResult validate(Vote vote) {
        return new JuryRegisteredValidationStep(juryRepository, validatingTelevote)
                .linkWith(new CountriesVotedRegisteredValidationStep(participantRepository))
                .linkWith(new CountriesVotedInFinalValidationStep(participantRepository))
                .linkWith(new VotesDifferentValidationStep(setUtils))
                .run(vote);
    }

    @AllArgsConstructor
    private static class JuryRegisteredValidationStep extends ValidationStep<Vote> {
        private final JuryRepository juryRepository;
        private final boolean skip;

        @Override
        public ValidationResult verify(Vote toValidate) {
            if (skip) {
                return checkNext(toValidate);
            }
            var juries = juryRepository.getJuries();

            Optional<Jury> jury = juries.stream()
                    .filter(j -> j.getLocalName().equalsIgnoreCase(toValidate.getJuryName()))
                    .findAny();

            if (jury.isEmpty()) {
                return ValidationResult.invalid("Vote registered to user that doesn't exist: " + toValidate.getJuryName());
            }
            return checkNext(toValidate);
        }
    }

    @AllArgsConstructor
    private static class CountriesVotedRegisteredValidationStep extends ValidationStep<Vote> {
        private final ParticipantRepository participantRepository;

        @Override
        public ValidationResult verify(Vote toValidate) {
            var participants = participantRepository.getParticipants();
            List<String> result = toValidate.getAllPoints().stream()
                    .map(countryVoted -> Map.entry(countryVoted, participants.stream()
                            .filter(p -> countryVoted.equals(p.getName()))
                            .findAny()))
                    .filter(entry -> entry.getValue().isPresent())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (result.isEmpty()) {
                return ValidationResult.invalid(toValidate.getJuryName() +
                        " voted for countries that are not registered: " + Strings.join(", ", result));
            }
            return checkNext(toValidate);
        }
    }

    @AllArgsConstructor
    private static class CountriesVotedInFinalValidationStep extends ValidationStep<Vote> {
        private final ParticipantRepository participantRepository;

        @Override
        public ValidationResult verify(Vote toValidate) {
            var participants = participantRepository.getParticipants();
            var votesNotInFinal = toValidate.getAllPoints().stream()
                    .map(countryVoted -> participants.stream()
                            .filter(p -> countryVoted.equals(p.getName()))
                            .findAny())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(participant -> !participant.isFinalist())
                    .map(Participant::getName)
                    .collect(Collectors.toList());

            if (!votesNotInFinal.isEmpty()) {
                return ValidationResult.invalid(toValidate.getJuryName() +
                        " voted for countries that are not finalists: " + Strings.join(", ", votesNotInFinal));
            }
            return checkNext(toValidate);
        }
    }

    @AllArgsConstructor
    private static class VotesDifferentValidationStep extends ValidationStep<Vote> {
        private final SetUtils setUtils;

        @Override
        public ValidationResult verify(Vote toValidate) {
            List<String> countriesVoted = toValidate.getAllPoints();
            List<String> duplicatedVotes = new ArrayList<>(setUtils.findDuplicates(countriesVoted));

            if (!duplicatedVotes.isEmpty()) {
                return ValidationResult.invalid(toValidate.getJuryName() +
                        " voted for duplicated countries: " + Strings.join(", ", duplicatedVotes));
            }
            return checkNext(toValidate);
        }
    }
}
