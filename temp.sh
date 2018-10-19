#!/bin/bash
declare -A microservices
# servicesbuilt=""

microservices+=(
        ["asamanager"]="asamanager"
        ["pcsauth"]="auth"
        ["pcsconfig"]="config"
        ["iothubmanager"]="iothubmanager"
        ["pcsstorageadapter"]="storageadapter"
        ["devicesimulation"]="simulation"
        ["devicetelemetry"]="devicetelemetry"
        ["webui"]="webui"
)

 for microservice in ${!microservices[@]}; do
        msfolder=${microservices[${microservice}]}
        echo "Changing release for $msfolder for $DNSNAME"
        #sed -i s/DNSNAME/$DNSNAME/g $msfolder/values.yaml
        sed -i s/dotnet/java/g $msfolder/values.yaml
 done
