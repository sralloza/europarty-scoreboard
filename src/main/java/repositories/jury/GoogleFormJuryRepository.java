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
public class GoogleFormJuryRepository extends GoogleFormCommonRepository implements JuryRepository {

    private final GSParticipantMapper mapper;
    private List<Jury> resultCached;

    @Inject
    public GoogleFormJuryRepository(GSParticipantMapper mapper, ConfigRepository configRepository) {
        super("googleSheets.sheetIDs.participants", GS_PARTICIPANTS_RANGE, configRepository);
        this.mapper = mapper;
    }

    @Override
    public List<Jury> getJuries() {
        if (resultCached != null) {
            return resultCached;
        }
        List<GoogleSheetsParticipant> googleSheetsParticipants = getGoogleSheetsParticipants();
        resultCached = mapper.buildJuries(googleSheetsParticipants);
        log.debug("Fetched {} juries from Google Sheets", resultCached.size());
        return resultCached;
    }
}
