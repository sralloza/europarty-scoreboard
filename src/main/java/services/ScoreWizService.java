package services;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.Jury;
import models.Participant;
import models.Scoreboard;
import models.SimpleJury;
import models.Televote;
import models.Vote;
import repositories.jury.JuryRepository;
import repositories.participant.ParticipantRepository;
import repositories.scorewiz.ScorewizRepository;
import repositories.televote.TelevoteRepository;
import repositories.vote.VoteRepository;
import validators.GlobalValidator;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class ScoreWizService {
    private final GlobalValidator validator;
    private final JuryRepository juryRepository;
    private final ParticipantRepository participantRepository;
    private final ScorewizRepository scorewizRepository;
    private final TelevoteRepository televoteRepository;
    private final VoteRepository votesRepository;
    private final ConfigRepository configRepository;

    @Inject
    public ScoreWizService(GlobalValidator validator,
                           JuryRepository juryRepository,
                           ParticipantRepository participantRepository,
                           ScorewizRepository scorewizRepository,
                           TelevoteRepository televoteRepository,
                           VoteRepository votesRepository,
                           ConfigRepository configRepository) {
        this.validator = validator;
        this.juryRepository = juryRepository;
        this.participantRepository = participantRepository;
        this.scorewizRepository = scorewizRepository;
        this.televoteRepository = televoteRepository;
        this.votesRepository = votesRepository;
        this.configRepository = configRepository;
    }

    public void createScoreboard() {
        String scoreboardName = configRepository.getString("scorewiz.scoreboard.name");
        createScoreboard(scoreboardName);
    }

    public void createScoreboard(String name) {
        log.info("Launching createScoreboard method with name: {}", name);
        if (configRepository.getBoolean("general.test")) {
            name += " - Test " + LocalDateTime.now();
        }
        validator.validateScoreboardName(name);

        List<Jury> juries = juryRepository.getJuriesSorted();
        List<Participant> participants = participantRepository.getParticipants();
        validator.validateJuries(juries);

        List<Vote> votes = votesRepository.getJuryVotes();
        validator.validateVotes(participants, juries, votes);

        List<Televote> televotes = televoteRepository.getTelevotes();
        validator.validateTelevotes(televotes);

        scorewizRepository.login();
        scorewizRepository.createScoreboard(name);
        scorewizRepository.setColors(name);

        scorewizRepository.setJuries(juries);
        scorewizRepository.setParticipants(participants);

        scorewizRepository.genJuryMapping();
        registerAllJuriesVotes(votes);
        scorewizRepository.setTelevotes(televotes);

        scorewizRepository.logout();
    }

    private void registerAllJuriesVotes(List<Vote> juryVotes) {
        for (Vote vote : juryVotes) {
            Jury jury = juryRepository.getByLocalName(vote.getJuryName());
            scorewizRepository.registerSingleJuryVotes(jury, vote);
        }
    }

    public void deleteAllScoreboards() {
        log.info("Launching deleteAllScoreboards method");
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

    public List<SimpleJury> getJuryList() {
        scorewizRepository.login();
        List<SimpleJury> simpleJuries = scorewizRepository.getJuries();
        scorewizRepository.logout();
        return simpleJuries;
    }

    public void validateData() {
        List<Jury> juries = juryRepository.getJuriesSorted();
        List<Participant> participants = participantRepository.getParticipants();
        validator.validateJuries(juries);

        List<Vote> votes = votesRepository.getJuryVotes();
        validator.validateVotes(participants, juries, votes);

        List<Televote> televotes = televoteRepository.getTelevotes();
        validator.validateTelevotes(televotes);
    }
}
