package models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class GoogleSheetsVote {
    private LocalDateTime timestamp;
    private String juryName;
    private String vote1Pt;
    private String vote2Pt;
    private String vote3Pt;
    private String vote4Pt;
    private String vote5Pt;
    private String vote6Pt;
    private String vote7Pt;
    private String vote8Pt;
    private String vote10Pt;
    private String vote12Pt;

    public GoogleSheetsVote(List<String> spreadsheetRow) {
        if (spreadsheetRow.size() != 12) {
            throw new IllegalArgumentException("Spreadsheet row must have 12 columns (found " + spreadsheetRow.size() + ")");
        }

        List<String> countries = spreadsheetRow.subList(2, spreadsheetRow.size());
        Set<String> duplicateCountries = countries.stream()
                .filter(i -> Collections.frequency(countries, i) > 1)
                .collect(Collectors.toSet());

        if (!duplicateCountries.isEmpty()) {
            throw new IllegalArgumentException("Spreadsheet row contains duplicate countries: " + duplicateCountries);
        }

        timestamp = LocalDateTime.parse(spreadsheetRow.get(0), DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm:ss"));
        juryName = spreadsheetRow.get(1);
        vote1Pt = spreadsheetRow.get(2);
        vote2Pt = spreadsheetRow.get(3);
        vote3Pt = spreadsheetRow.get(4);
        vote4Pt = spreadsheetRow.get(5);
        vote5Pt = spreadsheetRow.get(6);
        vote6Pt = spreadsheetRow.get(7);
        vote7Pt = spreadsheetRow.get(8);
        vote8Pt = spreadsheetRow.get(9);
        vote10Pt = spreadsheetRow.get(10);
        vote12Pt = spreadsheetRow.get(11);
    }
}
