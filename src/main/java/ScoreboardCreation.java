import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.VotesRepository;
import repositories.scorewiz.ScorewizRepository;
import services.ScoreWizService;

import java.io.IOException;
import java.time.LocalDateTime;

public class ScoreboardCreation {
    public static void main(String[] args) throws IOException {
        ScoreWizService scoreWizService = new ScoreWizService(
                new JuryRepository(),
                new ParticipantRepository(),
                new ScorewizRepository(),
                new VotesRepository());

        scoreWizService.createScorewiz("Test " + LocalDateTime.now());
    }
}
