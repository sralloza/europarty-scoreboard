package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import repositories.common.LocalCommonRepository;

import java.util.List;

import static constants.EuropartyConstants.JsonFiles.PARTICIPANTS;

public class ParticipantRepository extends LocalCommonRepository {

    public ParticipantRepository() {
        super(PARTICIPANTS);
    }

    public List<String> getParticipants() {
        return readJson(new TypeReference<>() {
        });
    }
}
