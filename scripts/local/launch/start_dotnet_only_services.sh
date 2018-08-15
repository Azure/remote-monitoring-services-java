#!/bin/bash
# Copyright (c) Microsoft. All rights reserved.

APP_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd ../../../ && pwd )"

source $APP_HOME/scripts/local/launch/set_env.sh
source $APP_HOME/scripts/local/launch/.env_uris.sh

set -e

echo "Starting Device-Simulation MS ....."
cd $APP_HOME/device-simulation/scripts/docker
./run >> $APP_HOME/scripts/local/device-simulation.txt &

echo "Starting ASA-Manager MS ....."
cd $APP_HOME/asa-manager/scripts/docker
./run >> $APP_HOME/scripts/local/asa-manager.txt &

echo "Starting PCS-Auth MS ....."
cd $APP_HOME/pcs-auth/scripts/docker
./run >> $APP_HOME/scripts/local/pcs-auth.txt &

set +e