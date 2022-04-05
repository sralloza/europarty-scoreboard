package repositories.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.Config;

import java.io.IOException;

public class LocalCommonRepository {
    private final String fileName;

    public LocalCommonRepository(String fileName) {
        this.fileName = fileName;
    }

    public <T> T readJson(TypeReference<T> typeReference) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(Config.getResource(fileName), typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
