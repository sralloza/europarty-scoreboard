package exceptions;

public class ConfigException extends RuntimeException {
    public ConfigException(String key) {
        super("Environment variable " + key + " not found and is required");
    }
}
