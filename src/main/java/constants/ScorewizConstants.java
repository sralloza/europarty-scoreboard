package constants;

import java.io.File;

import static config.Config.SW_BASE_URL;
import static constants.EuropartyConstants.RESOURCES_PATH;

public class ScorewizConstants {
    public static final String SW_MENU_URL = SW_BASE_URL + "/my/scoreboards";
    public static final String SW_ACTION_URL_TEMPLATE = SW_BASE_URL + "/%s/%s/%s";

    public static final String WEBDRIVER_NAME = "chromedriver";
    public static final File LOCAL_WEBDRIVER_PATH = new File(RESOURCES_PATH + WEBDRIVER_NAME);
}
