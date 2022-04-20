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
    private int votingOrder = 0;

    public GoogleSheetsParticipant(List<String> spreadsheetRow) {
        /* Valid sizes:
         *  1: Countries without jury assigned
         *  3: Countries with jury assigned
         *  4: Countries excluded from being voted for
         *  5: Countries with a voting order set.
         */
        var VALID_SIZES = Set.of(1, 3, 4, 5);

        if (!VALID_SIZES.contains(spreadsheetRow.size())) {
            throw new IllegalArgumentException("Spreadsheet row must have " + VALID_SIZES
                    + " columns (found " + spreadsheetRow.size() + ")");
        }

        countryName = spreadsheetRow.get(0);
        if (spreadsheetRow.size() > 1) {
            juryLocalName = emptyStringToNull(spreadsheetRow.get(1));
            juryRealName = emptyStringToNull(spreadsheetRow.get(2));
        }
        if (spreadsheetRow.size() > 3) {
            excluded = spreadsheetRow.get(3).equalsIgnoreCase("true");
        }
        if (spreadsheetRow.size() > 4) {
            votingOrder = Integer.parseInt(spreadsheetRow.get(4));
        }
    }

    private String emptyStringToNull(String s) {
        return s.isBlank() ? null : s;
    }
}