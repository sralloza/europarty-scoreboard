package repositories.participant;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import mappers.GSParticipantMapper;
import models.GoogleSheetsParticipant;
import models.Participant;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;

import static constants.GoogleSheetsConstants.GS_PARTICIPANTS_RANGE;

@Slf4j
public class GoogleFormParticipantRepository extends GoogleFormCommonRepository implements ParticipantRepository {
    private final GSParticipantMapper mapper;

    @Inject
    public GoogleFormParticipantRepository(GSParticipantMapper mapper, ConfigRepository configRepository) {
        super("googleSheets.sheetIDs.participants", GS_PARTICIPANTS_RANGE, configRepository);
        this.mapper = mapper;
    }

    @Override
    public List<Participant> getParticipants() {
        List<GoogleSheetsParticipant> googleSheetsParticipants = getGoogleSheetsParticipants();
        log.debug("Fetched {} participants from Google Sheets", googleSheetsParticipants.size());
        return mapper.buildParticipants(googleSheetsParticipants);
    }
}
