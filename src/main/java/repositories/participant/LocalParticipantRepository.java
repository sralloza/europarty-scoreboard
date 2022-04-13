package repositories.participant;

import com.fasterxml.jackson.core.type.TypeReference;
import repositories.common.LocalCommonRepository;

import java.util.List;

import static constants.EuropartyConstants.JsonFiles.PARTICIPANTS;

public class LocalParticipantRepository extends LocalCommonRepository implements ParticipantRepository {

    public LocalParticipantRepository() {
        super(PARTICIPANTS);
    }

    public List<String> getParticipants() {
        return readJson(new TypeReference<>() {
        });
    }
}
