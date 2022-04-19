package config;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import exceptions.JarResourceNotFoundException;
import exceptions.NormalResourceNotFoundException;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import static constants.EuropartyConstants.RESOURCES_PATH;

public class ConfigRepository {
    private final Config config;

    @Inject
    private ConfigRepository(Config config) {
        this.config = config;
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }


    @SneakyThrows
    public static InputStream getResource(String path) {
        final File jarFile = new File(ConfigRepository.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if (jarFile.isFile()) {
            final String finalPath = "/" + path;
            return Optional.ofNullable(ConfigRepository.class.getResourceAsStream("/" + path))
                    .orElseThrow(() -> new JarResourceNotFoundException(finalPath));
        } else {
            final String finalPath = RESOURCES_PATH + path;
            InputStream stream = new FileInputStream(finalPath);
            return Optional.of(stream)
                    .orElseThrow(() -> new NormalResourceNotFoundException(finalPath));
        }
    }
}