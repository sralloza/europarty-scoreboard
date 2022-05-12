import com.google.inject.Guice;
import com.google.inject.Injector;
import services.ScoreWizService;

public class GoogleFormsDataValidation {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GoogleFormModule());
        ScoreWizService scoreWizService = injector.getInstance(ScoreWizService.class);

        scoreWizService.validateData();
    }
}
