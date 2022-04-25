package exceptions;

import models.Jury;
import models.Televote;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(Televote televote) {
        super("Country not found for televote " + televote);
    }

    public CountryNotFoundException(String country, String juryName) {
        super("Country " + country + " not found (jury " + juryName + " voted for it)");
    }

    public CountryNotFoundException(Jury jury) {
        this(jury.getCountry(), jury.getName());
    }

}
