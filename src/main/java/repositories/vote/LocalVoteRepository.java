package repositories.vote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Votes;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LocalVoteRepository {

    public Map<String, Votes> getJuryVotes() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                new File("src/main/resources/votes.json"),
                new TypeReference<>() {
                });
    }
}
