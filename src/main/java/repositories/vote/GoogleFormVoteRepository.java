package repositories.vote;

import com.google.inject.Inject;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Vote;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;
import java.util.stream.Collectors;

import static config.Config.GS_VOTE_ID;
import static constants.GoogleSheetsConstants.GS_VOTES_RANGE;

public class GoogleFormVoteRepository extends GoogleFormCommonRepository implements VoteRepository {
    private static final String SPREADSHEET_ID = GS_VOTE_ID;

    private final GSVoteMapper mapper;

    @Inject
    public GoogleFormVoteRepository(GSVoteMapper mapper) {
        super(SPREADSHEET_ID, GS_VOTES_RANGE);
        this.mapper = mapper;
    }

    @Override
    public List<Vote> getJuryVotes() {
        List<GoogleSheetsVote> googleSheetsVotes = getGoogleSheetsVotes();
        return googleSheetsVotes.stream()
                .map(mapper::buildVote)
                .collect(Collectors.toList());
    }
}
