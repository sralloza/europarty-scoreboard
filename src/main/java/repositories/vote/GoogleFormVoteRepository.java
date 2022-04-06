package repositories.vote;

import com.google.inject.Inject;
import config.Config;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Vote;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GoogleFormVoteRepository extends GoogleFormCommonRepository implements VoteRepository {
    private static final String SPREADSHEET_ID = Config.get("google.spreadsheets.vote.id");

    private final GSVoteMapper mapper;

    @Inject
    public GoogleFormVoteRepository(GSVoteMapper mapper) {
        super(SPREADSHEET_ID);
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