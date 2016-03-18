xGateway component
============================

This component includes the implementation of all functional features for the GreenCom Home gateway (i.e. running on raspberry PI or similar constrained environments)
Most sub-components in this folder will be shared across HL, DG and DS Gateway.

Main sub-component folders
-------------------------

- xGatewayAPI (Resp. Daniela Fisseler - FIT)
- ConfigurationBroker (Resp. Riccardo Tomasi - ISMB)
- Distribution (Resp. Daniela Fisseler - FIT)


Main Maven Goals
----------------

- *mvn install*
	- Full build and local install of the component, including all sub-components
	
- *mvn test*
	- run all junit tests in this component	

- *mvn package*
	- full build and deploy of all jars into the *Distribution/* submodule

- *mvn clean*
	- Full project clean-up
	
- *mvn test*
	- run all junit tests in this component.

- *mvn surefire-report:report* 
	- same as *mvn test* but generating HTML report. 

- *mvn sonar:sonar* 
	- Run sonar tests on your project (incl. test coverage, etc). You need to have a local sonar server running on port 9000.

- *mvn dependency:copy-dependencies* 
	- Downloads the full dependencies set in the *target/dependency* folder of each sub-component.

Additional flags/notes
----------------------

- you can use the * -o * flag for offline mode i.e. to avoid re-dowloading all dependencies (e.g. *mvn -o install*)
- many build/compilation issues (e.g. due to partial/interrupted operations) can be normally solved by cleaning before building (e.g. *mvn clean install*)

Contacts
---------------
This component is managed by [Daniela Fisseler](mailto:daniela.fisseler@fit.fraunhofer.de).
Any question/issue can be signalled in the [wp4 mailing list](greencom-wp4@ismb.it). 

**Important Note:** Before adding any new sub-components this folder, please contact the component responsible and ensure consistancy with the naming (and role) in the latest stable architecture document.



