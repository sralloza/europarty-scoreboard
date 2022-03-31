import repositories.ParticipantRepository;
import repositories.VotesRepository;
import repositories.scorewiz.ScorewizRepository;
import services.ScoreWizService;

import java.io.IOException;
import java.time.LocalDateTime;

public class ScoreboardCreation {
    public static void main(String[] args) throws IOException {
        ScoreWizService scoreWizService = new ScoreWizService(
                new VotesRepository(),
                new ScorewizRepository(),
                new ParticipantRepository());

        scoreWizService.createScorewiz("Test " + LocalDateTime.now());
    }
}
