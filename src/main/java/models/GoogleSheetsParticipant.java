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
    private boolean finalist;
    private int votingOrder = 0;

    public GoogleSheetsParticipant(List<String> spreadsheetRow) {
        /* Valid sizes:
         *  4: Countries without a voting order set.
         *  5: Countries with a voting order set.
         */
        var VALID_SIZES = Set.of(4, 5);

        if (!VALID_SIZES.contains(spreadsheetRow.size())) {
            throw new IllegalArgumentException("Spreadsheet row must have " + VALID_SIZES
                    + " columns (found " + spreadsheetRow.size() + ")");
        }

        countryName = spreadsheetRow.get(0);

        juryLocalName = emptyStringToNull(spreadsheetRow.get(1));
        juryRealName = emptyStringToNull(spreadsheetRow.get(2));

        finalist = getFinalist(spreadsheetRow);

        if (spreadsheetRow.size() > 4) {
            votingOrder = Integer.parseInt(spreadsheetRow.get(4));
        }
    }

    private boolean getFinalist(List<String> spreadsheetRow) {
        var finalistString = spreadsheetRow.get(3).toLowerCase();

        switch (finalistString) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new IllegalArgumentException("Finalist must be true or false (received: " + finalistString
                        + ", spreadsheet row: " + spreadsheetRow + ")");
        }
    }

    private String emptyStringToNull(String s) {
        return s.isBlank() ? null : s;
    }
}
