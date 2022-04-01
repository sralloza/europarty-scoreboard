package repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Televote;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TelevoteRepository {

    public List<Televote> getTelevotes() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Integer> rawTelevotes = mapper.readValue(
                new File("src/main/resources/televotes.json"),
                new TypeReference<>() {
                });
        return rawTelevotes.entrySet().stream()
                .map(entry -> new Televote(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
