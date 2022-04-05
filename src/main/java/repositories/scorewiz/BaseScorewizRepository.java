package repositories.scorewiz;

import com.google.inject.Inject;
import config.Config;
import lombok.SneakyThrows;
import models.Scoreboard;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScorewizUtils;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static repositories.scorewiz.SubmitType.TAG_INPUT_TYPE_SUBMIT;

public class BaseScorewizRepository {
    protected static final String BASE_URL = Config.get("scorewiz.baseUrl");
    protected static final String MENU_URL = BASE_URL + "/my/scoreboards";
    protected static final Boolean HEADLESS = !Config.get("debug").equals("true");
    protected static final String SCOREWIZ_PASSWORD = Config.get("scorewiz.password");
    protected static final String SCOREWIZ_USERNAME = Config.get("scorewiz.username");
    protected static final String URL_ACTION_TEMPLATE = BASE_URL + "/%s/%s/%s";
    private static final String WEBDRIVER_NAME = "chromedriver";
    private static final File LOCAL_WEBDRIVER_PATH = new File("src/main/resources/" + WEBDRIVER_NAME);
    protected WebDriver driver;
    protected Map<String, String> juryVoteURLMap;
    protected Scoreboard selectedScoreboard;
    private File extractedDriverFile;
    protected final ScorewizUtils scorewizUtils;

    @Inject
    public BaseScorewizRepository(ScorewizUtils scorewizUtils) {
        this.scorewizUtils = scorewizUtils;
    }

    @SneakyThrows
    private void provisionWebdriverFile() {
        if (LOCAL_WEBDRIVER_PATH.exists()) {
            System.out.println("Skipping extraction of webdriver file");
            return;
        }

        extractedDriverFile = File.createTempFile("chromedriver", "").getAbsoluteFile();
        System.out.println("Extracted webdriver file: " + extractedDriverFile);
        if (!extractedDriverFile.setExecutable(true)) {
            System.err.println("Failed to set executable flag on chromedriver: " + extractedDriverFile);
        }
        FileUtils.copyInputStreamToFile(Config.getResource(WEBDRIVER_NAME), extractedDriverFile);
    }

    private void removeWebdriverFile() {
        if (LOCAL_WEBDRIVER_PATH.exists()) {
            System.out.println("Skipping removal of webdriver file");
            return;
        }
        if (!extractedDriverFile.delete()) {
            System.out.println("Failed to delete webdriver file: " + extractedDriverFile);
        }

    }

    private void provisionDriver() {
        provisionWebdriverFile();
        System.setProperty("webdriver.chrome.driver",
                Objects.requireNonNullElse(extractedDriverFile, LOCAL_WEBDRIVER_PATH).getPath());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        if (HEADLESS) {
            options.addArguments("--headless");
        }

        driver = new ChromeDriver(options);
    }

    protected String getActionUrl(String action, String extraAction) {
        return getActionUrl(action) + "/" + extraAction;
    }

    protected String getActionUrl(String action) {
        if (selectedScoreboard == null) {
            throw new IllegalStateException("No scoreboard selected");
        }
        return getActionUrl(action, selectedScoreboard);
    }

    private String getActionUrl(String action, Scoreboard scoreboard) {
        return String.format(URL_ACTION_TEMPLATE, action, scoreboard.getSid(), scoreboard.getPass());
    }

    protected String getScoreboardUrl() {
        if (selectedScoreboard == null) {
            throw new IllegalStateException("No scoreboard selected");
        }
        return getScoreboardUrl(selectedScoreboard);
    }

    protected String getScoreboardUrl(Scoreboard scoreboard) {
        return getActionUrl("menu", scoreboard);
    }

    protected String getLoginURL() {
        return BASE_URL + "/login";
    }

    protected String getLogoutURL() {
        return BASE_URL + "/logout";
    }

    protected String getNewURL() {
        return BASE_URL + "/new";
    }

    public void login() {
        provisionDriver();
        driver.get(getLoginURL());
        driver.findElement(By.name("email")).sendKeys(SCOREWIZ_USERNAME);
        driver.findElement(By.name("pass")).sendKeys(SCOREWIZ_PASSWORD);

        submit(TAG_INPUT_TYPE_SUBMIT);
    }

    public void logout() {
        driver.get(getLogoutURL());
        driver.close();
        removeWebdriverFile();
    }

    protected void scrollToElement(WebElement element) {
        runJavascript("arguments[0].scrollIntoView(true);", element);
        waitPageLoads();
    }

    protected void waitPageLoads() {
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                webDriver -> runJavascript("return document.readyState").equals("complete"));
    }

    protected void removeHeader() {
        try {
            ((JavascriptExecutor) driver).executeScript("document.getElementsByTagName(\"header\")[0].remove()");
        } catch (JavascriptException e) {
            // ignore
        }
    }

    protected void submit(SubmitType submitType) {
        By selector;
        switch (submitType) {
            case TAG_INPUT_TYPE_SUBMIT:
                selector = By.xpath("//input[@type='submit']");
                break;
            case ID_NAMES_SUBMIT:
                selector = By.id("namesSubmit");
                break;
            case ID_VOTES_SUBMIT:
                selector = By.id("votesSubmit");
                break;
            default:
                throw new IllegalArgumentException("Invalid SubmitType");

        }
        submit(selector);
    }

    private void submit(By selector) {
        WebElement namesSubmitBtn = driver.findElement(selector);
        scrollToElement(namesSubmitBtn);
        namesSubmitBtn.click();
    }

    protected Object runJavascript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    protected void setCountryInFormWithAutocomplete(String country, Integer index) {
        WebElement flagInput = driver.findElement(By.id("flag-select-" + (index)));
        scrollToElement(flagInput);
        flagInput.clear();
        flagInput.sendKeys(country);

        List<WebElement> selectors = driver.findElements(By.className("autocomplete-option"));
        WebElement selector = findSelector(selectors, country);
        selector.click();
        waitPageLoads();
    }

    private WebElement findSelector(List<WebElement> selectors, String participant) {
        return selectors.stream()
                .filter(s -> s.getText().equalsIgnoreCase(participant))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No participant found for " + participant));
    }

    protected List<WebElement> findMainMenuButtons(MainMenuButtonType buttonType) {
        if (!driver.getCurrentUrl().equals(MENU_URL)) {
            driver.get(MENU_URL);
        }
        return findMainMenuButtons(buttonType, driver.findElement(By.className("node")));
    }

    protected List<WebElement> findMainMenuButtons(MainMenuButtonType buttonType, WebElement webElement) {
        return webElement.findElements(By.className("node")).stream()
                .map(node -> node.findElements(By.tagName("a")).stream()
                        .filter(e -> e.getText().equals(buttonType.getContent()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No button found")))
                .collect(Collectors.toList());
    }
}
