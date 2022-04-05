package utils;

import models.Scoreboard;

public class ScorewizUtils {
    public Scoreboard getScoreboardFromURL(String url) {
        String[] paths = url.split("/");
        return new Scoreboard()
                .setSid(paths[paths.length - 2])
                .setPass(paths[paths.length - 1]);
    }
}
