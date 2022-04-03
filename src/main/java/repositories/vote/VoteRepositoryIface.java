package repositories.vote;

import models.Votes;

import java.util.Map;

public interface VoteRepositoryIface {

    Map<String, Votes> getJuryVotes();
}
