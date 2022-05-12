package repositories.scorewiz;

import com.google.inject.Inject;
import config.ConfigRepository;
import exceptions.CountryNotFoundException;
import exceptions.JuryMappingNotFoundException;
import exceptions.JuryNotFoundException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import models.Jury;
import models.Participant;
import models.Scoreboard;
import models.SimpleJury;
import models.Televote;
import models.Vote;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScorewizUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static constants.EuropartyConstants.VOTE_POINTS_LIST;
import static models.MainMenuButtonType.DELETE;
import static models.MainMenuButtonType.EDIT;
import static models.SubmitType.ID_NAMES_SUBMIT;
import static models.SubmitType.ID_VOTES_SUBMIT;
import static models.SubmitType.TAG_INPUT_TYPE_SUBMIT;

@Slf4j
public class ScorewizRepository extends BaseScorewizRepository {
    private final StyleHelper styleHelper;

    @Inject
    public ScorewizRepository(ScorewizUtils scorewizUtils, ConfigRepository configRepository, StyleHelper styleHelper) {
        super(scorewizUtils, configRepository);
        this.styleHelper = styleHelper;
    }

    public void createScoreboard(String name) {
        log.debug("Creating scoreboard {}", name);
        driver.get(getNewURL());

        WebElement titleInput = driver.findElement(By.id("title"));
        scrollToElement(titleInput);
        titleInput.sendKeys(name);

        submit(TAG_INPUT_TYPE_SUBMIT);
        log.debug("Scoreboard {} created", name);

        processScorewizVars();
    }

    public void processScorewizVars() {
        log.debug("Processing scorewiz vars");
        if (Stream.of(selectedScoreboard, juryVoteURLMap).filter(Objects::nonNull).count() == 2) {
            log.debug("Vars already set");
            return;
        }

        selectedScoreboard = scorewizUtils.getScoreboardFromURL(driver.getCurrentUrl());
    }

    public List<SimpleJury> getJuries() {
        log.debug("Getting juries");
        String url = getSetOptionsURL("juries");
        driver.get(url);
        int nParticipants = driver.findElements(By.className("select")).size();

        log.debug("Found {} juries, processing them", nParticipants);
        return IntStream.range(0, nParticipants)
                .mapToObj(i -> getSimpleJury(i + 1))
                .takeWhile(jury -> jury.getCountryName() != null && !jury.getCountryName().isBlank())
                .takeWhile(jury -> jury.getJuryLocalName() != null && !jury.getJuryLocalName().isBlank())
                .collect(Collectors.toList());
    }

    public void setJuries(List<Jury> juries) {
        log.debug("Setting {} juries", juries.size());
        String juriesURL = getSetOptionsURL("juries");
        driver.get(juriesURL);
        waitPageLoads();

        removeHeaderAndFooter();

        IntStream.range(0, juries.size()).forEach(i -> {
                    Jury jury = juries.get(i);
                    log.debug("Setting jury {}", jury);
                    setCountryInFormWithAutocomplete(jury.getCountry(), i + 1);

                    WebElement juryInput = driver.findElement(By.id("name" + (i + 1)));
                    juryInput.clear();
                    juryInput.sendKeys(jury.getLocalName());
                }
        );
        submit(ID_NAMES_SUBMIT);
        log.debug("Juries set");
    }

    private SimpleJury getSimpleJury(int position) {
        String countryName = driver.findElement(By.id("flag-select-" + position)).getAttribute("value");
        String juryLocalName = driver.findElement(By.id("name" + position)).getAttribute("value");
        return new SimpleJury()
                .setCountryName(countryName)
                .setJuryLocalName(juryLocalName);
    }

    public void setParticipants(List<Participant> participants) {
        log.debug("Setting {} participants", participants.size());
        String participantsURL = getSetOptionsURL("participants");
        driver.get(participantsURL);
        waitPageLoads();
        removeHeaderAndFooter();

        IntStream.range(0, participants.size()).forEach(i -> {
                    String participant = participants.get(i).getName();
                    setCountryInFormWithAutocomplete(participant, i + 1);
                }
        );

        submit(ID_NAMES_SUBMIT);
        log.debug("Participants set");
    }

    public void genJuryMapping() {
        log.debug("Generating jury mapping");
        juryVoteURLMap = new HashMap<>();

        driver.get(getActionURL("votesOverview"));
        waitPageLoads();

        List<WebElement> juryRows = driver.findElements(By.xpath("//tbody//tr"));
        for (WebElement juryRow : juryRows) {
            List<WebElement> juryColumns = juryRow.findElements(By.xpath("td"));
            List<WebElement> filteredCols = juryColumns.stream()
                    .filter(e -> !e.getText().toLowerCase().contains("votes set"))
                    .collect(Collectors.toList());

            String juryName = filteredCols.get(0).getText();
            String juryVoteURL = filteredCols.get(1).findElement(By.cssSelector("a")).getAttribute("href");
            log.debug("Adding jury {} to jury mapping with url {}", juryName, juryVoteURL);
            juryVoteURLMap.put(juryName, juryVoteURL);
        }

        if (juryVoteURLMap.isEmpty()) {
            throw new JuryMappingNotFoundException();
        }

        log.debug("Jury mapping generated");
    }

    public void registerSingleJuryVotes(Jury jury, Vote userVotes) {
        log.debug("Registering votes for jury {}: {}", jury, userVotes);
        if (juryVoteURLMap == null) {
            throw new JuryMappingNotFoundException();
        }
        if (jury == null) {
            throw new RuntimeException("Jury is null");
        }
        String juryVoteURL = Optional.ofNullable(juryVoteURLMap.get(jury.getLocalName()))
                .orElseThrow(() -> new JuryNotFoundException(jury, juryVoteURLMap));

        driver.get(juryVoteURL);
        waitPageLoads();

        runJavascript("noDnd()");
        removeHeaderAndFooter();
        waitPageLoads();

        VOTE_POINTS_LIST.forEach(i -> {
            WebElement selectElement = driver.findElement(By.name("p" + i + "[]"));
            scrollToElement(selectElement);
            Select selectPoints = new Select(selectElement);
            selectPoints.selectByVisibleText(userVotes.getCountryByPoints(i));
        });

        submit(ID_VOTES_SUBMIT);
        log.debug("Votes registered for jury {}", jury);
    }

    public void setTelevotes(List<Televote> televotes) {
        log.debug("Setting {} televotes", televotes.size());
        String televotesURL = getActionURL("setTelevote");
        driver.get(televotesURL);
        waitPageLoads();

        televotes.forEach(televote -> {
                    log.debug("Setting televote {}", televote);
                    WebElement input = driver.findElements(By.className("select")).stream()
                            .filter(e -> e.getText().equals(televote.getCountry()))
                            .findFirst()
                            .orElseThrow(() -> new CountryNotFoundException(televote))
                            .findElement(By.className("int"));

                    scrollToElement(input);
                    input.clear();
                    input.sendKeys(televote.getVotes().toString());
                }
        );

        submit(ID_NAMES_SUBMIT);
        log.debug("Televotes set");
    }

    public List<Scoreboard> getScoreboards() {
        log.debug("Getting scoreboards");
        return findMainMenuButtons(EDIT).stream()
                .map(e -> e.getAttribute("href"))
                .map(scorewizUtils::getScoreboardFromURL)
                .collect(Collectors.toList());
    }

    public void deleteScoreboards() {
        log.debug("Deleting scoreboards");
        while (true) {
            Optional<WebElement> deleteBtnOpt = findMainMenuButtons(DELETE).stream().findFirst();
            if (deleteBtnOpt.isEmpty()) {
                break;
            }
            log.debug("Deleting one scoreboard");
            removeHeaderAndFooter();
            var deleteBtn = deleteBtnOpt.get();
            scrollToElement(deleteBtn);
            deleteBtn.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3000));
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
            driver.switchTo().defaultContent();

            waitPageLoads();
        }
        log.debug("All scoreboards have been deleted");
    }

    @SneakyThrows
    public void setColors(String scoreboardName) {
        log.debug("Setting colors ({})", this.selectedScoreboard);
        styleHelper.setStyles(driver, this.selectedScoreboard, scoreboardName, "participantVoted");
    }
}
