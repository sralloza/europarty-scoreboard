package validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import validators.helpers.ValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
