import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.VoteRepository;
import repositories.scorewiz.ScorewizRepository;
import services.ScoreWizService;

import java.io.IOException;

public class VoteRegistration {
    public static void main(String[] args) throws IOException {
        ScoreWizService service = new ScoreWizService(
                new JuryRepository(),
                new ParticipantRepository(),
                new ScorewizRepository(),
                new VoteRepository());

        service.setJuryVotes();
    }
}
