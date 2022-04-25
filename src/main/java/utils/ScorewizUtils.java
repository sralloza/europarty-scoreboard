package utils;

import lombok.extern.slf4j.Slf4j;
import models.Scoreboard;

@Slf4j
public class ScorewizUtils {
    public Scoreboard getScoreboardFromURL(String url) {
        log.debug("Getting scoreboard from URL: {}", url);
        String[] paths = url.split("/");
        return new Scoreboard()
                .setSid(Integer.parseInt(paths[paths.length - 2]))
                .setPass(paths[paths.length - 1]);
    }
}
