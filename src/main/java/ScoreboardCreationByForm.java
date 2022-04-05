import com.google.inject.Guice;
import com.google.inject.Injector;
import config.Config;
import services.ScoreWizService;

import java.io.IOException;
import java.time.LocalDateTime;

public class ScoreboardCreationByForm {
    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new GoogleFormModule());
        ScoreWizService scoreWizService = injector.getInstance(ScoreWizService.class);

        String scoreboardName = Config.get("scorewiz.scoreboard.name");
        if (Config.get("debug").equals("true")) {
            scoreboardName += " - Test " + LocalDateTime.now();
        }
        scoreWizService.createScoreboard(scoreboardName);
    }
}
