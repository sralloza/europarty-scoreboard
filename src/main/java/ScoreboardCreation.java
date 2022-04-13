import com.google.inject.Guice;
import com.google.inject.Injector;
import services.ScoreWizService;

import java.time.LocalDateTime;

import static config.Config.DEBUG;
import static config.Config.SW_SCOREBOARD_NAME;

public class ScoreboardCreation {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new LocalModule());
        ScoreWizService scoreWizService = injector.getInstance(ScoreWizService.class);

        String scoreboardName = SW_SCOREBOARD_NAME;
        if (DEBUG) {
            scoreboardName += " - Test " + LocalDateTime.now();
        }
        scoreWizService.createScoreboard(scoreboardName);
    }
}
