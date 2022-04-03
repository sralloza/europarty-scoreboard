package repositories.vote;

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

    public GoogleFormVoteRepository() {
        super(SPREADSHEET_ID);
        mapper = new GSVoteMapper();
    }

    @Override
    public List<Vote> getJuryVotes() {
        List<GoogleSheetsVote> googleSheetsVotes = getGoogleSheetsVotes();
        return googleSheetsVotes.stream()
                .map(mapper::buildVote)
                .collect(Collectors.toList());
    }
}
