package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ParticipantRepository {

    public List<String> getParticipants() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                new File("src/main/resources/participants.json"),
                new TypeReference<>() {
                });
    }
}
