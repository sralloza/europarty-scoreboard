package repositories.vote;

import models.Vote;

import java.util.List;

public interface VoteRepository {

    List<Vote> getJuryVotes();
}
