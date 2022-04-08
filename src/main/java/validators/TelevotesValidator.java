package validators;

import exceptions.CountryNotFoundException;
import models.Televote;

import java.util.List;

public class TelevotesValidator {

    public void validate(List<String> savedParticipants, List<Televote> televotes) {
        televotes.forEach(televote -> {
            if (!savedParticipants.contains(televote.getCountry())) {
                throw new CountryNotFoundException(televote);
            }
        });
    }
}
