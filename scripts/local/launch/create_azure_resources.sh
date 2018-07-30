#!/usr/bin/env bash
set -e
log_file=".env"
envvars=".envvars"

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
   pcs -t remotemonitoring -s local  | tee -a "$log_file"
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
        export $line
		echo "export $line" >> $log_file
    done < $envvars
}

truncate -s 0 $log_file
truncate -s 0 $envvars

check_dependencies
create_resources

tail -n 19 $log_file >> $envvars
truncate -s 0 $log_file
head -n -2 $envvars

set +e

set_env_vars