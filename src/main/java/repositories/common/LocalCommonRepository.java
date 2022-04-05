package repositories.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.Config;
import constants.EuropartyConstants.JsonFiles;

import java.io.IOException;

public class LocalCommonRepository {
    private final JsonFiles fileName;

    public LocalCommonRepository(JsonFiles fileName) {
        this.fileName = fileName;
    }

    public <T> T readJson(TypeReference<T> typeReference) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(Config.getResource(fileName.getFileName()), typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
