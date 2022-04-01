package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Config instance;
    private final Properties properties;

    public Config() {
        properties = new Properties();
        try {
            InputStream input = new FileInputStream("src/main/resources/config.properties");
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }

        return instance;
    }

    public static String get(String key) {
        return getInstance().properties.getProperty(key);
    }
}