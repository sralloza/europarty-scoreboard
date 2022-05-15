package validators;

import models.Vote;
import validators.helpers.ValidationResult;

import java.util.List;

public interface VotesValidationService {
    ValidationResult validate(List<Vote> votes);
}
