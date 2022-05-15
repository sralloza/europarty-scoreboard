package exceptions;

import models.Televote;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(Televote televote) {
        super("Country not found for televote " + televote);
    }
}
