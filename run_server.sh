#!/bin/bash
set -e
: ${1?'Missing partner config. Usage: ./run_server.sh <partner_config_file> <port> <loglevel>. Example: ./run_server.sh test.json 8080 info'}
: ${2?'Missing port. Port need to match the port in partner json file (base_url property). Usage: ./run_server.sh <partner_config_file> <port> <loglevel>. Example: ./run_server.sh test.json 8080 info'}
: ${BASIC_USER?'Missing BASIC_USER environment variable.'}
: ${BASIC_PASSWORD?'Missing BASIC_PASSWORD environment variable.'}
LOG_LEVEL="${3:-warn}"
./build.sh
DOCKER_COMMAND="mvn exec:java -Dexec.args=$1"
echo Starting server with command $DOCKER_COMMAND on port $2
docker run -it --name partner-server --rm \
 --mount source=maven_cache,target=/root/.m2 \
 -p "$2:$2" \
 -e "TERM=xterm-256color" \
 -e CONFIG=$1 \
 -e "BASIC_USER=$BASIC_USER" \
 -e "BASIC_PASSWORD=$BASIC_PASSWORD" \
 --net=host \
 partner-connect-api bash -c "$DOCKER_COMMAND"
