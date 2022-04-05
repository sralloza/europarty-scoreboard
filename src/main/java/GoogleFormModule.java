import com.google.inject.AbstractModule;
import repositories.televote.GoogleFormTelevoteRepository;
import repositories.televote.TelevoteRepository;
import repositories.vote.GoogleFormVoteRepository;
import repositories.vote.VoteRepository;

public class GoogleFormModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(VoteRepository.class).to(GoogleFormVoteRepository.class);
        bind(TelevoteRepository.class).to(GoogleFormTelevoteRepository.class);
    }
}
