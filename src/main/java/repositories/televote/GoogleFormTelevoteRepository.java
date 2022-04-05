package repositories.televote;

import com.google.inject.Inject;
import config.Config;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Televote;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;

public class GoogleFormTelevoteRepository extends GoogleFormCommonRepository implements TelevoteRepository {
    private static final String SPREADSHEET_ID = Config.get("google.spreadsheets.televote.id");

    private final GSVoteMapper mapper;

    @Inject
    public GoogleFormTelevoteRepository(GSVoteMapper mapper) {
        super(SPREADSHEET_ID);
        this.mapper = mapper;
    }

    @Override
    public List<Televote> getTelevotes() {
        List<GoogleSheetsVote> googleSheetsVotes = getGoogleSheetsVotes();
        return mapper.buildTelevotes(googleSheetsVotes);
    }
}
