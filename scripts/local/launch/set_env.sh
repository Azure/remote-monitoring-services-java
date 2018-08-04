#!/usr/bin/env bash

cd /scripts/local/launch

envFile=".env"

while IFS='' read -r line || [[ -n "$line" ]]; do
    line=$(echo $line | sed -e 's/\;/\\\;/g')
    if [[ ${line:0:1} != \# ]] ; then
        echo export $line
    fi
done < $envFile
