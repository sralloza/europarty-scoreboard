package models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class GoogleSheetsParticipant {
    private String countryName;
    private String juryLocalName;
    private String juryRealName;
    private boolean excluded = false;

    public GoogleSheetsParticipant(List<String> spreadsheetRow) {
        var VALID_SIZES = Set.of(1, 3, 4);
        if (!VALID_SIZES.contains(spreadsheetRow.size())) {
            throw new IllegalArgumentException("Spreadsheet row must have " + VALID_SIZES
                    + " columns (found " + spreadsheetRow.size() + ")");
        }

        countryName = spreadsheetRow.get(0);
        System.out.println(spreadsheetRow);
        if (spreadsheetRow.size() > 1) {
            juryLocalName = spreadsheetRow.get(1);
            juryRealName = spreadsheetRow.get(2);
        }
        if (spreadsheetRow.size() > 3) {
            excluded = spreadsheetRow.get(3).equalsIgnoreCase("true");
        }
    }
}