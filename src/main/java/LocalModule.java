import com.google.inject.AbstractModule;
import repositories.televote.LocalTelevoteRepository;
import repositories.televote.TelevoteRepository;
import repositories.vote.LocalVoteRepository;
import repositories.vote.VoteRepository;

public class LocalModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(VoteRepository.class).to(LocalVoteRepository.class);
        bind(TelevoteRepository.class).to(LocalTelevoteRepository.class);
    }
}
