package repositories.vote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Votes;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LocalVoteRepository implements VoteRepositoryIface {

    public Map<String, Votes> getJuryVotes() {
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
