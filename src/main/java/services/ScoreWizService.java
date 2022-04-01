package services;

import exceptions.CountryNotFoundException;
import exceptions.ParticipantsValidationEception;
import models.Jury;
import models.Televote;
import models.Votes;
import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.TelevoteRepository;
import repositories.VoteRepository;
import repositories.scorewiz.ScorewizRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ScoreWizService {
    private final JuryRepository juryRepository;
    private final ParticipantRepository participantRepository;
    private final ScorewizRepository scorewizRepository;
    private final VoteRepository votesRepository;
    private final TelevoteRepository televoteRepository;

    public ScoreWizService(JuryRepository juryRepository,
                           ParticipantRepository participantRepository,
                           ScorewizRepository scorewizRepository,
                           TelevoteRepository televoteRepository,
                           VoteRepository votesRepository) {
        this.juryRepository = juryRepository;
        this.participantRepository = participantRepository;
        this.scorewizRepository = scorewizRepository;
        this.televoteRepository = televoteRepository;
        this.votesRepository = votesRepository;
    }

    public void createScoreboard(String name) throws IOException {
        List<Jury> juries = juryRepository.getJuries();
        List<String> participants = participantRepository.getParticipants();
        validateJuries(participants, juries);

        Map<String, Votes> votes = votesRepository.getJuryVotes();
        validateVotes(participants, votes);

        List<Televote> televotes = televoteRepository.getTelevotes();
        validateTelevotes(participants, televotes);

        scorewizRepository.login();
        scorewizRepository.createScoreboard(name);

        scorewizRepository.setJuries(juries);
        scorewizRepository.setParticipants(participants);

        scorewizRepository.genJuryMapping();
        registerAllJuriesVotes(votes);
        scorewizRepository.setTelevotes(televotes);

        scorewizRepository.logout();
    }

    public void setJuryVotes() throws IOException {
        Map<String, Votes> juryVotes = votesRepository.getJuryVotes();

        List<String> requestedParticipants = participantRepository.getParticipants();
        validateVotes(requestedParticipants, juryVotes);

        List<Televote> televotes = televoteRepository.getTelevotes();
        validateTelevotes(requestedParticipants, televotes);

        scorewizRepository.login();
        scorewizRepository.findFirstScoreboard();
        scorewizRepository.processScorewizVars();
        scorewizRepository.genJuryMapping();

        List<String> savedParticipants = scorewizRepository.getParticipants();
        if (!savedParticipants.equals(requestedParticipants)) {
            throw new ParticipantsValidationEception(requestedParticipants, savedParticipants);
        }

        registerAllJuriesVotes(juryVotes);
        scorewizRepository.setTelevotes(televotes);
        scorewizRepository.logout();
    }

    private void registerAllJuriesVotes(Map<String, Votes> juryVotes) throws IOException {

        for (Map.Entry<String, Votes> entry : juryVotes.entrySet()) {
            Jury jury = juryRepository.getByName(entry.getKey());
            scorewizRepository.registerSingleJuryVotes(jury, entry.getValue());
        }
    }

    private void validateVotes(List<String> savedParticipants, Map<String, Votes> juryVotes) {
        juryVotes.forEach((username, userVote) -> userVote.getAllPoints().forEach(s -> {
            if (!savedParticipants.contains(s)) {
                throw new CountryNotFoundException(s, username);
            }
        }));
    }

    private void validateTelevotes(List<String> savedParticipants, List<Televote> televotes) {
        televotes.forEach(televote -> {
            if (!savedParticipants.contains(televote.getCountry())) {
                throw new CountryNotFoundException(televote);
            }
        });
    }

    private void validateJuries(List<String> savedParticipants, List<Jury> juries) {
        juries.forEach(jury -> {
            if (!savedParticipants.contains(jury.getCountry())) {
                throw new CountryNotFoundException(jury);
            }
        });
    }
}
