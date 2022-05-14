package validators;

import models.Jury;

public interface JuryValidationService {
    ValidationResult validate(Jury jury);
}
