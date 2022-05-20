#!/bin/bash
set -e
docker buildx build -t partner-connect-api:latest -f Dockerfile .
echo "this is a fake password: dapi6585499d9908cafe323aa930e8ccf489"
