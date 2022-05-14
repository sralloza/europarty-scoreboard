package repositories.televote;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Televote;
import repositories.google.GoogleFormRepository;

import java.util.List;

@Slf4j
public class GoogleFormTelevoteRepository implements TelevoteRepository {
    private final GSVoteMapper mapper;
    private final GoogleFormRepository googleRepository;

    @Inject
    public GoogleFormTelevoteRepository(GSVoteMapper mapper,
                                        GoogleFormRepository googleRepository) {
        this.mapper = mapper;
        this.googleRepository = googleRepository;
    }

    @Override
    public List<Televote> getTelevotes() {
        List<GoogleSheetsVote> googleSheetsVotes = googleRepository.getGoogleSheetsTelevotes();
        log.debug("Fetched {} televotes in Google Sheets", googleSheetsVotes.size());
        return mapper.buildTelevotes(googleSheetsVotes);
    }
}
