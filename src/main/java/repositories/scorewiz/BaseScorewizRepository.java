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

        WebElement submitButton = driver.findElement(By.xpath("//input[@type='submit']"));
        scrollToElement(submitButton);
        submitButton.click();
    }

    public void logout() {
        driver.get(getLogoutURL());
        driver.close();
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        waitPageLoads();
    }

    protected void waitPageLoads() {
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }
}
