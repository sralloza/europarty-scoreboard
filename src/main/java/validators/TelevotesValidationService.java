package validators;

import models.Televote;
import validators.helpers.ValidationResult;

public interface TelevotesValidationService {
    ValidationResult validate(Televote televote);
}
