package models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SimpleJury {
    private String countryName;
    private String juryLocalName;
}
