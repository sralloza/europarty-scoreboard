package validators;

import models.Jury;
import models.Participant;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.participant.ParticipantRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class JuryValidationTest {
    @Mock
    private ParticipantRepository participantRepository;

    private JuryValidationService juryValidationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        juryValidationService = new DefaultJuryValidationService(participantRepository);

        var participants = List.of(
                new Participant("country1", true),
                new Participant("country2", false)
        );
        when(participantRepository.getParticipants()).thenReturn(participants);
    }

    @Test
    public void validateJuryHappyCase() {
        // Given
        var jury = new Jury("country1", "localName1", "realName1", 1);

        // When
        var result = juryValidationService.validate(jury);

        // Then
        assertEquals(ValidationResult.valid(), result);
    }

    @Test
    public void validateJuryError() {
        // Given
        var jury = new Jury("country3", "localName1", "realName1", 1);

        // When
        var result = juryValidationService.validate(jury);

        // Then
        assertEquals(ValidationResult.invalid("Jury realName1 is assigned to an invalid country: country3"), result);
    }
}
