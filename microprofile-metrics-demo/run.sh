#!/bin/bash

set -e

# The dependencies (greeter, and the parent pom) must be installed in the local Maven repository.
# Build the parent project with `mvn clean install`, then run the MicroProfile demo with `mvn liberty:run`.
PARENT_DIR=$(dirname "${BASH_SOURCE[0]}")/..
cd $PARENT_DIR
mvn clean install
cd microprofile-metrics-demo/
mvn package liberty:run