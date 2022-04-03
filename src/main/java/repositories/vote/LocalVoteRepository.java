package repositories.vote;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Vote;
import repositories.common.LocalCommonRepository;

import java.util.List;

public class LocalVoteRepository extends LocalCommonRepository implements VoteRepository {

    public LocalVoteRepository() {
        super("src/main/resources/votes.json");
    }

    public List<Vote> getJuryVotes() {
        return readJson(new TypeReference<>() {
        });
    }
}
