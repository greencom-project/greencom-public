GreenCom Project
============================

This page provides an overview of the main GreenCom artefacts.

Maven
-------------------------
Parent POM shared by all components. Provides a shared configuration of the Eclipse Tycho-based build. It further references GreenCom's Tycho target platform definition (eu.greencom.provisioning.target-platform-luna). 
xGateway
-------------------------
Components of the GreenCom runtime installed in prosumer's houses. 
See [xGateway documentation](https://linksmart.eu/redmine/projects/greencom/wiki/XGateway) for details.

MGMBroker
-------------------------
Components of the distributed MGMBroker connecting the prosumer's houses and the GreenCom server.
See [MGMBroker documentation](https://linksmart.eu/redmine/projects/greencom/wiki/MGMBroker) for details.

MGM
-------------------------
Client components to access GreenCom's cloud services (Data warehouse, Grid topology, Metadata etc.) See [MGM documentation](https://linksmart.eu/redmine/projects/greencom/wiki/MGM) for details.

MGA
-------------------------
Microgrid aggregator GUI and generic aggregation components.

Miscellaneous
-------------------------
Components and scripts shared across all GreenCom layers (Logging, platform maintenance).

Provisioning
-------------------------
Definitions of installable artefacts (home and server-side runtimes) and Tycho target platform used to resolve relatred dependencies.

