package exceptions;

import models.Jury;

import java.util.Map;

public class JuryNameNotFoundException extends RuntimeException {
    public JuryNameNotFoundException(Jury jury, Map<String, String> juryMapping) {
        super("No jury name  found for " + jury + " in jury mapping " + juryMapping);
    }

    public JuryNameNotFoundException(String name) {
        super("No jury name  found for " + name);
    }
}
