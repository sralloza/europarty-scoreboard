package validators;

import com.google.inject.Inject;
import models.Jury;

import java.util.List;

public class GlobalValidator {
    private final JuriesValidator juriesValidator;

    @Inject
    public GlobalValidator(JuriesValidator juriesValidator) {
        this.juriesValidator = juriesValidator;
    }

    public void validateJuries(List<String> savedParticipants, List<Jury> juries) {
        juriesValidator.validateJuries(savedParticipants, juries);
    }
}