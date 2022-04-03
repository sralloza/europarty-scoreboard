package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import repositories.common.LocalCommonRepository;

import java.util.List;

public class ParticipantRepository extends LocalCommonRepository {

    public ParticipantRepository() {
        super("src/main/resources/participants.json");
    }

    public List<String> getParticipants() {
        return readJson(new TypeReference<>() {
        });
    }
}
