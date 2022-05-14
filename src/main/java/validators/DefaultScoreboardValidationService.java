package validators;

public class DefaultScoreboardValidationService implements ScoreboardValidationService {
    @Override
    public ValidationResult validate(String scoreboardName) {
        return new InvalidCharactersInNameValidationStep().verify(scoreboardName);
    }

    private static class InvalidCharactersInNameValidationStep extends ValidationStep<String> {
        @Override
        public ValidationResult verify(String scoreboardName) {
            if (scoreboardName.contains("\"") || scoreboardName.contains("'")) {
                return ValidationResult.invalid("Invalid name, contains a quote: '" + scoreboardName + "'");
            }
            return ValidationResult.valid();
        }
    }
}
