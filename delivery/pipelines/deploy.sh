#!/usr/bin/env bash

set -euo pipefail

profile=${1?Usage: delivery/pipelines/deploy.sh <profile> [mode]}
if [[ ! -f delivery/profiles/$profile.env ]]; then
  echo "Profile $profile not found"
  exit 1
fi

mode=${2:-all}
validModes=(all validate skip-delete)
if [[ ! " ${validModes[@]} " =~ " ${mode} " ]]; then
  echo "Invalid mode $mode"
  exit 1
fi

cp delivery/profiles/$profile.env .env
echo $VAULT_PASSWORD > .vault-pass.txt
ansible-vault decrypt --vault-password-file=.vault-pass.txt --output=.env .env
rm .vault-pass.txt

JARS_FOLDER="delivery/jars"
DATA_VALIDATION_JAR="$JARS_FOLDER/europarty-data-validation.jar"
DELETE_FORS_JAR="$JARS_FOLDER/europarty-delete-forms.jar"
CREATE_FORM_JAR="$JARS_FOLDER/europarty-create-form.jar"

mkdir -p $JARS_FOLDER

if [[ ! -f ./$DATA_VALIDATION_JAR ]]; then
  echo "+Building data validation jar"
  export EUROPARTY_MAIN_CLASS="GoogleFormsDataValidation"
  ./gradlew fatJar
  cp build/libs/*.jar ./$DATA_VALIDATION_JAR
  unset EUROPARTY_MAIN_CLASS
fi

if [[ ! -f ./$DELETE_FORS_JAR ]]; then
  echo "+Building delete forms jar"
  export EUROPARTY_MAIN_CLASS="DeleteAllScoreboards"
  ./gradlew fatJar
  cp build/libs/*.jar ./$DELETE_FORS_JAR
  unset EUROPARTY_MAIN_CLASS
fi

if [[ ! -f ./$CREATE_FORM_JAR ]]; then
  echo "+Building create form jar"
  ./gradlew fatJar
  cp build/libs/*.jar ./$CREATE_FORM_JAR
fi


set -o allexport && source .env && set +o allexport
echo "========== Validating data =========="
java -jar $DATA_VALIDATION_JAR
echo "+Data validation completed successfully"

if [[ $mode == "validate" ]]; then
  echo "+Skipping delete and create form"
  exit 0
fi

if [[ $mode == "skip-delete" ]]; then
  echo "+Skipping delete forms"
else
  echo "========== Deleting forms ==========="
  java -jar $DELETE_FORS_JAR
  echo "+Forms deleted successfully"
fi

echo "========== Creating form ============"
java -jar $CREATE_FORM_JAR
echo "+Form created successfully"
