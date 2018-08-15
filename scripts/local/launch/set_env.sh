#!/usr/bin/env bash

cd scripts/local/launch
source .env.sh


############## TODO: Revisit
envFile=".env.sh"

while IFS='' read -r line || [[ -n "$line" ]]; do
    line=$(echo $line | sed -e 's/\;/\\\;/g')
    if [[ ${line:0:1} != \# ]]; then
        if [ "${line:0:1}" != " " ] ; then
            export $line
        fi
    fi
done < $envFile
