package utils;

import java.util.Set;

public class BooleanUtils {
    private final static Set<String> TRUE_VALUES = Set.of("true", "yes", "1", "on", "si", "sí");
    private final static Set<String> FALSE_VALUES = Set.of("false", "no", "0", "off");

    public static boolean stringToBoolean(String value) {
        value = value.toLowerCase();

        if (TRUE_VALUES.contains(value)) {
            return true;
        }
        if (FALSE_VALUES.contains(value)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid boolean value: " + value);
    }
}
