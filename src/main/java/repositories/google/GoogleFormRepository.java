package repositories.google;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import com.google.inject.Singleton;
import config.ConfigRepository;
import exceptions.InvalidPrivateKeyException;
import exceptions.NoValidVotesFoundException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

import static constants.GoogleSheetsConstants.GS_PARTICIPANTS_RANGE;
import static constants.GoogleSheetsConstants.GS_VOTES_RANGE;


@Singleton
@Slf4j
public class GoogleFormRepository {
    private final ConfigRepository configRepository;
    private final Cache<String, List<GoogleSheetsVote>> voteCache;
    private final Cache<String, List<GoogleSheetsParticipant>> participantsCache;

    @Inject
    public GoogleFormRepository(ConfigRepository configRepository) {
        this.configRepository = configRepository;
        this.voteCache = Caffeine.newBuilder().build();
        this.participantsCache = Caffeine.newBuilder().build();
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

    public List<GoogleSheetsVote> getGoogleSheetsTelevotes() {
        return getGoogleSheetsVotesByConfigKey("googleSheets.sheetIDs.televotes");
    }

    public List<GoogleSheetsVote> getGoogleSheetsVotes() {
        return getGoogleSheetsVotesByConfigKey("googleSheets.sheetIDs.votes");
    }

    private List<GoogleSheetsVote> getGoogleSheetsVotesByConfigKey(String configKey) {
        String spreadsheetId = configRepository.getString(configKey);
        String spreadsheetRange = GS_VOTES_RANGE;
        String key = buildKey(spreadsheetId, spreadsheetRange);

        List<GoogleSheetsVote> cachedResult = voteCache.getIfPresent(key);
        if (cachedResult == null) {
            log.info("Downloading Google Sheets votes");
            var result = getRows(spreadsheetId, spreadsheetRange, GoogleSheetsVote::new);
            voteCache.put(key, result);
            return result;
        }
        log.info("Loading Google Sheets votes from cache");
        return cachedResult;
    }

    public List<GoogleSheetsParticipant> getGoogleSheetsParticipants() {
        String spreadsheetId = configRepository.getString("googleSheets.sheetIDs.participants");
        String spreadsheetRange = GS_PARTICIPANTS_RANGE;
        String key = buildKey(spreadsheetId, spreadsheetRange);

        List<GoogleSheetsParticipant> cachedResult = participantsCache.getIfPresent(key);
        if (cachedResult == null) {
            log.info("Downloading Google Sheets participants");
            var result = getRows(spreadsheetId,spreadsheetRange,GoogleSheetsParticipant::new);
            participantsCache.put(key, result);
            return result;
        }
        log.info("Loading Google Sheets participants from cache");
        return cachedResult;
    }

    private String buildKey(String spreadsheetId, String spreadsheetRange) {
        return spreadsheetId + "." + spreadsheetRange;
    }

    @SneakyThrows
    private <T> List<T> getRows(String spreadsheetId, String spreadsheetRange, Function<List<String>, T> mapper) {
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
            throw new NoValidVotesFoundException("No valid lines found (found only " + strValues.size() + " rows)");
        }

        return strValues.stream()
                .skip(1)
                .map(mapper)
                .peek(line -> log.debug("Parsed line from google sheets: {}", line))
                .collect(Collectors.toList());
    }
}
