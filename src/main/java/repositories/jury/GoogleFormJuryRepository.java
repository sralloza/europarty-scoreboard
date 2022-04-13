package repositories.jury;

import com.google.inject.Inject;
import mappers.GSParticipantMapper;
import models.GoogleSheetsParticipant;
import models.Jury;
import repositories.common.GoogleFormCommonRepository;

import java.util.List;

import static config.Config.GS_PARTICIPANTS_ID;

public class GoogleFormJuryRepository extends GoogleFormCommonRepository implements JuryRepository {
    private static final String SPREADSHEET_ID = GS_PARTICIPANTS_ID;
    private static final String RANGE = "A:C";

    private final GSParticipantMapper mapper;
    private List<Jury> resultCached;

    @Inject
    public GoogleFormJuryRepository(GSParticipantMapper mapper) {
        super(SPREADSHEET_ID, RANGE);
        this.mapper = mapper;
    }

    @Override
    public List<Jury> getJuries() {
        if (resultCached != null) {
            return resultCached;
        }
        List<GoogleSheetsParticipant> googleSheetsParticipants = getGoogleSheetsParticipants();
        resultCached = mapper.buildJuries(googleSheetsParticipants);
        return resultCached;
    }
}
