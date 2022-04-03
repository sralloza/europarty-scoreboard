package repositories.televote;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import config.Config;
import lombok.SneakyThrows;
import mappers.GSVoteMapper;
import models.GoogleSheetsVote;
import models.Televote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleFormTelevoteRepository implements TelevoteRepository {
    private static final String APPLICATION_NAME = Config.get("application.name");
    private static final String SERVICE_ACCOUNT_EMAIL = Config.get("google.credentials.email");
    private static final String SPREADSHEET_ID = Config.get("google.spreadsheets.televote.id");
    private static final String SPREADSHEET_RANGE = "A:L";

    private final GSVoteMapper mapper;

    public GoogleFormTelevoteRepository() {
        mapper = new GSVoteMapper();
    }

    private static Credential authorize() throws Exception {
        return new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountScopes(List.of(SheetsScopes.SPREADSHEETS))
                .setServiceAccountPrivateKeyFromPemFile(new File("src/main/resources/key.pem"))
                .build();

    }

    private static Sheets getSheetsService() throws Exception {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @SneakyThrows
    @Override
    public List<Televote> getTelevotes() {
        Sheets sheetsService = getSheetsService();
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, SPREADSHEET_RANGE)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        List<List<String>> strValues = values.stream()
                .map(r -> r.stream().map(String::valueOf).collect(Collectors.toList()))
                .collect(Collectors.toList());

        int skip = strValues.size() > 1 ? 1 : 0;
        var votes = strValues.stream()
                .skip(skip)
                .filter(r -> !r.isEmpty())
                .map(GoogleSheetsVote::new)
                .collect(Collectors.toList());
        return mapper.buildTelevotes(votes);
    }
}
