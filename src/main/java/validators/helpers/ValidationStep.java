package validators.helpers;

public abstract class ValidationStep<T> {

    private ValidationStep<T> previousStep;
    private ValidationStep<T> nextStep;

    public ValidationStep<T> linkWith(ValidationStep<T> next) {
        this.nextStep = next;
        next.previousStep = this;
        return next;
    }

    public abstract ValidationResult verify(T toValidate);

    public ValidationResult run (T toValidate) {
        if (previousStep != null) {
            return previousStep.run(toValidate);
        }
        return verify(toValidate);
    }

    protected ValidationResult checkNext(T toValidate) {
        if (nextStep == null) {
            return ValidationResult.valid();
        }

        return nextStep.verify(toValidate);
    }
}
