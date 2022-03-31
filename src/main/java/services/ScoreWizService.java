package services;

import models.Jury;
import models.Votes;
import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.VotesRepository;
import repositories.scorewiz.ScorewizRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ScoreWizService {
    private final VotesRepository votesRepository;
    private final ScorewizRepository scorewizRepository;
    private final ParticipantRepository participantRepository;

    public ScoreWizService(VotesRepository votesRepository, ScorewizRepository scorewizRepository, ParticipantRepository participantRepository) {
        this.votesRepository = votesRepository;
        this.scorewizRepository = scorewizRepository;
        this.participantRepository = participantRepository;
    }

    public void createScorewiz(String name) throws IOException {
        scorewizRepository.login();
        scorewizRepository.createScoreboard(name);

        List<Jury> juries = JuryRepository.getJuries();
        List<String> participants = participantRepository.getParticipants();

        Map<String, Votes> votes = votesRepository.getJuryVotes();
        votes.forEach((username, userVote) ->
                checkAllParticipantsInVotesExist(participants, username, userVote));

        scorewizRepository.setJuries(juries);
        scorewizRepository.setParticipants(participants);

        scorewizRepository.genJuryMapping();

        scorewizRepository.registerAllJuriesVotes(votes);

        scorewizRepository.logout();
    }

    public void setJuryVotes() throws IOException {
        Map<String, Votes> juryVotes = votesRepository.getJuryVotes();

        scorewizRepository.login();
        scorewizRepository.processScorewizVars();
        scorewizRepository.genJuryMapping();

        List<String> savedParticipants = scorewizRepository.getParticipants();

        juryVotes.forEach((username, userVote) ->
                checkAllParticipantsInVotesExist(savedParticipants, username, userVote));

        scorewizRepository.registerAllJuriesVotes(juryVotes);
        scorewizRepository.logout();
    }

    private void checkAllParticipantsInVotesExist(List<String> savedParticipants,
                                                  String username,
                                                  Votes juryVotes) {
        juryVotes.getAllPoints().forEach(s -> {
            if (!savedParticipants.contains(s)) {
                throw new RuntimeException("Participant " + s + " not found ("
                        + username + " voted for it)");
            }
        });
    }
}
