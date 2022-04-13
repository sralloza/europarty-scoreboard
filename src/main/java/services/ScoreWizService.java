package services;

import com.google.inject.Inject;
import exceptions.ParticipantsValidationEception;
import models.Jury;
import models.Participant;
import models.Scoreboard;
import models.Televote;
import models.Vote;
import repositories.jury.JuryRepository;
import repositories.participant.ParticipantRepository;
import repositories.scorewiz.ScorewizRepository;
import repositories.televote.TelevoteRepository;
import repositories.vote.VoteRepository;
import validators.GlobalValidator;

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

    public void createScoreboard(String name) {
        List<Jury> juries = juryRepository.getJuries();
        List<Participant> participants = participantRepository.getParticipants();
        validator.validateJuries(participants, juries);

        List<Vote> votes = votesRepository.getJuryVotes();
        validator.validateVotes(participants, juries, votes);

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

    public void setJuryVotes() {
        List<Vote> juryVotes = votesRepository.getJuryVotes();
        List<Jury> juries = juryRepository.getJuries();

        List<Participant> requestedParticipants = participantRepository.getParticipants();
        validator.validateVotes(requestedParticipants, juries, juryVotes);

        validator.validateVotes(requestedParticipants, juries, juryVotes);

        List<Televote> televotes = televoteRepository.getTelevotes();
        validator.validateTelevotes(requestedParticipants, televotes);

        scorewizRepository.login();
        scorewizRepository.openFirstScoreboard();
        scorewizRepository.processScorewizVars();
        scorewizRepository.genJuryMapping();

        List<Participant> savedParticipants = scorewizRepository.getParticipants();
        if (!savedParticipants.equals(requestedParticipants)) {
            throw new ParticipantsValidationEception(requestedParticipants, savedParticipants);
        }

        registerAllJuriesVotes(juryVotes);
        scorewizRepository.setTelevotes(televotes);
        scorewizRepository.logout();
    }

    private void registerAllJuriesVotes(List<Vote> juryVotes) {
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
