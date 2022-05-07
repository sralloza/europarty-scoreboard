package repositories;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class HttpRepository {
    @SneakyThrows
    public CompletableFuture<Boolean> sendRequest(String baseURL,
                                                  String path,
                                                  Set<Cookie> cookies,
                                                  Map<String, String> data) {
        String payload = data.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(payload));

        CookieManager cookieManager = new CookieManager();
        CookieStore cookieStore = cookieManager.getCookieStore();

        for (Cookie cookie : cookies) {
            HttpCookie sessionCookie = new HttpCookie(cookie.getName(), cookie.getValue());
            cookieStore.add(new URI(baseURL), sessionCookie);
        }

        HttpClient client = HttpClient.newBuilder().cookieHandler(cookieManager).build();
        HttpRequest request = requestBuilder.build();
        log.debug("Request URL: " + request.uri());
        log.debug("Request headers: " + request.headers());
        log.debug("Request cookies: " + cookieStore.getCookies());
        log.debug("Request payload: " + payload);
        log.debug("Request data: " + data);

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(u -> {
                    log.debug("Response status: " + u.statusCode());
                    log.debug("Response headers: " + u.headers());
                    log.debug("Request body: " + u.body());
                    return Set.of("2", "3").contains(String.format("%03d", u.statusCode()).substring(0, 1));
                });
    }
}
