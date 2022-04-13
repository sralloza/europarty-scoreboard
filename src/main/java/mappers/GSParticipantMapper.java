package mappers;

import models.GoogleSheetsParticipant;
import models.Jury;

import java.util.List;
import java.util.stream.Collectors;

public class GSParticipantMapper {
    public Jury buildJury(GoogleSheetsParticipant googleSheetsParticipant) {
        return new Jury()
                .setCountry(googleSheetsParticipant.getCountryName())
                .setLocalName(googleSheetsParticipant.getJuryLocalName())
                .setName(googleSheetsParticipant.getJuryRealName());

    }

    public List<Jury> buildJuries(List<GoogleSheetsParticipant> googleSheetsParticipants) {
        return googleSheetsParticipants.stream()
                .map(this::buildJury)
                .filter(jury -> jury.getName() != null)
                .filter(jury -> jury.getLocalName() != null)
                .collect(Collectors.toList());
    }

    public List<String> buildParticipants(List<GoogleSheetsParticipant> googleSheetsParticipants) {
        return googleSheetsParticipants.stream()
                .map(GoogleSheetsParticipant::getCountryName)
                .collect(Collectors.toList());
    }
}
