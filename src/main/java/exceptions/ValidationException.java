package exceptions;

import com.beust.jcommander.Strings;
import validators.ValidationResult;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {
    public ValidationException(List<ValidationResult> results) {
        super("Validation failed: " + Strings.join("\n - ", results.stream()
                .filter(ValidationResult::notValid)
                .map(ValidationResult::getErrorMsg)
                .collect(Collectors.toList())));
    }
}
