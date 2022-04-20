package repositories.jury;

import exceptions.JuryNotFoundException;
import models.Jury;

import java.util.Comparator;
import java.util.List;

public interface JuryRepository {
    List<Jury> getJuries();

    default List<Jury> getJuriesSorted() {
        List<Jury> juries = getJuries();
        juries.sort(Comparator.comparingInt(Jury::getVoteOrder).reversed());
        return juries;
    }

    default Jury getByName(String name) {
        for (Jury jury : getJuries()) {
            if (jury.getName().equals(name)) {
                return jury;
            }
        }
        throw new JuryNotFoundException(name);
    }
}

