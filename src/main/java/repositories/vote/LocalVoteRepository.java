package repositories.vote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Vote;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LocalVoteRepository implements VoteRepository {

    public List<Vote> getJuryVotes() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(
                    new File("src/main/resources/votes.json"),
                    new TypeReference<>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
