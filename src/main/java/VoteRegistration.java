import repositories.ParticipantRepository;
import repositories.VotesRepository;
import repositories.scorewiz.ScorewizRepository;
import services.ScoreWizService;

import java.io.IOException;

public class VoteRegistration {
    public static void main(String[] args) throws IOException {
        ScoreWizService service = new ScoreWizService(
                new VotesRepository(),
                new ScorewizRepository(),
                new ParticipantRepository());

        service.setJuryVotes();
    }
}
