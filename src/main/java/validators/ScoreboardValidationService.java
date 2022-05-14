package validators;

import validators.helpers.ValidationResult;

public interface ScoreboardValidationService {
    ValidationResult validate(String scoreboardName);
}
