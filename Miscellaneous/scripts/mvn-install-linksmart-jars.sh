#!/bin/bash

#this script can help you install linksmart bundles locally inside Maven. (given that you have the jars)
#WARNING by Riccardo: this has been prepared by Riccardo as a quick fix: the group/artifacts names might be wrong

#just run this in the folder where you have the jars.

mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.config.managerconfigurator-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.config.managerconfigurator
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.middlewareapi-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.middlewareapi
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.middlewareclients-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.middlewareclients
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.network.backbone.data-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.network.backbone.data
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.network.backbone.jxta-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.network.backbone.jxta
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.network.backbone.router-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.network.backbone.router
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.network.backbone.soap-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.network.backbone.soap
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.network.identitymanager-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.network.identitymanager
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.network.networkmanager-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.network.networkmanager
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.network.soaptunneling-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.network.soaptunneling
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.network.tunneling-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.network.tunneling
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.security.communicationsecuritymanager.impl.asym-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.security.communicationsecuritymanager.impl.asym
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.security.communicationsecuritymanager.impl.sym-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.security.communicationsecuritymanager.impl.sym
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.security.cryptomanager-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.security.cryptomanager
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.security.trustmanager-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.security.trustmanager
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.tools.log4jfragment-1.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.tools.log4jfragment
mvn install:install-file -Dpackaging=jar -Dfile=eu.linksmart.wsprovider-2.0.0.jar -DgroupId=eu.linksmart -Dversion=2.0.0 -DartifactId=eu.linksmart.wsprovider

