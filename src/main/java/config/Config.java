package config;

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
        System.out.println("Looking for resource: " + path);
        final File jarFile = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if (jarFile.isFile()) {
            System.out.println("Launching inside jar");
            final String finalPath = "/" + path;
            return Optional.ofNullable(Config.class.getResourceAsStream("/" + path))
                    .orElseThrow(() -> new RuntimeException("Resource not found inside jar: " + finalPath));
        } else {
            System.out.println("Launching outside jar");
            final String finalPath = "src/main/resources/" + path;
            InputStream stream = new FileInputStream(finalPath);
            return Optional.of(stream)
                    .orElseThrow(() -> new RuntimeException("Resource not found outside jar: " + finalPath));
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