package validators;

import exceptions.CountryNotFoundException;
import exceptions.ExcludedCountryException;
import models.Participant;
import models.Televote;

import java.util.List;
import java.util.Optional;

public class TelevotesValidator {

    public void validate(List<Participant> savedParticipants, List<Televote> televotes) {
        televotes.forEach(televote -> {
            Optional<Participant> participantOpt = savedParticipants.stream()
                    .filter(p -> televote.getCountry().equals(p.getName()))
                    .findAny();
            if (participantOpt.isEmpty()) {
                throw new CountryNotFoundException(televote);
            }
            Participant participant = participantOpt.get();
            if (participant.isExcluded()) {
                throw new ExcludedCountryException(participant, televote);
            }
        });
    }
}
