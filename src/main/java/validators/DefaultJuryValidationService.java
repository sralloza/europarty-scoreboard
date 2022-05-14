package validators;

import lombok.AllArgsConstructor;
import models.Jury;
import models.Participant;
import repositories.participant.ParticipantRepository;

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
        return new AssignedInvalidCountry(participantRepository).verify(jury);
    }

    @AllArgsConstructor
    private static class AssignedInvalidCountry extends ValidationStep<Jury> {
        private final ParticipantRepository participantRepository;

        @Override
        public ValidationResult verify(Jury jury) {
            List<Participant> participants = participantRepository.getParticipants();
            boolean countryNotFound = participants.stream()
                    .map(Participant::getName)
                    .noneMatch(e -> e.equals(jury.getCountry()));
            if (countryNotFound) {
                return ValidationResult.invalid("Jury " + jury.getName() +
                        " is assigned to an invalid country: " + jury.getCountry());
            }
            return ValidationResult.valid();
        }
    }
}
