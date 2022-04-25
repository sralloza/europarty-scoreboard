# Europarty Scoreboard

## Configuration

Configuration is managed by environment variables.

Required:

- **SW_USERNAME**: Your scorewiz username
- **SW_PASSWORD**: Your scorewiz password
- **GOOGLE_CREDS_PRIVATE_KEY**: The private key of your Google credentials. Endlines characters must be replaced with `"\\n"`.
- **GOOGLE_CREDS_EMAIL**: Your Google credentials email.
- **GS_VOTE_ID**: The Google Sheet ID of the votes sheet.
- **GS_TELEVOTE_ID**: The Google Sheet ID of the televotes sheet.
- **GS_PARTICIPANTS_ID**: The Google Sheet ID of the participants sheet.

Optional:
- **SW_BASE_URL**: The base URL of your scorewiz installation. Defaults to `https://scorewiz.com`.
- **SW_SCOREBOARD_NAME**: The name of the scoreboard you want to use. Defaults to `Europarty 2022`.
- **TEST**: If set to `true`, the datetime will be appended to the scoreboard name. Defaults to `false`.
- **HEADLESS**: Whether to run the browser in headless mode. Defaults to `true`.
- **LOG_LEVEL**: The log level to use. Defaults to `info`.

## Google Sheets Authentication

1. Create a new Google Cloud Platform project.
2. Create a [new service account](https://console.cloud.google.com/iam-admin/serviceaccounts).
3. Create an ssh key for the created service account. You must select the P12 format.
