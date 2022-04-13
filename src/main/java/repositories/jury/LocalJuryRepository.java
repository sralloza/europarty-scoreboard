package repositories.jury;

import com.fasterxml.jackson.core.type.TypeReference;
import exceptions.JuryNotFoundException;
import models.Jury;
import repositories.common.LocalCommonRepository;

import java.util.List;

import static constants.EuropartyConstants.JsonFiles.JURIES;

public class LocalJuryRepository extends LocalCommonRepository implements JuryRepository {
    private List<Jury> juryList;

    public LocalJuryRepository() {
        super(JURIES);
    }

    public List<Jury> getJuries() {
        if (juryList != null) {
            return juryList;
        }
        juryList = readJson(new TypeReference<>() {
        });
        return juryList;
    }

    public Jury getByName(String name) {
        for (Jury jury : getJuries()) {
            if (jury.getName().equals(name)) {
                return jury;
            }
        }
        throw new JuryNotFoundException(name);
    }
}
