package repositories.televote;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Televote;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;

import static constants.GoogleSheetsConstants.GS_VOTES_RANGE;

@Slf4j
public class GoogleFormTelevoteRepository implements TelevoteRepository {
    private final GSVoteMapper mapper;
    private final GoogleFormCommonRepository googleRepository;

    @Inject
    public GoogleFormTelevoteRepository(GSVoteMapper mapper,
                                        GoogleFormCommonRepository googleRepository) {
        this.mapper = mapper;
        this.googleRepository = googleRepository;
    }

    @Override
    public List<Televote> getTelevotes() {
        List<GoogleSheetsVote> googleSheetsVotes = googleRepository.getGoogleSheetsVotes();
        log.debug("Fetched {} televotes in Google Sheets", googleSheetsVotes.size());
        return mapper.buildTelevotes(googleSheetsVotes);
    }
}
