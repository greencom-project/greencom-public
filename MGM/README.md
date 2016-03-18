MGM component
============================

This component includes the implementation of all functional features for the GreenCom Microgrid Manager 

Main sub-component folders
-------------------------

- MGMAPI (Resp. Jonas Sejr ACTUA)


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
This component is managed by [Jonas Sejr](mailto:jse@actua.dk).
Any question/issue can be signalled in the [wp7 mailing list](greencom-wp7@ismb.it). 

**Important Note:** Before adding any new sub-components this folder, please contact the component responsible and ensure consistancy with the naming (and role) in the latest stable architecture document.



