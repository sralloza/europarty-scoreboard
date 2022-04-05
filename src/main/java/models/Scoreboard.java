package models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Scoreboard {
    private String sid;
    private String pass;
}
