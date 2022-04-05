package config;

import exceptions.JarResourceNotFoundException;
import exceptions.NormalResourceNotFoundException;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class Config {
    private static Config instance;
    private final Properties properties;

    public Config() {
        properties = new Properties();
        try {
            InputStream input = getResource("config.properties");
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @SneakyThrows
    public static InputStream getResource(String path) {
        final File jarFile = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if (jarFile.isFile()) {
            final String finalPath = "/" + path;
            return Optional.ofNullable(Config.class.getResourceAsStream("/" + path))
                    .orElseThrow(() -> new JarResourceNotFoundException(finalPath));
        } else {
            final String finalPath = "src/main/resources/" + path;
            InputStream stream = new FileInputStream(finalPath);
            return Optional.of(stream)
                    .orElseThrow(() -> new NormalResourceNotFoundException(finalPath));
        }
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public static String get(String key) {
        return getInstance().properties.getProperty(key);
    }
}