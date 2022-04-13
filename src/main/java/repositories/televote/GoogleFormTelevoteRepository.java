package repositories.televote;

import com.google.inject.Inject;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Televote;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;

import static config.Config.GS_TELEVOTE_ID;
import static constants.GoogleSheetsConstants.GS_VOTES_RANGE;

public class GoogleFormTelevoteRepository extends GoogleFormCommonRepository implements TelevoteRepository {
    private static final String SPREADSHEET_ID = GS_TELEVOTE_ID;

    private final GSVoteMapper mapper;

    @Inject
    public GoogleFormTelevoteRepository(GSVoteMapper mapper) {
        super(SPREADSHEET_ID, GS_VOTES_RANGE);
        this.mapper = mapper;
    }

    @Override
    public List<Televote> getTelevotes() {
        List<GoogleSheetsVote> googleSheetsVotes = getGoogleSheetsVotes();
        return mapper.buildTelevotes(googleSheetsVotes);
    }
}
