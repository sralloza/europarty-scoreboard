set -e

cat juries.json | jq -r '.|=sort_by(.country)' > juries2.json
rm juries.json
mv juries2.json juries.json