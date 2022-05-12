package mappers;

import lombok.extern.slf4j.Slf4j;
import models.GoogleSheetsParticipant;
import models.Jury;
import models.Participant;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class GSParticipantMapper {
    public Jury buildJury(GoogleSheetsParticipant googleSheetsParticipant) {
        log.debug("Building jury from GoogleSheetsParticipant: {}", googleSheetsParticipant);
        return new Jury()
                .setCountry(googleSheetsParticipant.getCountryName())
                .setLocalName(googleSheetsParticipant.getJuryLocalName())
                .setName(googleSheetsParticipant.getJuryRealName())
                .setVoteOrder(googleSheetsParticipant.getVotingOrder());
    }

    public List<Jury> buildJuries(List<GoogleSheetsParticipant> googleSheetsParticipants) {
        log.debug("Building juries from {} GoogleSheetsParticipants", googleSheetsParticipants.size());
        return googleSheetsParticipants.stream()
                .map(this::buildJury)
                .filter(jury -> jury.getName() != null)
                .filter(jury -> jury.getLocalName() != null)
                .collect(Collectors.toList());
    }

    public Participant buildParticipant(GoogleSheetsParticipant googleSheetsParticipant) {
        log.debug("Building participant from GoogleSheetsParticipant: {}", googleSheetsParticipant);
        return new Participant()
                .setName(googleSheetsParticipant.getCountryName())
                .setFinalist(googleSheetsParticipant.isFinalist());
    }

    public List<Participant> buildParticipants(List<GoogleSheetsParticipant> googleSheetsParticipants) {
        log.debug("Building participants from {} GoogleSheetsParticipants", googleSheetsParticipants.size());
        return googleSheetsParticipants.stream()
                .map(this::buildParticipant)
                .collect(Collectors.toList());
    }
}
