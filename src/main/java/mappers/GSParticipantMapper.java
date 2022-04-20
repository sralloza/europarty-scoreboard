package mappers;

import models.GoogleSheetsParticipant;
import models.Jury;
import models.Participant;

import java.util.List;
import java.util.stream.Collectors;

public class GSParticipantMapper {
    public Jury buildJury(GoogleSheetsParticipant googleSheetsParticipant) {
        return new Jury()
                .setCountry(googleSheetsParticipant.getCountryName())
                .setLocalName(googleSheetsParticipant.getJuryLocalName())
                .setName(googleSheetsParticipant.getJuryRealName())
                .setVoteOrder(googleSheetsParticipant.getVotingOrder());
    }

    public List<Jury> buildJuries(List<GoogleSheetsParticipant> googleSheetsParticipants) {
        return googleSheetsParticipants.stream()
                .map(this::buildJury)
                .filter(jury -> jury.getName() != null)
                .filter(jury -> jury.getLocalName() != null)
                .collect(Collectors.toList());
    }

    public Participant buildParticipant(GoogleSheetsParticipant googleSheetsParticipant) {
        return new Participant()
                .setName(googleSheetsParticipant.getCountryName())
                .setExcluded(googleSheetsParticipant.isExcluded());
    }

    public List<Participant> buildParticipants(List<GoogleSheetsParticipant> googleSheetsParticipants) {
        return googleSheetsParticipants.stream()
                .map(this::buildParticipant)
                .collect(Collectors.toList());
    }
}
