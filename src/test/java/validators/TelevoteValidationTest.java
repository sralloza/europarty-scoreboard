package validators;

import models.Participant;
import models.Televote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.participant.ParticipantRepository;
import validators.helpers.ValidationResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TelevoteValidationTest {
    @Mock
    private ParticipantRepository participantRepository;

    private TelevotesValidationService televotesValidationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        televotesValidationService = new DefaultTelevoteValidationService(participantRepository);

        var participants = List.of(
                new Participant("country1", true),
                new Participant("country2", false)
        );
        when(participantRepository.getParticipants()).thenReturn(participants);
    }

    @Test
    public void validateJuryHappyCase() {
        // Given
        var televote = new Televote("country1", 2);

        // When
        var result = televotesValidationService.validate(televote);

        // Then
        assertEquals(ValidationResult.valid(), result);
    }

    @Test
    public void validateJuryErrorNotFound() {
        // Given
        var televote = new Televote("country3", 1);

        // When
        var result = televotesValidationService.validate(televote);

        // Then
        assertEquals(ValidationResult.invalid("Televote voting for non-existent country: country3"), result);
    }

    @Test
    public void validateJuryErrorNotInFinal() {
        // Given
        var televote = new Televote("country2", 2);

        // When
        var result = televotesValidationService.validate(televote);

        // Then
        assertEquals(ValidationResult.invalid("Televote voting for eliminated country: country2"), result);
    }
}
