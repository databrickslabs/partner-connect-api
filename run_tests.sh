#!/bin/bash
: ${1?'Missing partner config. Usage: ./run_tests.sh partner.json [loglevel]. Example: ./run_tests.sh test.json info'}
: ${BASIC_USER?'Missing BASIC_USER environment variable.'}
: ${BASIC_PASSWORD?'Missing BASIC_PASSWORD environment variable.'}
: ${PAT_TOKEN?'Missing PAT_TOKEN environment variable.'}
LOG_LEVEL="${2:-warn}"

./build.sh
DOCKER_COMMAND="mvn -Dorg.slf4j.simpleLogger.defaultLogLevel=$LOG_LEVEL test -DCONFIG=$1"
echo Starting tests with command $DOCKER_COMMAND
report_dir=target/surefire-reports
report_file=target/test-reports.zip
rm -r -f $report_dir
rm -f $report_file
docker run -it --name partner-test --rm \
  --mount source=maven_cache,target=/root/.m2 \
  -v "$(pwd)"/$report_dir:/partner-connect-api/$report_dir \
  -e "BASIC_USER=$BASIC_USER" \
  -e "BASIC_PASSWORD=$BASIC_PASSWORD" \
  -e "PAT_TOKEN=$PAT_TOKEN" \
  -e TERM=xterm-256color \
  -e REMOTE_SERVER=true \
  --net=host \
  partner-connect-api \
  bash -c "$DOCKER_COMMAND"
zip -r -q $report_file $report_dir
echo "Test run completed. Please upload the following artifacts to the partner Artifacts Intake Form here: https://docs.google.com/forms/d/e/1FAIpQLSc2vcAqAOVlE7Llo3GMhLrK3klzYXQ5LeWyqaR6L20RjHpygQ/viewform.
         - Test report: $(pwd)/$report_file
         - Partner config: $(pwd)/partners/test/$1"