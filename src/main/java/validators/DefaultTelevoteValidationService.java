package validators;

import lombok.AllArgsConstructor;
import models.Participant;
import models.Televote;
import repositories.participant.ParticipantRepository;
import validators.helpers.ValidationResult;
import validators.helpers.ValidationStep;

import javax.inject.Inject;
import java.util.Optional;

public class DefaultTelevoteValidationService implements TelevotesValidationService {
    private final ParticipantRepository participantRepository;

    @Inject
    public DefaultTelevoteValidationService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public ValidationResult validate(Televote televote) {
        return new CountryVotedValidationStep(participantRepository)
                .linkWith(new CountryNotInFinalValidationStep(participantRepository))
                .run(televote);
    }

    @AllArgsConstructor
    private static class CountryVotedValidationStep extends ValidationStep<Televote> {
        private final ParticipantRepository participantRepository;

        @Override
        public ValidationResult verify(Televote toValidate) {
            Optional<Participant> participantOpt = participantRepository.getParticipants().stream()
                    .filter(p -> toValidate.getCountry().equals(p.getName()))
                    .findAny();
            if (participantOpt.isEmpty()) {
                return ValidationResult.invalid("Televote voting for non-existent country: " + toValidate.getCountry());
            }
            return checkNext(toValidate);
        }
    }

    @AllArgsConstructor
    private static class CountryNotInFinalValidationStep extends ValidationStep<Televote>{
        private final ParticipantRepository participantRepository;

        @Override
        public ValidationResult verify(Televote toValidate) {
            Optional<Boolean> notInFinal = participantRepository.getParticipants().stream()
                    .filter(p -> toValidate.getCountry().equals(p.getName()))
                    .findAny()
                    .map(Participant::isFinalist);
            if (notInFinal.isPresent()) {
                if (!notInFinal.get()) {
                    return ValidationResult.invalid("Televote voting for eliminated country: " + toValidate.getCountry());
                }
            }
            return checkNext(toValidate);
        }
    }
}
