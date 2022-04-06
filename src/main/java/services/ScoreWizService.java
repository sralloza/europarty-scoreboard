package services;

import com.google.inject.Inject;
import exceptions.CountryNotFoundException;
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

import java.io.IOException;
import java.util.List;

public class ScoreWizService {
    private final JuryRepository juryRepository;
    private final ParticipantRepository participantRepository;
    private final ScorewizRepository scorewizRepository;
    private final VoteRepository votesRepository;
    private final TelevoteRepository televoteRepository;

    @Inject
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

        List<Vote> votes = votesRepository.getJuryVotes();
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
        List<Vote> juryVotes = votesRepository.getJuryVotes();

        List<String> requestedParticipants = participantRepository.getParticipants();
        validateVotes(requestedParticipants, juryVotes);

        List<Televote> televotes = televoteRepository.getTelevotes();
        validateTelevotes(requestedParticipants, televotes);

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

    private void validateVotes(List<String> savedParticipants, List<Vote> juryVotes) {
        juryVotes.forEach(vote -> vote.getAllPoints().forEach(s -> {
            if (!savedParticipants.contains(s)) {
                throw new CountryNotFoundException(s, vote.getJuryName());
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
