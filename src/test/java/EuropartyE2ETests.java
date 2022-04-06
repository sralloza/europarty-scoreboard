import com.google.inject.Guice;
import com.google.inject.Injector;
import models.Scoreboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import services.ScoreWizService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EuropartyE2ETests {
    private ScoreWizService googleFormService;
    private ScoreWizService localService;

    @BeforeEach
    public void setUp() {
        Injector googleFormInjector = Guice.createInjector(new GoogleFormModule());
        Injector localInjector = Guice.createInjector(new LocalModule());

        googleFormService = googleFormInjector.getInstance(ScoreWizService.class);
        localService = localInjector.getInstance(ScoreWizService.class);

        localService.deleteAllScoreboards();
    }

    @AfterEach
    public void tearDown() {
        localService.deleteAllScoreboards();
    }

    public static Integer[] getTimes() {
        return new Integer[]{0, 1, 2, 5};
    }

    @ParameterizedTest(name = "shouldCreateScoreboardWithLocalData (repeat={0})")
    @MethodSource("getTimes")
    public void shouldCreateScoreboardWithLocalData(Integer times) throws IOException {
        for (int i = 0; i < times; i++) {
            localService.createScoreboard("test-" + i);
        }

        List<Scoreboard> scoreboards = localService.getScoreboards();
        assertEquals(times, scoreboards.size());
    }

    @ParameterizedTest(name = "shouldCreateScoreboardWithGoogleFormData (repeat={0})")
    @MethodSource("getTimes")
    public void shouldCreateScoreboardWithGoogleFormData(Integer times) throws IOException {
        for (int i = 0; i < times; i++) {
            googleFormService.createScoreboard("test-" + i);
        }

        List<Scoreboard> scoreboards = googleFormService.getScoreboards();
        assertEquals(times, scoreboards.size());
    }
}
