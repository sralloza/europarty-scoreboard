package mappers;

import models.GoogleSheetsVote;
import models.Vote;

public class VoteMapper {
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
}
