package repositories.scorewiz;

import config.ConfigRepository;
import exceptions.LoginException;
import exceptions.SelectorNotFoundException;
import models.Scoreboard;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScorewizUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static repositories.scorewiz.SubmitType.TAG_INPUT_TYPE_SUBMIT;

public class BaseScorewizRepository {
    protected final ScorewizUtils scorewizUtils;
    protected final ConfigRepository configRepository;
    protected WebDriver driver;
    protected Map<String, String> juryVoteURLMap;
    protected Scoreboard selectedScoreboard;
    protected String baseURL;

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

    private void provisionDriver() {
        FirefoxOptions options = new FirefoxOptions();
//        options.addArguments("start-maximized");
        if (configRepository.getBoolean("general.headless")) {
            options.addArguments("--headless");
            options.setHeadless(true);
        }

        driver = new FirefoxDriver(options);
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
