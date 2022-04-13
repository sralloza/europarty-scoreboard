import com.google.inject.Guice;
import com.google.inject.Injector;
import services.ScoreWizService;

public class DeleteAllScoreboards {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new LocalModule());
        ScoreWizService scoreWizService = injector.getInstance(ScoreWizService.class);

        scoreWizService.deleteAllScoreboards();
    }
}
