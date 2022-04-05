import com.google.inject.Guice;
import com.google.inject.Injector;
import config.Config;
import services.ScoreWizService;

import java.io.IOException;
import java.time.LocalDateTime;

public class DeleteAllScoreboards {
    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new LocalModule());
        ScoreWizService scoreWizService = injector.getInstance(ScoreWizService.class);

        scoreWizService.deleteAllScoreboards();
    }
}
