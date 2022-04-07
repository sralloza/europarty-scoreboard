package constants;

import config.Config;

public class ScorewizConstants {
    public static final String SW_BASE_URL = Config.get("scorewiz.baseUrl");
    public static final String SW_MENU_URL = SW_BASE_URL + "/my/scoreboards";
    public static final String SW_ACTION_URL_TEMPLATE = SW_BASE_URL + "/%s/%s/%s";

    public static final String SCOREWIZ_PASSWORD = Config.get("scorewiz.password");
    public static final String SCOREWIZ_USERNAME = Config.get("scorewiz.username");
}
