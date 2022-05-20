#!/bin/bash
./build.sh
DOCKER_COMMAND="openapi-generator-cli generate -g markdown -i openapi/partner-connect-2.0.yaml -o api-doc/"
echo Starting to generate doc with command $DOCKER_COMMAND
docker run -it --name partner-doc --rm \
  -v "$(pwd)"/api-doc:/partner-connect-api/api-doc \
  -e TERM=xterm-256color \
  partner-connect-api \
  bash -c "$DOCKER_COMMAND"
echo "Successfully generated doc under api-doc/"