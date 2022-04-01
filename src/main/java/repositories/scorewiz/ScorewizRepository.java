package repositories.scorewiz;

import exceptions.CountryNotFoundException;
import exceptions.JuryMappingNotFoundException;
import exceptions.JuryNameNotFoundException;
import models.Jury;
import models.Televote;
import models.Votes;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static repositories.scorewiz.SubmitType.ID_NAMES_SUBMIT;
import static repositories.scorewiz.SubmitType.ID_VOTES_SUBMIT;
import static repositories.scorewiz.SubmitType.TAG_INPUT_TYPE_SUBMIT;

public class ScorewizRepository extends BaseScorewizRepository {
    public ScorewizRepository() {
        super();
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
        if (Stream.of(scorewizSid, scorewizPass, juryMapping).filter(Objects::nonNull).count() == 3) {
            System.out.println("Vars already set");
            return;
        }

        String[] paths = driver.getCurrentUrl().split("/");

        scorewizSid = paths[paths.length - 2];
        scorewizPass = paths[paths.length - 1];
    }

    public void setJuries(List<Jury> juries) {
        String juriesURL = getActionUrl("setOptions", "juries");
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


    public List<String> getParticipants() {
        String participantsURL = getActionUrl("setOptions", "participants");
        driver.get(participantsURL);
        waitPageLoads();

        int nParticipants = driver.findElements(By.className("select")).size();

        return IntStream.range(1, nParticipants + 1).mapToObj(i -> {
                            WebElement input = driver.findElement(By.id("flag-select-" + i));
                            return input.getAttribute("value");
                        }
                )
                .takeWhile(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public void setParticipants(List<String> participants) {
        String participantsURL = getActionUrl("setOptions", "participants");
        driver.get(participantsURL);
        waitPageLoads();
        removeHeader();

        IntStream.range(0, participants.size()).forEach(i -> {
                    String participant = participants.get(i);
                    setCountryInFormWithAutocomplete(participant, i + 1);
                }
        );

        submit(ID_NAMES_SUBMIT);
    }

    public void genJuryMapping() {
        juryMapping = new HashMap<>();

        driver.get(getActionUrl("votesOverview"));
        waitPageLoads();

        List<WebElement> juryRows = driver.findElements(By.xpath("//tbody//tr"));
        for (WebElement juryRow : juryRows) {
            List<WebElement> juryColumns = juryRow.findElements(By.xpath("td"));
            List<WebElement> filteredCols = juryColumns.stream()
                    .filter(e -> !e.getText().toLowerCase().contains("votes set"))
                    .collect(Collectors.toList());

            String juryName = filteredCols.get(0).getText();
            String juryVoteURL = filteredCols.get(1).findElement(By.cssSelector("a")).getAttribute("href");
            juryMapping.put(juryName, juryVoteURL);
        }

        if (juryMapping.isEmpty()) {
            throw new JuryMappingNotFoundException();
        }
    }

    public void registerSingleJuryVotes(Jury jury, Votes userVotes) {
        String juryVoteURL = Optional.ofNullable(juryMapping.get(jury.getLocalName()))
                .orElseThrow(() -> new JuryNameNotFoundException(jury, juryMapping));

        driver.get(juryVoteURL);
        waitPageLoads();

        runJavascript("noDnd()");
        waitPageLoads();

        IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 10, 12).forEach(i -> {
            WebElement selectElement = driver.findElement(By.name("p" + i + "[]"));
            scrollToElement(selectElement);
            Select selectPoints = new Select(selectElement);
            selectPoints.selectByVisibleText(userVotes.getCountryByPoints(i));
        });

        submit(ID_VOTES_SUBMIT);
    }

    public void setTelevotes(List<Televote> televotes) {
        String televotesURL = getActionUrl("setTelevote");
        driver.get(televotesURL);
        waitPageLoads();

        televotes.forEach(televote -> {
                    System.out.println("Setting televote " + televote);
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

    public void findFirstScoreboard() {
        WebElement editBtn = driver.findElements(By.tagName("a")).stream()
                .filter(e -> e.getText().equals("EDIT"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No edit button found"));
        scrollToElement(editBtn);
        editBtn.click();
    }
}
