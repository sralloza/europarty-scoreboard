package config;

import exceptions.ConfigException;
import exceptions.JarResourceNotFoundException;
import exceptions.NormalResourceNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import static constants.EuropartyConstants.RESOURCES_PATH;

public class Config {
    public static final String SW_USERNAME = getString("SW_USERNAME", true);
    public static final String SW_PASSWORD = getString("SW_PASSWORD", true);
    public static final String SW_BASE_URL = getString("SW_BASE_URL", false, "http://scorewiz.eu");
    public static final String SW_SCOREBOARD_NAME = getString("SW_SCOREBOARD_NAME", false, "Europarty 2022");
    public static final boolean DEBUG = getBoolean("DEBUG", false, false);
    public static final String APPLICATION_NAME = getString("APPLICATION_NAME", false, "Europarty 2022");
    public static final String GOOGLE_CREDS_EMAIL = getString("GOOGLE_CREDS_EMAIL", true);
    public static final String GS_VOTE_ID = getString("GS_VOTE_ID", true);
    public static final String GS_TELEVOTE_ID = getString("GS_TELEVOTE_ID", true);


    private static boolean dotenvLoaded = false;

    private Config() {
    }

    private static void ensureDotenvLoaded() {
        if (dotenvLoaded) {
            return;
        }
        Dotenv.configure()
                .ignoreIfMissing()
                .systemProperties()
                .load();
        dotenvLoaded = true;
    }

    public static String getString(String key, boolean isRequired, String defaultValue) {
        ensureDotenvLoaded();
        if (!isRequired && defaultValue == null) {
            throw new RuntimeException("A non required value can't have null as default value: " + key);
        }
        var result = Optional.ofNullable(System.getenv(key))
                .orElse(System.getProperty(key));
        if (isRequired) {
            return Optional.ofNullable(result).orElseThrow(() -> new ConfigException(key));
        }
        return Optional.ofNullable(result).orElse(defaultValue);
    }

    public static String getString(String key, boolean isRequired) {
        return getString(key, isRequired, null);
    }

    public static boolean getBoolean(String key, boolean isRequired, Boolean defaultValue) {
        return Optional.ofNullable(getString(key, isRequired, defaultValue.toString()))
                .map(Boolean::parseBoolean).orElse(defaultValue);
    }

    public static boolean getBoolean(String key, boolean isRequired) {
        return getBoolean(key, isRequired, null);
    }

    @SneakyThrows
    public static InputStream getResource(String path) {
        final File jarFile = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if (jarFile.isFile()) {
            final String finalPath = "/" + path;
            return Optional.ofNullable(Config.class.getResourceAsStream("/" + path))
                    .orElseThrow(() -> new JarResourceNotFoundException(finalPath));
        } else {
            final String finalPath = RESOURCES_PATH + path;
            InputStream stream = new FileInputStream(finalPath);
            return Optional.of(stream)
                    .orElseThrow(() -> new NormalResourceNotFoundException(finalPath));
        }
    }
}