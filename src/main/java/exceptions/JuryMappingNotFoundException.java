package exceptions;

public class JuryMappingNotFoundException extends RuntimeException{
    public JuryMappingNotFoundException() {
        super("Jury Mapping exists but it's empty");
    }
}
