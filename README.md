# Europarty Scoreboard

[![E2E Tests](https://github.com/sralloza/europarty-scoreboard/actions/workflows/ci.yml/badge.svg)](https://github.com/sralloza/europarty-scoreboard/actions/workflows/ci.yml)

## Configuration

Configuration is managed by environment variables.

### Required

- **SW_USERNAME**: Your scorewiz username
- **SW_PASSWORD**: Your scorewiz password
- **GOOGLE_CREDS_PRIVATE_KEY**: The private key of your Google credentials. Endlines characters must be replaced with `"\\n"`.
- **GOOGLE_CREDS_EMAIL**: Your Google credentials email.
- **GS_VOTE_ID**: The Google Sheet ID of the votes sheet.
- **GS_TELEVOTE_ID**: The Google Sheet ID of the televotes sheet.
- **GS_PARTICIPANTS_ID**: The Google Sheet ID of the participants sheet.

### Optional

#### Style

- **SW_FOREGROUND_LETTERS**: Color of the letters and numbers on the scoreboard. Can be `white` or `black`. Defaults to `white`.
- **SW_BACKGROUND_SCOREBOARD_PAGE**: Background color of the scoreboard page. Defaults to `1B0F3B`.
- **SW_BACKGROUND_PARTICIPANT_DEFAULT**: Default background for any participant yet to vote, not getting votes yet and not voting itself. Default to `967C48`.
- **SW_BACKGROUND_PARTICIPANT_VOTED**: Background of participants that already voted. Defaults to `B35B40`.
- **SW_BACKGROUND_PARTICIPANT_RECEIVING_VOTES**: Background of the participants currently getting votes. Defaults to `AFB376`.
- **SW_BACKGROUND_PARTICIPANT_VOTING**: Background of the participant currently voting. Defaults to `A68414`.
- **SW_UPPERCASE_PARTICIPANTS**: Display the names of participants in upper case. Defaults to `true`.
- **SW_FAST_MODE**: Enable fast mode (lower points appear at once, only three highest scores come in one by one). Defaults to `true`.

#### Other

- **SW_BASE_URL**: The base URL of your scorewiz installation. Defaults to `https://scorewiz.com`.
- **SW_SCOREBOARD_NAME**: The name of the scoreboard you want to use. Defaults to `Europarty 2022`.
- **TEST**: If set to `true`, the datetime will be appended to the scoreboard name. Defaults to `false`.
- **HEADLESS**: Whether to run the browser in headless mode. Defaults to `true`.
- **LOG_LEVEL**: The log level to use. Defaults to `info`.

## Google Sheets Authentication

1. Create a new Google Cloud Platform project.
2. Create a [new service account](https://console.cloud.google.com/iam-admin/serviceaccounts).
3. Create an ssh key for the created service account. You must select the P12 format.

## Scripts

To launch the application, fill the `.env` file with the required environment variables as shown below.

```env
SW_USERNAME='libol64870@sartess.com'
SW_PASSWORD='libol64870@sartess.com'
GOOGLE_CREDS_EMAIL='example@google.com'
GS_VOTE_ID='vote-spreadsheet-id'
GS_TELEVOTE_ID='tele-vote-spreadsheet-id'
GS_PARTICIPANTS_ID='participants-spreadsheet-id'
GOOGLE_CREDS_PRIVATE_KEY='raw-private-key-with-\n-characters'
LOG_LEVEL='DEBUG'
```

Then, run the following script:

```bash
delivery/pipelines/deploy.sh <profile> [MODE]
```

Where `profile` is the name of the profile to use (see `delivery/profiles` folder) and `MODE` is the mode to use. It can be `all`, `validate` or `skip-delete`. Defaults to `all`.

- `all`: validate the data in the Google Sheets, delete all scoreboards and create a new scoreboard based on the data in the Google Sheets.
- `validate`: validate the data in the Google Sheets.
- `skip-delete`: validate the data in the Google Sheets create a new scoreboard based on the data in the Google Sheets without deleting old scoreboards.

Note: the script will create jar files in the folder `delivery/jars`. To force the script to recompile them, remove the folder.
