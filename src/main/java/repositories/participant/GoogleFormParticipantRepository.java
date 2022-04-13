package repositories.participant;

import com.google.inject.Inject;
import mappers.GSParticipantMapper;
import models.GoogleSheetsParticipant;
import models.Jury;
import repositories.common.GoogleFormCommonRepository;
import repositories.jury.JuryRepository;

import java.util.List;

import static config.Config.GS_PARTICIPANTS_ID;
import static constants.GoogleSheetsConstants.GS_PARTICIPANTS_RANGE;

public class GoogleFormParticipantRepository extends GoogleFormCommonRepository implements ParticipantRepository {
    private static final String SPREADSHEET_ID = GS_PARTICIPANTS_ID;

    private final GSParticipantMapper mapper;

    @Inject
    public GoogleFormParticipantRepository(GSParticipantMapper mapper) {
        super(SPREADSHEET_ID, GS_PARTICIPANTS_RANGE);
        this.mapper = mapper;
    }

    @Override
    public List<String> getParticipants() {
        List<GoogleSheetsParticipant> googleSheetsParticipants = getGoogleSheetsParticipants();
        return mapper.buildParticipants(googleSheetsParticipants);
    }
}
