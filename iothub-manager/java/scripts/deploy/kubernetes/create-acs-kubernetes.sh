#!/usr/bin/env bash

### CONFIGURATION ###

# Configure the parameters below and remove the next 2 lines
echo "You need to configure this script before starting the deployment."
exit -1



CLUSTER_NAME="your-ms-alias-foobar"                 # Service details, remember to prefix with your alias/name.
RESOURCE_GROUP="your-ms-alias-foobar-rg"            # Service details, remember to prefix with your alias/name.
DNS_PREFIX="your-ms-alias-foobar-dns"               # Service details, remember to prefix with your alias/name.
SUBSCRIPTION="5c736ca9-d3af-4f10-af27-955b1f8a1748" # Subscription where services will be billed.
LOCATION="westus"                                   # Azure Region where the cluster is created.
AGENT_COUNT=3                                       # Number of VMs where services can be deployed.
MASTER_COUNT=1                                      # Number of VMs where load balancers will be installed.
                                                    # 1 is ok for testing. Use 3 or 5 for reliable deployments.
AGENT_VM_SIZE="Standard_A2"                         # VM size.
ADMIN_USERNAME="azureiotpcs"                        # Used for "kubectl" authentication.
SSH_KEY_FILE="$HOME/.ssh/azureiotpcs/id_rsa"        # Used for "kubectl" authentication.



##############################
### DO NOT EDIT CODE BELOW ###
##############################

COL_NO="\033[0m" # no color
COL_ERR="\033[1;31m" # light red
COL_H1="\033[1;33m" # yellow
COL_H2="\033[1;36m" # light cyan

test_dependencies() {
    _TMPV=$(which az)
    if [ ! -f "$_TMPV" ]; then
        error "Azure CLI tool not found. To install Azure CLI see: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli"
        exit -1;
    fi

    if [ ! -f "$SSH_KEY_FILE" ]; then
        error "`$SSH_KEY_FILE` not found. Review the configuration.";
        exit -1;
    fi

    _TMPV="$HOME/.ssh/id_rsa"
    if [ ! -f "$_TMPV" ]; then
        error "SSH private key file `$_TMPV` not found. Use `ssh-keygen` to create a key.";
        exit -1;
    fi
}

azure_authentication() {
    header "Authentication"
    announce "Press CTRL+C if you alread logged in"
    az login
    az account show
}

set_current_subscription() {
    header "Set current subscription"
    az account set --subscription $SUBSCRIPTION
}

create_resource_group() {
    header "Create resource group"
    az group create --name $RESOURCE_GROUP --location $LOCATION
}

create_cluster() {
    SSH_KEY_PUBLIC=$(cat "$SSH_KEY_FILE.pub")
    header "Create cluster, this might take a while..."
    az acs create \
        --orchestrator-type Kubernetes \
        --name "$CLUSTER_NAME" \
        --dns-prefix "$DNS_PREFIX" \
        --admin-username "$ADMIN_USERNAME" \
        --ssh-key-value "$SSH_KEY_PUBLIC" \
        --agent-count "$AGENT_COUNT" \
        --master-count "$MASTER_COUNT" \
        --agent-vm-size "$AGENT_VM_SIZE" \
        --location "$LOCATION" \
        --resource-group "$RESOURCE_GROUP"
}

header() {
    echo -e "${COL_H1}\n### $1 ${COL_NO}\n"
}

announce() {
    echo -e "${COL_H2}\n> $1 ${COL_NO}"
}

error() {
    echo -e "${COL_ERR}$1 ${COL_NO}";
}

get_kubernetes_credentials() {
    echo -e "${COL_H1}\n### Kubernetes authentication${COL_NO}"
    az acs kubernetes get-credentials --resource-group "$RESOURCE_GROUP" --name "$CLUSTER_NAME" --ssh-key-file "$SSH_KEY_FILE"
    kubectl get nodes
}

test_dependencies
azure_authentication
set_current_subscription
create_resource_group
create_cluster
get_kubernetes_credentials

