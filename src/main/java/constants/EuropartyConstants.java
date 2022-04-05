package constants;

import java.util.List;

public class EuropartyConstants {
    public static final List<Integer> VOTE_POINTS_LIST = List.of(1, 2, 3, 4, 5, 6, 7, 8, 10, 12);

    public enum JsonFiles {
        VOTES("votes.json"),
        TELEVOTES("televotes.json"),
        PARTICIPANTS("participants.json"),
        JURIES("juries.json");

        private String fileName;

        JsonFiles(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
