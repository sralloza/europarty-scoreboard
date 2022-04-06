package mappers;

import models.GoogleSheetsVote;
import models.Televote;
import models.Vote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static constants.EuropartyConstants.VOTE_POINTS_LIST;

public class GSVoteMapper {
    public Vote buildVote(GoogleSheetsVote GSvote) {
        return new Vote()
                .setJuryName(GSvote.getJuryName())
                .setCountry1Points(GSvote.getVote1Pt())
                .setCountry2Points(GSvote.getVote2Pt())
                .setCountry3Points(GSvote.getVote3Pt())
                .setCountry4Points(GSvote.getVote4Pt())
                .setCountry5Points(GSvote.getVote5Pt())
                .setCountry6Points(GSvote.getVote6Pt())
                .setCountry7Points(GSvote.getVote7Pt())
                .setCountry8Points(GSvote.getVote8Pt())
                .setCountry10Points(GSvote.getVote10Pt())
                .setCountry12Points(GSvote.getVote12Pt());
    }

    public List<Televote> buildTelevotes(List<GoogleSheetsVote> GSvotes) {
        Map<String, Integer> votesMap = new HashMap<>();
        List<Vote> votes = GSvotes.stream().map(this::buildVote).collect(Collectors.toList());
        for (Vote vote : votes) {
            VOTE_POINTS_LIST.forEach(n -> {
                addPoints(votesMap, vote.getCountryByPoints(n), n);
            });
        }
        return votesMap.entrySet().stream()
                .map(e -> new Televote(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private void addPoints(Map<String, Integer> votesMap, String country, int points) {
        if (votesMap.containsKey(country)) {
            votesMap.put(country, votesMap.get(country) + points);
        } else {
            votesMap.put(country, points);
        }
    }
}