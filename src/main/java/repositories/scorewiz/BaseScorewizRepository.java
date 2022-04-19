package repositories.scorewiz;

import config.ConfigRepository;
import exceptions.LoginException;
import exceptions.SelectorNotFoundException;
import lombok.SneakyThrows;
import models.Scoreboard;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
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

import static constants.ScorewizConstants.LOCAL_WEBDRIVER_PATH;
import static constants.ScorewizConstants.WEBDRIVER_NAME;
import static repositories.scorewiz.SubmitType.TAG_INPUT_TYPE_SUBMIT;

public class BaseScorewizRepository {
    protected WebDriver driver;
    protected Map<String, String> juryVoteURLMap;
    protected Scoreboard selectedScoreboard;
    private File extractedDriverFile;

    protected String baseURL;

    protected final ScorewizUtils scorewizUtils;
    protected final ConfigRepository configRepository;

    public BaseScorewizRepository(ScorewizUtils scorewizUtils, ConfigRepository configRepository) {
        this.scorewizUtils = scorewizUtils;
        this.configRepository = configRepository;

        baseURL = configRepository.getString("scorewiz.web.baseURL");
    }

    protected String getMenuURL() {
        return baseURL + "/my/scoreboards";
    }

    protected String getActionURLTemplate() {
        return baseURL + "/%s/%s/%s";
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
        FileUtils.copyInputStreamToFile(ConfigRepository.getResource(WEBDRIVER_NAME), extractedDriverFile);
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
        if (!configRepository.getBoolean("general.headless")) {
            options.addArguments("--headless");
        }

        driver = new ChromeDriver(options);
    }

    protected String getSetOptionsURL(String action) {
        return getActionURL("setOptions") + "/" + action;
    }

    protected String getActionURL(String action) {
        if (selectedScoreboard == null) {
            throw new IllegalStateException("No scoreboard selected");
        }
        return getActionURL(action, selectedScoreboard);
    }

    private String getActionURL(String action, Scoreboard scoreboard) {
        return String.format(getActionURLTemplate(), action, scoreboard.getSid(), scoreboard.getPass());
    }

    protected String getScoreboardURL() {
        if (selectedScoreboard == null) {
            throw new IllegalStateException("No scoreboard selected");
        }
        return getScoreboardURL(selectedScoreboard);
    }

    protected String getScoreboardURL(Scoreboard scoreboard) {
        return getActionURL("menu", scoreboard);
    }

    protected String getLoginURL() {
        return baseURL + "/login";
    }

    protected String getLogoutURL() {
        return baseURL + "/logout";
    }

    protected String getNewURL() {
        return baseURL + "/new";
    }

    public void login() {
        provisionDriver();
        var username = configRepository.getString("scorewiz.credentials.username");
        var password = configRepository.getString("scorewiz.credentials.password");
        driver.get(getLoginURL());
        driver.findElement(By.name("email")).sendKeys(username);
        driver.findElement(By.name("pass")).sendKeys(password);

        submit(TAG_INPUT_TYPE_SUBMIT);

        ensureLoginCorrect();
    }

    private void ensureLoginCorrect() {
        String url = driver.getCurrentUrl();
        var username = configRepository.getString("scorewiz.credentials.username");
        var password = configRepository.getString("scorewiz.credentials.password");

        try {
            WebElement error = driver.findElement(By.id("error"));
            // TODO: a exception should not log username and password
            throw new LoginException(driver.getCurrentUrl(), error.getText(), username, password);
        } catch (NoSuchElementException e) {
            if (!url.equals(getMenuURL())) {
                throw new LoginException(driver.getCurrentUrl());
            }
        }
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
                .orElseThrow(() -> new SelectorNotFoundException(participant));
    }

    protected List<WebElement> findMainMenuButtons(MainMenuButtonType buttonType) {
        driver.get(getMenuURL());
        waitPageLoads();
        return findMainMenuButtons(buttonType, driver.findElement(By.className("node")));
    }

    protected List<WebElement> findMainMenuButtons(MainMenuButtonType buttonType, WebElement webElement) {
        return webElement.findElements(By.className("node")).stream()
                .map(node -> node.findElements(By.tagName("a")).stream()
                        .filter(e -> e.getText().equals(buttonType.getContent()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
