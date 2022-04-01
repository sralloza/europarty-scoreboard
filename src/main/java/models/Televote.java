package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Televote {
    private String country;
    private Integer votes;
}
