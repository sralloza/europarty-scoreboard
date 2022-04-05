package repositories.common;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import config.Config;
import exceptions.FileDeleteException;
import exceptions.NoValidVotesFoundException;
import lombok.SneakyThrows;
import models.GoogleSheetsVote;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleFormCommonRepository {
    private static final String APPLICATION_NAME = Config.get("application.name");
    private static final String SERVICE_ACCOUNT_EMAIL = Config.get("google.credentials.email");
    private static final String SPREADSHEET_RANGE = "A:L";

    private static String spreadsheetId;
    private static String spreadsheetRange;

    public GoogleFormCommonRepository(String spreadsheetId, String spreadsheetRange) {
        GoogleFormCommonRepository.spreadsheetId = spreadsheetId;
        GoogleFormCommonRepository.spreadsheetRange = spreadsheetRange;
    }

    public GoogleFormCommonRepository(String spreadsheetId) {
        this(spreadsheetId, SPREADSHEET_RANGE);
    }

    private static Credential authorize() throws Exception {
        InputStream keyStream = Config.getResource("key.pem");

        File keyFile = File.createTempFile("key", ".p12").getAbsoluteFile();
        FileUtils.copyInputStreamToFile(keyStream, keyFile);
        System.out.println("Key file created: " + keyFile);

        var creds = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountScopes(List.of(SheetsScopes.SPREADSHEETS))
                .setServiceAccountPrivateKeyFromPemFile(keyFile)
                .build();
        if (!keyFile.delete()) {
            throw new FileDeleteException(keyFile);
        }
        System.out.println("Key deleted");
        return creds;
    }

    private static Sheets getSheetsService() throws Exception {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @SneakyThrows
    public List<GoogleSheetsVote> getGoogleSheetsVotes() {
        Sheets sheetsService = getSheetsService();
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, spreadsheetRange)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        List<List<String>> strValues = values.stream()
                .map(r -> r.stream().map(String::valueOf).collect(Collectors.toList()))
                .filter(r -> !r.isEmpty())
                .collect(Collectors.toList());

        if (strValues.size() < 2) {
            throw new NoValidVotesFoundException("No valid votes found (found only" + strValues.size() + "rows)");
        }

        return strValues.stream()
                .skip(1)
                .map(GoogleSheetsVote::new)
                .collect(Collectors.toList());
    }
}
