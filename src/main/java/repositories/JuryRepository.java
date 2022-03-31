package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Jury;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JuryRepository {
    private List<Jury> juryList;

    public List<Jury> getJuries() throws IOException {
        if (juryList != null) {
            return juryList;
        }
        ObjectMapper mapper = new ObjectMapper();
        juryList = mapper.readValue(
                new File("src/main/resources/juries.json"),
                new TypeReference<>() {
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
