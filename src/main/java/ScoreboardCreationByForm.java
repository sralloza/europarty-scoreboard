import config.Config;
import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.scorewiz.ScorewizRepository;
import repositories.televote.GoogleFormTelevoteRepository;
import repositories.vote.GoogleFormVoteRepository;
import services.ScoreWizService;

import java.io.IOException;
import java.time.LocalDateTime;

public class ScoreboardCreationByForm {
    public static void main(String[] args) throws IOException {
        ScoreWizService scoreWizService = new ScoreWizService(
                new JuryRepository(),
                new ParticipantRepository(),
                new ScorewizRepository(),
                new GoogleFormTelevoteRepository(),
                new GoogleFormVoteRepository());

        String scoreboardName = Config.get("scorewiz.scoreboard.name");
        if (Config.get("debug").equals("true")) {
            scoreboardName += " - Test " + LocalDateTime.now();
        }
        scoreWizService.createScoreboard(scoreboardName);
    }
}
