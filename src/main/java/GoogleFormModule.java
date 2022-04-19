import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import repositories.jury.GoogleFormJuryRepository;
import repositories.jury.JuryRepository;
import repositories.participant.GoogleFormParticipantRepository;
import repositories.participant.ParticipantRepository;
import repositories.televote.GoogleFormTelevoteRepository;
import repositories.televote.TelevoteRepository;
import repositories.vote.GoogleFormVoteRepository;
import repositories.vote.VoteRepository;

public class GoogleFormModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(JuryRepository.class).to(GoogleFormJuryRepository.class);
        bind(ParticipantRepository.class).to(GoogleFormParticipantRepository.class);
        bind(TelevoteRepository.class).to(GoogleFormTelevoteRepository.class);
        bind(VoteRepository.class).to(GoogleFormVoteRepository.class);
        bind(Config.class).toInstance(ConfigFactory.load());
    }
}
