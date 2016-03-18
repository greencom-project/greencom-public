#!/bin/bash

# Start script for a P2 platform: creates an Equinox config.ini file 
# based on available bundles in plugins folder and starts them.
#
# Identify and move Equinox jar
EQUINOX=`find plugins -name "org.eclipse.osgi_*.jar"`
if [ -n "$EQUINOX" ]
then
    mv -f $EQUINOX .
    EQUINOX=`basename "$EQUINOX"`
else
  # Find e.g. "org.eclipse.osgi-3.6.2.jar" or "org.eclipse.osgi_3.9.1.v20130814-1242.jar"
  # Exclude "org.eclipse.osgi.services-3.2.100.jar"
  EQUINOX=`find . -name "org.eclipse.osgi[^.]*"`
fi

# Create Equinox configuration 
BUNDLES=""
for BUNDLE in `find plugins/ -name "*.jar"`
 do 
  if [ -n "$BUNDLES" ]; then BUNDLES+=","; fi
  # Inspect manifest fro fragments
  FRAGMENT=`unzip -p  $BUNDLE META-INF/MANIFEST.MF | grep "Fragment-Host:"`
  # Start only non-fragment bundles
  if [ -n "$FRAGMENT" ]; then BUNDLES+="$BUNDLE"; else BUNDLES+="$BUNDLE@start"; fi
 done

# Create config folder, if not exists
mkdir -p configuration

# Expected to contain new bundle versions, default for Fileinstall
mkdir -p load

# Generate config.ini file
cat << TEMPLATE > configuration/config.ini
osgi.bundles=${BUNDLES}
osgi.console=true
osgi.os=linux
osgi.arch=x86_64
osgi.debug
osgi.noShutdown=true
osgi.bundles.defaultStartLevel=4
osgi.configuration.cascaded=false
eclipse.consoleLog=true
eclipse.exitOnError=true
eclipse.ignoreApp=true
equinox.ds.error=true
equinox.ds.print=true
org.osgi.service.http.port=8080
TEMPLATE

echo "Starting $EQUINOX"
java -jar "$EQUINOX" -console

