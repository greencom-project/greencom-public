MGMBroker component
============================

It includes three sub-components (i.e. Maven modules): *MGMBrokerAPI*, *MGMBrokerGateway*,  *MGMBrokerManager*.
You can run components using a target platform available [here](https://dl.dropboxusercontent.com/u/18936021/mgmbroker_target%20platform.zip)

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


Relevant links
---------------

An explaination on how MGMBroker components works is available on the [wiki](https://confluence.fit.fraunhofer.de/confluence/pages/viewpage.action?title=MGMBroker&spaceKey=GCOM).
This implementation matches with [last GreenCom architecture version](https://confluence.fit.fraunhofer.de/confluence/download/attachments/17728455/GreenCom%20Components-20130712.vpp?version=1&modificationDate=1373640289293&api=v2)


Contacts
---------------
This component is managed by [Ivan Grimaldi](mailto:grimaldi@ismb.it).
Any question/issue can be signalled in the [wp7 mailing list](greencom-wp7@ismb.it). 




