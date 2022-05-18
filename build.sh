#!/bin/bash
set -e
docker buildx build -t partner-connect-api:latest -f Dockerfile .