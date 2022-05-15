package validators;

import lombok.AllArgsConstructor;
import models.Jury;
import models.Participant;
import repositories.participant.ParticipantRepository;
import validators.helpers.ValidationResult;
import validators.helpers.ValidationStep;

import javax.inject.Inject;
import java.util.List;

public class DefaultJuryValidationService implements JuryValidationService {
    private final ParticipantRepository participantRepository;

    @Inject
    public DefaultJuryValidationService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public ValidationResult validate(Jury jury) {
        return new AssignedInvalidCountry(participantRepository).run(jury);
    }

    @AllArgsConstructor
    private static class AssignedInvalidCountry extends ValidationStep<Jury> {
        private final ParticipantRepository participantRepository;

        @Override
        public ValidationResult verify(Jury toValidate) {
            List<Participant> participants = participantRepository.getParticipants();
            boolean countryNotFound = participants.stream()
                    .map(Participant::getName)
                    .noneMatch(e -> e.equals(toValidate.getCountry()));
            if (countryNotFound) {
                return ValidationResult.invalid("Jury " + toValidate.getName() +
                        " is assigned to an invalid country: " + toValidate.getCountry());
            }
            return checkNext(toValidate);
        }
    }
}
