import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.TelevoteRepository;
import repositories.scorewiz.ScorewizRepository;
import repositories.vote.GoogleFormVoteRepository;
import services.ScoreWizService;

import java.io.IOException;

public class VoteRegistrationGoogle {
    public static void main(String[] args) throws IOException {
        ScoreWizService service = new ScoreWizService(
                new JuryRepository(),
                new ParticipantRepository(),
                new ScorewizRepository(),
                new TelevoteRepository(),
                new GoogleFormVoteRepository());

        service.setJuryVotes();
    }
}
