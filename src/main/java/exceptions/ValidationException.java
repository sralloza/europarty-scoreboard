package exceptions;

import com.beust.jcommander.Strings;
import validators.helpers.ValidationResult;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {
    public ValidationException(List<ValidationResult> results) {
        super("\nValidation failed:" + Strings.join("", results.stream()
                .filter(ValidationResult::notValid)
                .map(ValidationResult::getErrorMsg)
                .map(t -> "\n - " + t)
                .sorted()
                .collect(Collectors.toList())) + "\n");
    }
}
