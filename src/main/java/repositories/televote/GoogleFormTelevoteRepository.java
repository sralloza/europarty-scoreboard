package repositories.televote;

import com.google.inject.Inject;
import config.ConfigRepository;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Televote;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;

import static constants.GoogleSheetsConstants.GS_VOTES_RANGE;

public class GoogleFormTelevoteRepository extends GoogleFormCommonRepository implements TelevoteRepository {
    private final GSVoteMapper mapper;

    @Inject
    public GoogleFormTelevoteRepository(GSVoteMapper mapper, ConfigRepository configRepository) {
        super("googleSheets.sheetIDs.televotes", GS_VOTES_RANGE, configRepository);
        this.mapper = mapper;
    }

    @Override
    public List<Televote> getTelevotes() {
        List<GoogleSheetsVote> googleSheetsVotes = getGoogleSheetsVotes();
        return mapper.buildTelevotes(googleSheetsVotes);
    }
}
