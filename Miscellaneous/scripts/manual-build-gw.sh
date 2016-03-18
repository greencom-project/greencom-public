#!/bin/bash

#mvnc.sh is a "coloured" version of mvn
MVNCOMMAND=mvnc.sh

echo Remember to run from trunk as ./Miscellaneous/scripts/manual-build-all.sh 
echo "At the end of the process, results should be available in /Provisioning/RepositoryGateway/Target"

read -p "Press [Enter] key to start compilation..."

#### Step Maven
cd Maven
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP Maven failed"
    exit
fi
cd ..

#### Step TargetPlatform
cd Provisioning/TargetPlatform/
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP TargetPlatform failed"
    exit
fi
cd ../..

#### Step MGMAPI
cd MGM/MGMAPI
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP MGMAPI failed"
    exit
fi
cd ../..

#### Step MGM
cd MGM
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP MGM failed"
    exit
fi
cd ..

#### Step MGMBrokerAPI
cd MGMBroker/MGMBrokerAPI
$MVNCOMMAND install -Dmaven.test.skip=true
if [ $? -ne 0 ]; then
    echo "STEP MGMBrokerAPI failed"
    exit
fi
cd ../..

#### Step MGMMetaInformationStoreAPI
cd MGM/MGMMetaInformationStoreAPI
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP MGMMetaInformationStoreAPI failed"
    exit
fi
cd ../..

#### Step xGatewayAPI
cd xGateway/xGatewayAPI
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP xGatewayAPI failed"
    exit
fi
cd ../..

#### Step WebApiConsumer
cd MGM/WebApiConsumer
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP 7 failed"
    exit
fi
cd ../..

#### Step ServiceBus
cd MGM/ServiceBus
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP ServiceBus failed"
    exit
fi
cd ../..

#### Step xGateway
cd xGateway
$MVNCOMMAND install -Dmaven.test.skip=true
if [ $? -ne 0 ]; then
    echo "STEP xGateway failed"
    exit
fi
cd ..

### Step 10
cd MGMBroker
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP 10 failed"
    exit
fi
cd ..

#### Step FeatureLinksmart
cd Provisioning/FeatureLinksmart/
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP FeatureLinksmart failed"
    exit
fi
cd ../..

#### Step FeatureOsgiRuntime
cd Provisioning/FeatureOsgiRuntime
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP FeatureOsgiRuntime failed"
    exit
fi
cd ../..

#### Step FeatureGateway2
cd Provisioning/FeatureGateway2
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP FeatureGateway2 failed"
    exit
fi
cd ../..

#### Step RepositoryGateway
cd Provisioning/RepositoryGateway
$MVNCOMMAND install
if [ $? -ne 0 ]; then
    echo "STEP RepositoryGateway failed"
    exit
fi
cd ../..

echo "Results should be available in /Provisioning/RepositoryGateway/Target"

