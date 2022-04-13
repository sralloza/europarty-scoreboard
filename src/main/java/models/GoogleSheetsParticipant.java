package models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class GoogleSheetsParticipant {
    private String countryName;
    private String juryLocalName;
    private String juryRealName;

    public GoogleSheetsParticipant(List<String> spreadsheetRow) {
        if (spreadsheetRow.size() != 3 && spreadsheetRow.size() != 1) {
            System.out.println(spreadsheetRow);
            throw new IllegalArgumentException("Spreadsheet row must have 1 or 3 columns (found " + spreadsheetRow.size() + ")");
        }

        countryName = spreadsheetRow.get(0);
        if (spreadsheetRow.size() == 3) {
            juryLocalName = spreadsheetRow.get(1);
            juryRealName = spreadsheetRow.get(2);
        }
    }
}
