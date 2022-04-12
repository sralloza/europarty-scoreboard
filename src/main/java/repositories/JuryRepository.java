package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import exceptions.JuryNameNotFoundException;
import models.Jury;
import repositories.common.LocalCommonRepository;

import java.util.List;

import static constants.EuropartyConstants.JsonFiles.JURIES;

public class JuryRepository extends LocalCommonRepository {
    private List<Jury> juryList;

    public JuryRepository() {
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
        throw new JuryNameNotFoundException(name);
    }
}
