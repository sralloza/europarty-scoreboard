package repositories.jury;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import mappers.GSParticipantMapper;
import models.GoogleSheetsParticipant;
import models.Jury;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;

import static constants.GoogleSheetsConstants.GS_PARTICIPANTS_RANGE;

@Slf4j
public class GoogleFormJuryRepository implements JuryRepository {

    private final GoogleFormCommonRepository googleRepository;
    private final GSParticipantMapper mapper;
    private List<Jury> resultCached;

    @Inject
    public GoogleFormJuryRepository(GSParticipantMapper mapper,
                                    GoogleFormCommonRepository googleRepository) {
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
