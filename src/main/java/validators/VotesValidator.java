package validators;

import exceptions.CountryNotFoundException;
import exceptions.ExcludedCountryException;
import exceptions.InvalidVoteException;
import exceptions.JuryNotFoundException;
import exceptions.TooManyVotesByOneJuryException;
import models.Jury;
import models.Participant;
import models.Vote;
import utils.SetUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VotesValidator {
    private final SetUtils setUtils;

    @Inject
    public VotesValidator(SetUtils setUtils) {
        this.setUtils = setUtils;
    }

    public void validate(List<Participant> savedParticipants, List<Jury> juries, List<Vote> juryVotes) {
        /* Conditions for valid vote:
         * 1. The jury must be registered in the list of juries (local name)
         * 2. Each country voted must be registered in the list of participants
         * 3. Each country voted must not be excluded from voting
         * 4. Each jury must vote only once
         * 5. Each jury can not vote a country more than once
         */

        // 1. The jury must be registered in the list of juries (local name)
        juryVotes.forEach(vote -> {
            Optional<Jury> jury = juries.stream()
                    .filter(j -> j.getLocalName().equalsIgnoreCase(vote.getJuryName()))
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
            throw new TooManyVotesByOneJuryException(duplicateVotes);
        }

        // 5. Each jury can not vote a country more than once
        juryVotes.forEach(vote -> {
            var countriesVoted = vote.getAllPoints();
            var duplicatedVotes = setUtils.findDuplicates(countriesVoted);
            if (!duplicatedVotes.isEmpty()) {
                throw new InvalidVoteException(duplicatedVotes, vote.getJuryName());
            }
        });
    }
}
