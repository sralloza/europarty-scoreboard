package repositories.jury;

import models.Jury;

import java.util.List;

public interface JuryRepository {
    List<Jury> getJuries();

    default Jury getByName(String name) {
        for (Jury jury : getJuries()) {
            if (jury.getName().equals(name)) {
                return jury;
            }
        }
        return null;
    }
}

