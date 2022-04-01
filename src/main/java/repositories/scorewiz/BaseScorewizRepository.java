package repositories.scorewiz;

import config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;

public class BaseScorewizRepository {
    protected static final String BASE_URL = Config.getInstance().getProperty("scorewiz.baseUrl");
    protected static final Boolean HEADLESS = !Config.getInstance().getProperty("debug").equals("true");
    protected static final String SCOREWIZ_PASSWORD = Config.getInstance().getProperty("scorewiz.password");
    protected static final String SCOREWIZ_USERNAME = Config.getInstance().getProperty("scorewiz.username");
    protected static final String URL_TEMPLATE = BASE_URL + "/%s/%s/%s";
    private static final String WEBDRIVER_PATH = Config.getInstance().getProperty("webdriver.chrome.driver");
    protected final WebDriver driver;

    protected Map<String, String> juryMapping;
    protected String scorewizSid;
    protected String scorewizPass;


    public BaseScorewizRepository() {
        System.setProperty("webdriver.chrome.driver", WEBDRIVER_PATH);

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
        return String.format(URL_TEMPLATE, action, scorewizSid, scorewizPass);
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
        driver.get(getLoginURL());
        driver.findElement(By.name("email")).sendKeys(SCOREWIZ_USERNAME);
        driver.findElement(By.name("pass")).sendKeys(SCOREWIZ_PASSWORD);

        submitInputTypeSubmit();
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
        ((JavascriptExecutor) driver).executeScript("document.getElementsByTagName(\"header\")[0].remove()");
    }

    protected void submitInputTypeSubmit() {
        submit(By.xpath("//input[@type='submit']"));
    }

    protected void submitVotesSubmitId() {
        submit(By.id("votesSubmit"));
    }

    protected void submitNamesSubmitId() {
        submit(By.id("namesSubmit"));
    }

    protected void submit(By selector) {
        WebElement namesSubmitBtn = driver.findElement(selector);
        scrollToElement(namesSubmitBtn);
        namesSubmitBtn.click();
    }

    protected Object runJavascript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }
}
