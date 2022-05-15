package validators;

import models.Jury;
import validators.helpers.ValidationResult;

public interface JuryValidationService {
    ValidationResult validate(Jury jury);
}
