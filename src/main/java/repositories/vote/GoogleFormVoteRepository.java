package repositories.vote;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Vote;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;
import java.util.stream.Collectors;

import static constants.GoogleSheetsConstants.GS_VOTES_RANGE;

@Slf4j
public class GoogleFormVoteRepository extends GoogleFormCommonRepository implements VoteRepository {
    private final GSVoteMapper mapper;

    @Inject
    public GoogleFormVoteRepository(GSVoteMapper mapper, ConfigRepository configRepository) {
        super("googleSheets.sheetIDs.votes", GS_VOTES_RANGE, configRepository);
        this.mapper = mapper;
    }

    @Override
    public List<Vote> getJuryVotes() {
        List<GoogleSheetsVote> googleSheetsVotes = getGoogleSheetsVotes();
        log.debug("Fetched {} votes from Google Sheets", googleSheetsVotes.size());
        return googleSheetsVotes.stream()
                .map(mapper::buildVote)
                .collect(Collectors.toList());
    }
}
