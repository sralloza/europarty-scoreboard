package repositories.scorewiz;

import com.google.inject.Inject;
import config.ConfigRepository;
import exceptions.CountryNotFoundException;
import exceptions.JuryMappingNotFoundException;
import exceptions.JuryNotFoundException;
import exceptions.NoScoreboardFoundException;
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
import static repositories.scorewiz.MainMenuButtonType.DELETE;
import static repositories.scorewiz.MainMenuButtonType.EDIT;
import static repositories.scorewiz.SubmitType.ID_NAMES_SUBMIT;
import static repositories.scorewiz.SubmitType.ID_VOTES_SUBMIT;
import static repositories.scorewiz.SubmitType.TAG_INPUT_TYPE_SUBMIT;

public class ScorewizRepository extends BaseScorewizRepository {
    @Inject
    public ScorewizRepository(ScorewizUtils scorewizUtils, ConfigRepository configRepository) {
        super(scorewizUtils, configRepository);
    }

    public void createScoreboard(String name) {
        driver.get(getNewURL());

        WebElement titleInput = driver.findElement(By.id("title"));
        scrollToElement(titleInput);
        titleInput.sendKeys(name);

        submit(TAG_INPUT_TYPE_SUBMIT);

        processScorewizVars();
    }

    public void processScorewizVars() {
        if (Stream.of(selectedScoreboard, juryVoteURLMap).filter(Objects::nonNull).count() == 2) {
            System.out.println("Vars already set");
            return;
        }

        selectedScoreboard = scorewizUtils.getScoreboardFromURL(driver.getCurrentUrl());
    }

    public void setJuries(List<Jury> juries) {
        String juriesURL = getSetOptionsURL("juries");
        driver.get(juriesURL);
        waitPageLoads();

        removeHeader();

        IntStream.range(0, juries.size()).forEach(i -> {
                    Jury jury = juries.get(i);
                    setCountryInFormWithAutocomplete(jury.getCountry(), i + 1);

                    WebElement juryInput = driver.findElement(By.id("name" + (i + 1)));
                    juryInput.clear();
                    juryInput.sendKeys(jury.getLocalName());
                }
        );
        submit(ID_NAMES_SUBMIT);
    }

    public List<SimpleJury> getJuries() {
        String url = getSetOptionsURL("juries");
        driver.get(url);
        int nParticipants = driver.findElements(By.className("select")).size();

        return IntStream.range(0, nParticipants)
                .mapToObj(i -> getSimpleJury(i + 1))
                .takeWhile(jury -> jury.getCountryName() != null && !jury.getCountryName().isBlank())
                .takeWhile(jury -> jury.getJuryLocalName() != null && !jury.getJuryLocalName().isBlank())
                .collect(Collectors.toList());
    }

    private SimpleJury getSimpleJury(int position) {
        String countryName = driver.findElement(By.id("flag-select-" + position)).getAttribute("value");
        String juryLocalName = driver.findElement(By.id("name" + position)).getAttribute("value");
        return new SimpleJury()
                .setCountryName(countryName)
                .setJuryLocalName(juryLocalName);
    }

    public List<Participant> getParticipants() {
        String participantsURL = getSetOptionsURL("participants");
        driver.get(participantsURL);
        waitPageLoads();

        int nParticipants = driver.findElements(By.className("select")).size();

        return IntStream.range(1, nParticipants + 1).mapToObj(i -> {
                            WebElement input = driver.findElement(By.id("flag-select-" + i));
                            return input.getAttribute("value");
                        }
                )
                .takeWhile(s -> !s.isEmpty())
                .map(p -> new Participant().setName(p))
                .collect(Collectors.toList());
    }

    public void setParticipants(List<Participant> participants) {
        String participantsURL = getSetOptionsURL("participants");
        driver.get(participantsURL);
        waitPageLoads();
        removeHeader();

        IntStream.range(0, participants.size()).forEach(i -> {
                    String participant = participants.get(i).getName();
                    setCountryInFormWithAutocomplete(participant, i + 1);
                }
        );

        submit(ID_NAMES_SUBMIT);
    }

    public void genJuryMapping() {
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
            juryVoteURLMap.put(juryName, juryVoteURL);
        }

        if (juryVoteURLMap.isEmpty()) {
            throw new JuryMappingNotFoundException();
        }
    }

    public void registerSingleJuryVotes(Jury jury, Vote userVotes) {
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
        removeHeader();
        waitPageLoads();

        VOTE_POINTS_LIST.forEach(i -> {
            WebElement selectElement = driver.findElement(By.name("p" + i + "[]"));
            scrollToElement(selectElement);
            Select selectPoints = new Select(selectElement);
            selectPoints.selectByVisibleText(userVotes.getCountryByPoints(i));
        });

        submit(ID_VOTES_SUBMIT);
    }

    public void setTelevotes(List<Televote> televotes) {
        String televotesURL = getActionURL("setTelevote");
        driver.get(televotesURL);
        waitPageLoads();

        televotes.forEach(televote -> {
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
    }

    public void openFirstScoreboard() {
        selectedScoreboard = getScoreboards().stream().findFirst().orElseThrow(NoScoreboardFoundException::new);
        driver.get(getScoreboardURL());
    }

    public List<Scoreboard> getScoreboards() {
        return findMainMenuButtons(EDIT).stream()
                .map(e -> e.getAttribute("href"))
                .map(scorewizUtils::getScoreboardFromURL)
                .collect(Collectors.toList());
    }

    public void deleteScoreboards() {
        while (true) {
            Optional<WebElement> deleteBtnOpt = findMainMenuButtons(DELETE).stream().findFirst();
            if (deleteBtnOpt.isEmpty()) {
                break;
            }
            removeHeader();
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
    }
}
