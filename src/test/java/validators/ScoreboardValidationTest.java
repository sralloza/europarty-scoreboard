package validators;

import models.Jury;
import models.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ScoreboardValidationTest {
    private ScoreboardValidationService scoreboardValidationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        scoreboardValidationService = new DefaultScoreboardValidationService();
    }

    @Test
    public void validateJuryHappyCase() {
        // Given
        var scoreboard = "Europarty";

        // When
        var result = scoreboardValidationService.validate(scoreboard);

        // Then
        assertEquals(ValidationResult.valid(), result);
    }

    @Test
    public void validateJuryError() {
        // Given
        var scoreboard = "Europarty's Scoreboard";

        // When
        var result = scoreboardValidationService.validate(scoreboard);

        // Then
        assertEquals(ValidationResult.invalid("Invalid name, contains a quote: 'Europarty's Scoreboard'"), result);
    }
}
