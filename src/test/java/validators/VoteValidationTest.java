package validators;

import models.Jury;
import models.Participant;
import models.Televote;
import models.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.jury.JuryRepository;
import repositories.participant.ParticipantRepository;
import utils.SetUtils;
import validators.helpers.ValidationResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class VoteValidationTest {
    @Mock
    private JuryRepository juryRepository;

    @Mock
    private ParticipantRepository participantRepository;

    private VoteValidationService voteValidationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        voteValidationService = new DefaultVoteValidationService(juryRepository, participantRepository, new SetUtils());

        var juries = List.of(
                new Jury("country1", "localName1", "realName1", 1),
                new Jury("country2", "localName2", "realName2", 2)
        );
        when(juryRepository.getJuries()).thenReturn(juries);

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
        var vote = new Vote("LOCALNAME1", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C10", "C12");

        // When
        var result = voteValidationService.validate(vote);

        // Then
        assertEquals(ValidationResult.valid(), result);
    }

    @Test
    public void validateJuryErrorNotFound() {
        // Given
        var vote = new Vote("invalidName", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C10", "C12");

        // When
        var result = voteValidationService.validate(vote);

        // Then
        assertEquals(ValidationResult.invalid("Vote registered to user that doesn't exist: invalidName"), result);
    }

    @Test
    public void validateJuryErrorNotInFinal() {
        // Given
        var vote = new Vote("localName1", "F1", "C2", "F3", "C4", "C5", "C6", "C7", "C8", "C10", "C12");

        // When
        var result = voteValidationService.validate(vote);

        // Then
        assertEquals(ValidationResult.invalid("localName1 voted for countries that are not finalists: F1, F3"), result);
    }
}
