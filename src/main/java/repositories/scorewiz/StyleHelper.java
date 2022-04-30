package repositories.scorewiz;

import config.ConfigRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.openqa.selenium.Keys.DELETE;
import static org.openqa.selenium.Keys.RETURN;
import static repositories.scorewiz.StyleConfigType.BACKGROUND;
import static repositories.scorewiz.StyleConfigType.COLOR;
import static repositories.scorewiz.StyleConfigType.GENERAL;

@Slf4j
public class StyleHelper {
    private final ConfigRepository config;
    private Consumer<WebElement> scroller;
    private Runnable waiter;
    private Consumer<String> jsExecutor;
    private WebDriver driver;

    @Inject
    public StyleHelper(ConfigRepository config) {
        this.config = config;
    }

    public void setStyles(WebElement form,
                          Consumer<WebElement> scroller,
                          Runnable waiter,
                          Consumer<String> jsExecutor,
                          WebDriver driver) {
        this.scroller = scroller;
        this.waiter = getWaiter(7000);
        this.jsExecutor = jsExecutor;
        this.driver = driver;

        setLettersColor(form);
        setBackgroundScoreboardPage(form);
        setBackgroundParticipantDefault(form);
        setBackgroundParticipantVoted(form);
        setBackgroundParticipantReceivingVotes(form);
        setBackgroundParticipantVoting(form);
        setUppercaseParticipants(form);
        setFastMode(form);
        throw new RuntimeException();
//        submit(form);
    }

    private Runnable getWaiter(Integer milliseconds) {
        return () -> {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private void setLettersColor(WebElement form) {
        fillRadioButton(form, "letters", COLOR);
    }

    private void setBackgroundScoreboardPage(WebElement form) {
        fillInputText(form, "scoreboardPage", BACKGROUND);
    }

    private void setBackgroundParticipantDefault(WebElement form) {
        fillInputText(form, "participantDefault", BACKGROUND);
    }

    private void setBackgroundParticipantVoted(WebElement form) {
        fillInputText(form, "participantVoted", BACKGROUND);
    }

    private void setBackgroundParticipantReceivingVotes(WebElement form) {
        fillInputText(form, "participantReceivingVotes", BACKGROUND);
    }

    private void setBackgroundParticipantVoting(WebElement form) {
        fillInputText(form, "participantVoting", BACKGROUND);
    }

    private void setUppercaseParticipants(WebElement form) {
        fillCheckBox(form, "uppercaseParticipants", GENERAL);
    }

    private void setFastMode(WebElement form) {
        fillCheckBox(form, "fastMode", GENERAL);
    }

    private void fillRadioButton(WebElement form, String configName, StyleConfigType configType) {
        String text = getConfigByType(configName, configType, ConfigRepository::getStyleString);
        String selector = config.getSelector(configName);
        Optional<WebElement> input = form.findElements(By.name(selector)).stream()
                .filter(w -> w.getAttribute("value").equals(text))
                .findAny();
        if (input.isEmpty()) {
            throw new RuntimeException("Can't find radio button with value " + text + " for selector " + selector);
        }
        scroller.accept(input.get());
        input.get().click();
        waiter.run();
    }

    private void fillInputText(WebElement form, String configName, StyleConfigType configType) {
        String text = getConfigByType(configName, configType, ConfigRepository::getStyleString).toUpperCase();
        String selector = config.getSelector(configName);
        List<WebElement> inputs = form.findElements(By.name(selector));
        if (inputs.size() != 1) {
            throw new RuntimeException("Expected one input found for selector " + selector +
                    " but found " + inputs.size());
        }

        WebElement input = inputs.get(0);

        log.debug("Input found, executing scroller");
        scroller.accept(input);
        log.debug("Scroller executed");

        log.debug("Building action");
        Actions builder = new Actions(driver);
        Action mouseOverHome = builder
                .moveToElement(input)
                .click()
                .sendKeys(DELETE)
                .build();

        log.debug("Executing action");
        mouseOverHome.perform();
        log.debug("Executed action: " + mouseOverHome);

        log.debug("Executing waiter");
        waiter.run();
        log.debug("Executed waiter");

        log.debug("Sending keys");
        input.sendKeys(text);
        log.debug("Sent keys");

        log.debug("Executing waiter");
        waiter.run();
        log.debug("Executed waiter");
    }

    private void fillCheckBox(WebElement form, String configName, StyleConfigType configType) {
        boolean selected = getConfigByType(configName, configType, ConfigRepository::getStyleBoolean);
        String selector = config.getSelector(configName);
        List<WebElement> inputs = form.findElements(By.name(selector));
        if (inputs.size() != 1) {
            throw new RuntimeException("Expected one input found for selector " + selector);
        }
        WebElement input = inputs.get(0);
        if (input.isSelected() != selected) {
            scroller.accept(input);
            input.click();
            waiter.run();
        }
    }

    private void submit(WebElement form) {
        WebElement submitButton = form.findElement(By.id("colorSubmit"));
        scroller.accept(submitButton);
        submitButton.click();
    }

    private <T> T getConfigByType(String configName,
                                  StyleConfigType configType,
                                  BiFunction<ConfigRepository, String, T> configGetter) {
        switch (configType) {
            case COLOR:
                return configGetter.apply(config, "fg." + configName);
            case BACKGROUND:
                return configGetter.apply(config, "bg." + configName);
        }
        return configGetter.apply(config, configName);
    }
}

