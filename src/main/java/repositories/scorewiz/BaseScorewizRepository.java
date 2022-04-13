package repositories.scorewiz;

import com.google.inject.Inject;
import config.Config;
import exceptions.LoginException;
import exceptions.SelectorNotFoundException;
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

import static config.Config.DEBUG;
import static config.Config.SW_BASE_URL;
import static config.Config.SW_PASSWORD;
import static config.Config.SW_USERNAME;
import static constants.ScorewizConstants.LOCAL_WEBDRIVER_PATH;
import static constants.ScorewizConstants.SW_ACTION_URL_TEMPLATE;
import static constants.ScorewizConstants.SW_MENU_URL;
import static constants.ScorewizConstants.WEBDRIVER_NAME;
import static repositories.scorewiz.SubmitType.TAG_INPUT_TYPE_SUBMIT;

public class BaseScorewizRepository {
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
        if (!DEBUG) {
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
        return String.format(SW_ACTION_URL_TEMPLATE, action, scoreboard.getSid(), scoreboard.getPass());
    }

    protected String getScoreboardUrl() {
        if (selectedScoreboard == null) {
            throw new IllegalStateException("No scoreboard selected");
        }
        return getScoreboardUrl(selectedScoreboard);
    }

    protected String getScoreboardUrl(Scoreboard scoreboard) {
        return getActionURL("menu", scoreboard);
    }

    protected String getLoginURL() {
        return SW_BASE_URL + "/login";
    }

    protected String getLogoutURL() {
        return SW_BASE_URL + "/logout";
    }

    protected String getNewURL() {
        return SW_BASE_URL + "/new";
    }

    public void login() {
        provisionDriver();
        driver.get(getLoginURL());
        driver.findElement(By.name("email")).sendKeys(SW_USERNAME);
        driver.findElement(By.name("pass")).sendKeys(SW_PASSWORD);

        submit(TAG_INPUT_TYPE_SUBMIT);

        String url = driver.getCurrentUrl();

        WebElement error = driver.findElement(By.id("error"));
        if (error != null) {
            throw new LoginException(driver.getCurrentUrl(), error.getText());
        }
        if (!url.equals(SW_MENU_URL)) {
            throw new LoginException(driver.getCurrentUrl());
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
        driver.get(SW_MENU_URL);
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
