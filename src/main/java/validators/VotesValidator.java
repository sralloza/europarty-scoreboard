package validators;

import exceptions.CountryNotFoundException;
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
         * 1. The jury must be registered in the list of juries (local name)
         * 2. Each country voted must be registered in the list of participants
         * 3. Each country voted must not be excluded from voting
         * 4. Each jury must vote only once
         */

        // 1. The jury must be registered in the list of juries (local name)
        juryVotes.forEach(vote -> {
            Optional<Jury> jury = juries.stream()
                    .filter(j -> j.getLocalName().equals(vote.getJuryName()))
                    .findAny();

            if (jury.isEmpty()) {
                throw new JuryNotFoundException(vote.getJuryName());
            }

            // 2. Each country voted must be registered in the list of participants
            vote.getAllPoints().forEach(countryVoted -> {
                Optional<Participant> participantOpt = savedParticipants.stream()
                        .filter(p -> countryVoted.equals(p.getName()))
                        .findAny();

                if (participantOpt.isEmpty()) {
                    throw new CountryNotFoundException(countryVoted, jury.get());
                }

                // 3. Each country voted must not be excluded from voting
                Participant participant = participantOpt.get();
                if (!participant.isFinalist()) {
                    throw new ExcludedCountryException(participant, vote);
                }
            });
        });

        // 4. Each jury must vote only once
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
