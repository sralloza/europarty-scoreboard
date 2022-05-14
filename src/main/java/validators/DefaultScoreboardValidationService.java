package validators;

import validators.helpers.ValidationResult;
import validators.helpers.ValidationStep;

public class DefaultScoreboardValidationService implements ScoreboardValidationService {
    @Override
    public ValidationResult validate(String scoreboardName) {
        return new InvalidCharactersInNameValidationStep().run(scoreboardName);
    }

    private static class InvalidCharactersInNameValidationStep extends ValidationStep<String> {
        @Override
        public ValidationResult verify(String toValidate) {
            if (toValidate.contains("\"") || toValidate.contains("'")) {
                return ValidationResult.invalid("Invalid name, contains a quote: '" + toValidate + "'");
            }
            return checkNext(toValidate);
        }
    }
}
