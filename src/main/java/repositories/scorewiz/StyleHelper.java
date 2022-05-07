package repositories.scorewiz;

import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.Scoreboard;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import repositories.HttpRepository;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Slf4j
public class StyleHelper {
    private static final String LETTERS_CFG = "letters";
    private static final String SCOREBOARD_PAGE_CFG = "scoreboardPage";
    private static final String PARTICIPANT_DEFAULT_CFG = "participantDefault";
    private static final String PARTICIPANT_RECEIVING_VOTES_CFG = "participantReceivingVotes";
    private static final String PARTICIPANT_VOTING_CFG = "participantVoting";
    private static final String UPPERCASE_PARTICIPANTS_CFG = "uppercaseParticipants";
    private static final String FAST_MODE_CFG = "fastMode";

    private final HttpRepository http;
    private final ConfigRepository config;

    @Inject
    public StyleHelper(HttpRepository http, ConfigRepository config) {
        this.http = http;
        this.config = config;
    }

    public void setStyles(WebDriver driver, Scoreboard scoreboard, String scoreboardName, String PARTICIPANT_VOTED_CFG) {
        Set<Cookie> cookies = driver.manage().getCookies();
        Map<String, String> data = Map.ofEntries(
                entry("sid", scoreboard.getSid().toString()),
                entry("pass", scoreboard.getPass()),
                entry("title", scoreboardName),
                entry(getSelector(LETTERS_CFG), getForegroundColor(LETTERS_CFG)),
                entry(getSelector(SCOREBOARD_PAGE_CFG), getBackgroundColor(SCOREBOARD_PAGE_CFG)),
                entry(getSelector(PARTICIPANT_DEFAULT_CFG), getBackgroundColor(PARTICIPANT_DEFAULT_CFG)),
                entry(getSelector(PARTICIPANT_VOTED_CFG), getBackgroundColor(PARTICIPANT_VOTED_CFG)),
                entry(getSelector(PARTICIPANT_RECEIVING_VOTES_CFG), getBackgroundColor(PARTICIPANT_RECEIVING_VOTES_CFG)),
                entry(getSelector(PARTICIPANT_VOTING_CFG), getBackgroundColor(PARTICIPANT_VOTING_CFG)),
                entry(getSelector(UPPERCASE_PARTICIPANTS_CFG), getBoolean(UPPERCASE_PARTICIPANTS_CFG)),
                entry(getSelector(FAST_MODE_CFG), getBoolean(FAST_MODE_CFG)));

        data = data.entrySet().stream()
                .filter(entry -> !entry.getValue().equals(""))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var url = config.getString("scorewiz.web.baseURL");
        var path = "/saveOptions/color";
        log.debug("Setting styles: {}", data);
        http.sendRequest(url, path, cookies, data)
                .thenAcceptAsync(response -> {
                    if (!response) {
                        log.error("Failed to set styles");
                    } else {
                        log.debug("Styles set successfully");
                    }
                });
    }

    private String getBoolean(String key) {
        return config.getStyleBoolean(key) ? "on" : "";
    }

    public String getSelector(String selector) {
        return config.getSelector(selector);
    }

    public String getBackgroundColor(String key) {
        return config.getStyleString("bg." + key);
    }

    public String getForegroundColor(String key) {
        return config.getStyleString("fg." + key);
    }
}

