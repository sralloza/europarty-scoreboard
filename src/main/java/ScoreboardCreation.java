import com.google.inject.Guice;
import com.google.inject.Injector;
import config.ConfigRepository;
import services.ScoreWizService;

import java.time.LocalDateTime;


public class ScoreboardCreation {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new LocalModule());
        ScoreWizService scoreWizService = injector.getInstance(ScoreWizService.class);
        ConfigRepository configRepository = injector.getInstance(ConfigRepository.class);

        String scoreboardName = configRepository.getString("scorewiz.scoreboard.name");
        if (configRepository.getBoolean("general.test")) {
            scoreboardName += " - Test " + LocalDateTime.now();
        }
        scoreWizService.createScoreboard(scoreboardName);
    }
}
