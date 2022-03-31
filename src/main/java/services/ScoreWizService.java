package services;

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
                           VoteRepository votesRepository,
                           TelevoteRepository televoteRepository) {
        this.juryRepository = juryRepository;
        this.votesRepository = votesRepository;
        this.scorewizRepository = scorewizRepository;
        this.participantRepository = participantRepository;
        this.televoteRepository = televoteRepository;
    }

    public void createScorewiz(String name) throws IOException {
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
            throw new RuntimeException("Requested participants are not the same as in the database");
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
                throw new RuntimeException("Participant " + s + " not found ("
                        + username + " voted for it)");
            }
        }));
    }

    private void validateTelevotes(List<String> savedParticipants, List<Televote> televotes) {
        televotes.forEach(t -> {
            if (!savedParticipants.contains(t.getCountry())) {
                throw new RuntimeException("Invalid televote, participant not found " + t.getCountry());
            }
        });
    }

    private void validateJuries(List<String> savedParticipants, List<Jury> juries) {
        juries.forEach(jury -> {
            if (!savedParticipants.contains(jury.getCountry())) {
                throw new RuntimeException("Jury " + jury.getCountry() + " not found ("
                        + jury + " requested it)");
            }
        });
    }

}
