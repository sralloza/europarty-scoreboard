package validators;

import models.Vote;
import validators.helpers.ValidationResult;

public interface VoteValidationService {
    ValidationResult validate(Vote vote);

    boolean isValidatingTelevote();

    void setValidatingTelevote(boolean validatingTelevote);
}
