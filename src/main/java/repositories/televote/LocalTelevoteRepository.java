package repositories.televote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Televote;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalTelevoteRepository implements TelevoteRepository {

    public List<Televote> getTelevotes() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Integer> rawTelevotes = null;
        try {
            rawTelevotes = mapper.readValue(
                    new File("src/main/resources/televotes.json"),
                    new TypeReference<>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return rawTelevotes.entrySet().stream()
                .map(entry -> new Televote(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
