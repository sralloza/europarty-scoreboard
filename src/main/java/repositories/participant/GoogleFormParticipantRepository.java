package repositories.participant;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import mappers.GSParticipantMapper;
import models.GoogleSheetsParticipant;
import models.Participant;
import repositories.google.GoogleFormRepository;

import java.util.List;

@Slf4j
public class GoogleFormParticipantRepository implements ParticipantRepository {
    private final GSParticipantMapper mapper;
    private final GoogleFormRepository googleRepository;

    @Inject
    public GoogleFormParticipantRepository(GSParticipantMapper mapper,
                                           GoogleFormRepository googleRepository) {
        this.mapper = mapper;
        this.googleRepository = googleRepository;
    }

    @Override
    public List<Participant> getParticipants() {
        List<GoogleSheetsParticipant> googleSheetsParticipants = googleRepository.getGoogleSheetsParticipants();
        log.debug("Fetched {} participants from Google Sheets", googleSheetsParticipants.size());
        return mapper.buildParticipants(googleSheetsParticipants);
    }
}
