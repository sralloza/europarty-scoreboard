package exceptions;

import org.openqa.selenium.WebElement;
import repositories.scorewiz.MainMenuButtonType;

public class MainMenuButtonNotFoundException extends RuntimeException {
    public MainMenuButtonNotFoundException(MainMenuButtonType buttonType, WebElement webElement) {
        super("No button found with type " + buttonType + " in " + webElement);
    }
}
