package models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Jury {
    private String country;
    private String localName;
    private String name;
    private int voteOrder;
}