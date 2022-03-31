package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import models.Jury;

import java.io.File;
import java.util.List;

public class JuryRepository {

    @SneakyThrows
    public static List<Jury> getJuries() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                new File("src/main/resources/juries.json"),
                new TypeReference<>() {
                });
    }

}
