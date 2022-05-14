import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import validators.DefaultJuryValidationService;
import validators.DefaultScoreboardValidationService;
import validators.DefaultTelevoteValidationService;
import validators.DefaultVoteValidationService;
import validators.DefaultVotesValidationService;
import validators.JuryValidationService;
import validators.ScoreboardValidationService;
import validators.TelevotesValidationService;
import validators.VoteValidationService;
import validators.VotesValidationService;

public class GoogleFormModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(JuryRepository.class).to(GoogleFormJuryRepository.class);
        bind(ParticipantRepository.class).to(GoogleFormParticipantRepository.class);
        bind(TelevoteRepository.class).to(GoogleFormTelevoteRepository.class);
        bind(VoteRepository.class).to(GoogleFormVoteRepository.class);

        bind(JuryValidationService.class).to(DefaultJuryValidationService.class);
        bind(ScoreboardValidationService.class).to(DefaultScoreboardValidationService.class);
        bind(TelevotesValidationService.class).to(DefaultTelevoteValidationService.class);
        bind(VotesValidationService.class).to(DefaultVotesValidationService.class);
        bind(VoteValidationService.class).to(DefaultVoteValidationService.class);

        bind(Config.class).toInstance(ConfigFactory.load());
    }
}
