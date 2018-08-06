#!/bin/bash

APP_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd ../../../ && pwd )"

source $APP_HOME/scripts/local/launch/set_env.sh
source $APP_HOME/scripts/local/launch/.env_uris.sh

set -e

cd $APP_HOME/device-simulation/scripts/docker
./run >> $APP_HOME/scripts/local/device-simulation.txt
cd $APP_HOME/asa-manager/scripts/docker
./run >> $APP_HOME/scripts/local/asa-manager.txt
cd $APP_HOME/pcs-auth/scripts/docker
./run >> $APP_HOME/scripts/local/pcs-auth.txt

set +e