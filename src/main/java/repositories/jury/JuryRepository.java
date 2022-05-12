package repositories.jury;

import exceptions.JuryNotFoundException;
import models.Jury;

import java.util.Comparator;
import java.util.List;

public interface JuryRepository {
    List<Jury> getJuries();

    default List<Jury> getJuriesSorted() {
        List<Jury> juries = getJuries();
        juries.sort(Comparator.comparingInt(Jury::getVoteOrder));
        return juries;
    }

    default Jury getByLocalName(String localName) {
        for (Jury jury : getJuries()) {
            if (jury.getLocalName().equalsIgnoreCase(localName)) {
                return jury;
            }
        }
        throw new JuryNotFoundException(localName);
    }
}

