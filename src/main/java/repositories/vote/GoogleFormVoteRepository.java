package repositories.vote;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Vote;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GoogleFormVoteRepository implements VoteRepository {
    private final GSVoteMapper mapper;
    private final GoogleFormCommonRepository googleRepository;

    @Inject
    public GoogleFormVoteRepository(GSVoteMapper mapper,
                                    GoogleFormCommonRepository googleRepository) {
        this.mapper = mapper;
        this.googleRepository = googleRepository;
    }

    @Override
    public List<Vote> getJuryVotes() {
        List<GoogleSheetsVote> googleSheetsVotes = googleRepository.getGoogleSheetsVotes();
        log.debug("Fetched {} votes from Google Sheets", googleSheetsVotes.size());
        return googleSheetsVotes.stream()
                .map(mapper::buildVote)
                .collect(Collectors.toList());
    }
}
