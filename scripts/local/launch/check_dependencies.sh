#!/usr/bin/env bash
# Copyright (c) Microsoft. All rights reserved.

set -e

APP_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd ../../../ && pwd )"

cd $APP_HOME/$1

./scripts/env-vars-check > /dev/null


set +e

