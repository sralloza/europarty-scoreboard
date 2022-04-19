package repositories.common;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.PemReader;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.inject.Inject;
import config.ConfigRepository;
import exceptions.InvalidPrivateKeyException;
import exceptions.NoValidVotesFoundException;
import lombok.SneakyThrows;
import models.GoogleSheetsParticipant;
import models.GoogleSheetsVote;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


public class GoogleFormCommonRepository {
    private final String spreadsheetId;
    private final String spreadsheetRange;
    private final ConfigRepository configRepository;

    @Inject
    public GoogleFormCommonRepository(String spreadsheetConf, String spreadsheetRange, ConfigRepository configRepository) {
        this.spreadsheetId = configRepository.getString(spreadsheetConf);
        this.spreadsheetRange = spreadsheetRange;
        this.configRepository = configRepository;
    }

    private PrivateKey getKey() throws Exception {
        String privateKey = configRepository.getString("googleSheets.credentials.privateKey");
        if (privateKey.contains("\\n")) {
            privateKey = privateKey.replace("\\n", "\n");
        }

        StringReader stream = new StringReader(privateKey);
        PemReader.Section parsedPrivateKey = PemReader.readFirstSectionAndClose(stream, "PRIVATE KEY");
        byte[] bytes = Optional.ofNullable(parsedPrivateKey)
                .orElseThrow(InvalidPrivateKeyException::new)
                .getBase64DecodedBytes();
        return SecurityUtils.getRsaKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private Credential authorize() throws Exception {
        PrivateKey key = getKey();

        return new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setServiceAccountId(configRepository.getString("googleSheets.credentials.email"))
                .setServiceAccountScopes(List.of(SheetsScopes.SPREADSHEETS))
                .setServiceAccountPrivateKey(key)
                .build();
    }

    private Sheets getSheetsService() throws Exception {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(configRepository.getString("scorewiz.scoreboard.name"))
                .build();
    }

    public List<GoogleSheetsVote> getGoogleSheetsVotes() {
        return this.getRows(GoogleSheetsVote::new);
    }

    public List<GoogleSheetsParticipant> getGoogleSheetsParticipants() {
        return this.getRows(GoogleSheetsParticipant::new);
    }

    @SneakyThrows
    private <T> List<T> getRows(Function<List<String>, T> mapper) {
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
            throw new NoValidVotesFoundException("No valid lines found (found only" + strValues.size() + "rows)");
        }

        return strValues.stream()
                .skip(1)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
