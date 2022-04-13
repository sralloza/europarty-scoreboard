package models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Participant {
    private String name;
    private boolean excluded = false;
}
