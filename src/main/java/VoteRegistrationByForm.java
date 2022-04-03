import repositories.JuryRepository;
import repositories.ParticipantRepository;
import repositories.scorewiz.ScorewizRepository;
import repositories.televote.GoogleFormTelevoteRepository;
import repositories.vote.GoogleFormVoteRepository;
import services.ScoreWizService;

import java.io.IOException;

public class VoteRegistrationByForm {
    public static void main(String[] args) throws IOException {
        ScoreWizService service = new ScoreWizService(
                new JuryRepository(),
                new ParticipantRepository(),
                new ScorewizRepository(),
                new GoogleFormTelevoteRepository(),
                new GoogleFormVoteRepository());

        service.setJuryVotes();
    }
}
