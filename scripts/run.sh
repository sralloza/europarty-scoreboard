#!/usr/bin/env bash

set -euo pipefail
folder=$(basename $(pwd))
if [[ "$folder" == "scripts" ]]; then
  echo "Do not run this script from the scripts folder"
  exit 1
fi

if [[ ! .env ]]; then
  echo ".env file does not exist"
  exit 1
fi

JARS_FOLDER="scripts/jars"
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

echo "========== Deleting forms ==========="
java -jar $DELETE_FORS_JAR
echo "+Forms deleted successfully"

echo "========== Creating form ============"
java -jar $CREATE_FORM_JAR
echo "+Form created successfully"
