import com.google.inject.Guice;
import com.google.inject.Injector;
import services.ScoreWizService;


public class ScoreboardCreation {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new LocalModule());
        ScoreWizService scoreWizService = injector.getInstance(ScoreWizService.class);

        scoreWizService.createScoreboard();
    }
}
