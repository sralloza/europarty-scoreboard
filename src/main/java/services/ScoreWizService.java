package services;

import com.google.inject.Inject;
import exceptions.ParticipantsValidationEception;
import models.Jury;
import models.Scoreboard;
import models.Televote;
import models.Vote;
import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.scorewiz.ScorewizRepository;
import repositories.televote.TelevoteRepository;
import repositories.vote.VoteRepository;
import validators.GlobalValidator;

import java.io.IOException;
import java.util.List;

public class ScoreWizService {
    private final GlobalValidator validator;
    private final JuryRepository juryRepository;
    private final ParticipantRepository participantRepository;
    private final ScorewizRepository scorewizRepository;
    private final TelevoteRepository televoteRepository;
    private final VoteRepository votesRepository;

    @Inject
    public ScoreWizService(GlobalValidator validator,
                           JuryRepository juryRepository,
                           ParticipantRepository participantRepository,
                           ScorewizRepository scorewizRepository,
                           TelevoteRepository televoteRepository,
                           VoteRepository votesRepository) {
        this.validator = validator;
        this.juryRepository = juryRepository;
        this.participantRepository = participantRepository;
        this.scorewizRepository = scorewizRepository;
        this.televoteRepository = televoteRepository;
        this.votesRepository = votesRepository;
    }

    public void createScoreboard(String name) throws IOException {
        List<Jury> juries = juryRepository.getJuries();
        List<String> participants = participantRepository.getParticipants();
        validator.validateJuries(participants, juries);

        List<Vote> votes = votesRepository.getJuryVotes();
        validator.validateVotes(participants, votes);

        List<Televote> televotes = televoteRepository.getTelevotes();
        validator.validateTelevotes(participants, televotes);

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
        List<Vote> juryVotes = votesRepository.getJuryVotes();

        List<String> requestedParticipants = participantRepository.getParticipants();
        validator.validateVotes(requestedParticipants, juryVotes);

        List<Televote> televotes = televoteRepository.getTelevotes();
        validator.validateTelevotes(requestedParticipants, televotes);

        scorewizRepository.login();
        scorewizRepository.openFirstScoreboard();
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

    private void registerAllJuriesVotes(List<Vote> juryVotes) throws IOException {
        for (Vote vote : juryVotes) {
            Jury jury = juryRepository.getByName(vote.getJuryName());
            scorewizRepository.registerSingleJuryVotes(jury, vote);
        }
    }

    public void deleteAllScoreboards() {
        scorewizRepository.login();
        scorewizRepository.deleteScoreboards();
        scorewizRepository.logout();
    }

    public List<Scoreboard> getScoreboards() {
        scorewizRepository.login();
        List<Scoreboard> scoreboards = scorewizRepository.getScoreboards();
        scorewizRepository.logout();
        return scoreboards;
    }
}
