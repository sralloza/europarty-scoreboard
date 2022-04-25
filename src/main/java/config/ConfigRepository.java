package config;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigRepository {
    private final Config config;

    @Inject
    private ConfigRepository(Config config) {
        this.config = config;
        log.debug("Config loaded: {}", config);
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }
}