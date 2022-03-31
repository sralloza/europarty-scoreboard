package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class Votes {
    @JsonProperty("1")
    private String country1Points;

    @JsonProperty("2")
    private String country2Points;

    @JsonProperty("3")
    private String country3Points;

    @JsonProperty("4")
    private String country4Points;

    @JsonProperty("5")
    private String country5Points;

    @JsonProperty("6")
    private String country6Points;

    @JsonProperty("7")
    private String country7Points;

    @JsonProperty("8")
    private String country8Points;

    @JsonProperty("10")
    private String country10Points;

    @JsonProperty("12")
    private String country12Points;

    public Set<String> getAllPoints() {
        return Set.of(
                country1Points,
                country2Points,
                country3Points,
                country4Points,
                country5Points,
                country6Points,
                country7Points,
                country8Points,
                country10Points,
                country12Points);
    }

    public String getCountryByPoints(Integer points) {
        switch (points) {
            case 1:
                return country1Points;
            case 2:
                return country2Points;
            case 3:
                return country3Points;
            case 4:
                return country4Points;
            case 5:
                return country5Points;
            case 6:
                return country6Points;
            case 7:
                return country7Points;
            case 8:
                return country8Points;
            case 10:
                return country10Points;
            case 12:
                return country12Points;
            default:
                return "";
        }
    }
}
