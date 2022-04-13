package exceptions;

import models.Jury;

import java.util.Map;

public class JuryNotFoundException extends RuntimeException {
    public JuryNotFoundException(Jury jury, Map<String, String> juryVoteURLMap) {
        super("Can't find jury by name " + jury.getName() + " (juryVoteURLMap=" + juryVoteURLMap + ")");
    }

    public JuryNotFoundException(String juryName, String reason) {
        super("Can't find jury by name " + juryName + " (reason=" + reason + ")");
    }

    public JuryNotFoundException(String name) {
        super("Can't find jury by name " + name);
    }
}
