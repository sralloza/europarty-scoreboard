package repositories.participant;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Participant;
import repositories.common.LocalCommonRepository;

import java.util.List;

import static constants.EuropartyConstants.JsonFiles.PARTICIPANTS;

public class LocalParticipantRepository extends LocalCommonRepository implements ParticipantRepository {

    public LocalParticipantRepository() {
        super(PARTICIPANTS);
    }

    public List<Participant> getParticipants() {
        List<String> participants = readJson(new TypeReference<>() {
        });
        return participants.stream()
                .map(p -> new Participant().setName(p))
                .collect(java.util.stream.Collectors.toList());
    }
}
