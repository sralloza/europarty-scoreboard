package repositories.jury;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import mappers.GSParticipantMapper;
import models.GoogleSheetsParticipant;
import models.Jury;
import repositories.google.GoogleFormRepository;

import java.util.List;

@Slf4j
public class GoogleFormJuryRepository implements JuryRepository {

    private final GoogleFormRepository googleRepository;
    private final GSParticipantMapper mapper;
    private List<Jury> resultCached;

    @Inject
    public GoogleFormJuryRepository(GSParticipantMapper mapper,
                                    GoogleFormRepository googleRepository) {
        this.mapper = mapper;
        this.googleRepository = googleRepository;
    }

    @Override
    public List<Jury> getJuries() {
        if (resultCached != null) {
            return resultCached;
        }
        List<GoogleSheetsParticipant> googleSheetsParticipants = googleRepository.getGoogleSheetsParticipants();
        resultCached = mapper.buildJuries(googleSheetsParticipants);
        log.debug("Fetched {} juries from Google Sheets", resultCached.size());
        return resultCached;
    }
}
