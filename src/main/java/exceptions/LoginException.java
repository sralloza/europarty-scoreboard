package exceptions;

public class LoginException extends RuntimeException {
    public LoginException(String currentUrl) {
        super("Failed to login (url=" + currentUrl + ")");
    }

    public LoginException(String currentUrl, String error, String username, String password) {
        super("Failed to login (url=" + currentUrl + ", error=" + error +
                ",username=" + username + ", password=" + password + ")");
    }
}
