package config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
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

    public String getSelector(String key) {
        return getString("scorewiz.selector." + key);
    }

    public String getStyleString(String key) {
        return getString("scorewiz.style." + key);
    }

    public boolean getStyleBoolean(String key) {
        return getBoolean("scorewiz.style." + key);
    }

    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }
}