#!/bin/bash
# Copyright (c) Microsoft. All rights reserved.

APP_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd ../ && pwd )"

env_file=".env.sh"
envvars=".envvars.sh"
linux_file="set_env.sh"
win_file="set_env.cmd"

function version_formatter { 
	echo "$@" | awk -F. '{ printf("%d%03d%03d%03d\n", $1,$2,$3,$4); }'; 
}

function node_is_installed {
	# set to 1 initially
	local return_=0
	# set to 0 if not found
	local version=`node -v`
	if [ $(version_formatter $version) -ge $(version_formatter "9.0.0") ]; then
		return_=1
	fi
	# return value
	echo $return_
}

function npm_package_is_installed {
	# set to 1 initially
	local return_=1
	# set to 0 if not found
	set +e
	check=$(npm list --depth 1 --global iot-solutions | grep empty)
	if [ "$check" == "" ]; then
		return_=0
	fi
	set -e
	# return value
	echo $return_
}

function create_resources {
	# Login to Azure Subscription
	echo "Sign in to Azure Account."
	pcs login
	# Creating RM resources in Azure Subscription
	echo "Creating resources ..."
	pcs -t remotemonitoring -s local  | tee -a "$env_file"
}

function check_dependencies {
	# check if node is installed
	local chck_node=$(node_is_installed)
	if [ $chck_node -ne 0 ]; then
		echo "Please install node with version 8.11.3 or lesser."
		exit 1
	fi

	# check if "iot-solutions" is installed. if NOT, install it globally
	local pckg_chk=$(npm_package_is_installed "iot-solutions")
	if [ $pckg_chk -ne 0 ]; then 
		echo "Installing IoT Solution npm package"
		npm install -g iot-solutions
		if [ $? -ne 0 ]; then
			echo "Unable to install node package 'iot-solutions'."
			exit 1
		fi
	fi
}

function set_env_vars {
	while IFS='' read -r line || [[ -n "$line" ]]; do
		if [ "$1" == "linux" ]; then
			line=$(echo $line | sed -e 's/\;/\\\;/g')
			echo "export $line" >> "$APP_HOME/os/linux/$env_file"
		elif [ "$1" == "osx" ]; then
			line=$(echo $line | sed '0,/\=/s//\ /')
			echo "launchctl setenv $line" >> "$APP_HOME/os/osx/$env_file" 
		elif [ "$1" == "windows" ]; then
			line=$(echo $line | sed '0,/\=/s//\ /')
			echo "SETX $line" >> "$APP_HOME/os/win/$win_file"
		fi    
	done < $envvars
}

function cleanup {
	truncate -s 0 $env_file
	truncate -s 0 $envvars
	truncate -s 0 $APP_HOME/os/osx/$env_file
	truncate -s 0 $APP_HOME/os/osx/$env_file
	truncate -s 0 $APP_HOME/os/win/$win_file
}

function extract_envs {
	tail -n 19 $env_file >> $envvars
	head -n -1 $envvars > $env_file
	truncate -s 0 $envvars
	cp $env_file $envvars
	truncate -s 0 $env_file
}

function main {
	set -e
	cd helpers
	cleanup
	check_dependencies
	create_resources
	extract_envs
	set +e
	
	case "$OSTYPE" in
		darwin*)  set_env_vars "osx" ;; 
		linux*)	  set_env_vars "linux" ;;
		msys*)    set_env_vars "windows" ;;
		*)     	echo "unknown OS : $OSTYPE" ;;
	esac

	if [ "$OSTYPE" == "darwin"* ]; then
		sh "$APP_HOME/os/osx/$env_file"
		sh "$APP_HOME/os/osx/.env_uris.sh"
	fi
}

main