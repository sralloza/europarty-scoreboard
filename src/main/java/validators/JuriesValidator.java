package validators;

import exceptions.CountryNotFoundException;
import models.Jury;
import models.Participant;

import java.util.List;

public class JuriesValidator {

    public void validate(List<Participant> savedParticipants, List<Jury> juries) {
        juries.forEach(jury -> {
            if (savedParticipants.stream().map(Participant::getName).noneMatch(e -> e.equals(jury.getCountry()))) {
                throw new CountryNotFoundException(jury);
            }
        });
    }
}
