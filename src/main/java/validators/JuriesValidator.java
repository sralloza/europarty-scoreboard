package validators;

import exceptions.CountryNotFoundException;
import models.Jury;

import java.util.List;

public class JuriesValidator {

    public void validate(List<String> savedParticipants, List<Jury> juries) {
        juries.forEach(jury -> {
            if (!savedParticipants.contains(jury.getCountry())) {
                throw new CountryNotFoundException(jury);
            }
        });
    }
}
