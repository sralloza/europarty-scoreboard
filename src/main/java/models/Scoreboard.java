package models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Scoreboard {
    private Integer sid;
    private String pass;
}
