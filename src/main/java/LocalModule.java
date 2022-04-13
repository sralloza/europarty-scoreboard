import com.google.inject.AbstractModule;
import repositories.jury.JuryRepository;
import repositories.jury.LocalJuryRepository;
import repositories.participant.LocalParticipantRepository;
import repositories.participant.ParticipantRepository;
import repositories.televote.LocalTelevoteRepository;
import repositories.televote.TelevoteRepository;
import repositories.vote.LocalVoteRepository;
import repositories.vote.VoteRepository;

public class LocalModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(JuryRepository.class).to(LocalJuryRepository.class);
        bind(ParticipantRepository.class).to(LocalParticipantRepository.class);
        bind(TelevoteRepository.class).to(LocalTelevoteRepository.class);
        bind(VoteRepository.class).to(LocalVoteRepository.class);
    }
}
