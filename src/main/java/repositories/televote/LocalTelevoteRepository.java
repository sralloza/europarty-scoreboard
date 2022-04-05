package repositories.televote;

import com.fasterxml.jackson.core.type.TypeReference;
import models.Televote;
import repositories.common.LocalCommonRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static constants.EuropartyConstants.JsonFiles.TELEVOTES;

public class LocalTelevoteRepository extends LocalCommonRepository implements TelevoteRepository {

    public LocalTelevoteRepository() {
        super(TELEVOTES);
    }

    public List<Televote> getTelevotes() {
        Map<String, Integer> televotes = readJson(new TypeReference<>() {
        });
        return televotes.entrySet().stream()
                .map(entry -> new Televote(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}