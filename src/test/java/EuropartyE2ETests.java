import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import models.Scoreboard;
import models.SimpleJury;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import services.ScoreWizService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleJuryList extends ArrayList<SimpleJury> {
}

public class EuropartyE2ETests {
    private ScoreWizService googleFormService;

    @AfterEach
    public void tearDown() {
        googleFormService.deleteAllScoreboards();
    }

    @BeforeEach
    public void setUp() {
        Injector googleFormInjector = Guice.createInjector(new GoogleFormModule());

        googleFormService = googleFormInjector.getInstance(ScoreWizService.class);

        googleFormService.deleteAllScoreboards();
    }

    public static Integer[] getTimes() {
        return new Integer[]{0, 1, 2, 5};
    }

    @ParameterizedTest(name = "shouldCreateScoreboardWithGoogleFormData (repeat={0})")
    @MethodSource("getTimes")
    public void shouldCreateScoreboardWithGoogleFormData(Integer times) {
        for (int i = 0; i < times; i++) {
            googleFormService.createScoreboard("test-" + i);
        }

        List<Scoreboard> scoreboards = googleFormService.getScoreboards();
        assertEquals(times, scoreboards.size());
    }

    @Test
    public void shouldSortJuryByVotingOrder() {
        googleFormService.createScoreboard("shouldSortJuryByVotingOrder");
        List<SimpleJury> actualJuries = googleFormService.getJuryList();
        List<SimpleJury> expectedJuries = readJson("juries.json", SimpleJuryList.class);
        assertEquals(expectedJuries, actualJuries);
    }

    private <T> T readJson(String filename, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(this.getClass().getResource(filename), clazz);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
