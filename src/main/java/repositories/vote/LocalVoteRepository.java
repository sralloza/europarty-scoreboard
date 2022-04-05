package repositories.vote;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Vote;
import repositories.common.LocalCommonRepository;

import java.util.List;

import static constants.EuropartyConstants.JsonFiles.VOTES;

public class LocalVoteRepository extends LocalCommonRepository implements VoteRepository {

    public LocalVoteRepository() {
        super(VOTES);
    }

    public List<Vote> getJuryVotes() {
        return readJson(new TypeReference<>() {
        });
    }
}
