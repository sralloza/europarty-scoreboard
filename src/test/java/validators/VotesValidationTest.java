package validators;

import models.Jury;
import models.Participant;
import models.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.jury.JuryRepository;
import repositories.participant.ParticipantRepository;
import validators.helpers.ValidationResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class VotesValidationTest {
    @Mock
    private JuryRepository juryRepository;

    @Mock
    private ParticipantRepository participantRepository;

    private VotesValidationService votesValidationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        votesValidationService = new DefaultVotesValidationService(juryRepository);

        var juries = List.of(
                new Jury("country1", "localName1", "realName1", 1),
                new Jury("country2", "localName2", "realName2", 2)
        );
        when(juryRepository.getJuries()).thenReturn(juries);
        when(juryRepository.getByLocalName(anyString())).thenCallRealMethod();

        var participants = List.of(
                new Participant("C1", true),
                new Participant("C2", true),
                new Participant("C3", true),
                new Participant("C4", true),
                new Participant("C5", true),
                new Participant("C6", true),
                new Participant("C7", true),
                new Participant("C8", true),
                new Participant("C9", true),
                new Participant("C10", true),
                new Participant("C11", true),
                new Participant("C12", true),
                new Participant("F1", false),
                new Participant("F2", false),
                new Participant("F3", false)
        );
        when(participantRepository.getParticipants()).thenReturn(participants);
    }

    @Test
    public void validateJuryHappyCase() {
        // Given
        var votes = List.of(
                new Vote("LOCALNAME1", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C10", "C12"),
                new Vote("localName2", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C10", "C12")
        );

        // When
        var result = votesValidationService.validate(votes);

        // Then
        assertEquals(ValidationResult.valid(), result);
    }

    @Test
    public void validateErrorJuriesVotedMoreThanOnce() {
        // Given
        var votes = List.of(
                new Vote("LOCALNAME1", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C10", "C12"),
                new Vote("localName1", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C10", "C12")
        );
        // When
        var result = votesValidationService.validate(votes);

        // Then
        assertEquals(ValidationResult.invalid("Detected duplicated votes by the following juries: realName1"), result);
    }
}
