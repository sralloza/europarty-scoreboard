package validators;

public class ScoreboardNameValidator {
    public void validate(String name) {
        if (name.contains("\"") || name.contains("'")) {
            throw new RuntimeException("Invalid name, contains a quote");
        }
    }
}
