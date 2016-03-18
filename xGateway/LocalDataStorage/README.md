LocalDataStorage sub-component
============================

LocalDataStorage is a sub-component in charge to communicate with a database.
It expose a TimeSeriesManager API interface.


How to use it
---------------
1 - To use this component you need of a MongoDB installation
2 - Configure the component through the property file located in src/main/resources/LocalDataStorage.properties
	In this file you have to set the properties "db.url=<host>:<port>" and "db.name=<db-name>"
	Note: if the db <db-name> doesn't exist, will be created when the first insert occurs
3 - For information about the service exposed see the TimeSeriesManager API documentation

Contacts
---------------
This sub-component is managed by [Giorgio Dal Toe'](mailto:daltoe@ismb.it).
Any question/issue can be signalled directly to me.