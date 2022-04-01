import config.Config;
import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.TelevoteRepository;
import repositories.VoteRepository;
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
                new TelevoteRepository(),
                new VoteRepository());

        String scoreboardName = Config.get("scorewiz.scoreboard.name");
        if (Config.get("debug").equals("true")) {
            scoreboardName += " - Test " + LocalDateTime.now();
        }
        scoreWizService.createScoreboard(scoreboardName);
    }
}
