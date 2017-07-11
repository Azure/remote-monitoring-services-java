#!/usr/bin/env bash

COL_NO="\033[0m" # no color
COL_ERR="\033[1;31m" # light red
COL_H1="\033[1;33m" # yellow
COL_H2="\033[1;36m" # light cyan

header() {
    echo -e "${COL_H1}\n### $1 ${COL_NO}"
}

error() {
    echo -e "${COL_ERR}$1 ${COL_NO}"
}

check_dependency_java() {
    set +e
    TEST=$(which java)
    if [[ -z "$TEST" ]]; then
        echo "ERROR: 'java' command not found."
        echo "Install OpenJDK or Oracle JDK and make sure the 'java' command is in the PATH."
        echo "OpenJDK installation: http://openjdk.java.net/install"
        echo "Oracle Java Standard Edition: http://www.oracle.com/technetwork/java/javase/downloads"
        exit 1
    fi
    set -e
}

check_dependency_docker() {
    set +e
    TEST=$(which docker)
    if [[ -z "$TEST" ]]; then
        echo "ERROR: 'docker' command not found."
        echo "Install Docker and make sure the 'docker' command is in the PATH."
        echo "Docker installation: https://www.docker.com/community-edition#/download"
        exit 1
    fi
    set -e
}
