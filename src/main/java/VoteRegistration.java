import com.google.inject.Guice;
import com.google.inject.Injector;
import services.ScoreWizService;

import java.io.IOException;

public class VoteRegistration {
    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new LocalModule());
        ScoreWizService service = injector.getInstance(ScoreWizService.class);

        service.setJuryVotes();
    }
}
