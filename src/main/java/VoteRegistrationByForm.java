import com.google.inject.Guice;
import com.google.inject.Injector;
import services.ScoreWizService;

public class VoteRegistrationByForm {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GoogleFormModule());
        ScoreWizService service = injector.getInstance(ScoreWizService.class);

        service.setJuryVotes();
    }
}
