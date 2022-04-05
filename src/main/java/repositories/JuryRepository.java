package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import constants.EuropartyConstants;
import models.Jury;
import repositories.common.LocalCommonRepository;

import java.io.IOException;
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

    public Jury getByName(String name) throws IOException {
        for (Jury jury : getJuries()) {
            if (jury.getName().equals(name)) {
                return jury;
            }
        }
        return null;
    }
}
