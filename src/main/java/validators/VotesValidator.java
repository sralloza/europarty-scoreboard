package validators;

import exceptions.DuplicateVoteException;
import exceptions.ExcludedCountryException;
import exceptions.JuryNotFoundException;
import models.Jury;
import models.Participant;
import models.Vote;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VotesValidator {

    public void validate(List<Participant> savedParticipants, List<Jury> juries, List<Vote> juryVotes) {
        /* Conditions for valid vote:
         * 1. The jury must be registered in the list of juries
         * 2. Each country voted must be registered in the list of participants
         * 3. Each country voted must not be excluded from voting
         * 4. Each jury must vote only once
         */

        juryVotes.forEach(vote -> {
            Optional<Jury> jury = juries.stream()
                    .filter(j -> j.getName().equals(vote.getJuryName()))
                    .findAny();

            if (jury.isEmpty()) {
                throw new JuryNotFoundException(vote.getJuryName());
            }

            vote.getAllPoints().forEach(s -> {
                Optional<Participant> participantOpt = savedParticipants.stream()
                        .filter(p -> s.equals(p.getName()))
                        .findAny();

                if (participantOpt.isEmpty()) {
                    throw new JuryNotFoundException(vote.getJuryName());
                }

                Participant participant = participantOpt.get();
                if (participant.isExcluded()) {
                    throw new ExcludedCountryException(participant, vote);
                }
            });
        });

        List<String> duplicateVotes = juryVotes.stream()
                .collect(Collectors.groupingBy(Vote::getJuryName))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!duplicateVotes.isEmpty()) {
            throw new DuplicateVoteException(duplicateVotes);
        }
    }
}
